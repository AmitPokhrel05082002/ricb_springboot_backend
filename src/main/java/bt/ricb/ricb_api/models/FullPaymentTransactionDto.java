package bt.ricb.ricb_api.models;

public class FullPaymentTransactionDto {
	 private String customerCid;
	    private String customerName;
	    private String departmentCode;
	    private String policyAccountNo;
	    private String amountPaid;
	    private String orderReferenceNo;
	    private String transactionId;
	    private String transactionDate;
	    private String remitterName;
	    private String remitterBank;
	    private String remitterMobileNo;
	    private String transactionStatus;
	    private String dataSource;
	    private String authCode;
	    private String authId;
	    private String createdDate;
	    private String remitterCid;
	    private Long requestId;

	    // Constructors
	    public FullPaymentTransactionDto() {
	    }

	    public FullPaymentTransactionDto(String customerCid, String customerName, String departmentCode, 
	                               String policyAccountNo, String amountPaid, String orderReferenceNo, 
	                               String transactionId, String transactionDate, String remitterName, 
	                               String remitterBank, String remitterMobileNo, String transactionStatus, 
	                               String dataSource, String authCode, String authId, String createdDate, 
	                               String remitterCid, Long requestId) {
	        this.customerCid = customerCid;
	        this.customerName = customerName;
	        this.departmentCode = departmentCode;
	        this.policyAccountNo = policyAccountNo;
	        this.amountPaid = amountPaid;
	        this.orderReferenceNo = orderReferenceNo;
	        this.transactionId = transactionId;
	        this.transactionDate = transactionDate;
	        this.remitterName = remitterName;
	        this.remitterBank = remitterBank;
	        this.remitterMobileNo = remitterMobileNo;
	        this.transactionStatus = transactionStatus;
	        this.dataSource = dataSource;
	        this.authCode = authCode;
	        this.authId = authId;
	        this.createdDate = createdDate;
	        this.remitterCid = remitterCid;
	        this.requestId = requestId;
	    }

	    // Getters and Setters
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

	    public String getDepartmentCode() {
	        return departmentCode;
	    }

	    public void setDepartmentCode(String departmentCode) {
	        this.departmentCode = departmentCode;
	    }

	    public String getPolicyAccountNo() {
	        return policyAccountNo;
	    }

	    public void setPolicyAccountNo(String policyAccountNo) {
	        this.policyAccountNo = policyAccountNo;
	    }

	    public String getAmountPaid() {
	        return amountPaid;
	    }

	    public void setAmountPaid(String amountPaid) {
	        this.amountPaid = amountPaid;
	    }

	    public String getOrderReferenceNo() {
	        return orderReferenceNo;
	    }

	    public void setOrderReferenceNo(String orderReferenceNo) {
	        this.orderReferenceNo = orderReferenceNo;
	    }

	    public String getTransactionId() {
	        return transactionId;
	    }

	    public void setTransactionId(String transactionId) {
	        this.transactionId = transactionId;
	    }

	    public String getTransactionDate() {
	        return transactionDate;
	    }

	    public void setTransactionDate(String transactionDate) {
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

	    public String getCreatedDate() {
	        return createdDate;
	    }

	    public void setCreatedDate(String createdDate) {
	        this.createdDate = createdDate;
	    }

	    public String getRemitterCid() {
	        return remitterCid;
	    }

	    public void setRemitterCid(String remitterCid) {
	        this.remitterCid = remitterCid;
	    }

	    public Long getRequestId() {
	        return requestId;
	    }

	    public void setRequestId(Long requestId) {
	        this.requestId = requestId;
	    }
}
