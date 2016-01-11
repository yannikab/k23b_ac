package k23b.am.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import k23b.am.srv.SrvException;
import k23b.am.srv.UserSrv;

@Path("client/")
public class ClientHandlers {

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
}
