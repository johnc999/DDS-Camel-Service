package au.gov.ato.abrs.integration.dds.routes.exception;

import au.gov.ato.documents.*;

import org.apache.camel.http.base.HttpOperationFailedException;

import au.gov.ato.abrs.integration.routes.exception.mapping.rest.RestErrorMapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DDSRestErrorMapping extends RestErrorMapping {
    
    public DDSRestErrorMapping(Throwable cause, String traceId) {
        // Initial generic mappings
		super(cause, traceId);  

		// Specific camel service mappings
        if (cause instanceof HttpOperationFailedException) {
        	processHttpOperationFailedException((HttpOperationFailedException)cause, traceId);
        	switch (((HttpOperationFailedException)cause).getStatusCode()) {        		
                case 400:
                case 422:
                    setStatusCode(422); // Unprocessable Entity
                    break;

                case 401:
                    setStatusCode(401); // Authentication Failed
                    break;
                    
                case 403:
                    setStatusCode(403); // Authorisation Failed
                    break;           
                    
                case 405:
                    setStatusCode(405); // Method not supported
                    break;                	
                	
                case 406:                	
                case 408:
                case 415:
                case 429:
                case 502:                	
                    setStatusCode(502); // Service unavailable
                    break;

                default:
                    setStatusCode(500); // Internal server error
                    break;
        	}
        } else if (cause instanceof IOException) {
        	processIOException((IOException)cause, traceId);
        	setStatusCode(500);
        }
    }
    
    private void processHttpOperationFailedException(HttpOperationFailedException cause, String traceId) {
    	List<StandardErrorReport> reports = new ArrayList<>();
    	StandardErrorReport report = new StandardErrorReport();
    	report.setDetail("DDS Operation Failed: " + cause.getMessage() + ", " + cause.getStatusText());
    	report.setSeverity(Severity.ERROR);		
		reports.add(report);
    	setMappedErrors(reports, traceId);
    }

    private void processIOException(IOException cause, String traceId) {
    	List<StandardErrorReport> reports = new ArrayList<>();
    	StandardErrorReport report = new StandardErrorReport();
    	report.setDetail("DDS communication error : " + cause.getMessage());
    	report.setSeverity(Severity.ERROR);
    	reports.add(report);
    	setMappedErrors(reports, traceId);
    }
}
