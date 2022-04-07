// TODO: Code Review - Why is test class this here

package au.gov.ato.wsdl;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class LogHandler implements SOAPHandler<SOAPMessageContext> {

   @Override
   public boolean handleMessage(SOAPMessageContext context) {
      logSoapMessage(false, context);
      return true;
   }

   @Override
   public boolean handleFault(SOAPMessageContext context) {
      logSoapMessage(true, context);
      return false;
   }

   @Override
   public void close(MessageContext context) {
   }

   @Override
   public Set<QName> getHeaders() {
      return new HashSet<QName>();      
   }

   private void logSoapMessage(boolean fault, SOAPMessageContext context) {
      //System.out.println("Context keys: " + context.keySet().stream().collect(Collectors.joining(", ")));
      //System.out.println();


      Boolean isOutBound = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
      SOAPMessage soapMsg = context.getMessage();

      try {
          if (isOutBound) {
            System.out.println(fault ? "FAULT_OUT" : "REQ_OUT");
            System.out.println("   Address: " + context.get("javax.xml.ws.service.endpoint.address"));
            System.out.println("   Operation: " + context.get("javax.xml.ws.wsdl.operation"));
      
          } else {
            System.out.println(fault ? "FAULT_IN" : "RES_IN");
          }

          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          soapMsg.writeTo(baos);
          System.out.println(String.format("   Payload: %s", baos.toString()));
          baos.close();

      } catch (SOAPException e) {
          e.printStackTrace();
      } catch (IOException e) {
          e.printStackTrace();
      }
  }   
}
