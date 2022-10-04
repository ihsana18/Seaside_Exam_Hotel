package indocyber.exam.rest;

import indocyber.exam.dto.admin.AdminGridDTO;
import indocyber.exam.dto.admin.UpdateAdminDTO;
import indocyber.exam.dto.customer.CustomerGridDTO;
import indocyber.exam.dto.customer.UpdateCustomerDTO;
import indocyber.exam.dto.room.RoomGridDTO;
import indocyber.exam.dto.transaction.TransactionGridDTO;
import indocyber.exam.entity.Customer;
import indocyber.exam.entity.Room;
import indocyber.exam.exception.ErrorResponse;
import indocyber.exam.exception.NotAllowedException;
import indocyber.exam.exception.NotAuthorizedException;
import indocyber.exam.exception.NotFoundException;
import indocyber.exam.service.CustomerService;
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
import java.util.List;

@Tag(
        description = "Customer resources to create, read, update, or customer data ",
        name = "Customer Resources"
)
@RestController
@RequestMapping("/api/customers")
public class CustomerRestController {

    @Autowired
    private CustomerService customerService;

    @Operation(summary = "Show all customer with paging details, can search its name. Available to Admin and SuperAdmin")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "Customer list found",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CustomerGridDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid page supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Page not found",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "403", description = "Not authorized",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})
    })
    @GetMapping("/index")
    public ResponseEntity<Page<CustomerGridDTO>> getAllCustomers(@RequestParam(defaultValue = "1") Integer page,
                                                                 @RequestParam(defaultValue = "") String name){
        Pageable pageable = PageRequest.of(page - 1,10, Sort.by("name"));
        Page<CustomerGridDTO> custPage = customerService.findAllDTO(pageable, name);
        if(page > custPage.getTotalPages()){
            throw new NotFoundException("Page not found");
        }
        return new ResponseEntity<>(custPage, HttpStatus.OK);
    }

    @Operation(summary = "Get a customer by its username. Available to Admin and SuperAdmin")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "Found the customer",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CustomerGridDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Customer not found",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "403", description = "Not authorized",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})
    })
    @GetMapping("/get/{username}")
    public ResponseEntity<Object> getCustomer(@PathVariable String username){
        Customer dto = null;
        dto = customerService.findByUsername(username);
        if(dto == null){
            throw new NotFoundException("Admin with username "+username+" is not found");
        }
        return new ResponseEntity<>(dto,HttpStatus.OK);
    }


    @Operation(summary = "Get a customer history by its username with paging details. Available to Admin and SuperAdmin")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "Found the customer",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TransactionGridDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Customer or page not found",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "403", description = "Not authorized",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})
    })
    @GetMapping("/history/{username}")
    public ResponseEntity<Object> getCustomerTransactionHistory(@RequestParam(defaultValue = "1") Integer page, @PathVariable String username){
        Pageable pageable = PageRequest.of(page - 1,10, Sort.by("id"));
        Page<TransactionGridDTO> transactionGrid = customerService.getCustomerTransaction(pageable, username);

        if(page > transactionGrid.getTotalPages()){
            throw new NotFoundException("Page not found");
        }

        return new ResponseEntity<>(transactionGrid, HttpStatus.OK);
    }

    @Operation(summary = "Get the authenticated customer's history with paging details. Available to customer")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "Found the customer",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TransactionGridDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Customer or page not found",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "403", description = "Not authorized",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})
    })
    @GetMapping("/myHistory")
    public ResponseEntity<Object> getCustomerTransactionHistoryForCustomer(@RequestParam(defaultValue = "1") Integer page, Authentication authentication){
        Pageable pageable = PageRequest.of(page - 1,10, Sort.by("id"));
        Page<TransactionGridDTO> transactionGrid = customerService.getCustomerTransaction(pageable, authentication.getName());

        if(page > transactionGrid.getTotalPages()){
            throw new NotFoundException("Page not found");
        }

        return new ResponseEntity<>(transactionGrid, HttpStatus.OK);
    }

    @Operation(summary = "Update a customer by its username. Only available to Customer")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "Found the customer and updated",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UpdateCustomerDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Customer not found",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "403", description = "Not authorized",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})
    })
    @PutMapping("/update/{username}")
    public ResponseEntity<Object> updateAdmin(@PathVariable String username, @Valid @RequestBody UpdateCustomerDTO dto, Authentication authentication){
        if (!username.equals(authentication.getName())){
            throw new NotAuthorizedException("You're not allowed to change other's profile");
        }
        Customer customer = null;
        customer = customerService.findByUsername(username);
        if(customer == null){
            throw new NotFoundException("Customer with username "+username+" is not found");
        }
        customerService.updateCustomerFromDTO(dto);
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    @Operation(summary = "Delete a customer by its username. Available to Admin and SuperAdmin")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "Found the customer and deleted"),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "405", description = "Failed to delete customer because they already have a transaction history"),
            @ApiResponse(responseCode = "404", description = "Customer not found",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "403", description = "Not authorized",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})
    })
    @DeleteMapping("/delete/{username}")
    public ResponseEntity<Object> deleteRoom(@PathVariable String username){
        Customer customer = null;
        customer = customerService.findByUsername(username);
        if(customer == null){
            throw new NotFoundException("Customer with username "+username+" not Found");
        }


        if(customer.getListTransaction().size() == 0){
            customerService.deleteById(customer.getId());
            return ResponseEntity.status(HttpStatus.OK).body(username);
        } else {
            throw new NotAllowedException("The customer already has a transaction history");
        }
    }

}
