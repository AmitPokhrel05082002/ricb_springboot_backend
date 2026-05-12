package bt.ricb.ricb_api.repository;

import bt.ricb.ricb_api.models.RliCollectionDateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RliCollectionDateRepository extends JpaRepository<RliCollectionDateEntity, Long> {
}