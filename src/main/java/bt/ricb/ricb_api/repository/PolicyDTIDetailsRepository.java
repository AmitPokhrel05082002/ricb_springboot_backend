package bt.ricb.ricb_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bt.ricb.ricb_api.models.PolicyDTIDetailsEntity;

@Repository
public interface PolicyDTIDetailsRepository extends JpaRepository<PolicyDTIDetailsEntity, Integer> {
	
}
