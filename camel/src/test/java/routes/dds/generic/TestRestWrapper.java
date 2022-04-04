package routes.dds.generic;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import au.gov.ato.abrs.integration.Module;
import au.gov.ato.abrs.integration.configuration.ConfigurationUtility;
import routes.dds.util.Utils;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.cdi.Uri;
import org.apache.camel.support.DefaultExchange;
import org.apache.camel.test.cdi.CamelCdiRunner;

import org.junit.*;

import org.junit.rules.ExternalResource;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(CamelCdiRunner.class)
public class TestRestWrapper {
	
	@Inject
    CamelContext context;

    @Inject
    @Uri("direct:test-rest")
    ProducerTemplate restTemplate;

    @Rule
    public WireMockRule wireMock = new WireMockRule(new WireMockConfiguration().port(Utils.WIRE_MOCK_SERVER_PORT)
            .withRootDirectory(Utils.WIRE_MOCK_SERVER_ROOT));

    Properties override = new Properties();
    Map<String, Object> headers = new HashMap<>();

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
    @After
    public void resetAfterTest() throws InterruptedException {
        // Reset config properties to original
        ConfigurationUtility.loadCamelConfigurationProperties(Module.NAME, context);
        // Clear override properties
        override.clear();
    }
    

    static class TestRoute extends RouteBuilder {
        @Override
        public void configure() throws Exception {
            from("direct:test-rest")
                    .id("test-rest-route")
                    .setHeader("mobile", constant("0478000000"))
                    .setHeader("Accept", constant("application/json"))
                    .setHeader("UID", constant("123456"))
                    .setHeader("sessionID", constant("1234567"))
                    .setHeader("requestID", constant("12345678"))
                    .setHeader("dds-to-impl-route", constant("direct:submitMobileValidation"))
                    .to("direct:rest.invoke.dds")
                    .to("mock:result");
        }
    }
    
    @Test
    public void testSuccess() throws Exception {
        Map<String, Object> headers = new HashMap<>();

        Exchange exchange = new DefaultExchange(context);
        exchange.getIn().setBody(new ByteArrayInputStream(new byte[]{}));
        exchange.getIn().setHeaders(headers);

        Exchange exchangeOut = restTemplate.send("direct:test-rest", exchange);
        String body = exchangeOut.getMessage().getBody(String.class);

        assertTrue("null".equals(body));
        assertEquals(204, (int)exchangeOut.getMessage().getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class));
    }
}
