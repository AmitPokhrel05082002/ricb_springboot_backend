package bt.ricb.ricb_api.models;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "gewogs")
public class GewogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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