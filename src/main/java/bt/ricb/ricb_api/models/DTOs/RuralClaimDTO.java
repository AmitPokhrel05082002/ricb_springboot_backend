package bt.ricb.ricb_api.models.DTOs;

import java.time.LocalDate;

public class RuralClaimDTO {

    private String citizenId;
    private LocalDate dateOfDeath;
    private String branchCode;
    private String statusCode;
    private Long policySerialNo;
    private String policyNo;
    private String deceasedName;
    private String causeOfDeath;
    private String deathReportingApplNo;
    private String deathPlace;
    private String deathType;
    private LocalDate claimIntmDate;
    private String claimIntmBy;
    private String claimIntmRelation;

    // ================= GETTERS & SETTERS =================

    public String getCitizenId() {
        return citizenId;
    }

    public void setCitizenId(String citizenId) {
        this.citizenId = citizenId;
    }


    public LocalDate getDateOfDeath() {
        return dateOfDeath;
    }

    public void setDateOfDeath(LocalDate dateOfDeath) {
        this.dateOfDeath = dateOfDeath;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public Long getPolicySerialNo() {
        return policySerialNo;
    }

    public void setPolicySerialNo(Long policySerialNo) {
        this.policySerialNo = policySerialNo;
    }

    public String getPolicyNo() {
        return policyNo;
    }

    public void setPolicyNo(String policyNo) {
        this.policyNo = policyNo;
    }

    public String getDeceasedName() {
        return deceasedName;
    }

    public void setDeceasedName(String deceasedName) {
        this.deceasedName = deceasedName;
    }

    public String getCauseOfDeath() {
        return causeOfDeath;
    }

    public void setCauseOfDeath(String causeOfDeath) {
        this.causeOfDeath = causeOfDeath;
    }

    public String getDeathReportingApplNo() {
        return deathReportingApplNo;
    }

    public void setDeathReportingApplNo(String deathReportingApplNo) {
        this.deathReportingApplNo = deathReportingApplNo;
    }

    public String getDeathPlace() {
        return deathPlace;
    }

    public void setDeathPlace(String deathPlace) {
        this.deathPlace = deathPlace;
    }

    public String getDeathType() {
        return deathType;
    }

    public void setDeathType(String deathType) {
        this.deathType = deathType;
    }

    public LocalDate getClaimIntmDate() {
        return claimIntmDate;
    }

    public void setClaimIntmDate(LocalDate claimIntmDate) {
        this.claimIntmDate = claimIntmDate;
    }

    public String getClaimIntmBy() {
        return claimIntmBy;
    }

    public void setClaimIntmBy(String claimIntmBy) {
        this.claimIntmBy = claimIntmBy;
    }

    public String getClaimIntmRelation() {
        return claimIntmRelation;
    }

    public void setClaimIntmRelation(String claimIntmRelation) {
        this.claimIntmRelation = claimIntmRelation;
    }
}