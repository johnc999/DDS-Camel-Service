package au.gov.ato.abrs.integration.dds.model;

public class EmailResponse {

	private String verificationLevelDescription;
	private String verificationMessage;

	public String getVerificationLevelDescription() {
		return verificationLevelDescription;
	}

	public String getVerificationMessage() {
		return verificationMessage;
	}

	public void setVerificationLevelDescription(String verificationLevelDescription) {
		this.verificationLevelDescription = verificationLevelDescription;
	}

	public void setVerificationMessage(String verificationMessage) {
		this.verificationMessage = verificationMessage;
	}
}