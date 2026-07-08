package bt.ricb.ricb_api.repository;

import bt.ricb.ricb_api.models.PolicyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PolicyRepository extends JpaRepository<PolicyEntity, Integer> {

    List<PolicyEntity> findByPolicyHolderId(Integer policyHolderId);

    boolean existsByPolicyNumber(String policyNumber);

    @Query(value = """
        SELECT COUNT(*)
        FROM policies p
        JOIN policy_holders ph
          ON p.policy_holder_id = ph.id
        WHERE p.policy_number = :policyNo
          AND ph.cid = :cid
        """, nativeQuery = true)
    long existsByCidAndPolicyNumber(@Param("cid") String cid,
                                    @Param("policyNo") String policyNo);
}