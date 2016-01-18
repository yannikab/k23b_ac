package k23b.am.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import k23b.am.dao.AgentDao;
import k23b.am.dao.JobDao;
import k23b.am.dao.RequestDao;
import k23b.am.srv.AdminSrv;
import k23b.am.srv.AgentSrv;
import k23b.am.srv.JobSrv;
import k23b.am.srv.RequestSrv;
import k23b.am.srv.SrvException;
import k23b.am.srv.UserSrv;

@Path("client/")
public class ClientHandlers {

    private static final Logger log = Logger.getLogger(ClientHandlers.class);

    @GET
    @Path("login/{username}/{password}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response login(
            @PathParam("username") String username,
            @PathParam("password") String password) {

        try {

            UserSrv.login(username, password);

            return Response.status(200).entity("Accepted").build();

        } catch (SrvException e) {
            // e.printStackTrace();
            return Response.status(200).entity("Invalid").build();
        }
    }

    @GET
    @Path("agents/{username}/{password}")
    @Produces(MediaType.APPLICATION_XML)
    public Response agents(
            @PathParam("username") String username,
            @PathParam("password") String password) {

        try {

            // if (!UserSrv.isAccepted(username))
            // return Response.status(200).entity(new Agents("Not Accepted")).build();
            //
            // if (!UserSrv.isLoggedIn(username))
            // return Response.status(200).entity(new Agents("Not Logged In")).build();
            //
            // UserDao ud = UserSrv.findByUsername(username);

            AgentContainer agentContainer = new AgentContainer("Accepted");

            // for (AgentDao ad : AgentSrv.findAllWithAdminId(ud.getAdminId())) {

            for (AgentDao ad : AgentSrv.findAll()) {

                Agent a = new Agent();

                a.setAgentId(ad.getAgentId());
                a.setRequestHash(RequestSrv.findById(ad.getRequestId()).getHash());
                a.setAdminUsername(AdminSrv.findById(ad.getAdminId()).getUsername());
                a.setTimeAccepted(ad.getTimeAccepted());
                a.setTimeJobRequest(ad.getTimeJobRequest());
                a.setTimeTerminated(ad.getTimeTerminated());

                agentContainer.getAgents().add(a);
            }

            return Response.status(200).entity(agentContainer).build();

        } catch (SrvException e) {
            // e.printStackTrace();
            return Response.status(200).entity(new AgentContainer("Invalid")).build();
        }
    }

    @GET
    @Path("jobs/{username}/{password}/{agentHash}")
    @Produces(MediaType.APPLICATION_XML)
    public Response agents(
            @PathParam("username") String username,
            @PathParam("password") String password,
            @PathParam("agentHash") String agentHash) {

        try {

            // if (!UserSrv.isAccepted(username))
            // return Response.status(200).entity(new Agents("Not Accepted")).build();
            //
            // if (!UserSrv.isLoggedIn(username))
            // return Response.status(200).entity(new Agents("Not Logged In")).build();
            //
            // UserDao ud = UserSrv.findByUsername(username);

            RequestDao rd = RequestSrv.findByHash(agentHash);

            if (rd == null)
                return Response.status(200).entity(new JobContainer("Invalid")).build();

            AgentDao ad = AgentSrv.findByRequestId(rd.getRequestId());

            if (ad == null)
                return Response.status(200).entity(new JobContainer("Invalid")).build();

            JobContainer jobContainer = new JobContainer("Accepted");

            for (JobDao jd : JobSrv.findAllWithAgentId(ad.getAgentId())) {

                Job j = new Job();

                j.setJobId(jd.getJobId());
                j.setAgentId(jd.getAgentId());
                j.setAdminId(jd.getAdminId());
                j.setTimeAssigned(jd.getTimeAssigned());
                j.setTimeSent(jd.getTimeSent());
                j.setParams(jd.getParams());
                j.setPeriodic(jd.getPeriodic());
                j.setPeriod(jd.getPeriod());
                j.setTimeStopped(jd.getTimeStopped());

                jobContainer.getJobs().add(j);
            }

            return Response.status(200).entity(jobContainer).build();

        } catch (SrvException e) {
            // e.printStackTrace();
            return Response.status(200).entity(new JobContainer("Invalid")).build();
        }
    }
}
