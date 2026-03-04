package bt.ricb.ricb_api.repository;

import bt.ricb.ricb_api.models.OTPEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OTPRepository extends JpaRepository<OTPEntity, String> {
	Boolean existsByCode(String paramString);

	Boolean existsByUserIdAndCode(String paramString1, String paramString2);
}