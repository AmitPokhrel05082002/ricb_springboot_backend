package bt.ricb.ricb_api.repository;

import bt.ricb.ricb_api.models.PolicyHolderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PolicyHolderRepository extends JpaRepository<PolicyHolderEntity, Integer> {
    Optional<PolicyHolderEntity> findByCid(String cid);
}