package bt.ricb.ricb_api.repository;

import bt.ricb.ricb_api.models.CountryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<CountryEntity, Integer> {}
