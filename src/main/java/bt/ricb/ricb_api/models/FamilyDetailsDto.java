package bt.ricb.ricb_api.models;

import java.sql.Date;

public class FamilyDetailsDto {
	private Long policySerial;
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
	private String address3;
	private String gewog;
	private String dzongkhag;
	private String mobileNo;
	private String status;
	private Integer age;
	private String relationship;
	private String yearOfDeath;
	private String causeOfDeath;

	public Long getPolicySerial() {
		return this.policySerial;
	}

	public void setPolicySerial(Long policySerial) {
		this.policySerial = policySerial;
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

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getAge() {
		return this.age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getRelationship() {
		return this.relationship;
	}

	public void setRelationship(String relationship) {
		this.relationship = relationship;
	}

	public String getYearOfDeath() {
		return this.yearOfDeath;
	}

	public void setYearOfDeath(String yearOfDeath) {
		this.yearOfDeath = yearOfDeath;
	}

	public String getCauseOfDeath() {
		return this.causeOfDeath;
	}

	public void setCauseOfDeath(String causeOfDeath) {
		this.causeOfDeath = causeOfDeath;
	}
}
