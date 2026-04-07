package bt.ricb.ricb_api.models.DTOs;

import java.time.LocalDate;

public class PolicyDTO {
    private String policyHolderName;
    private String policyNumber;
    private Integer policySerialNumber; // must be Integer
    private LocalDate intimationDate;
    private String nomineeName;
    private Double sumAssured;
    private String status;
    private String branchCode;

    // Getters and Setters
    public String getPolicyHolderName() { return policyHolderName; }
    public void setPolicyHolderName(String policyHolderName) { this.policyHolderName = policyHolderName; }

    public String getPolicyNumber() { return policyNumber; }
    public void setPolicyNumber(String policyNumber) { this.policyNumber = policyNumber; }

    public Integer getPolicySerialNumber() { return policySerialNumber; }
    public void setPolicySerialNumber(Integer policySerialNumber) { this.policySerialNumber = policySerialNumber; }

    public LocalDate getIntimationDate() { return intimationDate; }
    public void setIntimationDate(LocalDate intimationDate) { this.intimationDate = intimationDate; }

    public String getNomineeName() { return nomineeName; }
    public void setNomineeName(String nomineeName) { this.nomineeName = nomineeName; }

    public Double getSumAssured() { return sumAssured; }
    public void setSumAssured(Double sumAssured) { this.sumAssured = sumAssured; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getBranchCode() { return branchCode; }
    public void setBranchCode(String branchCode) { this.branchCode = branchCode; }
}