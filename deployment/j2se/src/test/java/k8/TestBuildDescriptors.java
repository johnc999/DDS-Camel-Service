package k8;

import static org.junit.Assert.fail;


import au.gov.ato.abrs.integration.CamelMain;
import au.gov.ato.abrs.integration.Module;
import au.gov.ato.abrs.integration.test.K8Template;
import au.gov.ato.abrs.integration.test.OpenApiSpec;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.BeforeClass;
import org.junit.Test;

// @Author: Johnathan Ingram (johnathan.ingram@ato.gov.au)
public class TestBuildDescriptors {

    @BeforeClass
    public static void startup() throws Exception {
        // Start the Camel J2SE
        try {
        	System.setProperty("DDS_URL", "http://localhost:21000");
            CamelMain.main(new String[] {});
        } catch (Throwable ex) {
            fail("Unable to startup service");
        }
    }

    @Test
    public void buildDescriptors() throws Throwable {
        Module module = new Module();

        // Write the OpenAPI spec file
        OpenAPI openApi = OpenApiSpec.parseOpenApi(module);
        OpenApiSpec.writeOpenAPISpecFile("../openapi/");

        // Write the K8 Descriptors
        K8Template.generateK8Descriptors(module, openApi, "../k8");
    }
}
