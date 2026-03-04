package bt.ricb.ricb_api.models;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "TL_MOBAPP_MTP_NEW_POLICY", indexes = {
	    @Index(name = "IDX_TRANSACTION_ID", columnList = "TRANSACTION_ID"),
	    @Index(name = "IDX_CUSTOMER_CODE", columnList = "CUSTOMER_CODE"),
	    @Index(name = "IDX_AGENT_CODE", columnList = "AGENT_CODE"),
	    @Index(name = "IDX_TRANSACTION_DATE", columnList = "TRANSACTION_DATE"),
	    @Index(name = "IDX_POLICY_DATES", columnList = "POLICY_START_DATE, POLICY_END_DATE")
	})
public class MtpNewPolicy {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SERIAL_NO")
    private Integer serialNo;
    
    @NotBlank
    @Size(max = 50)
    @Column(name = "ID", nullable = false, length = 50)
    private String id;
    
    @NotBlank
    @Size(max = 100)
    @Column(name = "REQUEST_ID", nullable = false, length = 100)
    private String requestId;
    
    @NotBlank
    @Size(max = 100)
    @Column(name = "TRANSACTION_ID", nullable = false, length = 100)
    private String transactionId;
    
    @NotNull
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Column(name = "TRANSACTION_DATE", nullable = false)
    private LocalDate transactionDate;
    
    @NotBlank
    @Size(max = 255)
    @Column(name = "REMITTER_NAME", nullable = false)
    private String remitterName;
    
    @NotBlank
    @Size(max = 255)
    @Column(name = "REMITTER_BANK", nullable = false)
    private String remitterBank;
    
    @Size(max = 20)
    @Column(name = "REMITTER_MOBILE_NO", length = 20)
    private String remitterMobileNo;
    
    @NotBlank
    @Size(max = 20)
    @Column(name = "TRANSACTION_STATUS", nullable = false, length = 20)
    private String transactionStatus = "SUCCESS";
    
    @NotBlank
    @Size(max = 50)
    @Column(name = "DATA_SOURCE", nullable = false, length = 50)
    private String dataSource;
    
    @Size(max = 50)
    @Column(name = "AUTH_CODE", length = 50)
    private String authCode;
    
    @Size(max = 50)
    @Column(name = "AUTH_ID", length = 50)
    private String authId;
    
    @Size(max = 20)
    @Column(name = "RESPONSE_CODE", length = 20)
    private String responseCode;
    
    @NotNull
    @Column(name = "UPDATED_DATE", nullable = false)
    private LocalDateTime updatedDate;
    
    @NotBlank
    @Size(max = 500)
    @Column(name = "REMARKS", nullable = false, length = 500)
    private String remarks;
    
    @NotBlank
    @Size(max = 50)
    @Column(name = "REMMITER_ACCOUNT_NO", nullable = false, length = 50)
    private String remmiterAccountNo;
    
    @Size(max = 10)
    @Column(name = "PROCESS_CORE", length = 10)
    private String processCore;
    
    @Column(name = "ERR_LOG", columnDefinition = "TEXT")
    private String errLog;
    
    @NotNull
    @Column(name = "UNDERWRITING_YEAR", nullable = false)
    private Integer underwritingYear;
    
    @NotBlank
    @Size(max = 10)
    @Column(name = "PRODUCT_CODE", nullable = false, length = 10)
    private String productCode = "MTP";
    
    @NotBlank
    @Size(max = 20)
    @Column(name = "CUSTOMER_CODE", nullable = false, length = 20)
    private String customerCode;
    
    @NotBlank
    @Size(max = 255)
    @Column(name = "CUSTOMER_NAME", nullable = false)
    private String customerName;
    
    @Size(max = 20)
    @Column(name = "AGENT_CODE", length = 20)
    private String agentCode;
    
    @NotNull
    @Column(name = "POLICY_START_DATE", nullable = false)
    private LocalDate policyStartDate;
    
    @NotNull
    @Column(name = "POLICY_END_DATE", nullable = false)
    private LocalDate policyEndDate;
    
    @NotNull
    @Positive
    @Column(name = "BASIC_TP_COVER_PREMIUM", nullable = false, precision = 15, scale = 2)
    private BigDecimal basicTpCoverPremium;
    
    @NotNull
    @Positive
    @Column(name = "PREMIUM_OWNER_DRIVER", nullable = false, precision = 15, scale = 2)
    private BigDecimal premiumOwnerDriver;
    
    @Positive
    @Column(name = "PREMIUM_HELPER", precision = 15, scale = 2)
    private BigDecimal premiumHelper;
    
    @Positive
    @Column(name = "PREMIUM_PASSENGER", precision = 15, scale = 2)
    private BigDecimal premiumPassenger;
    
    @NotNull
    @Positive
    @Column(name = "TOTAL_PREMIUM_AMOUNT", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalPremiumAmount;
    
    @NotBlank
    @Size(max = 10)
    @Column(name = "INSURED_TYPE", nullable = false, length = 10)
    private String insuredType = "I";
    
    @NotNull
    @Pattern(regexp = "[YN]", message = "RICB_EMPLOYEE must be Y or N")
    @Column(name = "RICB_EMPLOYEE", nullable = false, length = 1)
    private String ricbEmployee;
    
    @NotBlank
    @Size(max = 255)
    @Column(name = "QUOTA_HOLDER_NAME", nullable = false)
    private String quotaHolderName;
    
    @Size(max = 50)
    @Column(name = "QUOTA_NUMBER", length = 50)
    private String quotaNumber;
    
    @Column(name = "QUOTA_DATE")
    private LocalDate quotaDate;
    
    @NotNull
    @Positive
    @Column(name = "TP_LIABILITY", nullable = false, precision = 15, scale = 2)
    private BigDecimal tpLiability;
    
    @NotNull
    @Pattern(regexp = "[YN]", message = "IMPORTED must be Y or N")
    @Column(name = "IMPORTED", nullable = false, length = 1)
    private String imported;
    
    @NotBlank
    @Size(max = 20)
    @Column(name = "MODEL", nullable = false, length = 20)
    private String model;
    
    @NotBlank
    @Size(max = 7)
    @Column(name = "MONTH_YEAR", nullable = false, length = 7)
    private String monthYear;
    
    @NotNull
    @Column(name = "CUBIC_CAPACITY", nullable = false)
    private Integer cubicCapacity;
    
    @NotBlank
    @Size(max = 50)
    @Column(name = "ENGINE_NO", nullable = false, length = 50)
    private String engineNo;
    
    @NotBlank
    @Size(max = 50)
    @Column(name = "CHASSIS_NO", nullable = false, length = 50)
    private String chassisNo;
    
    @NotBlank
    @Size(max = 20)
    @Column(name = "REGISTRATION_NO", nullable = false, length = 20)
    private String registrationNo;
    
    @NotNull
    @Min(1) @Max(10)
    @Column(name = "VEHICLE_TYPE", nullable = false)
    private Integer vehicleType;
    
    @NotNull
    @Pattern(regexp = "[CDEP]", message = "FUEL_TYPE must be C, D, E, or P")
    @Column(name = "FUEL_TYPE", nullable = false, length = 1)
    private String fuelType;
    
    @NotNull
    @Column(name = "PASSENGER_CAPACITY", nullable = false)
    private Integer passengerCapacity;
    
    @Column(name = "WEIGHT", precision = 10, scale = 2)
    private BigDecimal weight;
    
    @NotNull
    @Column(name = "PURCHASE_DATE", nullable = false)
    private LocalDate purchaseDate;
    
    @NotNull
    @Column(name = "REGISTRATION_DATE", nullable = false)
    private LocalDate registrationDate;
    
    @Size(max = 255)
    @Column(name = "DRIVER_NAME")
    private String driverName;
    
    @Size(max = 20)
    @Column(name = "DRIVER_CID", length = 20)
    private String driverCid;
    
    @Column(name = "DRIVER_DOB")
    private LocalDate driverDob;
    
    @NotNull
    @Min(1) @Max(7)
    @Column(name = "VEHICLE_CATEGORY", nullable = false)
    private Integer vehicleCategory;
    
    @NotBlank
    @Size(max = 20)
    @Column(name = "CUSTOMER_CID", nullable = false, length = 20)
    private String customerCid;
    
    @Positive
    @Column(name = "GST_PREMIUM", precision = 15, scale = 2)
    private BigDecimal gstPremium;
    
    
    // Constructors
    public MtpNewPolicy() {}
    
    // Getters and Setters
    public Integer getSerialNo() { return serialNo; }
    public void setSerialNo(Integer serialNo) { this.serialNo = serialNo; }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    
    public LocalDate getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDate transactionDate) { this.transactionDate = transactionDate; }
    
    public String getRemitterName() { return remitterName; }
    public void setRemitterName(String remitterName) { this.remitterName = remitterName; }
    
    public String getRemitterBank() { return remitterBank; }
    public void setRemitterBank(String remitterBank) { this.remitterBank = remitterBank; }
    
    public String getRemitterMobileNo() { return remitterMobileNo; }
    public void setRemitterMobileNo(String remitterMobileNo) { this.remitterMobileNo = remitterMobileNo; }
    
    public String getTransactionStatus() { return transactionStatus; }
    public void setTransactionStatus(String transactionStatus) { this.transactionStatus = transactionStatus; }
    
    public String getDataSource() { return dataSource; }
    public void setDataSource(String dataSource) { this.dataSource = dataSource; }
    
    public String getAuthCode() { return authCode; }
    public void setAuthCode(String authCode) { this.authCode = authCode; }
    
    public String getAuthId() { return authId; }
    public void setAuthId(String authId) { this.authId = authId; }
    
    public String getResponseCode() { return responseCode; }
    public void setResponseCode(String responseCode) { this.responseCode = responseCode; }
    
    public LocalDateTime getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(LocalDateTime updatedDate) { this.updatedDate = updatedDate; }
    
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    
    public String getRemmiterAccountNo() { return remmiterAccountNo; }
    public void setRemmiterAccountNo(String remmiterAccountNo) { this.remmiterAccountNo = remmiterAccountNo; }
    
    public String getProcessCore() { return processCore; }
    public void setProcessCore(String processCore) { this.processCore = processCore; }
    
    public String getErrLog() { return errLog; }
    public void setErrLog(String errLog) { this.errLog = errLog; }
    
    public Integer getUnderwritingYear() { return underwritingYear; }
    public void setUnderwritingYear(Integer underwritingYear) { this.underwritingYear = underwritingYear; }
    
    public String getProductCode() { return productCode; }
    public void setProductCode(String productCode) { this.productCode = productCode; }
    
    public String getCustomerCode() { return customerCode; }
    public void setCustomerCode(String customerCode) { this.customerCode = customerCode; }
    
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    
    public String getAgentCode() { return agentCode; }
    public void setAgentCode(String agentCode) { this.agentCode = agentCode; }
    
    public LocalDate getPolicyStartDate() { return policyStartDate; }
    public void setPolicyStartDate(LocalDate policyStartDate) { this.policyStartDate = policyStartDate; }
    
    public LocalDate getPolicyEndDate() { return policyEndDate; }
    public void setPolicyEndDate(LocalDate policyEndDate) { this.policyEndDate = policyEndDate; }
    
    public BigDecimal getBasicTpCoverPremium() { return basicTpCoverPremium; }
    public void setBasicTpCoverPremium(BigDecimal basicTpCoverPremium) { this.basicTpCoverPremium = basicTpCoverPremium; }
    
    public BigDecimal getPremiumOwnerDriver() { return premiumOwnerDriver; }
    public void setPremiumOwnerDriver(BigDecimal premiumOwnerDriver) { this.premiumOwnerDriver = premiumOwnerDriver; }
    
    public BigDecimal getPremiumHelper() { return premiumHelper; }
    public void setPremiumHelper(BigDecimal premiumHelper) { this.premiumHelper = premiumHelper; }
    
    public BigDecimal getPremiumPassenger() { return premiumPassenger; }
    public void setPremiumPassenger(BigDecimal premiumPassenger) { this.premiumPassenger = premiumPassenger; }
    
    public BigDecimal getGstPremium() { return gstPremium; }
    public void setGstPremium(BigDecimal gstPremium) { this.gstPremium = gstPremium; }
    
    public BigDecimal getTotalPremiumAmount() { return totalPremiumAmount; }
    public void setTotalPremiumAmount(BigDecimal totalPremiumAmount) { this.totalPremiumAmount = totalPremiumAmount; }
    
    public String getInsuredType() { return insuredType; }
    public void setInsuredType(String insuredType) { this.insuredType = insuredType; }
    
    public String getRicbEmployee() { return ricbEmployee; }
    public void setRicbEmployee(String ricbEmployee) { this.ricbEmployee = ricbEmployee; }
    
    public String getQuotaHolderName() { return quotaHolderName; }
    public void setQuotaHolderName(String quotaHolderName) { this.quotaHolderName = quotaHolderName; }
    
    public String getQuotaNumber() { return quotaNumber; }
    public void setQuotaNumber(String quotaNumber) { this.quotaNumber = quotaNumber; }
    
    public LocalDate getQuotaDate() { return quotaDate; }
    public void setQuotaDate(LocalDate quotaDate) { this.quotaDate = quotaDate; }
    
    public BigDecimal getTpLiability() { return tpLiability; }
    public void setTpLiability(BigDecimal tpLiability) { this.tpLiability = tpLiability; }
    
    public String getImported() { return imported; }
    public void setImported(String imported) { this.imported = imported; }
    
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    
    public String getMonthYear() { return monthYear; }
    public void setMonthYear(String monthYear) { this.monthYear = monthYear; }
    
    public Integer getCubicCapacity() { return cubicCapacity; }
    public void setCubicCapacity(Integer cubicCapacity) { this.cubicCapacity = cubicCapacity; }
    
    public String getEngineNo() { return engineNo; }
    public void setEngineNo(String engineNo) { this.engineNo = engineNo; }
    
    public String getChassisNo() { return chassisNo; }
    public void setChassisNo(String chassisNo) { this.chassisNo = chassisNo; }
    
    public String getRegistrationNo() { return registrationNo; }
    public void setRegistrationNo(String registrationNo) { this.registrationNo = registrationNo; }
    
    public Integer getVehicleType() { return vehicleType; }
    public void setVehicleType(Integer vehicleType) { this.vehicleType = vehicleType; }
    
    public String getFuelType() { return fuelType; }
    public void setFuelType(String fuelType) { this.fuelType = fuelType; }
    
    public Integer getPassengerCapacity() { return passengerCapacity; }
    public void setPassengerCapacity(Integer passengerCapacity) { this.passengerCapacity = passengerCapacity; }
    
    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }
    
    public LocalDate getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; }
    
    public LocalDate getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDate registrationDate) { this.registrationDate = registrationDate; }
    
    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }
    
    public String getDriverCid() { return driverCid; }
    public void setDriverCid(String driverCid) { this.driverCid = driverCid; }
    
    public LocalDate getDriverDob() { return driverDob; }
    public void setDriverDob(LocalDate driverDob) { this.driverDob = driverDob; }
    
    public Integer getVehicleCategory() { return vehicleCategory; }
    public void setVehicleCategory(Integer vehicleCategory) { this.vehicleCategory = vehicleCategory; }
    
    public String getCustomerCid() { return customerCid; }
    public void setCustomerCid(String customerCid) { this.customerCid = customerCid; }
}