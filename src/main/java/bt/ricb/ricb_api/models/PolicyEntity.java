package bt.ricb.ricb_api.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalDate;


@Entity
@Table(name = "policies")
public class PolicyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "policy_holder_id", nullable = false)
    private Integer policyHolderId;

    @Column(name = "policy_holder_name", nullable = false)
    private String policyHolderName;

    @Column(name = "policy_number", nullable = false, unique = true)
    private String policyNumber;

    @Column(name = "policy_serial_no", nullable = true)
    private Integer policySerialNumber;

    @Column(name = "intimation_date")
    private LocalDate intimationDate;

    @Column(name = "nominee_name")
    private String nomineeName;

    @Column(name = "sum_assured", nullable = false)
    private Double sumAssured;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "branch_code")
    private String branchCode;

    @Column(name = "claim_status")
    private String claimStatus;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ================= Getters and Setters =================
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getPolicyHolderId() { return policyHolderId; }
    public void setPolicyHolderId(Integer policyHolderId) { this.policyHolderId = policyHolderId; }

    public String getPolicyHolderName() { return policyHolderName; }
    public void setPolicyHolderName(String policyHolderName) { this.policyHolderName = policyHolderName; }

    public String getPolicyNumber() { return policyNumber; }
    public void setPolicyNumber(String policyNumber) { this.policyNumber = policyNumber; }

    public Integer getPolicySerialNumber() { return policySerialNumber; }
    public void setPolicySerialNumber(Integer policySerialNumber) { this.policySerialNumber = policySerialNumber; }

    public LocalDate getIntimationDate() { return intimationDate; }
    public void setIntimationDate(LocalDate intimationDate) { this.intimationDate = intimationDate; }

    public String getNomineeName() { return nomineeName; }
    public void setNomineeName(String nomineeName) { this.nomineeName = nomineeName; }

    public Double getSumAssured() { return sumAssured; }
    public void setSumAssured(Double sumAssured) { this.sumAssured = sumAssured; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getClaimStatus() { return claimStatus; }
    public void setClaimStatus(String claimStatus) { this.claimStatus = claimStatus;  }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public String getBranchCode() { return branchCode; }
    public void setBranchCode(String branchCode) { this.branchCode = branchCode; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}