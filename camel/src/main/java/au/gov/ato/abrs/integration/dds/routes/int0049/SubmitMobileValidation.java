package au.gov.ato.abrs.integration.dds.routes.int0049;

import au.gov.ato.abrs.integration.cdi.qualifier.ConfigurationProperty;
import au.gov.ato.abrs.integration.dds.model.MobileResponseResult;

import javax.inject.Inject;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.ValidationException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

public class SubmitMobileValidation extends RouteBuilder {
	
	private static String ROUTE_ID = "DDS:Int0049:SubmitMobileValidation";
	
    String restMobileValidationEndpoint;

    @Inject 
    public SubmitMobileValidation(@ConfigurationProperty(module = "dds", property = "dds.rest.mobile.validation.endpoint")String cfgRestMobileValidationEndpoint) {
        // Convert config [%parameter%] to Camel ${header.xxx} paramter
        restMobileValidationEndpoint = cfgRestMobileValidationEndpoint.replace("[%", "${").replace("%]", "}");
    }


    @Override
    public void configure() throws Exception {

        from("direct:dds.int0049.submitMobileValidation")
            
        	.routeId(ROUTE_ID)
        	
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
                      
            .process(new Processor() {
				public void process(Exchange exchange) throws Exception {    
					
					MobileResponseResult responseWrapperIn = exchange.getIn().getBody(MobileResponseResult.class);
					
					if ("3".equals(responseWrapperIn.getResponse().getVerificationStatus())) {
						exchange.getIn().setBody(null); 	// generic rest invoker will return http response status: 204
					} else {
						exchange.getIn().setHeader("dds-success-status-code", 200);
					}
					
                }
            })
            
            .log(LoggingLevel.INFO, "Completed for: ${header.mobile}")
            
        .end();
    }
}
