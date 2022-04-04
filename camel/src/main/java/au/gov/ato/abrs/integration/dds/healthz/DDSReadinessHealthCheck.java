package au.gov.ato.abrs.integration.dds.healthz;

import java.util.Map;
import java.util.UUID;

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
import au.gov.ato.abrs.integration.dds.model.EmailResponseResult;

@ApplicationScoped
@HealthCheck("dds-readiness-check")
public class DDSReadinessHealthCheck extends AbstractHealthCheck {

    public static final long CHECK_INTERVAL_MS = 120_000l;
    
    public static final String VALID_EMAIL = "joe@test.com";

    private static Logger log = LoggerFactory.getLogger(DDSReadinessHealthCheck.class);
    
    @Inject
    @Uri("direct:submitEmailValidation")
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
        builder.detail("Check DDS readiness using submit email validation");

        // Perform a valid email search against DDS to make sure the service is available
        try {
        	// need to check if these can be random numbers
            Exchange exchange = ExchangeBuilder.anExchange(getCamelContext())
                    .withBody(null)
                     // TODO: Code Review - UID, sessionID, requestID not mentioned in documentation .. check with Rob, but I don't think this is required                    
                    .withHeader("email", VALID_EMAIL)
                    .withHeader("UID", UUID.randomUUID().toString())
                    .withHeader("sessionID", UUID.randomUUID().toString())
                    .withHeader("requestID", UUID.randomUUID().toString())
                    .build();

            Exchange exchangeOut = template.send(exchange);
            Object res = exchangeOut.getMessage().getBody();
            if (res instanceof Exception)
                throw (Exception)res;
            
            EmailResponseResult response = exchangeOut.getMessage().getBody(EmailResponseResult.class);
            
            if (("200".equals(exchangeOut.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE).toString())) &&
               ("verified".equalsIgnoreCase(response.getResponse().getVerificationLevelDescription()))) {
                  log.info("DDS submit email validation readiness health check succeeded for valid email");
                  builder.up();
            } else {
                throw new Exception("DDS submit email validation readiness healtch check failed for valid email");            
            }
        } catch (Throwable ex) {
            log.warn("DDS submit email validation readiness health check FAILED: " + ex.getMessage());
            builder.error(ex);
            builder.message("DDS submit email validation readiness health check FAILED with " + ex.getMessage());
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