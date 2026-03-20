package bt.ricb.ricb_api.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Table(name = "claims")
public class ClaimEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="claimant_id", nullable = false)
    private Integer claimantId;

    @Column(name="payee_id", nullable = false)
    private Integer payeeId;

    @Column(name="policy_holder_id", nullable = false)
    private Integer policyHolderId;

    @Column(name="nearest_branch_id", nullable = false)
    private Integer nearestBranchId;

    @Column(name="claim_type", nullable = false)
    private String claimType;

    @Column(name="g2c_application_number")
    private String g2cApplicationNumber;

    @Column(name="date_of_death")
    private LocalDate dateOfDeath;

    @Column(name="place_of_death")
    private String placeOfDeath;

    @Column(name="death_type")
    private String deathType;

    @Column(name="cause_of_death")
    private String causeOfDeath;

    @Column(name="date_of_loss")
    private LocalDate dateOfLoss;

    @Column(name="place_of_loss")
    private String placeOfLoss;

    @Column(name="cause_of_loss")
    private String causeOfLoss;

    @Column(name="status", nullable = false)
    private String status;

    @Column(name="remarks")
    private String remarks;

    @Column(name="submitted_at")
    private LocalDateTime submittedAt;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    @Column(name="updated_at")
    private LocalDateTime updatedAt;


    @Column(name="cin", nullable=false, unique=true)
    private String cin;

    public ClaimEntity() {}

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getClaimantId() { return claimantId; }
    public void setClaimantId(Integer claimantId) { this.claimantId = claimantId; }

    public Integer getPayeeId() { return payeeId; }
    public void setPayeeId(Integer payeeId) { this.payeeId = payeeId; }

    public Integer getPolicyHolderId() { return policyHolderId; }
    public void setPolicyHolderId(Integer policyHolderId) { this.policyHolderId = policyHolderId; }

    public Integer getNearestBranchId() { return nearestBranchId; }
    public void setNearestBranchId(Integer nearestBranchId) { this.nearestBranchId = nearestBranchId; }

    public String getClaimType() { return claimType; }
    public void setClaimType(String claimType) { this.claimType = claimType; }

    public String getG2cApplicationNumber() { return g2cApplicationNumber; }
    public void setG2cApplicationNumber(String g2cApplicationNumber) { this.g2cApplicationNumber = g2cApplicationNumber; }

    public LocalDate getDateOfDeath() { return dateOfDeath; }
    public void setDateOfDeath(LocalDate dateOfDeath) { this.dateOfDeath = dateOfDeath; }

    public String getPlaceOfDeath() { return placeOfDeath; }
    public void setPlaceOfDeath(String placeOfDeath) { this.placeOfDeath = placeOfDeath; }

    public String getDeathType() { return deathType; }
    public void setDeathType(String deathType) { this.deathType = deathType; }

    public String getCauseOfDeath() { return causeOfDeath; }
    public void setCauseOfDeath(String causeOfDeath) { this.causeOfDeath = causeOfDeath; }

    public LocalDate getDateOfLoss() { return dateOfLoss; }
    public void setDateOfLoss(LocalDate dateOfLoss) { this.dateOfLoss = dateOfLoss; }

    public String getPlaceOfLoss() { return placeOfLoss; }
    public void setPlaceOfLoss(String placeOfLoss) { this.placeOfLoss = placeOfLoss; }

    public String getCauseOfLoss() { return causeOfLoss; }
    public void setCauseOfLoss(String causeOfLoss) { this.causeOfLoss = causeOfLoss; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getCin() {
        return cin;
    }

    public void setCin(String cin) {
        this.cin = cin;
    }
}