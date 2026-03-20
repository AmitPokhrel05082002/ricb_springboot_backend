package bt.ricb.ricb_api.models.DTOs;

import java.time.LocalDate;

public class PolicyHolderDTO {

    private String cid;
    private LocalDate dateOfBirth;

    // Getters and Setters
    public String getCid() { return cid; }
    public void setCid(String cid) { this.cid = cid; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
}