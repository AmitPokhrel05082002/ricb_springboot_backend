package bt.ricb.ricb_api.models.DTOs;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.sql.Timestamp;

public class GewogDTO {

    private Integer gewogId;
    private Integer dzongkhagId;
    private String gewogName;
    private Boolean isActive;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Integer getGewogId() { return gewogId; }
    public void setGewogId(Integer gewogId) { this.gewogId = gewogId; }

    public Integer getDzongkhagId() { return dzongkhagId; }
    public void setDzongkhagId(Integer dzongkhagId) { this.dzongkhagId = dzongkhagId; }

    public String getGewogName() { return gewogName; }
    public void setGewogName(String gewogName) { this.gewogName = gewogName; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}