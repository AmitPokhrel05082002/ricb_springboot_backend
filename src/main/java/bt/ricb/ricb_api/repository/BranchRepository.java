package bt.ricb.ricb_api.repository;


import bt.ricb.ricb_api.models.BranchEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BranchRepository extends JpaRepository<BranchEntity, Integer> {}