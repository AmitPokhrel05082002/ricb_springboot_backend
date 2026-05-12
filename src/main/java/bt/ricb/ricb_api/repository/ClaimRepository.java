package bt.ricb.ricb_api.repository;

import bt.ricb.ricb_api.models.ClaimEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClaimRepository extends JpaRepository<ClaimEntity, Integer> {

    Long countByStatus(String status);

    Optional<ClaimEntity> findByCin(String cin);

    @Query(value = "SELECT cin FROM claims WHERE cin LIKE CONCAT('CIN-', :year, '%') ORDER BY id DESC LIMIT 1", nativeQuery = true)
    String getLastCinByYear(@Param("year") int year);

    // ================= Branch Filter =================

    Long countByNearestBranchId(String nearestBranchId);

    Long countByStatusAndNearestBranchId(String status, String nearestBranchId);

    List<ClaimEntity> findByNearestBranchId(String nearestBranchId);
}