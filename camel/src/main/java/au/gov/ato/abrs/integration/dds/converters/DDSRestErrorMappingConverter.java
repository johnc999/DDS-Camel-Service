package au.gov.ato.abrs.integration.dds.converters;

import au.gov.ato.abrs.integration.dds.routes.exception.DDSRestErrorMapping;
import au.gov.ato.abrs.integration.routes.exception.mapping.OpenTraceId;
import org.apache.camel.Converter;
import org.apache.camel.Exchange;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@Converter(generateLoader = true)
public class DDSRestErrorMappingConverter {
    
    @Converter
    public static DDSRestErrorMapping toDDSExceptionMappedError(Throwable cause, Exchange exchange) {    
        DDSRestErrorMapping rem = new DDSRestErrorMapping(cause, OpenTraceId.getOpenTraceId(exchange));
        exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, rem.getStatusCode());
        return rem;
    }

}
