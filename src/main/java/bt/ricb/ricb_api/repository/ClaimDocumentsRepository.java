package bt.ricb.ricb_api.repository;

import bt.ricb.ricb_api.models.ClaimDocumentsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClaimDocumentsRepository extends JpaRepository<ClaimDocumentsEntity, Integer> {

    List<ClaimDocumentsEntity> findByClaimId(Integer claimId);
}