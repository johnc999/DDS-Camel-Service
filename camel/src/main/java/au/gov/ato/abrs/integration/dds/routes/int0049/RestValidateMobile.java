package au.gov.ato.abrs.integration.dds.routes.int0049;

import au.gov.ato.abrs.integration.dds.model.MobileResponseResult;
import au.gov.ato.abrs.integration.routes.exception.mapping.ErrorMapping;

import javax.ws.rs.core.MediaType;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestParamType;

public class RestValidateMobile extends RouteBuilder {
    
    @Override
    public void configure() throws Exception {
    	
        rest("/organisations")
			// TODO: Call id("") twice. Can remove this.
            .id("rest.dds.int0049.validatemobile")
            .get("/validate/mobile/{mobile}")
            .bindingMode(RestBindingMode.off)            
            .description("Validate mobile")
            .id("validate-mobile")
            
            .produces(MediaType.APPLICATION_JSON)
            .outType(MobileResponseResult.class)
            
            .param()
                .name("mobile").type(RestParamType.path).dataType("string").required(true)
                .description("The mobile phone number to validate")
                .dataFormat("[\\s0-9+-\\.\\(\\)]+")  // Hint for ingress controller to allow other characters for path param
            .endParam()
            
            .responseMessage()
	            .code(200).message("Failed mobile validation")
	        .endResponseMessage()
	        .responseMessage()
	            .code(204).message("Successful mobile validation")
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
	        .responseMessage()
	            .code(422).message("Unprocessable Entity, mobile number could not be validated")
	            .responseModel(ErrorMapping.class)
	        .endResponseMessage()	        
	        .responseMessage()
	            .code(500).message("Internal Server Error, mobile number could not be validated")
	            .responseModel(ErrorMapping.class)
	        .endResponseMessage()
	        .responseMessage()
	            .code(502).message("Service unavailable, mobile number could not be validated")
	            .responseModel(ErrorMapping.class)
	        .endResponseMessage()  
                    
        .route()
	        .setHeader("dds-to-impl-route", constant("direct:dds.int0049.submitMobileValidation"))
	        .to("direct:rest.invoke.dds");
    }
}
