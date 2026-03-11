package bt.ricb.ricb_api.repository;


import bt.ricb.ricb_api.models.ClaimActionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClaimActionsRepository extends JpaRepository<ClaimActionEntity, Integer> {
}