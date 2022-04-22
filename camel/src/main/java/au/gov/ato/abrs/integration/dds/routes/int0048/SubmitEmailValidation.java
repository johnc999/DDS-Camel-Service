package au.gov.ato.abrs.integration.dds.routes.int0048;

import au.gov.ato.abrs.integration.cdi.qualifier.ConfigurationProperty;
import au.gov.ato.abrs.integration.dds.model.EmailResponseResult;

import javax.inject.Inject;

import org.apache.camel.LoggingLevel;
import org.apache.camel.ValidationException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

public class SubmitEmailValidation extends RouteBuilder {
	
	private static String ROUTE_ID = "DDS:Int0048:SubmitEmailValidation";
	
    @Inject
    @ConfigurationProperty(module = "dds", property = "dds.rest.email.validation.endpoint")
    String restEmailValidationEndpoint;
	
    @Override
    public void configure() throws Exception {

        from("direct:dds.int0048.submitEmailValidation")
            
        	.routeId(ROUTE_ID)
        	
            // Protocol invoker will handle errors on a protocol level
            .errorHandler(noErrorHandler())  
            
            .choice()
            .when(simple("${header.email} == null || ${header.email} == ''"))
                .throwException(new ValidationException(null, "'email' parameter is missing"))
            .end()
                      
            .setHeader("Content-Type", constant("application/json"))
            .setHeader("Accept", constant("application/json"))
                                   
            .toD(restEmailValidationEndpoint).id(ROUTE_ID + ".invokeQAS")
            
            .unmarshal().json(JsonLibrary.Jackson, EmailResponseResult.class)
            
            .log(LoggingLevel.INFO, "Completed for: ${header.email}")

        .end();
    }
}
