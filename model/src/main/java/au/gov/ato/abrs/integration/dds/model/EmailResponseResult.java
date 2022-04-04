package au.gov.ato.abrs.integration.dds.model;

public class EmailResponseResult {
	EmailResponse ResponseObject;

	public EmailResponse getResponse() {
		return ResponseObject;
	}

	public void setResponse(EmailResponse responseObject) {
		this.ResponseObject = responseObject;
	}
}
