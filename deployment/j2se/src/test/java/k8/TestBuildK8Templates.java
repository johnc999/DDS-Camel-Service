package k8;

import au.gov.ato.abrs.integration.CamelMain;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import au.gov.ato.abrs.integration.Module;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.parameters.Parameter;

// @Author: Johnathan Ingram (johnathan.ingram@ato.gov.au)
public class TestBuildK8Templates {

    @BeforeClass
    public static void startup() throws Exception {
        // Start the Camel J2SE
        try {
            CamelMain.main(new String[] {});
        } catch (Throwable ex) {
            fail("Unable to startup service");
        }
    }

    @Test
    public void buildK8Templates() throws Exception {
        try {
            // Obtain Open API Spec
            URL oipenAPIUrl = new URL("http://localhost:8080/api/v" + Module.translateToApiVersion(Module.VERSION) + "/"
                    + Module.NAME.toLowerCase() + "/api-doc");
            HttpURLConnection conn = (HttpURLConnection) oipenAPIUrl.openConnection();

            int statusCode = conn.getResponseCode();
            assertEquals(200, statusCode);
            String openApi = new BufferedReader(new InputStreamReader(conn.getInputStream())).lines()
                    .collect(Collectors.joining("\n"));
            System.out.println(openApi);

            // Parse spec to build template
            SwaggerParseResult swagger = new OpenAPIV3Parser().readContents(openApi);
            OpenAPI openAPI = swagger.getOpenAPI();

            Map<String, String> templateVars = new HashMap<>();
            templateVars.put("service.name", Module.NAME.toLowerCase());
            templateVars.put("service.version", Module.translateToApiVersion(Module.VERSION));
            templateVars.put("parent.artifact.name", Module.PARENT_ARTIFACT_ID);
            templateVars.put("artifact.name", Module.ARTIFACT_ID);
            templateVars.put("artifact.version", Module.VERSION);
            templateVars.put("paths", "");

            // Build API paths with path params {xxx}, that will match regex using (\w)+ and
            // $ ends with for full match
            openAPI.getPaths().forEach((endpoint, item) -> {            
                String paths = templateVars.get("paths");

                Operation operation = null != item.getGet() ? item.getGet()
                    : null != item.getPut() ? item.getPut()
                        : null != item.getPatch() ? item.getPatch()
                            : null != item.getPost() ? item.getPost()
                                : null != item.getDelete() ? item.getDelete()
                                    : null != item.getHead() ? item.getHead()
                                        : null;

                List<Parameter> pathParameters = null == operation.getParameters()
                    ? new ArrayList<>()
                    : operation.getParameters().stream()
                        .filter(e -> e.getIn().equalsIgnoreCase("path"))
                        .collect(Collectors.toList());

                // Note: Replace \{\w+\} with [\w_%-;,'~*#!=@:&\-\"\.\[\]\(\)\+]+
                //       Most valid chars for path component in URI
                String path = endpoint;
                for (Parameter pathParam : pathParameters) {
                    if (null != pathParam.getSchema() && null != pathParam.getSchema().getFormat()) {
                        // Replace with actual regex in format
                        path = path.replace("{" + pathParam.getName() + "}", pathParam.getSchema().getFormat());
                    } else {
                        // Default replacing with (\w)+
                        path = path.replace("{" + pathParam.getName() + "}", "(\\w)+");
                    }
                }
                path = path + "$";

                templateVars.put("endpoint", endpoint);
                templateVars.put("path", path);

                String pathSnippet = substituteTempalate("k8/path-snippet.template", templateVars);
                paths += pathSnippet;
                templateVars.put("paths", paths);

                templateVars.remove("endpoint");
                templateVars.remove("path");
            });

            // Output to the k8 dir under deployment
            String content;

            content = substituteTempalate("k8/camel-service-deployment.template", templateVars);
            writeTemplate(content, Module.NAME.toLowerCase() + "-v" + Module.translateToApiVersion(Module.VERSION)
                    + "-camel-service-deployment.yaml");

            content = substituteTempalate("k8/service.template", templateVars);
            writeTemplate(content,
                    Module.NAME.toLowerCase() + "-v" + Module.translateToApiVersion(Module.VERSION) + "-service.yaml");

            content = substituteTempalate("k8/service-ingress.template", templateVars);
            writeTemplate(content, Module.NAME.toLowerCase() + "-v" + Module.translateToApiVersion(Module.VERSION)
                    + "-service-ingress.yaml");

            // Output the OpenAPI Spec File
            writeOpenAPISpecFile(openApi);
        } catch (Throwable ex) {
            ex.printStackTrace(System.err);
            throw ex;
        }
    }

    private void writeOpenAPISpecFile(String content) throws IOException {
        File specFile = new File("../openapi/",
                Module.NAME.toLowerCase() + "-v" + Module.translateToApiVersion(Module.VERSION) + "-" + "openapi.json");
        if (specFile.exists())
            specFile.delete();

        Files.write(specFile.toPath(), content.getBytes(StandardCharsets.UTF_8));
    }

    private void writeTemplate(String content, String fileName) throws IOException {
        File k8Dir = new File("../k8");
        if (!k8Dir.exists()) {
            Files.createDirectories(k8Dir.toPath());
        }

        File deploymentTeamplate = new File(k8Dir, fileName);
        if (deploymentTeamplate.exists())
            deploymentTeamplate.delete();

        Files.write(deploymentTeamplate.toPath(), content.getBytes(StandardCharsets.UTF_8));
    }

    private String substituteTempalate(String template, Map<String, String> templateVars) {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(template);
        String content = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));

        content = substituteString(content, templateVars);
        return content;
    }

    private String substituteString(String content, Map<String, String> templateVars) {
        for (String name : templateVars.keySet()) {
            content = content.replace("{" + name + "}", templateVars.get(name));
        }
        return content;
    }
}
