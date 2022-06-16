package au.gov.ato.abrs.integration.dds.routes.generic;

import java.io.IOException;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;

import au.gov.ato.abrs.integration.cdi.qualifier.ConfigurationProperty;
import au.gov.ato.abrs.integration.dds.routes.exception.DDSRestErrorMapping;
import au.gov.ato.abrs.integration.marshallers.JsonPojoMarshaller;

public class RestInvoker extends RouteBuilder {

    private static String ROUTE_ID_BASE = "DDS:REST:";

    @Inject
    @ConfigurationProperty(module = "dds", property = "dds.rest.retry.max")
    int maxRetries;

    @Inject
    @ConfigurationProperty(module = "dds", property = "dds.rest.retry.delay.pattern")
    String delayPattern;

    @Override
    public void configure() throws Exception {
        // Config

        // Retriable Exceptions
        // ------------------------------------
        onException(IOException.class)
            .handled(true)
                .maximumRedeliveries(maxRetries)
                .delayPattern(delayPattern)
            .end()
            // Retries have been exceeded, handle as a normal exception
            .process(e -> {e.getIn().setBody(e.getProperty(Exchange.EXCEPTION_CAUGHT));})
            .log(LoggingLevel.ERROR, "DDS implementation route ${header.dds-to-impl-route} failed after " + maxRetries + " attempts: " + simple("${exception.message}"))
        .to("direct:rest.response.dds");
        

        // Non-retryable Exceptions 
        // ------------------------------------
        onException(Throwable.class)
            .handled(true)
            .process(e -> {e.getIn().setBody(e.getProperty(Exchange.EXCEPTION_CAUGHT));})
            .log(LoggingLevel.ERROR, "DDS implementation route ${header.dds-to-impl-route} failed: " + simple("${exception.message}"))
        .to("direct:rest.response.dds");



        // REST Invoke Route
        // ------------------------------------
        from("direct:rest.invoke.dds")
            .routeId(ROUTE_ID_BASE + "Invoke")
            
            .unmarshal(new JsonPojoMarshaller())

            .log("${header.dds-to-impl-route}")
            
            .toD("${header.dds-to-impl-route}")		

            .log(LoggingLevel.TRACE, "dds implementation route ${header.dds-to-impl-route} completed")
        .to("direct:rest.response.dds");


        // REST Response
        // ------------------------------------
        from("direct:rest.response.dds")
            .routeId(ROUTE_ID_BASE + "HandleResponse")
            .choice()
                .when(simple("${bodyAs(java.lang.Throwable)} != null"))
                    // Error occured, assuming body is the exception
                    .convertBodyTo(DDSRestErrorMapping.class)    // Will also set HTTP_RESPONSE_CODE
                .endChoice()           
                .otherwise()
                    // Success, determine success code to use
                    .choice()
                        .when(header("dds-success-status-code").isNotNull())
                            .setHeader(Exchange.HTTP_RESPONSE_CODE, simple("${header.dds-success-status-code}", Integer.class))
                        .when(simple("${body} == null"))
                            .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(204))
                        .otherwise()                            
                            .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
                        .endChoice()
                    .end()
                .endChoice()
            .end()

            // Result as JSON success body or exception mapped to message
            // Sanitise headers
            .removeHeaders("*", "Camel*")
            .setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON))

            .marshal().json()                
        .end();
    }     
}
