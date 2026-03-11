package bt.ricb.ricb_api.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "claim_actions")
public class ClaimActionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "claim_id")
    private Integer claimId;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type")
    private ActionType actionType;

    private String remarks;

    @Column(name = "actioned_by")
    private Integer actionedBy;

    @Column(name = "actioned_at")
    private LocalDateTime actionedAt;

    public enum ActionType {
        Verified,
        Approved,
        Rejected,
        Resubmitted
    }

    // ===== Getters & Setters =====

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getClaimId() { return claimId; }
    public void setClaimId(Integer claimId) { this.claimId = claimId; }

    public ActionType getActionType() { return actionType; }
    public void setActionType(ActionType actionType) { this.actionType = actionType; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public Integer getActionedBy() { return actionedBy; }
    public void setActionedBy(Integer actionedBy) { this.actionedBy = actionedBy; }

    public LocalDateTime getActionedAt() { return actionedAt; }
    public void setActionedAt(LocalDateTime actionedAt) { this.actionedAt = actionedAt; }
}