package bt.ricb.ricb_api.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "countries")
public class CountryEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(name = "phone_code")
	private Integer phoneCode;
	@Column(name = "country_code")
	private char countryCode;
	@Column(name = "country_name")
	private String countryName;
	@Column(name = "asian_tag")
	private Integer asianTag;

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getPhoneCode() {
		return this.phoneCode;
	}

	public void setPhoneCode(Integer phoneCode) {
		this.phoneCode = phoneCode;
	}

	public char getCountryCode() {
		return this.countryCode;
	}

	public void setCountryCode(char countryCode) {
		this.countryCode = countryCode;
	}

	public String getCountryName() {
		return this.countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public Integer getAsianTag() {
		return this.asianTag;
	}

	public void setAsianTag(Integer asianTag) {
		this.asianTag = asianTag;
	}
}
