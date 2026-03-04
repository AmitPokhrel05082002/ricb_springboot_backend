package bt.ricb.ricb_api.repository;

import bt.ricb.ricb_api.models.BusinessLineEntity;
import java.util.List;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BusinessLineRepository extends JpaRepository<BusinessLineEntity, Integer> {
  @Query(value = "SELECT DISTINCT(business_name) AS business_name, business_id FROM line_of_business order by business_id", nativeQuery = true)
  List<Tuple> getAllBusinessLines();
}
