package bt.ricb.ricb_api.models.DTOs;

import java.time.LocalDateTime;

public class ClaimSummaryDTO {

    private String cin;
    private String claimantName;
    private String policyHolderName;
    private LocalDateTime submittedDate;
    private String status;
    private String remarks;
    private LocalDateTime createdAt;

    // Getters and Setters
    public String getCin() { return cin; }
    public void setCin(String cin) { this.cin = cin; }

    public String getClaimantName() { return claimantName; }
    public void setClaimantName(String claimantName) { this.claimantName = claimantName; }

    public String getPolicyHolderName() { return policyHolderName; }
    public void setPolicyHolderName(String policyHolderName) { this.policyHolderName = policyHolderName; }

    public LocalDateTime getSubmittedDate() { return submittedDate; }
    public void setSubmittedDate(LocalDateTime submittedDate) { this.submittedDate = submittedDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }


    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}