package indocyber.exam.repository;

import indocyber.exam.dto.room.RoomGridDTO;
import indocyber.exam.dto.transaction.InsertTransactionDTO;
import indocyber.exam.dto.transaction.TransactionGridDTO;
import indocyber.exam.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    @Query("""
            SELECT new indocyber.exam.dto.transaction.TransactionGridDTO(tr.id, cus.name, roo.roomNumber, tr.checkIn, tr.checkOut, tr.bill, tr.isPending, tr.isExpired, tr.isConfirmed)
            FROM Transaction tr
                JOIN tr.customer cus
                JOIN tr.room roo
            WHERE cus.name LIKE %:customerName%
                AND roo.roomNumber LIKE %:roomNumber%
                AND cast(tr.checkOut as date) >= cast(GETDATE() as date)
            """)
    List<TransactionGridDTO> findAll(@Param("customerName") String customerName,
                                     @Param("roomNumber") String roomNumber,
                                     Pageable pageable);

    @Query("""
            SELECT new indocyber.exam.dto.transaction.TransactionGridDTO(tr.id, cus.name, roo.roomNumber, tr.checkIn, tr.checkOut, tr.bill, tr.isPending, tr.isExpired, tr.isConfirmed)
            FROM Transaction tr
                JOIN tr.customer cus
                JOIN tr.room roo
            WHERE cus.name LIKE %:customerName%
                AND roo.roomNumber LIKE %:roomNumber%
                AND cast(tr.checkOut as date) >= cast(GETDATE() as date)
            """)
    Page<TransactionGridDTO> findAllDTOPage(@Param("customerName") String customerName,
                                            @Param("roomNumber") String roomNumber,
                                            Pageable pageable);

    @Query("""
            SELECT new indocyber.exam.dto.transaction.TransactionGridDTO(tr.id, cus.name, roo.roomNumber, tr.checkIn, tr.checkOut, tr.bill, tr.isPending, tr.isExpired, tr.isConfirmed)
            FROM Transaction tr
                JOIN tr.customer cus
                JOIN tr.room roo
            WHERE cus.username = :username
                AND tr.isConfirmed = 1
            """)
    List<TransactionGridDTO> findAllByUsername(@Param("username") String username,
                                     Pageable pageable);

    @Query("""
            SELECT new indocyber.exam.dto.transaction.TransactionGridDTO(tr.id, cus.name, roo.roomNumber, tr.checkIn, tr.checkOut, tr.bill, tr.isPending, tr.isExpired, tr.isConfirmed)
            FROM Transaction tr
                JOIN tr.customer cus
                JOIN tr.room roo
            WHERE cus.username = :username
                AND tr.isConfirmed = 1
            """)
    Page<TransactionGridDTO> findAllByUsernamePage(@Param("username") String username,
                                               Pageable pageable);

    @Query("""
            SELECT new indocyber.exam.dto.transaction.TransactionGridDTO(tr.id, cus.name, roo.roomNumber, tr.checkIn, tr.checkOut, tr.bill, tr.isPending, tr.isExpired, tr.isConfirmed)
            FROM Transaction tr
                JOIN tr.customer cus
                JOIN tr.room roo
            WHERE tr.id = :id
            """)
    TransactionGridDTO findByIdDTO(@Param("id") Integer id);

    @Query("""
           SELECT new indocyber.exam.dto.transaction.TransactionGridDTO(tr.id, cus.name, roo.roomNumber, tr.checkIn, tr.checkOut, tr.bill, tr.isPending, tr.isExpired, tr.isConfirmed)
           FROM Transaction tr
               JOIN tr.customer cus
               JOIN tr.room roo
           WHERE roo.roomNumber = :roomNumber
                AND (tr.isPending = 1 OR (tr.isConfirmed = 1 AND tr.isExpired = 0))
            """)
    TransactionGridDTO findActiveTransactionByRoom(@Param("roomNumber") String roomNumber);

    @Query("""
            SELECT COUNT(*)
            FROM Transaction tr
               JOIN tr.customer cus
               JOIN tr.room roo
            WHERE cus.name LIKE %:customerName%
               AND roo.roomNumber LIKE %:roomNumber%
            """)
    long count(@Param("customerName") String customerName,
               @Param("roomNumber") String roomNumber);

    @Query("""
            SELECT COUNT(*)
            FROM Transaction tr
               JOIN tr.customer cus
               JOIN tr.room roo
            WHERE cus.username = :username
                AND tr.isConfirmed = 1
            """)
    long count(@Param("username") String username);
}
