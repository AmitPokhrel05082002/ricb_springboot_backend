package bt.ricb.ricb_api.repository;

import bt.ricb.ricb_api.models.UserEntity;
import java.util.List;
import jakarta.persistence.Tuple;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<UserEntity, String> {
	@Query(value = "SELECT IF(COUNT(*) = 0, 0, 1) as passwordExists " +
            "FROM apps_users " + 
            "WHERE password = :password AND cid = :cidNo AND status = 1", 
    nativeQuery = true)
	Integer checkPassword(@Param("password") String password, @Param("cidNo") String cidNo);
	
	UserEntity getByCid(String paramString);

	@Modifying
	@Transactional
	@Query(value = "UPDATE apps_users SET status=1 WHERE cid=:cid", nativeQuery = true)
	void updateUserStatus(@Param("cid") String paramString);

	@Modifying
	@Transactional
	@Query(value = "UPDATE apps_users SET PASSWORD = :password where cid=:cid AND mobile= :mobile", nativeQuery = true)
	void updatePassword(@Param("password") String paramString1, @Param("cid") String paramString2,
			@Param("mobile") String paramString3);

	@Query(value = "SELECT IF(COUNT(*) = 0, 0, 1) existStatus, STATUS FROM apps_users WHERE cid=:cidNo AND mobile=:mobileNo\r\n", nativeQuery = true)
	List<Tuple> checkUserDetails(@Param("cidNo") String paramString1, @Param("mobileNo") String paramString2);

	Boolean existsByCidAndMobile(String paramString1, String paramString2);

	UserEntity getByCidAndMobile(String paramString1, String paramString2);
}