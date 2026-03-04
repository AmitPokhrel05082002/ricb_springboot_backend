package bt.ricb.ricb_api.models;

public class AddressDto {
	private String address_type_id;
	private String mailing_address;
	private String country_id;
	private String dzongkhag_id;
	private String dungkhag_id;
	private String gewog_id;
	private String village_id;

	public String getAddress_type_id() {
		return this.address_type_id;
	}

	public void setAddress_type_id(String address_type_id) {
		this.address_type_id = address_type_id;
	}

	public String getMailing_address() {
		return this.mailing_address;
	}

	public void setMailing_address(String mailing_address) {
		this.mailing_address = mailing_address;
	}

	public String getCountry_id() {
		return this.country_id;
	}

	public void setCountry_id(String country_id) {
		this.country_id = country_id;
	}

	public String getDzongkhag_id() {
		return this.dzongkhag_id;
	}

	public void setDzongkhag_id(String dzongkhag_id) {
		this.dzongkhag_id = dzongkhag_id;
	}

	public String getDungkhag_id() {
		return this.dungkhag_id;
	}

	public void setDungkhag_id(String dungkhag_id) {
		this.dungkhag_id = dungkhag_id;
	}

	public String getGewog_id() {
		return this.gewog_id;
	}

	public void setGewog_id(String gewog_id) {
		this.gewog_id = gewog_id;
	}

	public String getVillage_id() {
		return this.village_id;
	}

	public void setVillage_id(String village_id) {
		this.village_id = village_id;
	}
}