package indocyber.exam.repository;

import indocyber.exam.dto.admin.AdminGridDTO;
import indocyber.exam.entity.Admin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;

public interface AdminRepository extends JpaRepository<Admin, Integer> {

    @Query("""
            SELECT new indocyber.exam.dto.admin.AdminGridDTO(acc.username, ad.jobTitle)
            FROM Admin ad
            JOIN ad.account acc
            """)
    List<AdminGridDTO> findAllDTO(Pageable pageable);

    @Query("""
            SELECT new indocyber.exam.dto.admin.AdminGridDTO(acc.username, ad.jobTitle)
            FROM Admin ad
            JOIN ad.account acc
            """)
    Page<AdminGridDTO> findAllDTOPage(Pageable pageable);

    @Query("""
            SELECT new indocyber.exam.dto.admin.AdminGridDTO(ad.id, acc.username, ad.jobTitle)
            FROM Admin ad
            JOIN ad.account acc
            WHERE acc.username = :username
            """)
    AdminGridDTO findByUsername(@Param("username") String username);

}
