package au.gov.ato.abrs.integration.dds.healthz;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.camel.cdi.Uri;
import org.apache.camel.health.HealthCheckResultBuilder;
import org.apache.camel.impl.health.AbstractHealthCheck;
import org.apache.camel.spi.annotations.HealthCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.gov.ato.abrs.integration.Module;
import au.gov.ato.abrs.integration.dds.model.MobileResponseResult;

@ApplicationScoped
@HealthCheck("dds-readiness-check")
public class DDSReadinessHealthCheck extends AbstractHealthCheck {

    public static final long CHECK_INTERVAL_MS = 120_000l;
    
    public static final String VALID_MOBILE = "0478111222";

    private static Logger log = LoggerFactory.getLogger(DDSReadinessHealthCheck.class);
    
    @Inject
    @Uri("direct:dds.int0049.submitMobileValidation")
    ProducerTemplate template;

    @PostConstruct
    public void init() {
    }

    public DDSReadinessHealthCheck() {
        super(Module.NAME, "dds-readiness-check");
        getConfiguration().setInterval(CHECK_INTERVAL_MS);
    }

    @Override
    protected void doCall(HealthCheckResultBuilder builder, Map<String, Object> options) {
        builder.unknown();
        builder.detail("Check DDS readiness using submit mobile validation");

        // Perform a valid mobile search against DDS to make sure the service is available
        try {
        	// need to check if these can be random numbers
            Exchange exchange = ExchangeBuilder.anExchange(getCamelContext())
                    .withBody(null)
                    .withHeader("mobile", VALID_MOBILE)
                    // Remove for now: .withHeader("UID", UUID.randomUUID().toString()) .withHeader("sessionID", UUID.randomUUID().toString()) .withHeader("requestID", UUID.randomUUID().toString())
                    .build();

            log.info("Exchange was built");
            Exchange exchangeOut = template.send(exchange);
            Object res = exchangeOut.getMessage().getBody();
            if (res instanceof Exception)
                throw (Exception)res;
            
            log.info("Returned response from Exchange");            
            MobileResponseResult response = exchangeOut.getMessage().getBody(MobileResponseResult.class);
            log.info("Response Code: " + exchangeOut.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE).toString());
            
            if (("200".equals(exchangeOut.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE).toString())) &&
               ("verified".equalsIgnoreCase(response.getResponse().getVerificationLevelDescription()))) {
                  log.info("DDS submit mobile validation readiness health check succeeded");
                  builder.up();
            } else {
            	log.info("DDS submit mobile validation readiness health check failed");
                throw new Exception("DDS submit mobile validation readiness health check failed");            
            }
        } catch (Throwable ex) {
        	log.warn("DDS submit mobile validation readiness health check FAILED: " + ex.getMessage());
        	StackTraceElement[] stktrace = ex.getStackTrace();
        	for (int i = 0; i < stktrace.length; i++) {
        		log.warn(stktrace[i].toString());
        	}        	            
            builder.error(ex);
            builder.message("DDS mobile mobile validation readiness health check FAILED with " + ex.getMessage());
            builder.down();
        }
    }

    @Override
    public boolean isReadiness() {
        return true; // Enable readiness checks
    }

    @Override
    public boolean isLiveness() {
        return false; // Disable liveness checks
    }
}