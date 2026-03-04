package bt.ricb.ricb_api.models;

import java.math.BigDecimal;

public class PolicyCoverDto {
	private Long policySerial;
	private String coverCode;
	private String coverDesc;
	private BigDecimal coverAmount;
	private BigDecimal premiumRate;
	private BigDecimal premiumAmount;

	public Long getPolicySerial() {
		return this.policySerial;
	}

	public void setPolicySerial(Long policySerial) {
		this.policySerial = policySerial;
	}

	public String getCoverCode() {
		return this.coverCode;
	}

	public void setCoverCode(String coverCode) {
		this.coverCode = coverCode;
	}

	public String getCoverDesc() {
		return this.coverDesc;
	}

	public void setCoverDesc(String coverDesc) {
		this.coverDesc = coverDesc;
	}

	public BigDecimal getCoverAmount() {
		return this.coverAmount;
	}

	public void setCoverAmount(BigDecimal coverAmount) {
		this.coverAmount = coverAmount;
	}

	public BigDecimal getPremiumRate() {
		return this.premiumRate;
	}

	public void setPremiumRate(BigDecimal premiumRate) {
		this.premiumRate = premiumRate;
	}

	public BigDecimal getPremiumAmount() {
		return this.premiumAmount;
	}

	public void setPremiumAmount(BigDecimal premiumAmount) {
		this.premiumAmount = premiumAmount;
	}
}