package bt.ricb.ricb_api.models;

public class CustomerDto {
	private Integer policyId;
	private String cid;
	private String assuredName;
	private String gender;
	private String phoneNo;
	private String dob;
	private String nomineeCid;
	private String relation;
	private String passportNo;
	private String assigneeName;
	private String sumInsured;

	public Integer getPolicyId() {
		return this.policyId;
	}

	public void setPolicyId(Integer policyId) {
		this.policyId = policyId;
	}

	public String getCid() {
		return this.cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getAssuredName() {
		return this.assuredName;
	}

	public void setAssuredName(String assuredName) {
		this.assuredName = assuredName;
	}

	public String getGender() {
		return this.gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getPhoneNo() {
		return this.phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getDob() {
		return this.dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getNomineeCid() {
		return this.nomineeCid;
	}

	public void setNomineeCid(String nomineeCid) {
		this.nomineeCid = nomineeCid;
	}

	public String getRelation() {
		return this.relation;
	}

	public void setRelation(String relation) {
		this.relation = relation;
	}

	public String getPassportNo() {
		return this.passportNo;
	}

	public void setPassportNo(String passportNo) {
		this.passportNo = passportNo;
	}

	public String getAssigneeName() {
		return this.assigneeName;
	}

	public void setAssigneeName(String assigneeName) {
		this.assigneeName = assigneeName;
	}

	public String getSumInsured() {
		return this.sumInsured;
	}

	public void setSumInsured(String sumInsured) {
		this.sumInsured = sumInsured;
	}
}