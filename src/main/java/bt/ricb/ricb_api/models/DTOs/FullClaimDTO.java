package bt.ricb.ricb_api.models.DTOs;

import java.time.LocalDateTime;

public class FullClaimDTO {
    private ClaimantDTO claimant;
    private PolicyHolderDTO policyHolder;
//    private PolicyDTO policy;
    private ClaimDTO claim;
    private PayeeDTO payee;



    // Getters and Setters
    public ClaimantDTO getClaimant() { return claimant; }
    public void setClaimant(ClaimantDTO claimant) { this.claimant = claimant; }

    public PayeeDTO getPayee() { return payee; }
    public void setPayee(PayeeDTO payee) { this.payee = payee; }

    public PolicyHolderDTO getPolicyHolder() { return policyHolder; }
    public void setPolicyHolder(PolicyHolderDTO policyHolder) { this.policyHolder = policyHolder; }

    public ClaimDTO getClaim() { return claim; }
    public void setClaim(ClaimDTO claim) { this.claim = claim; }


//    public PolicyDTO getPolicy() { return policy; }
//    public void setPolicy(PolicyDTO policy) { this.policy = policy; }
}