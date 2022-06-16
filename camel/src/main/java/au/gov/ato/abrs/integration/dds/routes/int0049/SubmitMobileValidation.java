package au.gov.ato.abrs.integration.dds.routes.int0049;

import au.gov.ato.abrs.integration.cdi.qualifier.ConfigurationProperty;
import au.gov.ato.abrs.integration.dds.model.MobileResponseResult;

import javax.inject.Inject;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.ValidationException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

public class SubmitMobileValidation extends RouteBuilder {
	
	private static String ROUTE_ID = "DDS:Int0049:SubmitMobileValidation";
	
    String restMobileValidationEndpoint;

    @Inject 
    public SubmitMobileValidation(@ConfigurationProperty(module = "dds", property = "dds.rest.mobile.validation.endpoint") String cfgRestMobileValidationEndpoint) {
        // Convert config [%parameter%] to Camel ${header.xxx} paramter
        restMobileValidationEndpoint = cfgRestMobileValidationEndpoint.replace("[%", "${").replace("%]", "}");
    }


    @Override
    public void configure() throws Exception {

        from("direct:dds.int0049.submitMobileValidation")
            
        	.routeId(ROUTE_ID)
        	
        	.log(LoggingLevel.INFO, "SubmitMobileValidation mobile header: ${header.mobile}")
        	
            // Protocol invoker will handle errors on a protocol level
            .errorHandler(noErrorHandler()) 
            
            .choice()
            .when(simple("${header.mobile} == null || ${header.mobile} == ''"))            
                .throwException(new ValidationException(null, "'mobile' parameter is missing"))
            .end()    
            
            .setHeader("Content-Type", constant("application/json"))            
            .setHeader("Accept", constant("application/json")) 
             
            .toD(restMobileValidationEndpoint).id(ROUTE_ID + ".invokeQAS")
            
            .unmarshal().json(JsonLibrary.Jackson, MobileResponseResult.class)
            
            .bean(this, "checkMobileResponse")
            
            .log(LoggingLevel.INFO, "Mobile ${header.mobile} validated")
            
        .end();
    }
    
    MobileResponseResult checkMobileResponse(Exchange exchange) {
    	MobileResponseResult responseWrapperIn = exchange.getIn().getBody(MobileResponseResult.class);
    	if ("3".equals(responseWrapperIn.getResponse().getVerificationStatus())) {
    		return null;        // RestOnvoker will set status code 204
    	} else {
    		exchange.getIn().setHeader("dds-success-status-code", 200);
    		return responseWrapperIn;
    	}
    }
}
