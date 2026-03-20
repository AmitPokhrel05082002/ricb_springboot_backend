package bt.ricb.ricb_api.models.DTOs;

import java.time.LocalDate;

public class PolicyDTO {
    private String policyName;
    private String policyNumber;
    private LocalDate intimationDate;
    private String nomineeName;
    private String relation;
    private Double sumAssured;
    private String status;

    // Getters and Setters
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
}