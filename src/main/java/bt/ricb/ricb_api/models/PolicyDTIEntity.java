package bt.ricb.ricb_api.models;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "MOBAPP_DTI_NEW_POLICY")
public class PolicyDTIEntity {
    @Column(name = "POL_SERIAL_NO", precision = 10)
    private Integer polSerialNo;
    
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO) // Or no @GeneratedValue at all
    @Column(name = "ID", precision = 38)
    private Long id;
    
    @Column(name = "REQUEST_ID", precision = 38)
    private Long requestId;
    
    @Column(name = "TRANSACTION_ID", length = 100)
    private String transactionId;
    
    @Column(name = "TRANSACTION_DATE")
    @Temporal(TemporalType.DATE)
    private Date transactionDate;
    
    @Column(name = "REMITTER_NAME", length = 100)
    private String remitterName;
    
    @Column(name = "REMITTER_BANK", length = 20)
    private String remitterBank;
    
    @Column(name = "REMITTER_MOBILE_NO", length = 20)
    private String remitterMobileNo;
    
    @Column(name = "TRANSACTION_STATUS", length = 10)
    private String transactionStatus;
    
    @Column(name = "DATA_SOURCE", length = 10)
    private String dataSource;
    
    @Column(name = "AUTH_CODE", length = 10)
    private String authCode;
    
    @Column(name = "AUTH_ID", length = 10)
    private String authId;
    
    @Column(name = "RESPONSE_CODE", length = 10)
    private String responseCode;
    
    @Column(name = "UPDATED_DATE")
    @Temporal(TemporalType.DATE)
    private Date updatedDate;
    
    @Column(name = "REMARKS", length = 255)
    private String remarks;
    
    @Column(name = "REMMITER_ACCOUNT_NO", length = 50)
    private String remitterAccountNo;
    
    @Column(name = "PROCESS_CORE", length = 1)
    private String processCore;
    
    @Column(name = "ERR_LOG", length = 1000)
    private String errLog;
    
    @Column(name = "UNDERWRITING_YEAR", precision = 4)
    private Integer underwritingYear;
    
    @Column(name = "CUSTOMER_CODE", length = 20)
    private String customerCode;
    
    @Column(name = "CUSTOMER_NAME", length = 500)
    private String customerName;
    
    @Column(name = "CUSTOMER_TYPE", length = 1)
    private String customerType;
    
    @Column(name = "AGENT_CODE", length = 20)
    private String agentCode;
    
    @Column(name = "JOURNEY_START_DATE")
    @Temporal(TemporalType.DATE)
    private Date journeyStartDate;
    
    @Column(name = "JOURNEY_END_DATE")
    @Temporal(TemporalType.DATE)
    private Date journeyEndDate;
    
    @Column(name = "NUMBER_OF_DAYS", precision = 10)
    private Integer numberOfDays;
    
    @Column(name = "PLACE_FROM", length = 500)
    private String placeFrom;
    
    @Column(name = "PLACE_TO", length = 500)
    private String placeTo;
    
    @Column(name = "CARRIER_TYPE", length = 20)
    private String carrierType;
    
    @Column(name = "CARRIER_REGN_NO", length = 500)
    private String carrierRegnNo;
    
    @Column(name = "NUMBER_OF_PASSENGER", precision = 10)
    private Integer numberOfPassenger;
    
    @Column(name = "SUM_INSURED_PER_PERSON", precision = 20, scale = 2)
    private BigDecimal sumInsuredPerPerson;
    
    @Column(name = "TOTAL_SUM_INSURED", precision = 20, scale = 2)
    private BigDecimal totalSumInsured;
    
    @Column(name = "TOTAL_PREMIUM", precision = 20, scale = 2)
    private BigDecimal totalPremium;

	public Integer getPolSerialNo() {
		return polSerialNo;
	}

	public void setPolSerialNo(Integer polSerialNo) {
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

	public BigDecimal getSumInsuredPerPerson() {
		return sumInsuredPerPerson;
	}

	public void setSumInsuredPerPerson(BigDecimal sumInsuredPerPerson) {
		this.sumInsuredPerPerson = sumInsuredPerPerson;
	}

	public BigDecimal getTotalSumInsured() {
		return totalSumInsured;
	}

	public void setTotalSumInsured(BigDecimal totalSumInsured) {
		this.totalSumInsured = totalSumInsured;
	}

	public BigDecimal getTotalPremium() {
		return totalPremium;
	}

	public void setTotalPremium(BigDecimal totalPremium) {
		this.totalPremium = totalPremium;
	}
    
    
    
    
    
    
    
}
