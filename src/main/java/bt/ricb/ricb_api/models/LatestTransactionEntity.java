package bt.ricb.ricb_api.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "transactions_latest")
public class LatestTransactionEntity {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer ID;
	private Integer REQUEST_ID;
	private String CUSTOMER_CID;
	private String CUSTOMER_NAME;
	private String DEPARTMENT_CODE;
	private String POLICY_ACCOUNT_NO;
	private String AMOUNT_PAID;
	private String ORDER_REFEREENCE_NO;
	private String TRANSACTION_ID;
	private String TRANSACTION_DATE;
	private String REMITTER_NAME;
	

	public Integer getID() {
		return this.ID;
	}

	private String REMITTER_BANK;
	private String REMMITER_ACCOUNT_NO;
	private String REMITTER_MOBILE_NO;
	private String REMITTER_CID;
	private String TRANSACTION_STATUS;
	private String DATA_SOURCE;
	private String AUTH_CODE;
	private String AUTH_ID;
	private String RESPONSE_CODE;
	private String CREATED_DATE;
	@Column(columnDefinition = "TEXT")
	private String remarks;

	public void setID(Integer iD) {
		this.ID = iD;
	}

	public Integer getREQUEST_ID() {
		return this.REQUEST_ID;
	}

	public void setREQUEST_ID(Integer rEQUEST_ID) {
		this.REQUEST_ID = rEQUEST_ID;
	}

	public String getCUSTOMER_CID() {
		return this.CUSTOMER_CID;
	}

	public void setCUSTOMER_CID(String cUSTOMER_CID) {
		this.CUSTOMER_CID = cUSTOMER_CID;
	}

	public String getCUSTOMER_NAME() {
		return this.CUSTOMER_NAME;
	}

	public void setCUSTOMER_NAME(String cUSTOMER_NAME) {
		this.CUSTOMER_NAME = cUSTOMER_NAME;
	}

	public String getDEPARTMENT_CODE() {
		return this.DEPARTMENT_CODE;
	}

	public void setDEPARTMENT_CODE(String dEPARTMENT_CODE) {
		this.DEPARTMENT_CODE = dEPARTMENT_CODE;
	}

	public String getPOLICY_ACCOUNT_NO() {
		return this.POLICY_ACCOUNT_NO;
	}

	public void setPOLICY_ACCOUNT_NO(String pOLICY_ACCOUNT_NO) {
		this.POLICY_ACCOUNT_NO = pOLICY_ACCOUNT_NO;
	}

	public String getAMOUNT_PAID() {
		return this.AMOUNT_PAID;
	}

	public void setAMOUNT_PAID(String aMOUNT_PAID) {
		this.AMOUNT_PAID = aMOUNT_PAID;
	}

	public String getORDER_REFEREENCE_NO() {
		return this.ORDER_REFEREENCE_NO;
	}

	public void setORDER_REFEREENCE_NO(String oRDER_REFEREENCE_NO) {
		this.ORDER_REFEREENCE_NO = oRDER_REFEREENCE_NO;
	}

	public String getTRANSACTION_ID() {
		return this.TRANSACTION_ID;
	}

	public void setTRANSACTION_ID(String tRANSACTION_ID) {
		this.TRANSACTION_ID = tRANSACTION_ID;
	}

	public String getTRANSACTION_DATE() {
		return this.TRANSACTION_DATE;
	}

	public void setTRANSACTION_DATE(String tRANSACTION_DATE) {
		this.TRANSACTION_DATE = tRANSACTION_DATE;
	}

	public String getREMITTER_NAME() {
		return this.REMITTER_NAME;
	}

	public void setREMITTER_NAME(String rEMITTER_NAME) {
		this.REMITTER_NAME = rEMITTER_NAME;
	}

	public String getREMITTER_BANK() {
		return this.REMITTER_BANK;
	}

	public void setREMITTER_BANK(String rEMITTER_BANK) {
		this.REMITTER_BANK = rEMITTER_BANK;
	}

	public String getREMMITER_ACCOUNT_NO() {
		return this.REMMITER_ACCOUNT_NO;
	}

	public void setREMMITER_ACCOUNT_NO(String rEMMITER_ACCOUNT_NO) {
		this.REMMITER_ACCOUNT_NO = rEMMITER_ACCOUNT_NO;
	}

	public String getREMITTER_MOBILE_NO() {
		return this.REMITTER_MOBILE_NO;
	}

	public void setREMITTER_MOBILE_NO(String rEMITTER_MOBILE_NO) {
		this.REMITTER_MOBILE_NO = rEMITTER_MOBILE_NO;
	}

	public String getREMITTER_CID() {
		return this.REMITTER_CID;
	}

	public void setREMITTER_CID(String rEMITTER_CID) {
		this.REMITTER_CID = rEMITTER_CID;
	}

	public String getTRANSACTION_STATUS() {
		return this.TRANSACTION_STATUS;
	}

	public void setTRANSACTION_STATUS(String tRANSACTION_STATUS) {
		this.TRANSACTION_STATUS = tRANSACTION_STATUS;
	}

	public String getDATA_SOURCE() {
		return this.DATA_SOURCE;
	}

	public void setDATA_SOURCE(String dATA_SOURCE) {
		this.DATA_SOURCE = dATA_SOURCE;
	}

	public String getAUTH_CODE() {
		return this.AUTH_CODE;
	}

	public void setAUTH_CODE(String aUTH_CODE) {
		this.AUTH_CODE = aUTH_CODE;
	}

	public String getAUTH_ID() {
		return this.AUTH_ID;
	}

	public void setAUTH_ID(String aUTH_ID) {
		this.AUTH_ID = aUTH_ID;
	}

	public String getRESPONSE_CODE() {
		return this.RESPONSE_CODE;
	}

	public void setRESPONSE_CODE(String rESPONSE_CODE) {
		this.RESPONSE_CODE = rESPONSE_CODE;
	}

	public String getCREATED_DATE() {
		return this.CREATED_DATE;
	}

	public void setCREATED_DATE(String cREATED_DATE) {
		this.CREATED_DATE = cREATED_DATE;
	}

	public String getRemarks() {
		return this.remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
}