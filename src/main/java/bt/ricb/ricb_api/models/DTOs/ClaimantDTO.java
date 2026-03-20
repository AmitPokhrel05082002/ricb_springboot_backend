package bt.ricb.ricb_api.models.DTOs;

public class ClaimantDTO {

    private String cid;
    private String fullName;
    private String mobileNumber;
    private String emailAddress;
    private Integer dzongkhagId;  // frontend provides names
    private Integer gewogId;
    private Integer villageId;

    // Getters and Setters
    public String getCid() { return cid; }
    public void setCid(String cid) { this.cid = cid; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }

    public String getEmailAddress() { return emailAddress; }
    public void setEmailAddress(String emailAddress) { this.emailAddress = emailAddress; }

    public Integer getDzongkhagId() { return dzongkhagId; }
    public void setDzongkhagId(Integer dzongkhagId) { this.dzongkhagId = dzongkhagId; }

    public Integer getGewogId() { return gewogId; }
    public void setGewogId(Integer gewogId) { this.gewogId = gewogId; }

    public Integer getVillageId() { return villageId; }
    public void setVillageId(Integer villageId) { this.villageId = villageId; }
}