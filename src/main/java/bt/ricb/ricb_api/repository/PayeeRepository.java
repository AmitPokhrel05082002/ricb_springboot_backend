package bt.ricb.ricb_api.repository;

import bt.ricb.ricb_api.models.PayeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PayeeRepository extends JpaRepository<PayeeEntity, Integer> {
    Optional<PayeeEntity> findByCid(String cid);
}