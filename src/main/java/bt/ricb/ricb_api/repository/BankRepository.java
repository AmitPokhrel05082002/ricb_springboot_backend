package bt.ricb.ricb_api.repository;

import bt.ricb.ricb_api.models.BankEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankRepository extends JpaRepository<BankEntity, Integer> {}