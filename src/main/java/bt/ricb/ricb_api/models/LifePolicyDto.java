package bt.ricb.ricb_api.models;

import java.sql.Date;

public class LifePolicyDto {
	private Long policySerial;
	private String branchCode;
	private Date proposalDate;
	private String productCode;
	private String customerCode;
	private String jointHolderCode;
	private Integer policyTerm;
	private Integer premiumPayingTerm;
	private Date policyStartDate;
	private Date policyEndDate;
	private Double sumAssured;
	private String modeOfPayment;
	private Double annualPremiumAmount;
	private Integer noOfInstalments;
	private Double premiumPerInstalment;
	private String ricbEmployee;
	private String ricbAgent;
	private Integer entryAgeProposer;
	private Integer entryAgeJointHolder;

	public Long getPolicySerial() {
		return this.policySerial;
	}

	public void setPolicySerial(Long policySerial) {
		this.policySerial = policySerial;
	}

	public String getBranchCode() {
		return this.branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	public Date getProposalDate() {
		return this.proposalDate;
	}

	public void setProposalDate(Date proposalDate) {
		this.proposalDate = proposalDate;
	}

	public String getProductCode() {
		return this.productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getCustomerCode() {
		return this.customerCode;
	}

	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}

	public String getJointHolderCode() {
		return this.jointHolderCode;
	}

	public void setJointHolderCode(String jointHolderCode) {
		this.jointHolderCode = jointHolderCode;
	}

	public Integer getPolicyTerm() {
		return this.policyTerm;
	}

	public void setPolicyTerm(Integer policyTerm) {
		this.policyTerm = policyTerm;
	}

	public Integer getPremiumPayingTerm() {
		return this.premiumPayingTerm;
	}

	public void setPremiumPayingTerm(Integer premiumPayingTerm) {
		this.premiumPayingTerm = premiumPayingTerm;
	}

	public Date getPolicyStartDate() {
		return this.policyStartDate;
	}

	public void setPolicyStartDate(Date policyStartDate) {
		this.policyStartDate = policyStartDate;
	}

	public Date getPolicyEndDate() {
		return this.policyEndDate;
	}

	public void setPolicyEndDate(Date policyEndDate) {
		this.policyEndDate = policyEndDate;
	}

	public Double getSumAssured() {
		return this.sumAssured;
	}

	public void setSumAssured(Double sumAssured) {
		this.sumAssured = sumAssured;
	}

	public String getModeOfPayment() {
		return this.modeOfPayment;
	}

	public void setModeOfPayment(String modeOfPayment) {
		this.modeOfPayment = modeOfPayment;
	}

	public Double getAnnualPremiumAmount() {
		return this.annualPremiumAmount;
	}

	public void setAnnualPremiumAmount(Double annualPremiumAmount) {
		this.annualPremiumAmount = annualPremiumAmount;
	}

	public Integer getNoOfInstalments() {
		return this.noOfInstalments;
	}

	public void setNoOfInstalments(Integer noOfInstalments) {
		this.noOfInstalments = noOfInstalments;
	}

	public Double getPremiumPerInstalment() {
		return this.premiumPerInstalment;
	}

	public void setPremiumPerInstalment(Double premiumPerInstalment) {
		this.premiumPerInstalment = premiumPerInstalment;
	}

	public String getRicbEmployee() {
		return this.ricbEmployee;
	}

	public void setRicbEmployee(String ricbEmployee) {
		this.ricbEmployee = ricbEmployee;
	}

	public String getRicbAgent() {
		return this.ricbAgent;
	}

	public void setRicbAgent(String ricbAgent) {
		this.ricbAgent = ricbAgent;
	}

	public Integer getEntryAgeProposer() {
		return this.entryAgeProposer;
	}

	public void setEntryAgeProposer(Integer entryAgeProposer) {
		this.entryAgeProposer = entryAgeProposer;
	}

	public Integer getEntryAgeJointHolder() {
		return this.entryAgeJointHolder;
	}

	public void setEntryAgeJointHolder(Integer entryAgeJointHolder) {
		this.entryAgeJointHolder = entryAgeJointHolder;
	}
}