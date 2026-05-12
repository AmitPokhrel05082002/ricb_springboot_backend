package bt.ricb.ricb_api.models.DTOs;

import java.util.List;

public class ClaimActionDTO {
    private String cin;
    private List<String> policyNumbers;
    private String remarks;

    // ===== Getters & Setters =====
    public String getCin() { return cin; }
    public void setCin(String cin) { this.cin = cin; }

    public List<String> getPolicyNumbers() {
        return policyNumbers;
    }
    public void setPolicyNumbers(List<String> policyNumbers) {
        this.policyNumbers = policyNumbers;
    }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}