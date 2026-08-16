package bt.ricb.ricb_api.repository;

import bt.ricb.ricb_api.models.UnderwritingOfficerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UnderwritingOfficerRepository
        extends JpaRepository<UnderwritingOfficerEntity, Long> {

    Optional<UnderwritingOfficerEntity> findByBranchCode(String branchCode);

}