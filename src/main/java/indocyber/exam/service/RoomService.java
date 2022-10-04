package indocyber.exam.service;

import indocyber.exam.dto.admin.AdminGridDTO;
import indocyber.exam.dto.customer.CustomerGridDTO;
import indocyber.exam.dto.room.InsertRoomDTO;
import indocyber.exam.dto.room.RoomGridDTO;
import indocyber.exam.dto.room.UpdateRoomDTO;
import indocyber.exam.dto.transaction.InsertTransactionDTO;
import indocyber.exam.dto.transaction.InsertTransactionDTORest;
import indocyber.exam.entity.Customer;
import indocyber.exam.entity.Room;
import indocyber.exam.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RoomService {

    void deleteTransaction(Transaction transaction);

    List<Room> findAll();

    Room findById(String roomNumber);

    void deleteById(String roomNumber);

    InsertTransactionDTO findActiveTransaction(String roomNumber);

    InsertTransactionDTORest findActiveTransactionRest(String roomNumber);

    List<RoomGridDTO> getRoomGrid(Integer page, String roomNumber);

    Page<RoomGridDTO> findAllDTO(Pageable pageable, String roomNumber);

    long getTotalPages(String roomNumber);

    List<RoomGridDTO> getVacantRoomGrid(Integer page, String roomType);

    Page<RoomGridDTO> findAllVacantDTO(Pageable pageable, String roomType);

    long getVacantTotalPages(String roomType);

    UpdateRoomDTO getRoomToUpdate(String roomNumber);

    void insertRoom(InsertRoomDTO dto);

    void updateRoom(UpdateRoomDTO dto);

    Customer getCustomerByUsername(String username);

    Boolean checkExistingRoomNumber(String roomNumber);
}
