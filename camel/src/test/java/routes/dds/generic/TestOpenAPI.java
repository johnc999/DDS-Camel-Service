package routes.dds.generic;

import au.gov.ato.abrs.integration.Module;
import au.gov.ato.abrs.integration.configuration.ConfigurationUtility;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.apache.camel.CamelContext;
import org.apache.camel.test.cdi.CamelCdiRunner;
import org.junit.*;
import org.junit.rules.ExternalResource;
import org.junit.runner.RunWith;
import routes.dds.util.Utils;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.stream.Collectors;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(CamelCdiRunner.class)
public class TestOpenAPI {
    
    @Inject
    CamelContext context;

    @Rule
    public WireMockRule wireMock = new WireMockRule(new WireMockConfiguration().port(Utils.WIRE_MOCK_SERVER_PORT));

    @ClassRule
    public static final ExternalResource resources = new ExternalResource() {
        @Override
        protected void before() throws Exception {
            System.setProperty("dds-integration-config-path", "src/test/resources/configuration");
        }

        @Override
        protected void after() {

        }
    };

    Properties override = new Properties();

    @BeforeClass
    public static void initProperties() {
        System.setProperty("dds-integration-config-path", "src/test/resources/configuration");
    }

    @After
    public void resetAfterTest() throws InterruptedException {
        // Reset config properties to original
        ConfigurationUtility.loadCamelConfigurationProperties(Module.NAME, context);
        // Clear override properties
        override.clear();
    }

    @Test
    public void testOpenApiSuccess() throws Exception {
        URL oipenAPIUrl = new URL("http://localhost:8080/api-doc");
        HttpURLConnection conn = (HttpURLConnection)oipenAPIUrl.openConnection();

        int statusCode = conn.getResponseCode();
        String openApi = new BufferedReader(new InputStreamReader(conn.getInputStream())).lines().collect(Collectors.joining("\n"));

        assertEquals(200, statusCode);
        assertTrue(openApi.contains("\"/organisations/validate/email/{email}\""));
        assertTrue(openApi.contains("\"summary\" : \"Validate email address\""));
        assertTrue(openApi.contains("\"/organisations/validate/mobile/{mobile}\""));
        assertTrue(openApi.contains("\"summary\" : \"Validate mobile\""));
    }    
    
    @Test
    public void testVerbSuccess() throws Exception {
        URL oipenAPIUrl = new URL("http://localhost:8080/api-doc");
        HttpURLConnection conn = (HttpURLConnection)oipenAPIUrl.openConnection();

        String openApi = new BufferedReader(new InputStreamReader(conn.getInputStream())).lines().collect(Collectors.joining("\n"));
        assertFalse("Route detected with operation id not set for OpenAPI spec", openApi.contains("verb"));
    }        
}
