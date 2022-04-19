package routes.dds.generic;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import au.gov.ato.abrs.integration.Module;
import au.gov.ato.abrs.integration.configuration.ConfigurationUtility;
import au.gov.ato.abrs.integration.dds.model.EmailResponseResult;
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
	
	private static String ROUTE_EMAIL_ENDPOINT = "direct:dds.int0048.submitEmailValidation";
	
	private static String ROUTE_MOBILE_ENDPOINT = "direct:dds.int0049.submitMobileValidation";
	
	@Inject
    CamelContext context;
    
    @Inject
    @Uri("direct:rest.invoke.dds")
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

    // TODO: Code Review - Need  -ve test(s) to ensure testing standard errors are being mapped correctly
    
    private Map<String, Object> createEmailHeaders(boolean restCall, String email) {
        Map<String, Object> headers = new HashMap<>();
        if (restCall) {
        	headers.put("dds-to-impl-route", ROUTE_EMAIL_ENDPOINT);
        }
        if (email != null) {
        	headers.put("email", email);
        }
        // Remove for now: headers.put("UID", "123456"); headers.put("sessionID", "1234567"); headers.put("requestID", "12345678");
        return headers;
    }  
    
    private Map<String, Object> createMobileHeaders(boolean restCall, String mobile) {
        Map<String, Object> headers = new HashMap<>();
        if (restCall) {
        	headers.put("dds-to-impl-route", ROUTE_MOBILE_ENDPOINT);
        }
        if (mobile != null) {
        	headers.put("mobile", mobile);
        }
        // Remove for now: headers.put("UID", "123456"); headers.put("sessionID", "1234567"); headers.put("requestID", "12345678");
        return headers;
    }
    
    @Test
    public void testRestValidEmailValidation() throws Exception {
    	
    	String emailAddress = "joe@test.com";

        Exchange exchange = new DefaultExchange(context);
        exchange.getIn().setBody(new ByteArrayInputStream(new byte[]{}));
        exchange.getIn().setHeaders(createEmailHeaders(true, emailAddress));

        Exchange exchangeOut = restTemplate.send(exchange);
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
        exchange.getIn().setHeaders(createEmailHeaders(true, emailAddress));
        
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
        exchange.getIn().setHeaders(createEmailHeaders(true, emailAddress));

        Exchange exchangeOut = restTemplate.send(exchange);        
        String json = exchangeOut.getMessage().getBody(String.class);
        assertTrue(json != null);
        assertTrue(json.contains("DDS Operation Failed: HTTP operation failed"));
        assertTrue(json.contains("with statusCode: 502, Bad Gateway\",\"code\":\"error\",\"severity\":\"error\""));
    }  
    
    @Test
    public void testRestInvalidFormatMobileValidation() throws Exception {
    	
    	String mobile = "0411-777-999";
    	
        Exchange exchange = new DefaultExchange(context);
        exchange.getIn().setBody(new ByteArrayInputStream(new byte[]{}));
        exchange.getIn().setHeaders(createMobileHeaders(true, mobile));
        
        Exchange exchangeOut = restTemplate.send(exchange);
        String json = exchangeOut.getMessage().getBody(String.class);

        assertTrue(json != null);
        assertTrue("{\"response\":{\"verificationLevelDescription\":\"Invalid format\",\"verificationStatus\":\"0\"}}".equals(json));
    }

    @Test
    public void testRestValidFormatMobileValidation() throws Exception {
    	
    	String mobile = "0478000000";
    	
        Exchange exchange = new DefaultExchange(context);
        exchange.getIn().setBody(new ByteArrayInputStream(new byte[]{}));
        exchange.getIn().setHeaders(createMobileHeaders(true, mobile));
        
        Exchange exchangeOut = restTemplate.send(exchange);
        String result = exchangeOut.getMessage().getBody(String.class);

        assertTrue("null".equals(result));
        assertTrue("204".equals(exchangeOut.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE).toString()));
    }
    
    @Test
    public void testBadRequestMobileMapping() throws Exception {
    	
        String mobile = "0478400400";
    	
        Exchange exchange = new DefaultExchange(context);
        exchange.getIn().setBody(new ByteArrayInputStream(new byte[]{}));
        exchange.getIn().setHeaders(createMobileHeaders(true, mobile));
        
        Exchange exchangeOut = restTemplate.send(exchange);               
        
        String json = exchangeOut.getMessage().getBody(String.class);
        assertTrue(json != null);
        assertTrue(json.contains("DDS Operation Failed: HTTP operation failed"));
        assertTrue(json.contains("with statusCode: 400, Bad Request\",\"code\":\"error\",\"severity\":\"error\""));
    }  
    
    @Test
    public void testUnknownHostMobileValidation() throws Exception {
    	
    	String mobile = "0478666776";

        Exchange exchange = new DefaultExchange(context);
        exchange.getIn().setBody(new ByteArrayInputStream(new byte[]{}));
        exchange.getIn().setHeaders(createMobileHeaders(true, mobile));

        Exchange exchangeOut = restTemplate.send(exchange);        
        // DDSRestErrorMapping errorMapping = exchangeOut.getMessage().getBody(DDSRestErrorMapping.class);
        String json = exchangeOut.getMessage().getBody(String.class);
        assertTrue(json != null);
        assertTrue(json.contains("DDS Operation Failed: HTTP operation failed"));
        assertTrue(json.contains("with statusCode: 502, Bad Gateway\",\"code\":\"error\",\"severity\":\"error\""));
    }      
}
