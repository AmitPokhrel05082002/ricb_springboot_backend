package bt.ricb.ricb_api.models.DTOs;

public class FullClaimDTO {
    private ClaimantDTO claimant;
    private PolicyHolderDTO policyHolder;
    private PolicyDTO policy;
    private ClaimDTO claim;
    private PayeeDTO payee;
    private ClaimDocumentsDTO documents;


    // Getters and Setters
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

    public PolicyDTO getPolicy() { return policy; }
    public void setPolicy(PolicyDTO policy) { this.policy = policy; }
}