package au.gov.ato.abrs.integration.dds.model;

public class MobileResponseResult {
	MobileResponse ResponseObject;

	public MobileResponse getResponse() {
		return ResponseObject;
	}

	public void setResponse(MobileResponse responseObject) {
		this.ResponseObject = responseObject;
	}
	
	public class MobileResponse {
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
}
