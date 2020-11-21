package com.fetch.rewards.FetchRewardsApp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
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
		logger.info("Echoing message back.");
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
    
    @GET
    @Path("/userAccount")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserBalance(@QueryParam("userAccountName") String userAccountName) {
    	try {
    		if (StringUtils.isBlank(userAccountName)) {
    			throw new IllegalArgumentException("User account name was null or empty.");
    		}
    		UserAccount userAccount = PaymentLogic.getInstance().getFullUserAccount(userAccountName);
    		UserAccountSummary summary = new UserAccountSummary();
    		summary.setBalance(userAccount.getUserAccountBalance());
    		summary.setAccountName(userAccount.getAccountName());
    		summary.setPayerList(userAccount.groupAllPaymentTransactionsByPayerName());
    		
    		return Response.ok(jsonTransformer.toJson(summary)).build();
    	} catch (Exception e) {
    		return Response.status(Status.BAD_REQUEST).entity(jsonTransformer.toJson(makeCatchExceptionOutput(e))).build();
    	}
    }
    
    @POST
    @Path("/payTransaction/today")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addPayerTransactionToday(
    		@FormParam("userAccountName") String userAccountName,
    		@FormParam("payerName") String payerName,
    		@FormParam("amontApplied") int amountApplied) {
    	
    	try {
    		if (StringUtils.isBlank(payerName)) {
    			throw new IllegalArgumentException("Payer name was not provided.");
    		}
    		UserAccount userAccount = PaymentLogic.getInstance().applyPaymentTransactionToUserAccount(payerName, amountApplied, new Date(), userAccountName);
    		Boolean wasPaymentApplied = userAccount != null;
    		
    		return Response.ok(jsonTransformer.toJson(new SingleValueWrapper(wasPaymentApplied))).build();
    	} catch (Exception e) {
    		return Response.status(Status.BAD_REQUEST).entity(jsonTransformer.toJson(makeCatchExceptionOutput(e))).build();
    	}
    }
    
    @POST
    @Path("/payTransaction")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addPayerTransaction(
    		@FormParam("userAccountName") String userAccountName,
    		@FormParam("payerName") String payerName,
    		@FormParam("amontApplied") int amountApplied,
    		@FormParam("datePaid") String datePaid) {
    	try {
    		if (StringUtils.isBlank(userAccountName)) {
    			throw new IllegalArgumentException("User Account was not specified.");
    		}
    		if (StringUtils.isBlank(payerName)) {
    			throw new IllegalArgumentException("Payer name was not provided.");
    		}
    		if (StringUtils.isBlank(datePaid)) {
    			throw new IllegalArgumentException("A date applied must be supplied");
    		}
    		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    		Date appliedDate = dateFormat.parse(datePaid);
    		
    		UserAccount userAccount = PaymentLogic.getInstance().applyPaymentTransactionToUserAccount(payerName, amountApplied, appliedDate, userAccountName);
    		Boolean wasPaymentApplied = userAccount != null;
    		
    		return Response.ok(jsonTransformer.toJson(new SingleValueWrapper(wasPaymentApplied))).build();
    	} catch (Exception e) {
    		return Response.status(Status.BAD_REQUEST).entity(jsonTransformer.toJson(makeCatchExceptionOutput(e))).build();
    	}
    }
    
    @POST
    @Path("/deduction")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response applyDeduction(
    		@FormParam("userAccountName") String userAccountName,
    		@FormParam("amontApplied") int amountApplied) {
    	try {
    		if (StringUtils.isBlank(userAccountName)) {
    			throw new IllegalArgumentException("User Account was not specified.");
    		}
    		List<PaymentTransaction> payments = PaymentLogic.getInstance().applyDeductionToUserAccount(amountApplied, userAccountName);
    		
    		return Response.ok(jsonTransformer.toJson(new ListWrapper(payments))).build();
    	} catch (Exception exception) {
    		return Response.status(Status.BAD_REQUEST).entity(jsonTransformer.toJson(makeCatchExceptionOutput(exception))).build();
    	}
    }
    
    /**
     * Make an object that can be returned to the user without exposing too much of the application exception. Though it will return
     * the actual cause of the error.
     * @param exception the exception that occurred
     * @return a FetchRewardsExceptionResponse object in to be build into JSON and sent to the caller.
     */
    private FetchRewardsExceptionResponse makeCatchExceptionOutput(Exception exception) {
    	FetchRewardsExceptionResponse responseMessage = new FetchRewardsExceptionResponse();
    	responseMessage.setExceptionMessage(exception.getMessage());
    	responseMessage.setExceptionName(exception.getClass().getName());
    	return responseMessage;
    }
}
