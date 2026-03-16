package bt.ricb.ricb_api.models;
import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "dzongkhags")
public class DzongkhagEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer dzongkhagId;

    private String dzongkhagName;
    private Boolean isActive;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Integer getDzongkhagId() { return dzongkhagId; }
    public void setDzongkhagId(Integer dzongkhagId) { this.dzongkhagId = dzongkhagId; }

    public String getDzongkhagName() { return dzongkhagName; }
    public void setDzongkhagName(String dzongkhagName) { this.dzongkhagName = dzongkhagName; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}