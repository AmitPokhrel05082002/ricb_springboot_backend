package bt.ricb.ricb_api.models;

import bt.ricb.ricb_api.models.AddressDto;
import bt.ricb.ricb_api.models.BankDetailsDto;

import java.util.List;

public class CcdbCustomerDto {

    private String name;
    private int nationality_id;
    private String cid;
    private int salutation_id;
    private String gender_id;
    private int occupation_id;
    private String dob;
    private int age;
    private String mobile_no;
    private String alternative_mobile_no;
    private String telephone_no;
    private String email_id;
    private String alternative_email;
    private String thram_no;
    private String house_no;
    private String household_no;
    private int marital_status;
    private String tpn;
    private int department_id;
    private List<AddressDto> address_details;
    private List<BankDetailsDto> bankdetails;

    // ================= GETTERS & SETTERS =================

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNationality_id() {
        return nationality_id;
    }

    public void setNationality_id(int nationality_id) {
        this.nationality_id = nationality_id;
    }

    public int getSalutation_id() {
        return salutation_id;
    }

    public void setSalutation_id(int salutation_id) {
        this.salutation_id = salutation_id;
    }

    public int getMarital_status() {
        return marital_status;
    }

    public void setMarital_status(int marital_status) {
        this.marital_status = marital_status;
    }

    public String getGender_id() {
        return gender_id;
    }

    public void setGender_id(String gender_id) {
        this.gender_id = gender_id;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public int getOccupation_id() {
        return occupation_id;
    }

    public void setOccupation_id(int occupation_id) {
        this.occupation_id = occupation_id;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getTpn() {
        return tpn;
    }

    public void setTpn(String tpn) {
        this.tpn = tpn;
    }

    public int getDepartment_id() {
        return department_id;
    }

    public void setDepartment_id(int department_id) {
        this.department_id = department_id;
    }

    public String getMobile_no() {
        return mobile_no;
    }

    public void setMobile_no(String mobile_no) {
        this.mobile_no = mobile_no;
    }

    public String getAlternative_mobile_no() {
        return alternative_mobile_no;
    }

    public void setAlternative_mobile_no(String alternative_mobile_no) {
        this.alternative_mobile_no = alternative_mobile_no;
    }

    public String getTelephone_no() {
        return telephone_no;
    }

    public void setTelephone_no(String telephone_no) {
        this.telephone_no = telephone_no;
    }

    public String getEmail_id() {
        return email_id;
    }

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }

    public String getAlternative_email() {
        return alternative_email;
    }

    public void setAlternative_email(String alternative_email) {
        this.alternative_email = alternative_email;
    }

    public String getThram_no() {
        return thram_no;
    }

    public void setThram_no(String thram_no) {
        this.thram_no = thram_no;
    }

    public String getHouse_no() {
        return house_no;
    }

    public void setHouse_no(String house_no) {
        this.house_no = house_no;
    }

    public String getHousehold_no() {
        return household_no;
    }

    public void setHousehold_no(String household_no) {
        this.household_no = household_no;
    }

    public List<BankDetailsDto> getBankdetails() {
        return bankdetails;
    }

    public void setBankdetails(List<BankDetailsDto> bankdetails) {
        this.bankdetails = bankdetails;
    }

    // 🔥 LIST GETTER/SETTER
    public List<AddressDto> getAddress_details() {
        return address_details;
    }

    public void setAddress_details(List<AddressDto> address_details) {
        this.address_details = address_details;
    }
}