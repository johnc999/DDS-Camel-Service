package au.gov.ato.abrs.integration.configuration;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.camel.CamelContext;
import org.apache.camel.health.HealthCheckRegistry;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.health.DefaultHealthCheckRegistry;
import org.apache.camel.impl.health.RoutesHealthCheckRepository;
import org.apache.camel.opentelemetry.OpenTelemetryTracer;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import au.gov.ato.abrs.integration.Module;

@ApplicationScoped
public class CustomCamelContext extends DefaultCamelContext {
    @Inject
    CamelContext camelContext;

    OpenTelemetryTracer otelTracer;

    final HealthCheckRegistry checkRegistry = new DefaultHealthCheckRegistry();

    @PostConstruct
    void customize() {
        // Set Name
        setName(Module.NAME + " Camel Service");

        // Enable MDC (Mapped Diagnostic Context)
        setUseMDCLogging(true);

        // Health Check for camel routes
        checkRegistry.register(new RoutesHealthCheckRepository());
        setExtension(HealthCheckRegistry.class, checkRegistry);

        // Init OpenTelemetry
        Tracer tracer = GlobalOpenTelemetry.get().getTracer("Tracer");
        otelTracer = new OpenTelemetryTracer();
        otelTracer.setTracer(tracer);
        otelTracer.init(this);
    }

}
