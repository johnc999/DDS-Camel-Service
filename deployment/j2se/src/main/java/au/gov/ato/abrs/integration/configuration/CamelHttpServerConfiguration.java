package au.gov.ato.abrs.integration.configuration;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;

import au.gov.ato.abrs.integration.Module;

@ApplicationScoped
public class CamelHttpServerConfiguration extends RouteBuilder {
    @Inject
    CamelContext camelContext;

    @Override
    public void configure() throws Exception {
        restConfiguration()
                .component("jetty")
                .host("0.0.0.0")  // Listening on local host will not support K8 ingress controller
                .port("8080")
                .contextPath("/api/v" + Module.translateToApiVersion(Module.VERSION))
                // .contextPath("/")
                .scheme("http")
                .bindingMode(RestBindingMode.json)
                // Open API
                .apiContextPath("/" + Module.NAME.toLowerCase() + "/api-doc")
                .apiProperty("api.title", Module.NAME + " Service")
                .apiProperty("api.version", Module.VERSION);
    }
}