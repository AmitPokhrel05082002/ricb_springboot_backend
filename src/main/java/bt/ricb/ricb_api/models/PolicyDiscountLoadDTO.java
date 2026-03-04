package bt.ricb.ricb_api.models;

import java.math.BigDecimal;

public class PolicyDiscountLoadDTO {
	private Long policySerial;
	private String discLoadCode;
	private String discLoadType;
	private String discLoadDesc;

	public Long getPolicySerial() {
		return this.policySerial;
	}

	private BigDecimal ratePer1000SA;
	private BigDecimal ratePcntOnPrem;
	private String doctorBMHCNo;
	private BigDecimal medicalFeesAmt;
	private BigDecimal reimbProposerAmt;

	public void setPolicySerial(Long policySerial) {
		this.policySerial = policySerial;
	}

	public String getDiscLoadCode() {
		return this.discLoadCode;
	}

	public void setDiscLoadCode(String discLoadCode) {
		this.discLoadCode = discLoadCode;
	}

	public String getDiscLoadType() {
		return this.discLoadType;
	}

	public void setDiscLoadType(String discLoadType) {
		this.discLoadType = discLoadType;
	}

	public String getDiscLoadDesc() {
		return this.discLoadDesc;
	}

	public void setDiscLoadDesc(String discLoadDesc) {
		this.discLoadDesc = discLoadDesc;
	}

	public BigDecimal getRatePer1000SA() {
		return this.ratePer1000SA;
	}

	public void setRatePer1000SA(BigDecimal ratePer1000SA) {
		this.ratePer1000SA = ratePer1000SA;
	}

	public BigDecimal getRatePcntOnPrem() {
		return this.ratePcntOnPrem;
	}

	public void setRatePcntOnPrem(BigDecimal ratePcntOnPrem) {
		this.ratePcntOnPrem = ratePcntOnPrem;
	}

	public String getDoctorBMHCNo() {
		return this.doctorBMHCNo;
	}

	public void setDoctorBMHCNo(String doctorBMHCNo) {
		this.doctorBMHCNo = doctorBMHCNo;
	}

	public BigDecimal getMedicalFeesAmt() {
		return this.medicalFeesAmt;
	}

	public void setMedicalFeesAmt(BigDecimal medicalFeesAmt) {
		this.medicalFeesAmt = medicalFeesAmt;
	}

	public BigDecimal getReimbProposerAmt() {
		return this.reimbProposerAmt;
	}

	public void setReimbProposerAmt(BigDecimal reimbProposerAmt) {
		this.reimbProposerAmt = reimbProposerAmt;
	}
}