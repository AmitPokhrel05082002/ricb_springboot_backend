package bt.ricb.ricb_api.models;

public class PaymentTransactionDto {
	private String custCid;
	private String txnId;
	private String remitterName;
	private String remitterBank;
	private String custName;
	private String deptCode;
	private String accNo;
	private String authCode;
	private String orderNo;
	private String dataSource;
	private String actualTxnDate;
	private String policyNo;
	private String amount;
	private String mobileNo;

	public String getTxnId() {
		return this.txnId;
	}

	public void setTxnId(String txnId) {
		this.txnId = txnId;
	}

	public String getRemitterBank() {
		return this.remitterBank;
	}

	public void setRemitterBank(String remitterBank) {
		this.remitterBank = remitterBank;
	}

	public String getAccNo() {
		return this.accNo;
	}

	public void setAccNo(String accNo) {
		this.accNo = accNo;
	}

	public String getAuthCode() {
		return this.authCode;
	}

	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}

	public String getOrderNo() {
		return this.orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getCustCid() {
		return this.custCid;
	}

	public void setCustCid(String custCid) {
		this.custCid = custCid;
	}

	public String getPolicyNo() {
		return this.policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	public String getCustName() {
		return this.custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getDeptCode() {
		return this.deptCode;
	}

	public void setDeptCode(String deptCode) {
		this.deptCode = deptCode;
	}

	public String getAmount() {
		return this.amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getRemitterName() {
		return this.remitterName;
	}

	public void setRemitterName(String remitterName) {
		this.remitterName = remitterName;
	}

	public String getMobileNo() {
		return this.mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getDataSource() {
		return this.dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	public String getActualTxnDate() {
		return this.actualTxnDate;
	}

	public void setActualTxnDate(String actualTxnDate) {
		this.actualTxnDate = actualTxnDate;
	}
	
}