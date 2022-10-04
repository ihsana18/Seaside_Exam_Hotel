package indocyber.exam.rest;

import indocyber.exam.dto.room.RoomGridDTO;
import indocyber.exam.dto.room.UpdateRoomDTO;
import indocyber.exam.dto.transaction.InsertTransactionDTO;
import indocyber.exam.dto.transaction.InsertTransactionDTORest;
import indocyber.exam.dto.transaction.TransactionGridDTO;
import indocyber.exam.entity.Customer;
import indocyber.exam.entity.Room;
import indocyber.exam.entity.Transaction;
import indocyber.exam.exception.ErrorResponse;
import indocyber.exam.exception.NotAllowedException;
import indocyber.exam.exception.NotFoundException;
import indocyber.exam.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@Tag(
        description = "Transaction resources to create, read, update, or delete transaction data ",
        name = "Transaction Resources"
)
@RestController
@RequestMapping("/api/transactions")
public class TransactionRestController {

    @Autowired
    private TransactionService transactionService;

    @Operation(summary = "Show all reservation with check out day after today with paging details, can search the customer name or the room number. Available to Admin and SuperAdmin")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "Transaction list found",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TransactionGridDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid page supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Page not found",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "403", description = "Not authorized",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})
    })
    @GetMapping("/index")
    public ResponseEntity<Page<TransactionGridDTO>> getAllCurrentReservation(@RequestParam(defaultValue = "1") Integer page,
                                                                @RequestParam(defaultValue = "") String name,
                                                                @RequestParam(defaultValue = "") String roomNumber){
        Pageable pageable = PageRequest.of(page - 1,10, Sort.by("id"));
        Page<TransactionGridDTO> transactionPage = transactionService.findAllDTO(pageable, name, roomNumber);
        if(page > transactionPage.getTotalPages()){
            throw new NotFoundException("Page not found");
        }
        return new ResponseEntity<>(transactionPage, HttpStatus.OK);
    }

    @Operation(summary = "Reserve a room by its number. Only available to Customer")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "201", description = "Created a new reservation",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Transaction.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "403", description = "Not authorized",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})
    })
    @PostMapping("/reserve/{roomNumber}")
    public ResponseEntity<Object> reserveARoom(@PathVariable String roomNumber, @Valid @RequestBody InsertTransactionDTO dto, Authentication authentication){
        Customer cust = transactionService.getCustomerByUsername(authentication.getName());
        List<Transaction> listTransaction = cust.getListTransaction();
        for(Transaction tr : listTransaction){
            if(tr.getIsPending() || (tr.getIsConfirmed() && !tr.getIsExpired())){
                throw new NotAllowedException("You're not allowed to reserve another room if you have an active reservation");
            }
        }
        Room room = transactionService.findVacantRoom(roomNumber);
        if(room == null){
            throw new NotAllowedException("The room is not vacant");
        }

        dto.setCustomerId(cust.getId());
        dto.setPrice(room.getPrice());
        dto.setRoomNumber(roomNumber);
        dto.setType(room.getType());
        Transaction transaction = transactionService.saveTransactionFromDTO(dto);
        return new ResponseEntity<>(transaction, HttpStatus.CREATED);
    }

    @Operation(summary = "Get the authenticated customer's active reservation. Only available to Customer")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "Found the reservation or no active reservation available",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = InsertTransactionDTORest.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "403", description = "Not authorized",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})
    })
    @GetMapping("/activeReservation")
    public ResponseEntity<Object> getActiveReservation(Authentication authentication){
        Customer cust = transactionService.getCustomerByUsername(authentication.getName());
        List<Transaction> transactions = cust.getListTransaction();
        Transaction transaction = null;
        for(Transaction tr : transactions){
            if(tr.getIsPending() || (tr.getIsConfirmed() && !tr.getIsExpired())){
                transaction = tr;
            }
        }

        if(transaction == null){
            return ResponseEntity.status(HttpStatus.OK).body("No active reservation available");
        }


        InsertTransactionDTORest dto = transactionService.getTransactionToSeeRest(transaction.getId());

        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    @Operation(summary = "Cancel the authenticated customer's reservation. Only available to Customer")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "Canceled the reservation or no active reservation available",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = InsertTransactionDTORest.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "403", description = "Not authorized",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})
    })
    @DeleteMapping("/cancelReservation")
    public ResponseEntity<Object> cancelReservation(Authentication authentication){
        Customer cust = transactionService.getCustomerByUsername(authentication.getName());
        List<Transaction> transactions = cust.getListTransaction();
        Transaction transaction = null;
        for(Transaction tr : transactions){
            if(tr.getIsPending()){
                transaction = tr;
            }
        }
        if(transaction == null){
            return ResponseEntity.status(HttpStatus.OK).body("No active reservation available");
        }
        transactionService.deleteById(transaction.getId());
        return ResponseEntity.status(HttpStatus.OK).body("Successfully canceled reservation");
    }

    @Operation(summary = "Confirm the authenticated customer's reservation. Only available to Customer")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "Canceled the reservation or no active reservation available",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = InsertTransactionDTORest.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "403", description = "Not authorized",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})
    })
    @PostMapping("/confirmReservation")
    public ResponseEntity<Object> confirmReservation(Authentication authentication){
        Customer cust = transactionService.getCustomerByUsername(authentication.getName());
        List<Transaction> transactions = cust.getListTransaction();
        Transaction transaction = null;
        for(Transaction tr : transactions){
            if(tr.getIsPending()){
                transaction = tr;
            }
        }
        if(transaction == null){
            return ResponseEntity.status(HttpStatus.OK).body("No active reservation available");
        }

        InsertTransactionDTORest dto = transactionService.confirmTransactionById(transaction.getId());
        return ResponseEntity.status(HttpStatus.OK).body("Successfully confirmed reservation");
    }



}
