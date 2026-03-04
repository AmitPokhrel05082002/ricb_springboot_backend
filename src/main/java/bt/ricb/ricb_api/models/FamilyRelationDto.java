package bt.ricb.ricb_api.models;

public class FamilyRelationDto {
	private String name;
	private String relationship_id;

	public String getName() {
		return this.name;
	}

	private String nationality_id;
	private String cid_no;
	private String contact_no;

	public void setName(String name) {
		this.name = name;
	}

	public String getRelationship_id() {
		return this.relationship_id;
	}

	public void setRelationship_id(String relationship_id) {
		this.relationship_id = relationship_id;
	}

	public String getNationality_id() {
		return this.nationality_id;
	}

	public void setNationality_id(String nationality_id) {
		this.nationality_id = nationality_id;
	}

	public String getCid_no() {
		return this.cid_no;
	}

	public void setCid_no(String cid_no) {
		this.cid_no = cid_no;
	}

	public String getContact_no() {
		return this.contact_no;
	}

	public void setContact_no(String contact_no) {
		this.contact_no = contact_no;
	}
}