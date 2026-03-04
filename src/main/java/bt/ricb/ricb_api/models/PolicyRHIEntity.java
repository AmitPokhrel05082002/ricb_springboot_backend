package bt.ricb.ricb_api.models;
import java.math.BigDecimal;
import java.util.Date;

public class PolicyRHIEntity {
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
    private String underwritingYear;
    private Date policyStartDate;
    private Date policyEndDate;
    private String dzongCode;
    private String gewogCode;
    private String village;
    private String customerCid;
    private String customerName;
    private String previousPolicyNo;
    private String householdNo;
    private String houseCategory;
    private BigDecimal sumInsured;
    private BigDecimal familyPrem;
    private BigDecimal subsidyPrem;
    private BigDecimal totalPrem;
    private BigDecimal collectedPremium;
    private Date collectionDate;
    private String statusCode;
    
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
	public String getUnderwritingYear() {
		return underwritingYear;
	}
	public void setUnderwritingYear(String underwritingYear) {
		this.underwritingYear = underwritingYear;
	}
	public Date getPolicyStartDate() {
		return policyStartDate;
	}
	public void setPolicyStartDate(Date policyStartDate) {
		this.policyStartDate = policyStartDate;
	}
	public Date getPolicyEndDate() {
		return policyEndDate;
	}
	public void setPolicyEndDate(Date policyEndDate) {
		this.policyEndDate = policyEndDate;
	}
	public String getDzongCode() {
		return dzongCode;
	}
	public void setDzongCode(String dzongCode) {
		this.dzongCode = dzongCode;
	}
	public String getGewogCode() {
		return gewogCode;
	}
	public void setGewogCode(String gewogCode) {
		this.gewogCode = gewogCode;
	}
	public String getVillage() {
		return village;
	}
	public void setVillage(String village) {
		this.village = village;
	}
	public String getCustomerCid() {
		return customerCid;
	}
	public void setCustomerCid(String customerCid) {
		this.customerCid = customerCid;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getPreviousPolicyNo() {
		return previousPolicyNo;
	}
	public void setPreviousPolicyNo(String previousPolicyNo) {
		this.previousPolicyNo = previousPolicyNo;
	}
	public String getHouseholdNo() {
		return householdNo;
	}
	public void setHouseholdNo(String householdNo) {
		this.householdNo = householdNo;
	}
	public String getHouseCategory() {
		return houseCategory;
	}
	public void setHouseCategory(String houseCategory) {
		this.houseCategory = houseCategory;
	}
	public BigDecimal getSumInsured() {
		return sumInsured;
	}
	public void setSumInsured(BigDecimal sumInsured) {
		this.sumInsured = sumInsured;
	}
	public BigDecimal getFamilyPrem() {
		return familyPrem;
	}
	public void setFamilyPrem(BigDecimal familyPrem) {
		this.familyPrem = familyPrem;
	}
	public BigDecimal getSubsidyPrem() {
		return subsidyPrem;
	}
	public void setSubsidyPrem(BigDecimal subsidyPrem) {
		this.subsidyPrem = subsidyPrem;
	}
	public BigDecimal getTotalPrem() {
		return totalPrem;
	}
	public void setTotalPrem(BigDecimal totalPrem) {
		this.totalPrem = totalPrem;
	}
	public BigDecimal getCollectedPremium() {
		return collectedPremium;
	}
	public void setCollectedPremium(BigDecimal collectedPremium) {
		this.collectedPremium = collectedPremium;
	}
	public Date getCollectionDate() {
		return collectionDate;
	}
	public void setCollectionDate(Date collectionDate) {
		this.collectionDate = collectionDate;
	}
	public String getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
    
    
}
