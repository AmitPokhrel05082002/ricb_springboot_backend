package bt.ricb.ricb_api.models.DTOs;

import java.time.LocalDateTime;
import java.util.List;

public class ClaimResponseDRO {
    private ClaimantDTO claimant;
    private PolicyHolderDTO policyHolder;
    private List<PolicyDTO> policies;
    private ClaimDTO claim;
    private PayeeDTO payee;
    private String cin;
    private LocalDateTime createdAt;
    private String status;



    // Getters and Setters
    public String getCin() { return cin; }
    public void setCin(String cin) { this.cin = cin; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public ClaimantDTO getClaimant() { return claimant; }
    public void setClaimant(ClaimantDTO claimant) { this.claimant = claimant; }

    public PayeeDTO getPayee() { return payee; }
    public void setPayee(PayeeDTO payee) { this.payee = payee; }

    public PolicyHolderDTO getPolicyHolder() { return policyHolder; }
    public void setPolicyHolder(PolicyHolderDTO policyHolder) { this.policyHolder = policyHolder; }

    public ClaimDTO getClaim() { return claim; }
    public void setClaim(ClaimDTO claim) { this.claim = claim; }

    public List<PolicyDTO> getPolicies() { return policies; }

    public void setPolicies(List<PolicyDTO> policies) { this.policies = policies; }
}