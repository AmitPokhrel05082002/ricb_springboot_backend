package bt.ricb.ricb_api.repository;

import bt.ricb.ricb_api.models.ClaimantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClaimantRepository extends JpaRepository<ClaimantEntity, Integer> {
    Optional<ClaimantEntity> findByCid(String cid);

    // Dzongkhag ID from name
    @Query(value = "SELECT dzongkhag_id FROM dzongkhags WHERE dzongkhag_name = :name", nativeQuery = true)
    Integer getDzongkhagIdByName(String name);

    // Gewog ID from name
    @Query(value = "SELECT gewog_id FROM gewogs WHERE gewog_name = :name", nativeQuery = true)
    Integer getGewogIdByName(String name);

    // Village ID from name
    @Query(value = "SELECT village_id FROM villages WHERE village_name = :name", nativeQuery = true)
    Integer getVillageIdByName(String name);

}