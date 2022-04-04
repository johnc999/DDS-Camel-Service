package routes.dds.generic;

import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class InitRestRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // Enable HTTP / REST
        restConfiguration()
                .component("netty-http")
                .contextPath("/")
                .host("localhost")
                .port(8080)
                // Open API
                .apiContextPath("/api-doc")
        ;
    }
}
