package bt.ricb.ricb_api.models;

import java.sql.Date;

public class NomineeDto {
	private Long policySerial;
	private Double benefitSharePercent;
	private String firstName;
	private String middleName;
	private String lastName;
	private String customerName;
	private String gender;
	private Date dateOfBirth;
	private String citizenId;
	private String proposerRelation;
	private String address1;
	private String address2;

	public Long getPolicySerial() {
		return this.policySerial;
	}

	private String address3;
	private String gewog;
	private String dzongkhag;
	private String mobileNo;
	private String appointeeName;
	private String appointeeAddress1;
	private String appointeeAddress2;
	private String appointeeAddress3;
	private String appointeeGewog;
	private String appointeeDzongkhag;
	private String appointeeMobile;
	private String appointeeEmailId;
	private String appointeeCid;

	public void setPolicySerial(Long policySerial) {
		this.policySerial = policySerial;
	}

	public Double getBenefitSharePercent() {
		return this.benefitSharePercent;
	}

	public void setBenefitSharePercent(Double benefitSharePercent) {
		this.benefitSharePercent = benefitSharePercent;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return this.middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getCustomerName() {
		return this.customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getGender() {
		return this.gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Date getDateOfBirth() {
		return this.dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getCitizenId() {
		return this.citizenId;
	}

	public void setCitizenId(String citizenId) {
		this.citizenId = citizenId;
	}

	public String getProposerRelation() {
		return this.proposerRelation;
	}

	public void setProposerRelation(String proposerRelation) {
		this.proposerRelation = proposerRelation;
	}

	public String getAddress1() {
		return this.address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return this.address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getAddress3() {
		return this.address3;
	}

	public void setAddress3(String address3) {
		this.address3 = address3;
	}

	public String getGewog() {
		return this.gewog;
	}

	public void setGewog(String gewog) {
		this.gewog = gewog;
	}

	public String getDzongkhag() {
		return this.dzongkhag;
	}

	public void setDzongkhag(String dzongkhag) {
		this.dzongkhag = dzongkhag;
	}

	public String getMobileNo() {
		return this.mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getAppointeeName() {
		return this.appointeeName;
	}

	public void setAppointeeName(String appointeeName) {
		this.appointeeName = appointeeName;
	}

	public String getAppointeeAddress1() {
		return this.appointeeAddress1;
	}

	public void setAppointeeAddress1(String appointeeAddress1) {
		this.appointeeAddress1 = appointeeAddress1;
	}

	public String getAppointeeAddress2() {
		return this.appointeeAddress2;
	}

	public void setAppointeeAddress2(String appointeeAddress2) {
		this.appointeeAddress2 = appointeeAddress2;
	}

	public String getAppointeeAddress3() {
		return this.appointeeAddress3;
	}

	public void setAppointeeAddress3(String appointeeAddress3) {
		this.appointeeAddress3 = appointeeAddress3;
	}

	public String getAppointeeGewog() {
		return this.appointeeGewog;
	}

	public void setAppointeeGewog(String appointeeGewog) {
		this.appointeeGewog = appointeeGewog;
	}

	public String getAppointeeDzongkhag() {
		return this.appointeeDzongkhag;
	}

	public void setAppointeeDzongkhag(String appointeeDzongkhag) {
		this.appointeeDzongkhag = appointeeDzongkhag;
	}

	public String getAppointeeMobile() {
		return this.appointeeMobile;
	}

	public void setAppointeeMobile(String appointeeMobile) {
		this.appointeeMobile = appointeeMobile;
	}

	public String getAppointeeEmailId() {
		return this.appointeeEmailId;
	}

	public void setAppointeeEmailId(String appointeeEmailId) {
		this.appointeeEmailId = appointeeEmailId;
	}

	public String getAppointeeCid() {
		return this.appointeeCid;
	}

	public void setAppointeeCid(String appointeeCid) {
		this.appointeeCid = appointeeCid;
	}
}