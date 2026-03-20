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
    // Native query to get bank_id using bank_name
    @Query(value = "SELECT id FROM banks WHERE name = :bankName", nativeQuery = true)
    Integer getBankIdByName(String bankName);

    @Query(value = "SELECT name FROM banks WHERE id = :id", nativeQuery = true)
    String getBankNameById(@Param("id") Integer id);


}