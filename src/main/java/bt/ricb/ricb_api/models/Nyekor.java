package bt.ricb.ricb_api.models;

import java.util.Date;
import java.util.List;

public class Nyekor {
	private Long polSerialNo;
    private Long id;
    private Long requestId;
    private String transactionId;
    private Date transactionDate;
    private String remitterName;
    private String remitterBank;
    private String remitterMobileNo;
    private String transactionStatus;
    private String dataSource;
    private String authCode;
    private String authId;
    private String responseCode;
    private Date updatedDate;
    private String remarks;
    private String remitterAccountNo;
    private String processCore;
    private String errLog;
    private Integer underwritingYear;
    private String customerCode;
    private String customerName;
    private String customerType;
    private String agentCode;
    private Date journeyStartDate;
    private Date journeyEndDate;
    private Integer numberOfDays;
    private String placeFrom;
    private String placeTo;
    private String carrierType;
    private String carrierRegnNo;
    private Integer numberOfPassenger;
    private Double sumInsuredPerPerson;
    private Double premiumPerPerson;
    private Double totalSumInsured;
    private Double totalPremium;
    
    private List<NyekorDetails> policyDetails;

	public Long getPolSerialNo() {
		return polSerialNo;
	}

	public void setPolSerialNo(Long polSerialNo) {
		this.polSerialNo = polSerialNo;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getRequestId() {
		return requestId;
	}

	public void setRequestId(Long requestId) {
		this.requestId = requestId;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getRemitterName() {
		return remitterName;
	}

	public void setRemitterName(String remitterName) {
		this.remitterName = remitterName;
	}

	public String getRemitterBank() {
		return remitterBank;
	}

	public void setRemitterBank(String remitterBank) {
		this.remitterBank = remitterBank;
	}

	public String getRemitterMobileNo() {
		return remitterMobileNo;
	}

	public void setRemitterMobileNo(String remitterMobileNo) {
		this.remitterMobileNo = remitterMobileNo;
	}

	public String getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(String transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	public String getAuthCode() {
		return authCode;
	}

	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}

	public String getAuthId() {
		return authId;
	}

	public void setAuthId(String authId) {
		this.authId = authId;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getRemitterAccountNo() {
		return remitterAccountNo;
	}

	public void setRemitterAccountNo(String remitterAccountNo) {
		this.remitterAccountNo = remitterAccountNo;
	}

	public String getProcessCore() {
		return processCore;
	}

	public void setProcessCore(String processCore) {
		this.processCore = processCore;
	}

	public String getErrLog() {
		return errLog;
	}

	public void setErrLog(String errLog) {
		this.errLog = errLog;
	}

	public Integer getUnderwritingYear() {
		return underwritingYear;
	}

	public void setUnderwritingYear(Integer underwritingYear) {
		this.underwritingYear = underwritingYear;
	}

	public String getCustomerCode() {
		return customerCode;
	}

	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerType() {
		return customerType;
	}

	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}

	public String getAgentCode() {
		return agentCode;
	}

	public void setAgentCode(String agentCode) {
		this.agentCode = agentCode;
	}

	public Date getJourneyStartDate() {
		return journeyStartDate;
	}

	public void setJourneyStartDate(Date journeyStartDate) {
		this.journeyStartDate = journeyStartDate;
	}

	public Date getJourneyEndDate() {
		return journeyEndDate;
	}

	public void setJourneyEndDate(Date journeyEndDate) {
		this.journeyEndDate = journeyEndDate;
	}

	public Integer getNumberOfDays() {
		return numberOfDays;
	}

	public void setNumberOfDays(Integer numberOfDays) {
		this.numberOfDays = numberOfDays;
	}

	public String getPlaceFrom() {
		return placeFrom;
	}

	public void setPlaceFrom(String placeFrom) {
		this.placeFrom = placeFrom;
	}

	public String getPlaceTo() {
		return placeTo;
	}

	public void setPlaceTo(String placeTo) {
		this.placeTo = placeTo;
	}

	public String getCarrierType() {
		return carrierType;
	}

	public void setCarrierType(String carrierType) {
		this.carrierType = carrierType;
	}

	public String getCarrierRegnNo() {
		return carrierRegnNo;
	}

	public void setCarrierRegnNo(String carrierRegnNo) {
		this.carrierRegnNo = carrierRegnNo;
	}

	public Integer getNumberOfPassenger() {
		return numberOfPassenger;
	}

	public void setNumberOfPassenger(Integer numberOfPassenger) {
		this.numberOfPassenger = numberOfPassenger;
	}

	public Double getSumInsuredPerPerson() {
		return sumInsuredPerPerson;
	}

	public void setSumInsuredPerPerson(Double sumInsuredPerPerson) {
		this.sumInsuredPerPerson = sumInsuredPerPerson;
	}

	public Double getPremiumPerPerson() {
		return premiumPerPerson;
	}

	public void setPremiumPerPerson(Double premiumPerPerson) {
		this.premiumPerPerson = premiumPerPerson;
	}

	public Double getTotalSumInsured() {
		return totalSumInsured;
	}

	public void setTotalSumInsured(Double totalSumInsured) {
		this.totalSumInsured = totalSumInsured;
	}

	public Double getTotalPremium() {
		return totalPremium;
	}

	public void setTotalPremium(Double totalPremium) {
		this.totalPremium = totalPremium;
	}

	public List<NyekorDetails> getPolicyDetails() {
		return policyDetails;
	}

	public void setPolicyDetails(List<NyekorDetails> policyDetails) {
		this.policyDetails = policyDetails;
	}
    
    
}
