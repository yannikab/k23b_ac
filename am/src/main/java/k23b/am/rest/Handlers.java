package k23b.am.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.Logger;

import k23b.am.dao.AgentDao;
import k23b.am.dao.JobDao;
import k23b.am.dao.RequestDao;
import k23b.am.dao.RequestStatus;
import k23b.am.dao.ResultDao;
import k23b.am.srv.AgentSrv;
import k23b.am.srv.JobSrv;
import k23b.am.srv.RequestSrv;
import k23b.am.srv.ResultSrv;
import k23b.am.srv.SrvException;

/**
 * A class for the handling of the REST communication between the AM and SA using methods that listen to certain URIs.
 */

@Path("handle/")
public class Handlers {

    @XmlTransient
    private static final Logger log = Logger.getLogger(Handlers.class);

    /**
     * The AM gets a request from the SA with the following parameters.
     * 
     * @param hashKey The hashkey of the SA who makes the request.
     * @param nmapVersion The version of nmap that the SA who makes the request has.
     * @param osVersion The version of OS that the SA who makes the request has.
     * @param interfaceMAC The MAC address that the SA who makes the request has.
     * @param interfaceIP The IP address that the SA who makes the request has.
     * @param deviceName The device name that the SA who makes the request has.
     * @return A Response to the SA with either a code error status or a code success and the request status.
     */

    @GET
    @Path("request/send/{hash}/{nmapVersion}/{osVersion}/{interfaceMAC}/{interfaceIP}/{deviceName}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getRequest(
            @PathParam("hash") String hashKey,
            @PathParam("nmapVersion") String nmapVersion,
            @PathParam("osVersion") String osVersion,
            @PathParam("interfaceMAC") String interfaceMAC,
            @PathParam("interfaceIP") String interfaceIP,
            @PathParam("deviceName") String deviceName) {

        log.info("");
        log.info("getRequest(): Handling request from agent with hash: " + hashKey);

        RequestDao reqDao;

        try {

            reqDao = RequestSrv.findByHash(hashKey);

            if (reqDao == null) {
                log.info("getRequest(): Agent hash not found, creating a new request entry.");
                reqDao = RequestSrv.create(hashKey, deviceName, interfaceIP, interfaceMAC, osVersion, nmapVersion);
            } else {
                log.info("getRequest(): Agent hash found, updating time received on existing entry.");
                RequestSrv.updateTimeReceived(reqDao.getRequestId());
            }

            log.info("Returning status: " + reqDao.getRequestStatus());

            return Response.status(200).entity(reqDao.getRequestStatus().toString()).build();

        } catch (SrvException e) {
            // e.printStackTrace();
            log.error(e.getMessage());

            return Response.status(500).build();
        }
    }

    /**
     * An SA makes a check to the AM for its request status.
     * 
     * @param hashKey The hashkey of the SA who makes the check.
     * @return A Response to the SA with either a code error status or a code success and the request status.
     */
    @GET
    @Path("request/check/{hashKey}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response checkRequest(
            @PathParam("hashKey") String hashKey) {

        log.info("");
        log.info("checkRequest(): Checking request from agent with hash: " + hashKey);

        try {

            RequestDao reqDao = RequestSrv.findByHash(hashKey);

            if (reqDao == null) {

                log.info("checkRequest(): Received check request from unknown agent with hash: " + hashKey);

                return Response.status(200).entity("Unknown").build();

            } else {

                RequestStatus rs = reqDao.getRequestStatus();

                log.info("Returning status: " + rs);

                return Response.status(200).entity(rs.toString()).build();
            }

        } catch (SrvException e) {
            // e.printStackTrace();
            log.error(e.getMessage());

            return Response.status(500).build();
        }
    }

    /**
     * An SA makes a Job request from the AM.
     * 
     * @param hashKey The hashkey of the SA who makes the check.
     * @return A Response to the SA with either a code error status or a code success and a list with the Jobs to run.
     */

    @GET
    @Path("jobs/get/{hashKey}")
    @Produces(MediaType.APPLICATION_XML)
    public Response getJobs(
            @PathParam("hashKey") String hashKey) {

        log.info("");
        log.info("getJobs(): Job request from agent with hash:" + hashKey);

        try {

            RequestDao rd = RequestSrv.findByHash(hashKey);

            if (rd == null) {

                log.info("getJobs(): Received job request from unknown agent with hash: " + hashKey);

                JobContainer jc = new JobContainer("Unknown");

                log.info("Returning status: " + jc.getStatus());

                return Response.status(200).entity(jc).build();
            }

            RequestStatus rs = rd.getRequestStatus();

            JobContainer jc = new JobContainer(rs.toString());

            if (rs == RequestStatus.PENDING || rs == RequestStatus.REJECTED) {

                try {

                    JAXBContext jaxbContext = JAXBContext.newInstance(JobContainer.class);
                    Marshaller marshaller = jaxbContext.createMarshaller();
                    marshaller.setEventHandler(new ResultValidationEventHandler());
                    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                    marshaller.marshal(jc, System.out);

                } catch (JAXBException e) {
                    // e.printStackTrace();
                    log.error(e.getMessage());
                }

                log.info("Returning status: " + jc.getStatus());

                return Response.status(200).entity(jc).build();
            }

            if (rs == RequestStatus.ACCEPTED) {

                AgentDao ad = AgentSrv.findByRequestId(rd.getRequestId());

                if (ad == null) {

                    log.error("No corresponding agent found for accepted request with id:" + rd.getRequestId());

                    return Response.status(500).build();
                }

                AgentSrv.jobsRequested(ad.getAgentId());

                Set<JobDao> jdSet = JobSrv.findAllWithAgentId(ad.getAgentId(), false);

                for (JobDao j : jdSet) {

                    Job job = new Job();

                    job.setJobId(j.getJobId());
                    job.setAgentId(j.getAgentId());
                    job.setAdminId(j.getAdminId());
                    job.setTimeAssigned(j.getTimeAssigned());
                    job.setTimeSent(j.getTimeSent());
                    job.setParams(j.getParams());
                    job.setPeriodic(j.getPeriodic());
                    job.setPeriod(j.getPeriod());
                    job.setTimeStopped(j.getTimeStopped());

                    jc.getJobs().add(job);
                }

                jc.getJobs().sort((first, second) -> {
                    if (first.getJobId() > second.getJobId())
                        return 1;
                    else if (first.getJobId() < second.getJobId())
                        return -1;
                    else
                        return 0;
                });

                for (JobDao j : jdSet)
                    JobSrv.send(j.getJobId());

                try {

                    JAXBContext jaxbContext = JAXBContext.newInstance(JobContainer.class);
                    Marshaller marshaller = jaxbContext.createMarshaller();
                    marshaller.setEventHandler(new ResultValidationEventHandler());
                    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                    marshaller.marshal(jc, System.out);

                } catch (JAXBException e) {
                    // e.printStackTrace();
                    log.error(e.getMessage());
                }

                log.info("Returning status: " + jc.getStatus());

                return Response.status(200).entity(jc).build();
            }

        } catch (SrvException e) {
            // e.printStackTrace();
            log.error(e.getMessage());
        }

        return Response.status(500).build();
    }

    /**
     * The AM gets a list with the Results of some Jobs the SA has finished.
     * 
     * @param hashKey The hashkey of the SA who makes the check.
     * @param resultContainer The list with the Results the SA has sent to the AM.
     * @return A Response to the SA with a code success.
     */

    @POST
    @Path("results/post/{hashKey}")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response getResults(
            @PathParam("hashKey") String hashKey,
            ResultContainer resultContainer) {

        log.info("");
        log.info("getResults(): Results received from agent with hash: " + hashKey);

        List<Result> resList = resultContainer.getResults();

        // In case we want the SA to send again some problematic Results
        List<Result> errorResList = new ArrayList<Result>();

        if (resList.isEmpty()) {
            log.info("Empty Result List");
        } else {
            log.info("Result List came with " + resList.size() + " items");
            for (Result res : resList) {
                try {
                    ResultDao resDao = ResultSrv.create(res.getJobId(), res.getJobResult());
                    if (resDao == null)
                        log.error("Result Dao was not created");
                } catch (SrvException e) {
                    System.out.println(e.getMessage());
                    log.error(e.getMessage());
                    errorResList.add(res);
                }
            }
        }

        return Response.status(200).build();
    }
}
