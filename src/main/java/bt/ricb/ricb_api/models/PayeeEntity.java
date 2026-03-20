package bt.ricb.ricb_api.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payees")
public class PayeeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="claimant_id", nullable = false)
    private Integer claimantId;

    @Column(name="same_as_claimant", nullable = false)
    private Integer sameAsClaimant;

    @Column(nullable = false, length = 11)
    private String cid;

    @Column(name="account_holder_name", nullable = false)
    private String accountHolderName;

    @Column(name="bank_id", nullable = false)
    private Integer bankId;

    @Column(name="account_number", nullable = false)
    private String accountNumber;

    @Column(name="mobile_number", nullable = false)
    private String mobileNumber;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    @Column(name="updated_at")
    private LocalDateTime updatedAt;

    public PayeeEntity() {}

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getClaimantId() { return claimantId; }
    public void setClaimantId(Integer claimantId) { this.claimantId = claimantId; }

    public Integer getSameAsClaimant() { return sameAsClaimant; }
    public void setSameAsClaimant(Integer sameAsClaimant) { this.sameAsClaimant = sameAsClaimant; }

    public String getCid() { return cid; }
    public void setCid(String cid) { this.cid = cid; }

    public String getAccountHolderName() { return accountHolderName; }
    public void setAccountHolderName(String accountHolderName) { this.accountHolderName = accountHolderName; }

    public Integer getBankId() { return bankId; }
    public void setBankId(Integer bankId) { this.bankId = bankId; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}