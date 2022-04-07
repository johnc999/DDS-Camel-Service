// TODO: Code Review - Why is this test class here

/*
package au.gov.ato.wsdl;

import au.com.ippayments._interface.api.dts.Dts;
import au.com.ippayments._interface.api.dts.DtsSoap;
import au.com.ippayments._interface.api.dts.SubmitSinglePayment;
import au.gov.ato.abrs.paymentgateway.model.singlepayment.SinglePaymentResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.Test;


import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;


public class LiveService {

   @Test
   public void testLiveConnection() throws IOException {
      // JAX-B Enum Mappings
      // https://javaee.github.io/jaxb-v2/doc/user-guide/ch03.html

      // https://download.asic.gov.au/media/4999931/message-implementation-guide-for-infobrokers-v15.pdf
      // String endpointUrl = "http://localhost:8080"; // Can use for tcpdump
      String endpointUrl = "https://demo.ippayments.com.au/interface/api/dts.asmx";
      //String username = "abrs2asic@ato.gov.au";
      //String password = "T0d@y1234";
      Dts service = new Dts();

      // Available ports
      Iterator<QName> it = service.getPorts();
      while (it.hasNext()) {
         QName m = it.next();
         System.out.println("Port QName: " + m);
      }


      //"<CDATA> + input + ]]>"
      

      DtsSoap web = service.getPort(DtsSoap.class);
      ((BindingProvider) web).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointUrl);
      //((BindingProvider) web).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
      //((BindingProvider) web).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);
      ((BindingProvider) web).getRequestContext().put(BindingProvider.SESSION_MAINTAIN_PROPERTY, true);
      List<Handler> handlerChain = ((BindingProvider) web).getBinding().getHandlerChain();
      handlerChain.add(new LogHandler());
      ((BindingProvider) web).getBinding().setHandlerChain(handlerChain);

      String transactionXML = "<![CDATA[\n" +
              "\t<Transaction>\n" +
              "\t\t<CustNumber>12345</CustNumber> \n" +
              "\t\t<CustRef>abcd1234</CustRef>\n" +
              "\t\t<Amount>1000</Amount>\n" +
              "\t\t<TrnType>1</TrnType>\n" +
              "\t\t<AccountNumber>123456</AccountNumber>\n" +
              "\t\t<CreditCard>\n" +
              "\t\t\t<SecureTransactionToken>stt_40361992-6c55-4d5c-83fa-0918971d3642</SecureTransactionToken> or <OneTimeToken>OTT_returned</OneTimeToken>\n" +
              "\t\t</CreditCard>\n" +
              "                <AdditionalReturnValues>\n" +
              "\t\t\t<CardType>true</CardType>\n" +
              "    \t\t\t<TruncatedCard>true</TruncatedCard>\n" +
              "    \t\t\t<ExpM>true</ExpM>\n" +
              "    \t\t\t<ExpY>true</ExpY>\n" +
              "\t\t</AdditionalReturnValues>\n" +
              "\t\t<Security>\n" +
              "\t\t\t<UserName>TMBR.Adhoc.API</UserName>\n" +
              "\t\t\t<Password>b75Y$q109KlE</Password>\n" +
              "\t\t</Security>\n" +
              "\t</Transaction>\n" +
              "]]>";

      SubmitSinglePayment ssp = new SubmitSinglePayment();
      ssp.setTrnXML(transactionXML);

      String sspr = web.submitSinglePayment(transactionXML);

      System.out.println("Port QName: " + sspr);
      System.out.println("Port QName: " + sspr.toString());

      XmlMapper xmlMapper = new XmlMapper();
      SinglePaymentResponse poppy = xmlMapper.readValue(sspr, SinglePaymentResponse.class);

      String requestXML =
              "\t<Transaction>\n" +
              "\t\t<CustNumber>12345</CustNumber> \n" +
              "\t\t<CustRef>abcd1234</CustRef>\n" +
              "\t\t<Amount>1000</Amount>\n" +
              "\t\t<TrnType>1</TrnType>\n" +
              "\t\t<AccountNumber>123456</AccountNumber>\n" +
              "\t\t<CreditCard>\n" +
              "\t\t\t<SecureTransactionToken>stt_40361992-6c55-4d5c-83fa-0918971d3642</SecureTransactionToken> or <OneTimeToken>OTT_returned</OneTimeToken>\n" +
              "\t\t</CreditCard>\n" +
              "                <AdditionalReturnValues>\n" +
              "\t\t\t<CardType>true</CardType>\n" +
              "    \t\t\t<TruncatedCard>true</TruncatedCard>\n" +
              "    \t\t\t<ExpM>true</ExpM>\n" +
              "    \t\t\t<ExpY>true</ExpY>\n" +
              "\t\t</AdditionalReturnValues>\n" +
              "\t\t<Security>\n" +
              "\t\t\t<UserName>TMBR.Adhoc.API</UserName>\n" +
              "\t\t\t<Password>b75Y$q109KlE</Password>\n" +
              "\t\t</Security>\n" +
              "\t</Transaction>\n";

      //Reading the XML
      JsonNode jsonNode = xmlMapper.readTree(requestXML.getBytes());

      //Create a new ObjectMapper
      ObjectMapper objectMapper = new ObjectMapper();

      //Get JSON as a string
      String value = objectMapper.writeValueAsString(jsonNode);

      System.out.println("Port QName: " + poppy);
      System.out.println("Port QName: " + poppy.toString());

   }

*/
/*   @Test
   public void usingClientProxyStopIsCalledForUnsupportedOperation() throws Exception {
      final JaxWsClientFactoryBean factory = new JaxWsClientFactoryBean();
      factory.setAddress("local://services/Book");
      factory.setServiceClass(IBookWebService.class);
      factory.setFeatures(Arrays.asList(new MetricsFeature(provider)));

      try {
         final Client client = factory.create();
         expectedException.expect(UncheckedException.class);
         client.invoke("getBooks");
      } finally {
         Mockito.verifyNoInteractions(endpointContext);
         Mockito.verifyNoInteractions(operationContext);
         Mockito.verifyNoInteractions(resourceContext);
      }
   }*//*

}
*/
