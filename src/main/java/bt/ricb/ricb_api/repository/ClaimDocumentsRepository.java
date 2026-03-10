package bt.ricb.ricb_api.repository;

import bt.ricb.ricb_api.models.ClaimDocumentsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClaimDocumentsRepository extends JpaRepository<ClaimDocumentsEntity, Integer> {

    Optional<ClaimDocumentsEntity> findByClaimId(Integer claimId);

}