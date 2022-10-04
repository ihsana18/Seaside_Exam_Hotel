package indocyber.exam.repository;

import indocyber.exam.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccountRepository extends JpaRepository<Account, String> {


    @Query("""
            SELECT COUNT (*)
            FROM Account acc
            WHERE acc.username = :username
            """)
    Long countExistingUsername(@Param("username") String username);

}
