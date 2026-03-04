package bt.ricb.ricb_api.dao;

import java.math.BigDecimal;
import java.util.Date;

public class PolicyDTIDetailDto {
	private Integer polSerialNo;
    private String couponNo;
    private String assuredName;
    private String citizenId;
    private Date dateOfBirth;
    private String gender;
    private String phoneNo;
    private String nomineeName;
    private String relation;
    private String nomineeAddress;
    private String nomineeCid;
    private Integer noOfPassenger;
    private BigDecimal individualSumInsured;
    private BigDecimal premiumAmount;
    private BigDecimal totalPremiumAmount;
    
	public Integer getPolSerialNo() {
		return polSerialNo;
	}
	public void setPolSerialNo(Integer polSerialNo) {
		this.polSerialNo = polSerialNo;
	}
	public String getCouponNo() {
		return couponNo;
	}
	public void setCouponNo(String couponNo) {
		this.couponNo = couponNo;
	}
	public String getAssuredName() {
		return assuredName;
	}
	public void setAssuredName(String assuredName) {
		this.assuredName = assuredName;
	}
	public String getCitizenId() {
		return citizenId;
	}
	public void setCitizenId(String citizenId) {
		this.citizenId = citizenId;
	}
	public Date getDateOfBirth() {
		return dateOfBirth;
	}
	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getPhoneNo() {
		return phoneNo;
	}
	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}
	public String getNomineeName() {
		return nomineeName;
	}
	public void setNomineeName(String nomineeName) {
		this.nomineeName = nomineeName;
	}
	public String getRelation() {
		return relation;
	}
	public void setRelation(String relation) {
		this.relation = relation;
	}
	public String getNomineeAddress() {
		return nomineeAddress;
	}
	public void setNomineeAddress(String nomineeAddress) {
		this.nomineeAddress = nomineeAddress;
	}
	public String getNomineeCid() {
		return nomineeCid;
	}
	public void setNomineeCid(String nomineeCid) {
		this.nomineeCid = nomineeCid;
	}
	public Integer getNoOfPassenger() {
		return noOfPassenger;
	}
	public void setNoOfPassenger(Integer noOfPassenger) {
		this.noOfPassenger = noOfPassenger;
	}
	public BigDecimal getIndividualSumInsured() {
		return individualSumInsured;
	}
	public void setIndividualSumInsured(BigDecimal individualSumInsured) {
		this.individualSumInsured = individualSumInsured;
	}
	public BigDecimal getPremiumAmount() {
		return premiumAmount;
	}
	public void setPremiumAmount(BigDecimal premiumAmount) {
		this.premiumAmount = premiumAmount;
	}
	public BigDecimal getTotalPremiumAmount() {
		return totalPremiumAmount;
	}
	public void setTotalPremiumAmount(BigDecimal totalPremiumAmount) {
		this.totalPremiumAmount = totalPremiumAmount;
	}
    
    

}
