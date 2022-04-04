package au.gov.ato.abrs.integration.dds.routes.exception;

import au.gov.ato.documents.*;

import org.apache.camel.InvalidPayloadException;
import org.apache.camel.ValidationException;
import org.apache.camel.http.base.HttpOperationFailedException;

import au.gov.ato.abrs.integration.routes.exception.mapping.rest.RestErrorMapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DDSRestErrorMapping extends RestErrorMapping {
    
    public DDSRestErrorMapping(Throwable cause, String traceId) {
        super(cause, traceId);

        if (cause instanceof HttpOperationFailedException) {
        	processHttpOperationFailedException((HttpOperationFailedException)cause, traceId);
        	switch (((HttpOperationFailedException)cause).getStatusCode()) {
        		case 400: setStatusCode(400); break;
        		case 401: setStatusCode(401); break;
        		case 403: setStatusCode(403); break;
        		case 405: setStatusCode(405); break;
        		case 406: setStatusCode(406); break;
        		case 415: setStatusCode(415); break;
        		case 501: setStatusCode(501); break;
        		case 502: setStatusCode(502); break;
        		case 503: setStatusCode(503); break;
        		default: setStatusCode(500); break;
        	}
        	setStatusCode(((HttpOperationFailedException) cause).getStatusCode());
        } else if (cause instanceof ValidationException) {
        	processValidationException((ValidationException)cause, traceId);
        	setStatusCode(400);          	
        } else if (cause instanceof IOException) {
        	processIOException((IOException)cause, traceId);
        	setStatusCode(503);
        } else if (cause instanceof InvalidPayloadException) {
        	processInvalidPayloadException((InvalidPayloadException)cause, traceId);
        	setStatusCode(503);
        } else {
        	processThrowable(cause, traceId);
        	setStatusCode(503);        	
        }
    }
    
    private void processValidationException(ValidationException cause, String traceId) {
    	List<StandardErrorReport> reports = new ArrayList<>();
    	StandardErrorReport report = new StandardErrorReport();
    	report.setDetail("DDS validation error : " + cause.getMessage());
    	report.setSeverity(Severity.ERROR);
    	reports.add(report);
    	setMappedErrors(reports, traceId);
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
    
    private void processInvalidPayloadException(InvalidPayloadException cause, String traceId) {
    	List<StandardErrorReport> reports = new ArrayList<>();
    	StandardErrorReport report = new StandardErrorReport();
    	report.setDetail("DDS invalid payload : " + cause.getMessage());
    	report.setSeverity(Severity.ERROR);
    	reports.add(report);
    	setMappedErrors(reports, traceId);
    }
    
    private void processThrowable(Throwable cause, String traceId) {
    	List<StandardErrorReport> reports = new ArrayList<>();
    	StandardErrorReport report = new StandardErrorReport();
    	report.setDetail("DDS exception : " + cause.getMessage());
    	report.setSeverity(Severity.ERROR);
    	reports.add(report);
    	setMappedErrors(reports, traceId);
    }
}
