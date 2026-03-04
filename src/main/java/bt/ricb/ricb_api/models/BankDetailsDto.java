package bt.ricb.ricb_api.models;

public class BankDetailsDto {
	private String bank_id;
	private String bank_name;
	private String account_no;
	private String account_type_id;

	public String getBank_id() {
		return this.bank_id;
	}

	public void setBank_id(String bank_id) {
		this.bank_id = bank_id;
	}

	public String getBank_name() {
		return this.bank_name;
	}

	public void setBank_name(String bank_name) {
		this.bank_name = bank_name;
	}

	public String getAccount_no() {
		return this.account_no;
	}

	public void setAccount_no(String account_no) {
		this.account_no = account_no;
	}

	public String getAccount_type_id() {
		return this.account_type_id;
	}

	public void setAccount_type_id(String account_type_id) {
		this.account_type_id = account_type_id;
	}
}
