package indocyber.exam.service;

import indocyber.exam.dto.customer.CustomerGridDTO;
import indocyber.exam.dto.room.InsertRoomDTO;
import indocyber.exam.dto.room.RoomGridDTO;
import indocyber.exam.dto.room.UpdateRoomDTO;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class RoomServiceImpl implements RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CustomerRepository customerRepository;

    private final int rowsInPage = 10;

    @Override
    public void deleteTransaction(Transaction transaction) {
        transactionRepository.delete(transaction);
    }

    @Override
    public List<Room> findAll() {
        return roomRepository.findAll();
    }

    @Override
    public Room findById(String roomNumber) {
        return roomRepository.findById(roomNumber).get();
    }

    @Override
    public void deleteById(String roomNumber) {
        roomRepository.deleteById(roomNumber);
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
    public InsertTransactionDTORest findActiveTransactionRest(String roomNumber) {
        TransactionGridDTO transaction = transactionRepository.findActiveTransactionByRoom(roomNumber);

        Optional<Transaction> nullableEntity = transactionRepository.findById(transaction.getId());
        Transaction tempTransaction = nullableEntity.get();

        Optional<Room> tempRoom = roomRepository.findById(roomNumber);

        InsertTransactionDTORest dto = new InsertTransactionDTORest(
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
    public List<RoomGridDTO> getRoomGrid(Integer page, String roomNumber) {
        Pageable pagination = PageRequest.of(page -1,rowsInPage, Sort.by("roomNumber"));

        List<RoomGridDTO> grid = roomRepository.findAll(roomNumber, pagination);

        TransactionGridDTO gridDTO = null;
        Transaction transaction = null;
        Room room = null;
        for(RoomGridDTO dto : grid){
            gridDTO = transactionRepository.findActiveTransactionByRoom(dto.getRoomNumber());
            if(gridDTO != null){
                transaction = transactionRepository.findById(gridDTO.getId()).get();
                if(dto.getIsReserved() && transaction.getCheckIn().equals(LocalDate.now()) && transaction.getIsConfirmed()){
                    room = roomRepository.findById(dto.getRoomNumber()).get();
                    room.setIsOccupied(true);
                    room.setIsReserved(false);
                    roomRepository.save(room);
                    dto.setIsOccupied(true);
                    dto.setIsReserved(false);
                }
            }

        }

        return grid;
    }

    @Override
    public Page<RoomGridDTO> findAllDTO(Pageable pageable, String roomNumber) {
        Page<RoomGridDTO> grid = roomRepository.findAllDTOPage(roomNumber, pageable);
        TransactionGridDTO gridDTO = null;
        Transaction transaction = null;
        Room room = null;
        for(RoomGridDTO dto : grid){
            gridDTO = transactionRepository.findActiveTransactionByRoom(dto.getRoomNumber());
            if(gridDTO != null){
                transaction = transactionRepository.findById(gridDTO.getId()).get();
                if(dto.getIsReserved() && transaction.getCheckIn().equals(LocalDate.now()) && (transaction.getIsConfirmed() && !transaction.getIsExpired())){
                    room = roomRepository.findById(dto.getRoomNumber()).get();
                    room.setIsOccupied(true);
                    room.setIsReserved(false);
                    roomRepository.save(room);
                    dto.setIsOccupied(true);
                    dto.setIsReserved(false);
                }
            }
        }
        return grid;
    }

    @Override
    public long getTotalPages(String roomNumber) {
        double totalData = (double)(roomRepository.count(roomNumber));
        long totalPage = (long)(Math.ceil(totalData / rowsInPage));
        return totalPage;
    }

    @Override
    public List<RoomGridDTO> getVacantRoomGrid(Integer page, String roomType) {
        Pageable pagination = PageRequest.of(page -1,rowsInPage, Sort.by("roomNumber"));

        List<RoomGridDTO> grid = roomRepository.findAllVacantRoom(roomType, pagination);

        return grid;
    }

    @Override
    public Page<RoomGridDTO> findAllVacantDTO(Pageable pageable, String roomType) {
        return roomRepository.findAllVacantPage(roomType, pageable);
    }

    @Override
    public long getVacantTotalPages(String roomType) {
        double totalData = (double)(roomRepository.countVacantRoom(roomType));
        long totalPage = (long)(Math.ceil(totalData / rowsInPage));
        return totalPage;
    }

    @Override
    public UpdateRoomDTO getRoomToUpdate(String roomNumber) {
        Optional<Room> nullableEntity = roomRepository.findById(roomNumber);
        Room entity = nullableEntity.get();
        UpdateRoomDTO roomDTO = new UpdateRoomDTO(
                entity.getRoomNumber(),
                entity.getType(),
                entity.getPrice(),
                entity.getIsReserved(),
                entity.getIsOccupied()
        );

        return roomDTO;
    }

    @Override
    public void insertRoom(InsertRoomDTO dto) {
        Room entity = new Room(
                dto.getRoomNumber(),
                dto.getType(),
                dto.getPrice(),
                false,
                false
        );

        roomRepository.save(entity);
    }

    @Override
    public void updateRoom(UpdateRoomDTO dto) {

        Room room = roomRepository.findById(dto.getRoomNumber()).get();

        Room entity = new Room(
                dto.getRoomNumber(),
                dto.getType(),
                dto.getPrice(),
                false,
                false
        );
        entity.setTransactionList(room.getTransactionList());

        roomRepository.save(entity);
    }


    @Override
    public Customer getCustomerByUsername(String username) {
        CustomerGridDTO dto = customerRepository.findByUsername(username);
        Optional<Customer> tempCustomer = customerRepository.findById(dto.getId());
        return tempCustomer.get();
    }

    @Override
    public Boolean checkExistingRoomNumber(String roomNumber) {
        Long totalRoom = customerRepository.countExistingRoomNumber(roomNumber);
        return totalRoom > 0;
    }
}
