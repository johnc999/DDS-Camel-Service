package au.gov.ato.abrs.integration.dds.routes.int0048;

import au.gov.ato.abrs.integration.cdi.qualifier.ConfigurationProperty;
import au.gov.ato.abrs.integration.dds.model.EmailResponseResult;

import javax.inject.Inject;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
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
            
// TODO: Code Review .. this looks like a debug log line            
            .log("header.email param: ${header.email}")     
            
            .choice()
            .when(simple("${header.email} == null || ${header.email} == ''"))
                .throwException(new ValidationException(null, "'email' parameter is missing"))
            .end()
            
// TODO: Code Review - Are we not at risk of leaking other headers to the backend call? .. is the email header being sent as an HTTP header?            
            .setHeader("Content-Type", constant("application/json"))
            .setHeader("Accept", constant("application/json"))
                        
// TODO: Code Review - What happens if there is an error .. like unable to connect or socket timeout .. what does the http component say about exceptions?            
            .toD(restEmailValidationEndpoint)
            
            .unmarshal().json(JsonLibrary.Jackson, EmailResponseResult.class)
           
// TODO: Code Review - What is this processor doing?    
//                     Should this not behave the same as mobile validation with response codes etc?        
            .process(new Processor() {
				public void process(Exchange exchange) throws Exception {
					
					EmailResponseResult responseBody = exchange.getIn().getBody(EmailResponseResult.class);					
					// do not process response body
					
                }
            })
            
// TODO: Code Review - Are we just returning the exact result or a cleaner result that is not ecapsulated like just returnin EmailResponse
//                     On our API we don't want nasty "{ response": "

// TODO : Code Review - Info log level indicating validation and result 

        .end();
    }
}
