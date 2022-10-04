package indocyber.exam.repository;

import indocyber.exam.dto.customer.CustomerGridDTO;
import indocyber.exam.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    @Query("""
            SELECT new indocyber.exam.dto.customer.CustomerGridDTO(cus.id, cus.name, cus.email, cus.address, acc.username)
            FROM Customer cus
            JOIN cus.account acc
            WHERE cus.name LIKE %:name%
            """)
    List<CustomerGridDTO> findAll(@Param("name") String name,
                                  Pageable pageable);

    @Query("""
            SELECT new indocyber.exam.dto.customer.CustomerGridDTO(cus.id, cus.name, cus.email, cus.address, acc.username)
            FROM Customer cus
            JOIN cus.account acc
            WHERE cus.name LIKE %:name%
            """)
    Page<CustomerGridDTO> findAllCustomerPage(@Param("name") String name,
                                              Pageable pageable);

    @Query("""
            SELECT COUNT(*)
            FROM Room roo
            WHERE roo.roomNumber = :roomNumber
            """)
    Long countExistingRoomNumber(@Param("roomNumber") String roomNumber);

    @Query("""
            SELECT COUNT(*)
            FROM Customer cus
            WHERE cus.name LIKE %:name%
            """)
    long count(@Param("name") String name);

    @Query("""
            SELECT new indocyber.exam.dto.customer.CustomerGridDTO(cus.id, cus.name, cus.email, cus.address)
            FROM Customer cus
            JOIN cus.account acc
            WHERE acc.username = :username
            """)
    CustomerGridDTO findByUsername(@Param("username") String username);

    @Query("""
            SELECT COUNT(*)
            FROM Customer cus
            JOIN cus.account acc
            WHERE cus.email = :email
            AND acc.username != :username
            """)
    Long countExistingEmail(@Param("username") String username,
                            @Param("email") String email);

}
