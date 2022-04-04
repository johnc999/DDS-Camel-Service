package routes.dds.mobilevalidation.int0049;

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
import au.gov.ato.abrs.integration.dds.model.MobileResponseResult;
import routes.dds.util.Utils;

@RunWith(CamelCdiRunner.class)
public class TestSubmitMobileValidation {

    private static String ROUTE_ENDPOINT = "direct:submitMobileValidation";

    @Inject
    CamelContext context;

    @Inject
    @Uri("direct:submitMobileValidation")
    ProducerTemplate template;

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

    private Map<String, Object> createHeaders(boolean restCall, String mobile) {
        Map<String, Object> headers = new HashMap<>();
        if (restCall) {
        	headers.put("dds-to-impl-route", ROUTE_ENDPOINT);
        }
        if (mobile != null) {
        	headers.put("mobile", mobile);
        }
        // Check if required:
        headers.put("UID", "123456");
        headers.put("sessionID", "1234567");
        headers.put("requestID", "12345678");
        return headers;
    }

    @Test
    public void testRestInvalidFormatMobileValidation() throws Exception {
    	
    	String mobile = "0411-777-999";
    	
        Exchange exchange = new DefaultExchange(context);
        exchange.getIn().setBody(new ByteArrayInputStream(new byte[]{}));
        exchange.getIn().setHeaders(createHeaders(true, mobile));
        
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
        exchange.getIn().setHeaders(createHeaders(true, mobile));
        
        Exchange exchangeOut = restTemplate.send(exchange);
        String result = exchangeOut.getMessage().getBody(String.class);

        assertTrue("null".equals(result));
        assertTrue("204".equals(exchangeOut.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE).toString()));
    }

    @Test
    public void testInvalidFormatMobileValidation() throws Exception {
    	
    	String mobile = "0411-777-222";

        Exchange exchange = new DefaultExchange(context);
        exchange.getIn().setBody(new ByteArrayInputStream(new byte[]{}));
        exchange.getIn().setHeaders(createHeaders(false, mobile));

        Exchange exchangeOut = template.send(exchange);
        MobileResponseResult response = exchangeOut.getIn().getBody(MobileResponseResult.class);
        assertTrue(response != null);
        assertTrue("Invalid format".equals(response.getResponse().getVerificationLevelDescription()));
        assertTrue("0".equals(response.getResponse().getVerificationStatus()));
        assertTrue("200".equals(exchangeOut.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE).toString()));
    }   
    
    @Test
    public void testUnknownMobileValidation() throws Exception {
    	
    	String mobile = "0444444444";

        Exchange exchange = new DefaultExchange(context);
        exchange.getIn().setBody(new ByteArrayInputStream(new byte[]{}));
        exchange.getIn().setHeaders(createHeaders(false, mobile));

        Exchange exchangeOut = template.send(exchange);
        MobileResponseResult response = exchangeOut.getIn().getBody(MobileResponseResult.class);
        assertTrue(response != null);
        assertTrue("Unknown".equals(response.getResponse().getVerificationLevelDescription()));
        assertTrue("1".equals(response.getResponse().getVerificationStatus()));
        assertTrue("200".equals(exchangeOut.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE).toString()));
    }   
    
    @Test
    public void testAbsentMobileValidation() throws Exception {
    	
    	String mobile = "-";

        Exchange exchange = new DefaultExchange(context);
        exchange.getIn().setBody(new ByteArrayInputStream(new byte[]{}));
        exchange.getIn().setHeaders(createHeaders(false, mobile));

        Exchange exchangeOut = template.send(exchange);
        MobileResponseResult response = exchangeOut.getIn().getBody(MobileResponseResult.class);
        assertTrue(response != null);
        assertTrue("Absent".equals(response.getResponse().getVerificationLevelDescription()));
        assertTrue("2".equals(response.getResponse().getVerificationStatus()));
        assertTrue("200".equals(exchangeOut.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE).toString()));
    }
    
    
    @Test
    public void testValidMobileValidation() throws Exception {
    	
    	String mobile = "0478333111";

        Exchange exchange = new DefaultExchange(context);
        exchange.getIn().setBody(new ByteArrayInputStream(new byte[]{}));
        exchange.getIn().setHeaders(createHeaders(false, mobile));

        Exchange exchangeOut = template.send(exchange);
        MobileResponseResult response = exchangeOut.getIn().getBody(MobileResponseResult.class);
        assertTrue(response == null);
        assertTrue("200".equals(exchangeOut.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE).toString()));
    }

    @Test
    public void testTeleserviceNotProvisionedMobileValidation() throws Exception {
    	
    	String mobile = "0499111222";

        Exchange exchange = new DefaultExchange(context);
        exchange.getIn().setBody(new ByteArrayInputStream(new byte[]{}));
        exchange.getIn().setHeaders(createHeaders(false, mobile));

        Exchange exchangeOut = template.send(exchange);
        MobileResponseResult response = exchangeOut.getIn().getBody(MobileResponseResult.class);
        assertTrue(response != null);
        assertTrue("Teleservice not provisioned".equals(response.getResponse().getVerificationLevelDescription()));
        assertTrue("4".equals(response.getResponse().getVerificationStatus()));
        assertTrue("200".equals(exchangeOut.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE).toString()));
    }   

    
    @Test
    public void testCallBarredMobileValidation() throws Exception {
    	
    	String mobile = "0478555444";

        Exchange exchange = new DefaultExchange(context);
        exchange.getIn().setBody(new ByteArrayInputStream(new byte[]{}));
        exchange.getIn().setHeaders(createHeaders(false, mobile));

        Exchange exchangeOut = template.send(exchange);
        MobileResponseResult response = exchangeOut.getIn().getBody(MobileResponseResult.class);
        assertTrue(response != null);
        assertTrue("Call barred".equals(response.getResponse().getVerificationLevelDescription()));
        assertTrue("5".equals(response.getResponse().getVerificationStatus()));
        assertTrue("200".equals(exchangeOut.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE).toString()));
    }  
    
    @Test
    public void testBadRequestMobileMapping() throws Exception {
    	
        String mobile = "0478400400";
    	
        Exchange exchange = new DefaultExchange(context);
        exchange.getIn().setBody(new ByteArrayInputStream(new byte[]{}));
        exchange.getIn().setHeaders(createHeaders(true, mobile));
        
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
        exchange.getIn().setHeaders(createHeaders(true, mobile));

        Exchange exchangeOut = restTemplate.send(exchange);        
        // DDSRestErrorMapping errorMapping = exchangeOut.getMessage().getBody(DDSRestErrorMapping.class);
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
