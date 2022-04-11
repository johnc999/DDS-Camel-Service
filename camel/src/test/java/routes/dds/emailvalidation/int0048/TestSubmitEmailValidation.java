// TODO: Code Review - Maybe routes.dds.int0048 to match other services
package routes.dds.emailvalidation.int0048;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.inject.Inject;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.cdi.Uri;
import org.apache.camel.support.DefaultExchange;
import org.apache.camel.test.cdi.CamelCdiRunner;
import org.junit.After;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.junit.runner.RunWith;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

import au.gov.ato.abrs.integration.Module;
import au.gov.ato.abrs.integration.configuration.ConfigurationUtility;
import au.gov.ato.abrs.integration.dds.model.EmailResponseResult;
import routes.dds.util.Utils;

// TODO: Code Review - Any reason why we have bambora soap UI references under resources?

@RunWith(CamelCdiRunner.class)
public class TestSubmitEmailValidation {

    private static String ROUTE_ENDPOINT = "direct:dds.int0048.submitEmailValidation";

    @Inject
    CamelContext context;

    @Inject
    @Uri("direct:dds.int0048.submitEmailValidation")
    ProducerTemplate template;

// TODO: Code Review - Should be testing business route, not REST route .. much easier testing and asserts work on Java ojects, not JSON
//                     Only need 1 test to test wrapper .. TestRestWrapper    
    @Inject
    @Uri("direct:rest.invoke.dds")
    ProducerTemplate restTemplate;

    @Rule
    public WireMockRule wireMock = new WireMockRule(new WireMockConfiguration().port(Utils.WIRE_MOCK_SERVER_PORT)
            .withRootDirectory(Utils.WIRE_MOCK_SERVER_ROOT));

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

    private Map<String, Object> createHeaders(boolean restCall, String email) {
        Map<String, Object> headers = new HashMap<>();
        if (restCall) {
        	headers.put("dds-to-impl-route", ROUTE_ENDPOINT);
        }
        if (email != null) {
        	headers.put("email", email);
        }
        // Remove for now: headers.put("UID", "123456"); headers.put("sessionID", "1234567"); headers.put("requestID", "12345678");
        return headers;
    }    
    
    @Test
    public void testRestValidEmailValidation() throws Exception {
    	
    	String emailAddress = "joe@test.com";

        Exchange exchange = new DefaultExchange(context);
        exchange.getIn().setBody(new ByteArrayInputStream(new byte[]{}));
        exchange.getIn().setHeaders(createHeaders(true, emailAddress));

        Exchange exchangeOut = restTemplate.send(exchange);
        EmailResponseResult response = exchangeOut.getIn().getBody(EmailResponseResult.class);
        assertTrue("verified".equalsIgnoreCase(response.getResponse().getVerificationLevelDescription()));
        assertTrue("verified".equalsIgnoreCase(response.getResponse().getVerificationMessage()));
        assertTrue("200".equals(exchangeOut.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE).toString()));
    }

    @Test
    public void testValidEmailValidation() throws Exception {
    	
    	String emailAddress = "joe200@test.com";

        Exchange exchange = new DefaultExchange(context);
        exchange.getIn().setBody(new ByteArrayInputStream(new byte[]{}));
        exchange.getIn().setHeaders(createHeaders(false, emailAddress));

        Exchange exchangeOut = template.send(exchange);
        EmailResponseResult response = exchangeOut.getIn().getBody(EmailResponseResult.class);
        assertTrue("verified".equalsIgnoreCase(response.getResponse().getVerificationLevelDescription()));
        assertTrue("verified".equalsIgnoreCase(response.getResponse().getVerificationMessage()));
        assertTrue("200".equals(exchangeOut.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE).toString()));
    }    
    
    @Test
    public void testBadRequestEmailMapping() throws Exception {
    	
        String emailAddress = "joe400@test.com";
    	
        Exchange exchange = new DefaultExchange(context);
        exchange.getIn().setBody(new ByteArrayInputStream(new byte[]{}));
        exchange.getIn().setHeaders(createHeaders(true, emailAddress));
        
        Exchange exchangeOut = restTemplate.send(exchange);               
        
        String json = exchangeOut.getMessage().getBody(String.class);
        assertTrue(json != null);
        assertTrue(json.contains("DDS Operation Failed: HTTP operation failed"));
        assertTrue(json.contains("with statusCode: 400, Bad Request\",\"code\":\"error\",\"severity\":\"error\""));
    }  
    
    @Test
    public void testUnknownHostEmailValidation() throws Exception {
    	
    	String emailAddress = "joe502@test.com";

        Exchange exchange = new DefaultExchange(context);
        exchange.getIn().setBody(new ByteArrayInputStream(new byte[]{}));
        exchange.getIn().setHeaders(createHeaders(true, emailAddress));

        Exchange exchangeOut = restTemplate.send(exchange);        
        String json = exchangeOut.getMessage().getBody(String.class);
        assertTrue(json != null);
        assertTrue(json.contains("DDS Operation Failed: HTTP operation failed"));
        assertTrue(json.contains("with statusCode: 502, Bad Gateway\",\"code\":\"error\",\"severity\":\"error\""));
    }  
    
    Properties override = new Properties();

    @After
    public void resetAfterTest() throws InterruptedException {
        // Reset config properties to original
        ConfigurationUtility.loadCamelConfigurationProperties(Module.NAME, context);
        // Clear override properties
        override.clear();
    }
}
