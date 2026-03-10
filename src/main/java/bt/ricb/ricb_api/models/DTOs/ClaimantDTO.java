package bt.ricb.ricb_api.models.DTOs;

public class ClaimantDTO {

    private String cid;
    private String fullName;
    private String mobileNumber;
    private String emailAddress;
    private String dzongkhagName;  // frontend provides names
    private String gewogName;
    private String villageName;

    // Getters and Setters
    public String getCid() { return cid; }
    public void setCid(String cid) { this.cid = cid; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }

    public String getEmailAddress() { return emailAddress; }
    public void setEmailAddress(String emailAddress) { this.emailAddress = emailAddress; }

    public String getDzongkhagName() { return dzongkhagName; }
    public void setDzongkhagName(String dzongkhagName) { this.dzongkhagName = dzongkhagName; }

    public String getGewogName() { return gewogName; }
    public void setGewogName(String gewogName) { this.gewogName = gewogName; }

    public String getVillageName() { return villageName; }
    public void setVillageName(String villageName) { this.villageName = villageName; }
}