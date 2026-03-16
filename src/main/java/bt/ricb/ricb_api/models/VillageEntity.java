package bt.ricb.ricb_api.models;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "villages")
public class VillageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer villageId;

    private Integer gewogId;
    private String villageName;
    private Boolean isActive;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Integer getVillageId() { return villageId; }
    public void setVillageId(Integer villageId) { this.villageId = villageId; }

    public Integer getGewogId() { return gewogId; }
    public void setGewogId(Integer gewogId) { this.gewogId = gewogId; }

    public String getVillageName() { return villageName; }
    public void setVillageName(String villageName) { this.villageName = villageName; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}