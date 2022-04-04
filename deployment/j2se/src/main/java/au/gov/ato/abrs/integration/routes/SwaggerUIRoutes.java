package au.gov.ato.abrs.integration.routes;

import javax.enterprise.context.ApplicationScoped;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

import au.gov.ato.abrs.integration.Module;

// @Author: Johnathan Ingram (johnathan.ingram@ato.gov.au)
@ApplicationScoped
public class SwaggerUIRoutes extends RouteBuilder  {

    private static String ROUTE_ID_BASE = "DDS:SwaggerU";

    @Override
    public void configure() throws Exception {
        // Swagger UI redirect
        from("jetty:http://0.0.0.0:8081/api/v" + Module.translateToApiVersion(Module.VERSION) + "/" + Module.NAME.toLowerCase() + "/swagger-ui")
                .routeId(ROUTE_ID_BASE)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant("301"))
                .setHeader("Location", constant("webjars/swagger-ui/index.html?url=/api/v" + Module.translateToApiVersion(Module.VERSION) + "/" + Module.NAME.toLowerCase() + "/api-doc&validatorUrl="))
        ;

        // Swagger UI api-doc bridget 8081 <-> 8080
        from("jetty:http://0.0.0.0:8081/api/v" + Module.translateToApiVersion(Module.VERSION) + "/" + Module.NAME.toLowerCase() + "/api-doc")
                .routeId(ROUTE_ID_BASE + ":ApiDocProxy")
                // .to("http://localhost:8080/camel/api-doc?bridgeEndpoint=true")
                .to("http://localhost:8080/api/v" + Module.translateToApiVersion(Module.VERSION) + "/" + Module.NAME.toLowerCase() + "/api-doc?bridgeEndpoint=true")
        ;
    }
}
