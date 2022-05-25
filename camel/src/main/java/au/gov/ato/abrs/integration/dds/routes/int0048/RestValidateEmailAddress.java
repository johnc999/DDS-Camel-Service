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
	            .dataFormat("[A-Z0-9._%+-]+@[A-Z0-9.-]+")  // Hint for ingress controller to allow other characters for path param
            .endParam()  
            
	        .responseMessage()
	            .code(200).message("Successful email validation")
	        .endResponseMessage()
	        .responseMessage()
	            .code(401).message("Authentication Failed, email could not be validated")
	            .responseModel(ErrorMapping.class)
	        .endResponseMessage()
	        .responseMessage()
	            .code(403).message("Authorisation Failed, email could not be validated")
	            .responseModel(ErrorMapping.class)
	        .endResponseMessage()
	        .responseMessage()
	            .code(405).message("Method not supported, email could not be validated")
	            .responseModel(ErrorMapping.class)
	        .endResponseMessage()
	        .responseMessage()
	            .code(422).message("Unprocessable Entity, email could not be validated")
	            .responseModel(ErrorMapping.class)
            .endResponseMessage()	        
	        .responseMessage()
	            .code(500).message("Internal Server Error, email could not be validated")
	            .responseModel(ErrorMapping.class)
	        .endResponseMessage()
	        .responseMessage()
	            .code(502).message("Service unavailable, email could not be validated")
	            .responseModel(ErrorMapping.class)
	        .endResponseMessage()   
	        
        .route()
	        .setHeader("dds-to-impl-route", constant("direct:dds.int0048.submitEmailValidation"))
	        .to("direct:rest.invoke.dds");
    }
}
