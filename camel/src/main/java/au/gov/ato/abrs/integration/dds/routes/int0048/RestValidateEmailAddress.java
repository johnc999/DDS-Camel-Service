package au.gov.ato.abrs.integration.dds.routes.int0048;

import au.gov.ato.abrs.integration.dds.model.EmailResponseResult;
import au.gov.ato.abrs.integration.routes.exception.mapping.ErrorMapping;

import javax.ws.rs.core.MediaType;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestParamType;

public class RestValidateEmailAddress extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        rest("/organisations")
            .id("rest.dds.int0048.validateemail")
            .get("/validate/email/{email}")
            .bindingMode(RestBindingMode.off)            
            .description("Validate email address")
            .id("validate-email-address")
            
            .produces(MediaType.APPLICATION_JSON)
            .outType(EmailResponseResult.class)
            
            .param()
	            .name("email").type(RestParamType.path).dataType("string").required(true)
	            .description("The email address to validate")
            .endParam()
// TODO: Code Review - mobile number mentioned on email validation endpoint            
	        .responseMessage()
	            .code(200).message("Successful email validation")
	        .endResponseMessage()
	        .responseMessage()
	            .code(400).message("Bad Request, mobile number could not be validated")
	            .responseModel(ErrorMapping.class)
	        .endResponseMessage()
	        .responseMessage()
	            .code(401).message("Authentication Failed, mobile number could not be validated")
	            .responseModel(ErrorMapping.class)
	        .endResponseMessage()
	        .responseMessage()
	            .code(403).message("Authorisation Failed, mobile number could not be validated")
	            .responseModel(ErrorMapping.class)
	        .endResponseMessage()
	        .responseMessage()
	            .code(405).message("Method not supported, mobile number could not be validated")
	            .responseModel(ErrorMapping.class)
	        .endResponseMessage()
// TODO: Code Review - Not sure we need these on our API
			.responseMessage()
	            .code(406).message("Request Not Acceptable, mobile number could not be validated")
	        .endResponseMessage()
// TODO: Code Review - Not sure we need these on our API
	        .responseMessage()
	            .code(415).message("Content Type Not Supported, mobile number could not be validated")
	            .responseModel(ErrorMapping.class)
	        .endResponseMessage()
	        .responseMessage()
	            .code(500).message("Internal Server Error, mobile number could not be validated")
	            .responseModel(ErrorMapping.class)
	        .endResponseMessage()
// TODO: Code Review - Not sure we need these on our API
			.responseMessage()
	            .code(501).message("Not implemented error, mobile number could not be validated")
	            .responseModel(ErrorMapping.class)
	        .endResponseMessage()
// TODO: Code Review - Our error, not text book error
		.responseMessage()
	            .code(502).message("Bad Gateway, mobile number could not be validated")
	            .responseModel(ErrorMapping.class)
	        .endResponseMessage()     
	        .responseMessage()
	            .code(503).message("Request Could Not Be Processed, mobile number could not be validated")
	            .responseModel(ErrorMapping.class)
	        .endResponseMessage()  	 
        .route()
	        .setHeader("dds-to-impl-route", constant("direct:dds.int0048.submitEmailValidation"))
	        .to("direct:rest.invoke.dds");
    }
}
