package bt.ricb.ricb_api.repository;

import bt.ricb.ricb_api.models.ClaimantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClaimantRepository extends JpaRepository<ClaimantEntity, Integer> {
    Optional<ClaimantEntity> findByCid(String cid);

}