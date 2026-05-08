package bt.ricb.ricb_api.repository;

import bt.ricb.ricb_api.models.AgencyUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AgencyUserRepository extends JpaRepository<AgencyUserEntity, String> {

    Optional<AgencyUserEntity> findByUsername(String username);

    Optional<AgencyUserEntity> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}