package bt.ricb.ricb_api.models.DTOs;

public class PayeeDTO {

    private String sameAsClaimant; // 1 = Yes, 0 = No
    private String cid;
    private String accountHolderName;
    private String bankName;   // frontend provides name
    private String accountNumber;
    private String mobileNumber;

    // Getters and Setters
    public String getSameAsClaimant() { return sameAsClaimant; }
    public void setSameAsClaimant(String sameAsClaimant) { this.sameAsClaimant = sameAsClaimant; }


    public String getCid() { return cid; }
    public void setCid(String cid) { this.cid = cid; }

    public String getAccountHolderName() { return accountHolderName; }
    public void setAccountHolderName(String accountHolderName) { this.accountHolderName = accountHolderName; }

    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }
}