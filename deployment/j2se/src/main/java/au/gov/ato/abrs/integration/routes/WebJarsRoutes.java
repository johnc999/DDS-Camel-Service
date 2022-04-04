package au.gov.ato.abrs.integration.routes;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Collections;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.io.IOUtils;

import au.gov.ato.abrs.integration.Module;

// @Author: Johnathan Ingram (johnathan.ingram@ato.gov.au)
@ApplicationScoped
public class WebJarsRoutes extends RouteBuilder {

    private static final String ROUTE_ID = "DDS:Webjars";

    private static final String WEBJAR_BASE_RESOURCE_PATH = "/META-INF/resources/webjars";
    private static final String WEBJAR_BASE_MAVEN_PATH = "/META-INF/maven/org.webjars";

    @Override
    public void configure() throws Exception {

        from("jetty:http://0.0.0.0:8081/api/v" + Module.translateToApiVersion(Module.VERSION) + "/" + Module.NAME.toLowerCase() +  "/webjars?matchOnUriPrefix=true")
                .routeId(ROUTE_ID)
                .process(exchange -> {
                            Message in = exchange.getIn();

                            String relativePath = in.getHeader(Exchange.HTTP_PATH, String.class);
                            File relativePathFile = new File(relativePath);
                            String requestPath = in.getHeader("CamelServletContextPath", String.class);

                            // Check if resource exists as requested
                            String webJarPath = String.format("%s/%s", WEBJAR_BASE_RESOURCE_PATH, relativePath).replaceAll("//", "/");
                            InputStream webJarResourceStream = this.getClass().getResourceAsStream(webJarPath);

                            if (null == webJarResourceStream) {
                                // Lookup using version specified in pom
                                String version = getWebJarVersion(relativePathFile.getParentFile().getName());
                                webJarPath = String.format("%s/%s/%s/%s", WEBJAR_BASE_RESOURCE_PATH, relativePathFile.getParentFile().getName(), version, relativePathFile.getName()).replaceAll("//", "/");
                                webJarResourceStream = this.getClass().getResourceAsStream(webJarPath);
                            }

                            if (null == webJarResourceStream) {
                                in.setHeader(Exchange.HTTP_RESPONSE_CODE, "404");
                            } else {
                                try {
                                    in.setBody(IOUtils.toByteArray(webJarResourceStream));
                                } finally {
                                    webJarResourceStream.close();
                                }
                            }
                        }
                );

    }

    private static final Map<String, String> webJarVersion = Collections.synchronizedMap(new HashMap<>());

    private String getWebJarVersion(String webJarName) {
        if (null == webJarVersion.get(webJarName)) {
            String webJarPomPropertiesPath = String.format("%s/%s/pom.properties", WEBJAR_BASE_MAVEN_PATH, webJarName).replaceAll("//", "/");
            try (InputStream webJarPomResourceStream = this.getClass().getResourceAsStream(webJarPomPropertiesPath);) {
                if (null != webJarPomResourceStream) {
                    String properties = IOUtils.toString(webJarPomResourceStream, StandardCharsets.UTF_8);
                    String version =  Arrays.asList(properties.split("\n")).stream()
                            .filter(e -> e.startsWith("version="))
                            .map(e -> e.split("version=")[1])
                            .collect(Collectors.joining(""));

                    webJarVersion.put(webJarName, version);
                }
            } catch(Exception ex) {
            }
        }

        return webJarVersion.get(webJarName);
    }
}
