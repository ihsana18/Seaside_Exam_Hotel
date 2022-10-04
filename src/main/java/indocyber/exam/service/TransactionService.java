package indocyber.exam.service;

import indocyber.exam.dto.customer.CustomerGridDTO;
import indocyber.exam.dto.room.RoomGridDTO;
import indocyber.exam.dto.transaction.InsertTransactionDTO;
import indocyber.exam.dto.transaction.InsertTransactionDTORest;
import indocyber.exam.dto.transaction.TransactionGridDTO;
import indocyber.exam.entity.Customer;
import indocyber.exam.entity.Room;
import indocyber.exam.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TransactionService {

    List<Customer> findAllCustomer();

    void saveTransaction(InsertTransactionDTO dto);

    Transaction saveTransactionFromDTO(InsertTransactionDTO dto);

    Transaction findById(Integer id);

    void deleteById(Integer id);

    void confirmTransaction(InsertTransactionDTO dto);

    InsertTransactionDTORest confirmTransactionById(Integer id);

    List<TransactionGridDTO> getTransactionGrid(Integer page, String customerName, String roomNumber);

    long getTotalPages(String customerName, String roomNumber);

    List<TransactionGridDTO> getCustomerTransaction(Integer page, String username);

    long getDetailPages(String username);

    Customer getCustomerByUsername(String username);

    InsertTransactionDTO getTransactionToSee(Integer id);

    InsertTransactionDTORest getTransactionToSeeRest(Integer id);

    void finishTransaction(Transaction transaction);

    void expireTransaction(Transaction transaction);

    InsertTransactionDTO findActiveTransaction(String roomNumber);

    Page<TransactionGridDTO> findAllDTO(Pageable pageable,String name, String roomNumber);

    Room findVacantRoom(String roomNumber);

}
