package indocyber.exam.service;

import indocyber.exam.dto.customer.CustomerGridDTO;
import indocyber.exam.dto.room.RoomGridDTO;
import indocyber.exam.dto.transaction.InsertTransactionDTO;
import indocyber.exam.dto.transaction.InsertTransactionDTORest;
import indocyber.exam.dto.transaction.TransactionGridDTO;
import indocyber.exam.entity.Customer;
import indocyber.exam.entity.Room;
import indocyber.exam.entity.Transaction;
import indocyber.exam.repository.CustomerRepository;
import indocyber.exam.repository.RoomRepository;
import indocyber.exam.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private CustomerRepository customerRepository;

    private final int rowsInPage = 10;


    @Override
    public List<Customer> findAllCustomer() {
        return customerRepository.findAll();
    }

    @Override
    public void saveTransaction(InsertTransactionDTO dto) {
        Optional<Room> tempRoom = roomRepository.findById(dto.getRoomNumber());
        Room room = tempRoom.get();
        Optional<Customer> tempCust = customerRepository.findById(dto.getCustomerId());
        Customer cust = tempCust.get();

        Transaction transaction = new Transaction(
                dto.getCheckIn(),
                dto.getCheckOut(),
                dto.getRoomNumber(),
                dto.getCustomerId(),
                dto.getBill(),
                true,
                false
        );
        transaction.setIsConfirmed(false);

        room.addTransaction(transaction);
        room.setIsReserved(true);
        cust.addTransaction(transaction);
        transactionRepository.save(transaction);
        roomRepository.save(room);
        customerRepository.save(cust);
    }

    @Override
    public Transaction saveTransactionFromDTO(InsertTransactionDTO dto) {
        Optional<Room> tempRoom = roomRepository.findById(dto.getRoomNumber());
        Room room = tempRoom.get();
        Optional<Customer> tempCust = customerRepository.findById(dto.getCustomerId());
        Customer cust = tempCust.get();

        Long totalDays = ChronoUnit.DAYS.between(dto.getCheckIn(), dto.getCheckOut()) + 1;
        BigDecimal bill = dto.getPrice().multiply(new BigDecimal(totalDays));

        Transaction transaction = new Transaction(
                dto.getCheckIn(),
                dto.getCheckOut(),
                dto.getRoomNumber(),
                dto.getCustomerId(),
                bill,
                true,
                false
        );
        transaction.setIsConfirmed(false);

        room.addTransaction(transaction);
        room.setIsReserved(true);
        cust.addTransaction(transaction);
        transactionRepository.save(transaction);
        roomRepository.save(room);
        customerRepository.save(cust);
        return transaction;
    }

    @Override
    public Transaction findById(Integer id) {
        return (transactionRepository.findById(id).isPresent())?transactionRepository.findById(id).get():null;
    }

    @Override
    public void deleteById(Integer id) {
        Transaction transaction = transactionRepository.findById(id).get();
        Room room = roomRepository.findById(transaction.getRoomNumber()).get();
        room.setIsReserved(false);
        transactionRepository.deleteById(id);
    }

    @Override
    public void confirmTransaction(InsertTransactionDTO dto) {
       Optional<Transaction> tempTransaction = transactionRepository.findById(dto.getId());
       Transaction transaction = tempTransaction.get();

       transaction.setIsPending(false);
       transaction.setIsConfirmed(true);
       transactionRepository.save(transaction);
    }

    @Override
    public InsertTransactionDTORest confirmTransactionById(Integer id) {
        Optional<Transaction> tempTransaction = transactionRepository.findById(id);
        Transaction transaction = tempTransaction.get();

        transaction.setIsPending(false);
        transaction.setIsConfirmed(true);

        Optional<Room> roomOptional = roomRepository.findById(transaction.getRoomNumber());
        Room room = roomOptional.get();

        InsertTransactionDTORest dto = new InsertTransactionDTORest(
                transaction.getId(),
                transaction.getCheckIn(),
                transaction.getCheckOut(),
                transaction.getRoomNumber(),
                transaction.getCustomerId(),
                transaction.getBill(),
                transaction.getIsPending(),
                transaction.getIsExpired()
        );
        dto.setType(room.getType());
        dto.setPrice(room.getPrice());


        transactionRepository.save(transaction);
        return dto;
    }

    @Override
    public List<TransactionGridDTO> getTransactionGrid(Integer page, String customerName, String roomNumber) {
        Pageable pagination = PageRequest.of(page -1,rowsInPage, Sort.by("id"));

        List<TransactionGridDTO> grid = transactionRepository.findAll(customerName, roomNumber, pagination);
        Transaction transaction = null;
        Room room = null;
        for(TransactionGridDTO dto : grid){
            if(dto.getIsPending() && LocalDate.now().isAfter(dto.getCheckIn())){
                dto.setIsPending(false);
                dto.setIsExpired(true);
                room = roomRepository.findById(roomNumber).get();
                room.setIsReserved(false);
                roomRepository.save(room);
                transaction = transactionRepository.findById(dto.getId()).get();
                transaction.setIsPending(false);
                transaction.setIsExpired(true);
                transactionRepository.save(transaction);
            }
        }



        return grid;
    }

    @Override
    public long getTotalPages(String customerName, String roomNumber) {
        double totalData = (double)(transactionRepository.count(customerName, roomNumber));
        long totalPage = (long)(Math.ceil(totalData / rowsInPage));
        return totalPage;
    }

    @Override
    public List<TransactionGridDTO> getCustomerTransaction(Integer page, String username) {
        Pageable pagination = PageRequest.of(page -1,rowsInPage, Sort.by("id"));

        List<TransactionGridDTO> grid = transactionRepository.findAllByUsername(username, pagination);

        return grid;
    }

    @Override
    public long getDetailPages(String username) {
        double totalData = (double)(transactionRepository.count(username));
        long totalPage = (long)(Math.ceil(totalData / rowsInPage));
        return totalPage;
    }

    @Override
    public Customer getCustomerByUsername(String username) {
        CustomerGridDTO dto = customerRepository.findByUsername(username);
        Optional<Customer> tempCust = customerRepository.findById(dto.getId());
        return tempCust.get();
    }

    @Override
    public InsertTransactionDTO getTransactionToSee(Integer id) {
        Optional<Transaction> nullableEntity = transactionRepository.findById(id);
        Transaction transaction = nullableEntity.get();
        Optional<Room> roomOptional = roomRepository.findById(transaction.getRoomNumber());
        Room room = roomOptional.get();

        InsertTransactionDTO dto = new InsertTransactionDTO(
                transaction.getId(),
                transaction.getCheckIn(),
                transaction.getCheckOut(),
                transaction.getRoomNumber(),
                transaction.getCustomerId(),
                transaction.getBill(),
                transaction.getIsPending(),
                transaction.getIsExpired()
                );
        dto.setType(room.getType());
        dto.setPrice(room.getPrice());
        return dto;
    }

    @Override
    public InsertTransactionDTORest getTransactionToSeeRest(Integer id) {
        Optional<Transaction> nullableEntity = transactionRepository.findById(id);
        Transaction transaction = nullableEntity.get();
        Optional<Room> roomOptional = roomRepository.findById(transaction.getRoomNumber());
        Room room = roomOptional.get();

        InsertTransactionDTORest dto = new InsertTransactionDTORest(
                transaction.getId(),
                transaction.getCheckIn(),
                transaction.getCheckOut(),
                transaction.getRoomNumber(),
                transaction.getCustomerId(),
                transaction.getBill(),
                transaction.getIsPending(),
                transaction.getIsExpired()
        );
        dto.setIsConfirmed(transaction.getIsConfirmed());
        dto.setType(room.getType());
        dto.setPrice(room.getPrice());
        return dto;
    }

    @Override
    public void finishTransaction(Transaction transaction) {
        Room room = roomRepository.findById(transaction.getRoomNumber()).get();
        room.setIsReserved(false);
        room.setIsOccupied(false);
        roomRepository.save(room);
        transaction.setIsExpired(true);
        transactionRepository.save(transaction);
    }

    @Override
    public void expireTransaction(Transaction transaction) {
        Room room = roomRepository.findById(transaction.getRoomNumber()).get();
        room.setIsReserved(false);
        room.setIsOccupied(false);
        roomRepository.save(room);
        transaction.setIsExpired(true);
        transaction.setIsPending(false);
        transactionRepository.save(transaction);
    }

    @Override
    public InsertTransactionDTO findActiveTransaction(String roomNumber) {
        TransactionGridDTO transaction = transactionRepository.findActiveTransactionByRoom(roomNumber);

        Optional<Transaction> nullableEntity = transactionRepository.findById(transaction.getId());
        Transaction tempTransaction = nullableEntity.get();

        Optional<Room> tempRoom = roomRepository.findById(roomNumber);

        InsertTransactionDTO dto = new InsertTransactionDTO(
                transaction.getId(),
                transaction.getCheckIn(),
                transaction.getCheckOut(),
                transaction.getRoomNumber(),
                tempTransaction.getCustomerId(),
                transaction.getBill(),
                transaction.getIsPending(),
                transaction.getIsExpired()
        );
        dto.setIsConfirmed(tempTransaction.getIsConfirmed());
        dto.setType(tempRoom.get().getType());
        dto.setPrice(tempRoom.get().getPrice());
        return dto;
    }

    @Override
    public Page<TransactionGridDTO> findAllDTO(Pageable pageable, String name, String roomNumber) {
        Page<TransactionGridDTO> grid = transactionRepository.findAllDTOPage(name, roomNumber, pageable);
        Transaction transaction = null;
        Room room = null;
        for(TransactionGridDTO dto : grid){
            if(dto.getIsPending() && LocalDate.now().isAfter(dto.getCheckIn())){
                dto.setIsPending(false);
                dto.setIsExpired(true);
                room = roomRepository.findById(roomNumber).get();
                room.setIsReserved(false);
                roomRepository.save(room);
                transaction = transactionRepository.findById(dto.getId()).get();
                transaction.setIsPending(false);
                transaction.setIsExpired(true);
                transactionRepository.save(transaction);
            }
        }

        return grid;
    }

    @Override
    public Room findVacantRoom(String roomNumber) {
        RoomGridDTO dto = roomRepository.findVacantRoom(roomNumber);

        return (dto != null)?roomRepository.findById(dto.getRoomNumber()).get():null;
    }
}
