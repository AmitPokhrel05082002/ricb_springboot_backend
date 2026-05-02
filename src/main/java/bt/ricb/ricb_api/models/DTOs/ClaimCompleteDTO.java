package bt.ricb.ricb_api.models.DTOs;

import java.util.List;

public class ClaimCompleteDTO {
    private String cin;
    private String remarks;
    private Integer actionedBy;

    // ===== Getters & Setters =====
    public String getCin() { return cin; }
    public void setCin(String cin) { this.cin = cin; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public Integer getActionedBy() { return actionedBy; }
    public void setActionedBy(Integer actionedBy) { this.actionedBy = actionedBy; }
}

