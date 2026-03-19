package bt.ricb.ricb_api.repository;

import bt.ricb.ricb_api.models.DzongkhagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DzongkhagRepository extends JpaRepository<DzongkhagEntity, Integer> {
    @Query("SELECT d.dzongkhagId, d.dzongkhagName FROM DzongkhagEntity d")
    List<Object[]> findIdAndName();
}
