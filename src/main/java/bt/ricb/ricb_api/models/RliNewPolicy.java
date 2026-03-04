package bt.ricb.ricb_api.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Positive;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "TL_LI_TR_RURAL_2026_TRN", indexes = {
    @Index(name = "IDX_TRANSACTION_ID", columnList = "TRANSACTION_ID"),
    @Index(name = "IDX_TRANSACTION_DATE", columnList = "TRANSACTION_DATE")
})
public class RliNewPolicy {

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
    private String underwritingYear;

    @NotBlank
    @Column(name = "PRESENT_HOUSEHOLD_NO", nullable = false, length = 10)
    private String householdNo;   // ✅ renamed for consistency

    @NotBlank
    @Column(name = "NO_OF_MEMBERS", nullable = false, length = 10)
    private String noOfEligibleMembers;

    @NotBlank
    @Column(name = "SUBSIDY_TAKEN", nullable = false, length = 10)
    private String subsidyInd;

    @NotNull
    @Positive
    @Column(name = "AMOUNT_PAID", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalPremiumAmount;

    @NotBlank
    @Size(max = 20)
    @Column(name = "CUSTOMER_CID", nullable = false, length = 20)
    private String customerCid;

    @NotBlank
    @Column(name = "CONTACT_PERSON_NAME", nullable = false, length = 10)
    private String contactPersonName;

    @NotBlank
    @Column(name = "CONTACT_PERSON_NO", nullable = false, length = 10)
    private String contactPersonNo;

    // Constructors
    public RliNewPolicy() {}

    // ✅ Correct JavaBean Getters & Setters

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

    public String getUnderwritingYear() { return underwritingYear; }
    public void setUnderwritingYear(String underwritingYear) { this.underwritingYear = underwritingYear; }

    public String getHouseholdNo() { return householdNo; }
    public void setHouseholdNo(String householdNo) { this.householdNo = householdNo; }

    public String getnoOfEligibleMembers() { return noOfEligibleMembers; }
    public void setNoOfEligibleMembers(String noOfEligibleMembers) { this.noOfEligibleMembers = noOfEligibleMembers; }

    public BigDecimal getTotalPremiumAmount() { return totalPremiumAmount; }
    public void setTotalPremiumAmount(BigDecimal totalPremiumAmount) { this.totalPremiumAmount = totalPremiumAmount; }

    public String getCustomerCid() { return customerCid; }
    public void setCustomerCid(String customerCid) { this.customerCid = customerCid; }

    public String getSubsidyInd() { return subsidyInd; }
    public void setSubsidyInd(String subsidyInd) { this.subsidyInd = subsidyInd; }

    public String getContactPersonName() { return contactPersonName; }
    public void setContactPersonName(String contactPersonName) { this.contactPersonName = contactPersonName; }

    public String getContactPersonNo() { return contactPersonNo; }
    public void setContactPersonNo(String contactPersonNo) { this.contactPersonNo = contactPersonNo; }
}
