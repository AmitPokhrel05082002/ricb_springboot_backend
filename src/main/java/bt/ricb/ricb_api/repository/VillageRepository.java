package bt.ricb.ricb_api.repository;

import bt.ricb.ricb_api.models.VillageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VillageRepository extends JpaRepository<VillageEntity, Integer> {
    List<VillageEntity> findByGewogId(Integer gewogId);
}
