package bt.ricb.ricb_api.models;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "policies")
public class PolicyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="policy_holder_id", nullable = false)
    private Integer policyHolderId;

    @Column(name="policy_name", nullable = false)
    private String policyName;

    @Column(name="policy_number", nullable = false, unique = true)
    private String policyNumber;

    @Column(name="intimation_date", nullable = false)
    private LocalDate intimationDate;

    @Column(name="nominee_name")
    private String nomineeName;

    @Column(name="relation")
    private String relation;

    @Column(name="sum_assured", nullable = false)
    private Double sumAssured;

    @Column(name="status", nullable = false)
    private String status;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    public PolicyEntity() {}

    // Getters and setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getPolicyHolderId() { return policyHolderId; }
    public void setPolicyHolderId(Integer policyHolderId) { this.policyHolderId = policyHolderId; }

    public String getPolicyName() { return policyName; }
    public void setPolicyName(String policyName) { this.policyName = policyName; }

    public String getPolicyNumber() { return policyNumber; }
    public void setPolicyNumber(String policyNumber) { this.policyNumber = policyNumber; }

    public LocalDate getIntimationDate() { return intimationDate; }
    public void setIntimationDate(LocalDate intimationDate) { this.intimationDate = intimationDate; }

    public String getNomineeName() { return nomineeName; }
    public void setNomineeName(String nomineeName) { this.nomineeName = nomineeName; }

    public String getRelation() { return relation; }
    public void setRelation(String relation) { this.relation = relation; }

    public Double getSumAssured() { return sumAssured; }
    public void setSumAssured(Double sumAssured) { this.sumAssured = sumAssured; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}