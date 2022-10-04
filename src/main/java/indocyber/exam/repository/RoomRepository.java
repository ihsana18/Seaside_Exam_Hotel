package indocyber.exam.repository;

import indocyber.exam.dto.room.RoomGridDTO;
import indocyber.exam.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, String> {

    @Query("""
            SELECT new indocyber.exam.dto.room.RoomGridDTO(roo.roomNumber, roo.type, roo.price, roo.isOccupied, roo.isReserved)
            FROM Room roo
            WHERE roo.roomNumber LIKE %:roomNumber%
            """)
    List<RoomGridDTO> findAll(@Param("roomNumber") String roomNumber,
                              Pageable pageable);

    @Query("""
            SELECT new indocyber.exam.dto.room.RoomGridDTO(roo.roomNumber, roo.type, roo.price, roo.isOccupied, roo.isReserved)
            FROM Room roo
            WHERE roo.roomNumber = :roomNumber
                AND roo.isOccupied = 0
                AND roo.isReserved = 0
            """)
    RoomGridDTO findVacantRoom(@Param("roomNumber") String roomNumber);

    @Query("""
            SELECT new indocyber.exam.dto.room.RoomGridDTO(roo.roomNumber, roo.type, roo.price, roo.isOccupied, roo.isReserved)
            FROM Room roo
            WHERE roo.roomNumber LIKE %:roomNumber%
            """)
    Page<RoomGridDTO> findAllDTOPage(@Param("roomNumber") String roomNumber,
                                     Pageable pageable);

    @Query("""
            SELECT COUNT(*)
            FROM Room roo
            WHERE roo.roomNumber LIKE %:roomNumber%
            """)
    long count(@Param("roomNumber") String roomNumber);

    @Query("""
            SELECT new indocyber.exam.dto.room.RoomGridDTO(roo.roomNumber, roo.type, roo.price, roo.isOccupied, roo.isReserved)
            FROM Room roo
            WHERE roo.type LIKE %:roomType%
                AND roo.isOccupied = 0
                AND roo.isReserved = 0
            """)
    List<RoomGridDTO> findAllVacantRoom(@Param("roomType") String roomType,
                              Pageable pageable);

    @Query("""
            SELECT new indocyber.exam.dto.room.RoomGridDTO(roo.roomNumber, roo.type, roo.price, roo.isOccupied, roo.isReserved)
            FROM Room roo
            WHERE roo.type LIKE %:roomType%
                AND roo.isOccupied = 0
                AND roo.isReserved = 0
            """)
    Page<RoomGridDTO> findAllVacantPage(@Param("roomType") String roomType,
                                        Pageable pageable);

    @Query("""
            SELECT COUNT(*)
            FROM Room roo
            WHERE roo.type LIKE %:roomType%
                AND roo.isOccupied = 0
                AND roo.isReserved = 0
            """)
    long countVacantRoom(@Param("roomType") String roomType);


}
