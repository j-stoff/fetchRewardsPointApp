package com.fetch.rewards.FetchRewardsApp;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("/fetchRewards")
public class FetchRewardsService {
	
	private final Logger logger = LogManager.getLogger(FetchRewardsService.class);
	private static final Gson jsonTransformer = new Gson();

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
	@Path("/echo")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String echo() {
        return "Echo";
    }
	
    @POST
    @Path("/userAccount/new")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createNewUserAccount(@FormParam("userAccountName") String userAccountName) {
    	try {
    		// Check if user exists, otherwise create a blank account.
    		boolean wasAccountCreated = PaymentLogic.getInstance().createNewUser(userAccountName);
    		
    		return Response.ok(jsonTransformer.toJson(wasAccountCreated)).build();
    	} catch (Exception e) {
    		return Response.status(Status.BAD_REQUEST).entity(jsonTransformer.toJson(e)).build();
    	}
    }
}
