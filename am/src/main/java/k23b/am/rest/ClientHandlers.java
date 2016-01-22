package k23b.am.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import k23b.am.dao.AdminDao;
import k23b.am.dao.AgentDao;
import k23b.am.dao.JobDao;
import k23b.am.dao.RequestDao;
import k23b.am.dao.ResultDao;
import k23b.am.srv.AdminSrv;
import k23b.am.srv.AgentSrv;
import k23b.am.srv.JobSrv;
import k23b.am.srv.RequestSrv;
import k23b.am.srv.ResultSrv;
import k23b.am.srv.SrvException;
import k23b.am.srv.UserCredentialsException;
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

        String service = new Object() {
        }.getClass().getEnclosingMethod().getName() + "(): ";

        log.info(service + "Log in requested." + String.format(" (username=%s)", username));

        try {

            if (!UserSrv.isAccepted(username)) {

                log.info(service + "User is not accepted.");

                if (UserSrv.findByUsername(username).getPassword().equals(password))
                    return Response.status(200).entity("Pending").build();
                else
                    return Response.status(200).entity("Incorrect Credentials").build();
            }

            UserSrv.login(username, password);

            log.info(service + "Logged in user.");

            return Response.status(200).entity("Accepted").build();

        } catch (UserCredentialsException e) {

            log.info(service + e.getMessage());

            return Response.status(200).entity("Incorrect Credentials").build();

        } catch (SrvException e) {

            log.error(service + e.getMessage());

            return Response.status(200).entity("Service Error").build();
        }
    }

    @GET
    @Path("register/{username}/{password}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response register(
            @PathParam("username") String username,
            @PathParam("password") String password) {

        String service = new Object() {
        }.getClass().getEnclosingMethod().getName() + "(): ";

        log.info(service + "User registration requested." + String.format(" (username=%s)", username));

        try {

            UserSrv.create(username, password);

            return Response.status(200).entity("Accepted").build();

        } catch (UserCredentialsException e) {

            log.info(service + e.getMessage());

            return Response.status(200).entity("User Exists").build();

        } catch (SrvException e) {

            log.error(service + e.getMessage());

            return Response.status(200).entity("Service Error").build();
        }
    }

    @GET
    @Path("agents/{username}/{password}")
    @Produces(MediaType.APPLICATION_XML)
    public Response agents(
            @PathParam("username") String username,
            @PathParam("password") String password) {

        String service = new Object() {
        }.getClass().getEnclosingMethod().getName() + "(): ";

        log.info(service + "Agents requested." + String.format(" (username=%s)", username));

        try {

            if (!UserSrv.isAccepted(username)) {

                log.info(service + "User is not accepted.");

                if (UserSrv.findByUsername(username).getPassword().equals(password))
                    return Response.status(200).entity(new AgentContainer("Pending")).build();
                else
                    return Response.status(200).entity(new AgentContainer("Incorrect Credentials")).build();
            }

            if (!UserSrv.isLoggedIn(username)) {

                log.info(service + "User is not logged in.");

                if (UserSrv.findByUsername(username).getPassword().equals(password))
                    return Response.status(200).entity(new AgentContainer("Not Logged In")).build();
                else
                    return Response.status(200).entity(new AgentContainer("Incorrect Credentials")).build();
            }

            AgentContainer agentContainer = new AgentContainer("Accepted");

            for (AgentDao ad : AgentSrv.findAllWithAdminId(UserSrv.findByUsername(username).getAdminId()))
                agentContainer.getAgents().add(AgentFactory.fromDao(ad));

            log.info(service + "Returning " + agentContainer.getAgents().size() + " agents.");

            return Response.status(200).entity(agentContainer).build();

        } catch (UserCredentialsException e) {

            log.info(service + e.getMessage());

            return Response.status(200).entity("Incorrect Credentials").build();

        } catch (SrvException e) {

            log.error(service + e.getMessage());

            return Response.status(200).entity(new AgentContainer("Service Error")).build();
        }
    }

    @GET
    @Path("jobs/{username}/{password}/{agentHash}")
    @Produces(MediaType.APPLICATION_XML)
    public Response agents(
            @PathParam("username") String username,
            @PathParam("password") String password,
            @PathParam("agentHash") String agentHash) {

        String service = new Object() {
        }.getClass().getEnclosingMethod().getName() + "(): ";

        log.info(service + "Jobs requested." + String.format(" (username=%s, agentHash=%s)", username, agentHash));

        try {

            if (!UserSrv.isAccepted(username)) {

                log.info(service + "User is not accepted.");

                if (UserSrv.findByUsername(username).getPassword().equals(password))
                    return Response.status(200).entity(new JobContainer("Pending")).build();
                else
                    return Response.status(200).entity(new JobContainer("Incorrect Credentials")).build();
            }

            if (!UserSrv.isLoggedIn(username)) {

                log.info(service + "User is not logged in.");

                if (UserSrv.findByUsername(username).getPassword().equals(password))
                    return Response.status(200).entity(new JobContainer("Not Logged In")).build();
                else
                    return Response.status(200).entity(new JobContainer("Incorrect Credentials")).build();
            }

            RequestDao rd = RequestSrv.findByHash(agentHash);

            if (rd == null) {

                log.info(service + "Request not found.");

                return Response.status(200).entity(new JobContainer("Invalid")).build();
            }

            AgentDao ad = AgentSrv.findByRequestId(rd.getRequestId());

            if (ad == null) {

                log.info(service + "Agent not found.");

                return Response.status(200).entity(new JobContainer("Invalid")).build();
            }

            JobContainer jobContainer = new JobContainer("Accepted");

            for (JobDao jd : JobSrv.findAllWithAgentId(ad.getAgentId()))
                jobContainer.getJobs().add(JobFactory.fromDao(jd));

            log.info(service + "Returning " + jobContainer.getJobs().size() + " jobs.");

            return Response.status(200).entity(jobContainer).build();

        } catch (UserCredentialsException e) {

            log.info(service + e.getMessage());

            return Response.status(200).entity("Incorrect Credentials").build();

        } catch (SrvException e) {

            log.error(service + e.getMessage());

            return Response.status(200).entity(new JobContainer("Service Error")).build();
        }
    }

    @GET
    @Path("results/agent/{username}/{password}/{agentHash}/{number}")
    @Produces(MediaType.APPLICATION_XML)
    public Response agentResults(
            @PathParam("username") String username,
            @PathParam("password") String password,
            @PathParam("agentHash") String agentHash,
            @PathParam("number") int number) {

        String service = new Object() {
        }.getClass().getEnclosingMethod().getName() + "(): ";

        log.info(service + "Agent results requested." + String.format(" (username=%s, agentHash=%s, number=%d)", username, agentHash, number));

        try {

            if (!UserSrv.isAccepted(username)) {

                log.info(service + "User is not accepted.");

                if (UserSrv.findByUsername(username).getPassword().equals(password))
                    return Response.status(200).entity(new ResultContainer("Pending")).build();
                else
                    return Response.status(200).entity(new ResultContainer("Incorrect Credentials")).build();
            }

            if (!UserSrv.isLoggedIn(username)) {

                log.info(service + "User is not logged in.");

                if (UserSrv.findByUsername(username).getPassword().equals(password))
                    return Response.status(200).entity(new ResultContainer("Not Logged In")).build();
                else
                    return Response.status(200).entity(new ResultContainer("Incorrect Credentials")).build();
            }

            RequestDao rd = RequestSrv.findByHash(agentHash);

            if (rd == null) {

                log.info(service + "Could not find request.");

                return Response.status(200).entity(new ResultContainer("Invalid")).build();
            }

            AgentDao ad = AgentSrv.findByRequestId(rd.getRequestId());

            if (ad == null) {

                log.info(service + "Could not find agent.");

                return Response.status(200).entity(new ResultContainer("Invalid")).build();
            }

            ResultContainer resultContainer = new ResultContainer("Accepted");

            for (ResultDao r : ResultSrv.findLast(ad.getAgentId(), number))
                resultContainer.getResults().add(ResultFactory.fromDao(r));

            resultContainer.getResults().sort(new ResultComparator());

            log.info(service + "Returning " + resultContainer.getResults().size() + " results.");

            return Response.status(200).entity(resultContainer).build();

        } catch (UserCredentialsException e) {

            log.info(service + e.getMessage());

            return Response.status(200).entity("Incorrect Credentials").build();

        } catch (SrvException e) {

            log.error(service + e.getMessage());

            return Response.status(200).entity(new ResultContainer("Service Error")).build();
        }
    }

    @GET
    @Path("results/all/{username}/{password}/{number}")
    @Produces(MediaType.APPLICATION_XML)
    public Response allResults(
            @PathParam("username") String username,
            @PathParam("password") String password,
            @PathParam("number") int number) {

        String service = new Object() {
        }.getClass().getEnclosingMethod().getName() + "(): ";

        log.info(service + "All results requested." + String.format(" (username=%s, number=%d)", username, number));

        try {

            if (!UserSrv.isAccepted(username)) {

                log.info(service + "User is not accepted.");

                if (UserSrv.findByUsername(username).getPassword().equals(password))
                    return Response.status(200).entity(new ResultContainer("Pending")).build();
                else
                    return Response.status(200).entity(new ResultContainer("Incorrect Credentials")).build();
            }

            if (!UserSrv.isLoggedIn(username)) {

                log.info(service + "User is not logged in.");

                if (UserSrv.findByUsername(username).getPassword().equals(password))
                    return Response.status(200).entity(new ResultContainer("Not Logged In")).build();
                else
                    return Response.status(200).entity(new ResultContainer("Incorrect Credentials")).build();
            }

            ResultContainer resultContainer = new ResultContainer("Accepted");

            for (ResultDao r : ResultSrv.findLast(number))
                resultContainer.getResults().add(ResultFactory.fromDao(r));

            resultContainer.getResults().sort(new ResultComparator());

            log.info(service + "Returning " + resultContainer.getResults().size() + " results.");

            return Response.status(200).entity(resultContainer).build();

        } catch (UserCredentialsException e) {

            log.info(service + e.getMessage());

            return Response.status(200).entity("Incorrect Credentials").build();

        } catch (SrvException e) {

            log.error(service + e.getMessage());

            return Response.status(200).entity(new ResultContainer("Service Error")).build();
        }
    }

    @POST
    @Path("users/")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.TEXT_PLAIN)
    public Response users(UserContainer userContainer) {

        String service = new Object() {
        }.getClass().getEnclosingMethod().getName() + "(): ";

        log.info(service + "Users received.");

        try {

            for (User u : userContainer.getUsers()) {

                log.info(service + "Received " + u.getJobs().size() + " jobs from user: " + u.getUsername());

                try {

                    if (!UserSrv.isAccepted(u.getUsername())) {

                        log.info(service + "User is not accepted.");
                        continue;
                    }

                    if (!UserSrv.isLoggedIn(u.getUsername())) {

                        log.info(service + "User is not logged in.");
                        continue;
                    }

                } catch (UserCredentialsException e) {

                    log.info(service + e.getMessage());
                    continue;
                }

                AdminDao ad = AdminSrv.findByUsername(u.getUsername());

                for (Job j : u.getJobs())
                    JobSrv.create(j.getAgentId(), ad.getAdminId(), j.getParams(), j.getPeriodic(), j.getPeriod());
            }

            return Response.status(200).entity("Accepted").build();

        } catch (SrvException e) {

            log.error(service + e.getMessage());

            return Response.status(200).entity("Service Error").build();
        }
    }
}
