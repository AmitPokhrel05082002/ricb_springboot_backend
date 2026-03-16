package bt.ricb.ricb_api.repository;

import bt.ricb.ricb_api.models.GewogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GewogRepository extends JpaRepository<GewogEntity, Integer> {
    List<GewogEntity> findByDzongkhagId(Integer dzongkhagId);
}