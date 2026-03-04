package bt.ricb.ricb_api.models;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "MOBAPP_DTI_NEW_POLICY_DTL")
public class PolicyDTIDetailsEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
    @Column(name = "POL_SERIAL_NO")
    private Integer polSerialNo;
    
    @Column(name = "COUPON_NO", length = 200)
    private String couponNo;
    
    @Column(name = "ASSURED_NAME", length = 200)
    private String assuredName;
    
    @Column(name = "CITIZEN_ID", length = 100)
    private String citizenId;
    
    @Column(name = "DATE_OF_BIRTH")
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;
    
    @Column(name = "GENDER", length = 1)
    private String gender;
    
    @Column(name = "PHONE_NO", length = 20)
    private String phoneNo;
    
    @Column(name = "NOMINEE_NAME", length = 500)
    private String nomineeName;
    
    @Column(name = "RELATION", length = 50)
    private String relation;
    
    @Column(name = "NOMINEE_ADDRESS", length = 500)
    private String nomineeAddress;
    
    @Column(name = "NOMINEE_CID", length = 50)
    private String nomineeCid;
    
    @Column(name = "NO_OF_PASSENGER")
    private Integer noOfPassenger;
    
    @Column(name = "INDIVIDUAL_SUM_INSURED", precision = 15, scale = 2)
    private BigDecimal individualSumInsured;
    
    @Column(name = "PREMIUM_AMOUNT", precision = 15, scale = 2)
    private BigDecimal premiumAmount;
    
    @Column(name = "TOTAL_PREMIUM_AMOUNT", precision = 15, scale = 2)
    private BigDecimal totalPremiumAmount;

    // Getters and Setters
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
