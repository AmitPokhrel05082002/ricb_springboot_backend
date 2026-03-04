package bt.ricb.ricb_api.models;

import java.math.BigDecimal;

public class PolicyPremiumDto {
	private Long policySerial;
	private BigDecimal tabularPremium;
	private BigDecimal staffRebate;
	private BigDecimal agentRebate;
	private BigDecimal paymentModeAdj;
	private BigDecimal saRebate;
	private BigDecimal accidentExtra;
	private BigDecimal occupHealthExtra;
	private BigDecimal premiumWaiver;
	private BigDecimal termRider;
	private BigDecimal educationAllowance;
	private BigDecimal annualPremium;
	private BigDecimal premiumInstalment;
	private BigDecimal discountInstalment;
	private BigDecimal loadingInstalment;
	private BigDecimal annualPremiumPayable;
	private BigDecimal payableInstalment;

	public Long getPolicySerial() {
		return this.policySerial;
	}

	public void setPolicySerial(Long policySerial) {
		this.policySerial = policySerial;
	}

	public BigDecimal getTabularPremium() {
		return this.tabularPremium;
	}

	public void setTabularPremium(BigDecimal tabularPremium) {
		this.tabularPremium = tabularPremium;
	}

	public BigDecimal getStaffRebate() {
		return this.staffRebate;
	}

	public void setStaffRebate(BigDecimal staffRebate) {
		this.staffRebate = staffRebate;
	}

	public BigDecimal getAgentRebate() {
		return this.agentRebate;
	}

	public void setAgentRebate(BigDecimal agentRebate) {
		this.agentRebate = agentRebate;
	}

	public BigDecimal getPaymentModeAdj() {
		return this.paymentModeAdj;
	}

	public void setPaymentModeAdj(BigDecimal paymentModeAdj) {
		this.paymentModeAdj = paymentModeAdj;
	}

	public BigDecimal getSaRebate() {
		return this.saRebate;
	}

	public void setSaRebate(BigDecimal saRebate) {
		this.saRebate = saRebate;
	}

	public BigDecimal getAccidentExtra() {
		return this.accidentExtra;
	}

	public void setAccidentExtra(BigDecimal accidentExtra) {
		this.accidentExtra = accidentExtra;
	}

	public BigDecimal getOccupHealthExtra() {
		return this.occupHealthExtra;
	}

	public void setOccupHealthExtra(BigDecimal occupHealthExtra) {
		this.occupHealthExtra = occupHealthExtra;
	}

	public BigDecimal getPremiumWaiver() {
		return this.premiumWaiver;
	}

	public void setPremiumWaiver(BigDecimal premiumWaiver) {
		this.premiumWaiver = premiumWaiver;
	}

	public BigDecimal getTermRider() {
		return this.termRider;
	}

	public void setTermRider(BigDecimal termRider) {
		this.termRider = termRider;
	}

	public BigDecimal getEducationAllowance() {
		return this.educationAllowance;
	}

	public void setEducationAllowance(BigDecimal educationAllowance) {
		this.educationAllowance = educationAllowance;
	}

	public BigDecimal getAnnualPremium() {
		return this.annualPremium;
	}

	public void setAnnualPremium(BigDecimal annualPremium) {
		this.annualPremium = annualPremium;
	}

	public BigDecimal getPremiumInstalment() {
		return this.premiumInstalment;
	}

	public void setPremiumInstalment(BigDecimal premiumInstalment) {
		this.premiumInstalment = premiumInstalment;
	}

	public BigDecimal getDiscountInstalment() {
		return this.discountInstalment;
	}

	public void setDiscountInstalment(BigDecimal discountInstalment) {
		this.discountInstalment = discountInstalment;
	}

	public BigDecimal getLoadingInstalment() {
		return this.loadingInstalment;
	}

	public void setLoadingInstalment(BigDecimal loadingInstalment) {
		this.loadingInstalment = loadingInstalment;
	}

	public BigDecimal getAnnualPremiumPayable() {
		return this.annualPremiumPayable;
	}

	public void setAnnualPremiumPayable(BigDecimal annualPremiumPayable) {
		this.annualPremiumPayable = annualPremiumPayable;
	}

	public BigDecimal getPayableInstalment() {
		return this.payableInstalment;
	}

	public void setPayableInstalment(BigDecimal payableInstalment) {
		this.payableInstalment = payableInstalment;
	}
}