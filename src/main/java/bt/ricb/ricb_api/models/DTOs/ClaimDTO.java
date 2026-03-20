package bt.ricb.ricb_api.models.DTOs;

import java.time.LocalDate;

public class ClaimDTO {

    private String claimType;  // Death, Maturity, etc.
    private String g2cApplicationNumber;
    private LocalDate dateOfDeath;
    private String placeOfDeath;
    private String deathType;   // Natural, Accidental, Suicide
    private String causeOfDeath;
    private LocalDate dateOfLoss;
    private String placeOfLoss;
    private String causeOfLoss;
    private Integer nearestBranchId;

    // Getters and Setters
    public String getClaimType() { return claimType; }
    public void setClaimType(String claimType) { this.claimType = claimType; }

    public String getG2cApplicationNumber() { return g2cApplicationNumber; }
    public void setG2cApplicationNumber(String g2cApplicationNumber) { this.g2cApplicationNumber = g2cApplicationNumber; }

    public LocalDate getDateOfDeath() { return dateOfDeath; }
    public void setDateOfDeath(LocalDate dateOfDeath) { this.dateOfDeath = dateOfDeath; }

    public String getPlaceOfDeath() { return placeOfDeath; }
    public void setPlaceOfDeath(String placeOfDeath) { this.placeOfDeath = placeOfDeath; }

    public String getDeathType() { return deathType; }
    public void setDeathType(String deathType) { this.deathType = deathType; }

    public String getCauseOfDeath() { return causeOfDeath; }
    public void setCauseOfDeath(String causeOfDeath) { this.causeOfDeath = causeOfDeath; }

    public LocalDate getDateOfLoss() { return dateOfLoss; }
    public void setDateOfLoss(LocalDate dateOfLoss) { this.dateOfLoss = dateOfLoss; }

    public String getPlaceOfLoss() { return placeOfLoss; }
    public void setPlaceOfLoss(String placeOfLoss) { this.placeOfLoss = placeOfLoss; }

    public String getCauseOfLoss() { return causeOfLoss; }
    public void setCauseOfLoss(String causeOfLoss) { this.causeOfLoss = causeOfLoss; }

    public Integer getNearestBranchId() { return nearestBranchId; }
    public void setNearestBranchId(Integer nearestBranchId) { this.nearestBranchId = nearestBranchId; }
}