package bt.ricb.ricb_api.repository;

import bt.ricb.ricb_api.models.PolicyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PolicyRepository extends JpaRepository<PolicyEntity, Integer> {
    List<PolicyEntity> findByPolicyHolderId(Integer policyHolderId);
    // Check if policy number already exists
    boolean existsByPolicyNumber(String policyNumber);
}