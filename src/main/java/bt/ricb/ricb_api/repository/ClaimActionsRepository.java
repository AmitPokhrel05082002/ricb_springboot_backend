package bt.ricb.ricb_api.repository;

import bt.ricb.ricb_api.models.ClaimActionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClaimActionsRepository extends JpaRepository<ClaimActionEntity, Integer> {

    List<ClaimActionEntity> findByClaimIdOrderByActionedAtDesc(Integer claimId);
}