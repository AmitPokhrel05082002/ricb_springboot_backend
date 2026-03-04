package bt.ricb.ricb_api.models;

import java.math.BigDecimal;
 import java.sql.Date;
 
 public class LifeInsuranceMainDto {
   private Long policySerial;
   private String id;
   private String requestId;
   private String customerCid;
   private String customerName;
   private String departmentCode;
   private BigDecimal amountPaid;
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
   
   public Long getPolicySerial() {
     return this.policySerial;
   }
   public void setPolicySerial(Long policySerial) {
     this.policySerial = policySerial;
   }
   public String getId() {
     return this.id;
   }
   public void setId(String id) {
     this.id = id;
   }
   public String getRequestId() {
     return this.requestId;
   }
   public void setRequestId(String requestId) {
     this.requestId = requestId;
   }
   public String getCustomerCid() {
     return this.customerCid;
   }
   public void setCustomerCid(String customerCid) {
     this.customerCid = customerCid;
   }
   public String getCustomerName() {
     return this.customerName;
   }
   public void setCustomerName(String customerName) {
     this.customerName = customerName;
   }
   public String getDepartmentCode() {
     return this.departmentCode;
   }
   public void setDepartmentCode(String departmentCode) {
     this.departmentCode = departmentCode;
   }
   public BigDecimal getAmountPaid() {
     return this.amountPaid;
   }
   public void setAmountPaid(BigDecimal amountPaid) {
     this.amountPaid = amountPaid;
   }
   public String getTransactionId() {
     return this.transactionId;
   }
   public void setTransactionId(String transactionId) {
     this.transactionId = transactionId;
   }
   public Date getTransactionDate() {
     return this.transactionDate;
   }
   public void setTransactionDate(Date transactionDate) {
     this.transactionDate = transactionDate;
   }
   public String getRemitterName() {
     return this.remitterName;
   }
   public void setRemitterName(String remitterName) {
     this.remitterName = remitterName;
   }
   public String getRemitterBank() {
     return this.remitterBank;
   }
   public void setRemitterBank(String remitterBank) {
     this.remitterBank = remitterBank;
   }
   public String getRemitterMobileNo() {
     return this.remitterMobileNo;
   }
   public void setRemitterMobileNo(String remitterMobileNo) {
     this.remitterMobileNo = remitterMobileNo;
   }
   public String getTransactionStatus() {
     return this.transactionStatus;
   }
   public void setTransactionStatus(String transactionStatus) {
     this.transactionStatus = transactionStatus;
   }
   public String getDataSource() {
     return this.dataSource;
   }
   public void setDataSource(String dataSource) {
     this.dataSource = dataSource;
   }
   public String getAuthCode() {
     return this.authCode;
   }
   public void setAuthCode(String authCode) {
     this.authCode = authCode;
   }
   public String getAuthId() {
     return this.authId;
   }
   public void setAuthId(String authId) {
     this.authId = authId;
   }
   public String getResponseCode() {
     return this.responseCode;
   }
   public void setResponseCode(String responseCode) {
     this.responseCode = responseCode;
   }
   public Date getUpdatedDate() {
     return this.updatedDate;
   }
   public void setUpdatedDate(Date updatedDate) {
     this.updatedDate = updatedDate;
   }
   public String getRemarks() {
     return this.remarks;
   }
   public void setRemarks(String remarks) {
     this.remarks = remarks;
   }
   public String getRemitterAccountNo() {
     return this.remitterAccountNo;
   }
   public void setRemitterAccountNo(String remitterAccountNo) {
     this.remitterAccountNo = remitterAccountNo;
   }
   public String getProcessCore() {
     return this.processCore;
   }
   public void setProcessCore(String processCore) {
     this.processCore = processCore;
   }
   public String getErrLog() {
     return this.errLog;
   }
   public void setErrLog(String errLog) {
     this.errLog = errLog;
   }
 }