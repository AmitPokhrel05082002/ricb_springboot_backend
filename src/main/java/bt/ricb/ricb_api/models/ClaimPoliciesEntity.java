package bt.ricb.ricb_api.models;

import jakarta.persistence.*;

@Entity
@Table(name = "claim_policies")
public class ClaimPoliciesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="claim_id", nullable = false)
    private Integer claimId;

    @Column(name="policy_id", nullable = false)
    private Integer policyId;

    public ClaimPoliciesEntity() {}

    // Getters and setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getClaimId() { return claimId; }
    public void setClaimId(Integer claimId) { this.claimId = claimId; }

    public Integer getPolicyId() { return policyId; }
    public void setPolicyId(Integer policyId) { this.policyId = policyId; }
}