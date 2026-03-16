package bt.ricb.ricb_api.models.DTOs;

import java.time.LocalDateTime;

public class ClaimResponseDRO {
    private ClaimantDTO claimant;
    private PolicyHolderDTO policyHolder;
    //    private PolicyDTO policy;
    private ClaimDTO claim;
    private PayeeDTO payee;
    private ClaimDocumentsDTO documents;
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

    public ClaimDocumentsDTO getDocuments() { return documents; }
    public void setDocuments(ClaimDocumentsDTO documents) { this.documents = documents; }

//    public PolicyDTO getPolicy() { return policy; }
//    public void setPolicy(PolicyDTO policy) { this.policy = policy; }
}