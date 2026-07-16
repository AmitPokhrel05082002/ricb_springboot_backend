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

    // Global counts
    Long countByClaimStatus(String claimStatus);

    // Count all policies for a branch using claims table
    @Query(value = """
        SELECT COUNT(DISTINCT p.id)
        FROM claims c
        JOIN policies p
            ON c.policy_holder_id = p.policy_holder_id
        WHERE c.nearest_branch_id = :branchId
        """, nativeQuery = true)
    Long countByClaimBranch(@Param("branchId") String branchId);

    // Count policies by claim status for a branch using claims table
    @Query(value = """
        SELECT COUNT(DISTINCT p.id)
        FROM claims c
        JOIN policies p
            ON c.policy_holder_id = p.policy_holder_id
        WHERE c.nearest_branch_id = :branchId
          AND p.claim_status = :claimStatus
        """, nativeQuery = true)
    Long countByClaimStatusAndClaimBranch(
            @Param("claimStatus") String claimStatus,
            @Param("branchId") String branchId);
}