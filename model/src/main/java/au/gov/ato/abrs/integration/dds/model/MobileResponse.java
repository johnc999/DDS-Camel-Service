package au.gov.ato.abrs.integration.dds.model;

public class MobileResponse {

// TODO: Code Review - What happened to looking at consolodating .. 
//                     can't remember my original comments as they are gone from source control

	private String verificationLevelDescription;
	private String verificationStatus;

	public String getVerificationLevelDescription() {
		return verificationLevelDescription;
	}

	public String getVerificationStatus() {
		return verificationStatus;
	}

	public void setVerificationLevelDescription(String verificationLevelDescription) {
		this.verificationLevelDescription = verificationLevelDescription;
	}

	public void setVerificationStatus(String verificationStatus) {
		this.verificationStatus = verificationStatus;
	}
}