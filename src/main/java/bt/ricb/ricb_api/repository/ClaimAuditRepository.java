package bt.ricb.ricb_api.repository;

import bt.ricb.ricb_api.models.ClaimAuditEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClaimAuditRepository extends JpaRepository<ClaimAuditEntity, Long> {
    List<ClaimAuditEntity> findByCinOrderByActionedAtDesc(String cin);
}