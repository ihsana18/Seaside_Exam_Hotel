package indocyber.exam.rest;

import indocyber.exam.dto.admin.AdminGridDTO;
import indocyber.exam.dto.admin.InsertAdminDTO;
import indocyber.exam.dto.customer.CustomerGridDTO;
import indocyber.exam.dto.customer.UpdateCustomerDTO;
import indocyber.exam.dto.room.InsertRoomDTO;
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
import indocyber.exam.exception.NotAuthorizedException;
import indocyber.exam.exception.NotFoundException;
import indocyber.exam.service.RoomService;
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
import java.nio.file.AccessDeniedException;
import java.util.List;

@Tag(
        description = "Room resources to create, read, update, or room data ",
        name = "Room Resources"
)
@RestController
@RequestMapping("/api/rooms")
public class RoomRestController {

    @Autowired
    private RoomService roomService;

    @Operation(summary = "Show all rooms  with paging details, can search their room number. Available to Admin and SuperAdmin")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "Room list found",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = RoomGridDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid page supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Page not found",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "403", description = "Not authorized",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})
    })
    @GetMapping("/index")
    public ResponseEntity<Page<RoomGridDTO>> getAllRooms(@RequestParam(defaultValue = "1") Integer page,
                                                         @RequestParam(defaultValue = "") String roomNumber){
        Pageable pageable = PageRequest.of(page - 1,10, Sort.by("roomNumber"));
        Page<RoomGridDTO> roomPage = roomService.findAllDTO(pageable, roomNumber);
        if(page > roomPage.getTotalPages()){
            throw new NotFoundException("Page not found");
        }
        return new ResponseEntity<>(roomPage, HttpStatus.OK);
    }

    @Operation(summary = "Get a room by its number. Available to Admin and SuperAdmin")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "Found the room",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = RoomGridDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Room not found",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "403", description = "Not authorized",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})
    })
    @GetMapping("/get/{roomNumber}")
    public ResponseEntity<Object> getARoom(@PathVariable String roomNumber){
        Room room = null;
        room = roomService.findById(roomNumber);
        if(room == null){
            throw new NotFoundException("Room with number "+roomNumber+" not Found");
        }
        return new ResponseEntity<>(room, HttpStatus.OK);
    }

    @Operation(summary = "Get a room active transaction/reservation by its room number. Available to Admin and SuperAdmin")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "Found the room",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = InsertTransactionDTORest.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Room or page not found",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "403", description = "Not authorized",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})
    })
    @GetMapping("/detail/{roomNumber}")
    public ResponseEntity<Object> getRoomDetails(@PathVariable String roomNumber){
       try{
           Room room = null;
           room = roomService.findById(roomNumber);
           if(room == null){
               throw new NotFoundException("Room with number "+roomNumber+" not Found");
           }

           InsertTransactionDTORest dto = roomService.findActiveTransactionRest(roomNumber);

           return new ResponseEntity<>(dto, HttpStatus.OK);
       } catch (Exception e){
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("There is a run-time error on the server.");
       }
    }

    @Operation(summary = "Adding a room. Available to Admin and SuperAdmin")
    @ApiResponses(value={
            @ApiResponse( responseCode = "201", description = "Successfully added the Admin",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = InsertRoomDTO.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "403", description = "Not authorized",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})
    })
    @PostMapping("/add")
    public ResponseEntity<Object> addRoom(@Valid @RequestBody InsertRoomDTO dto){
        if(dto.getType().equals("Single") || dto.getType().equals("Double") || dto.getType().equals("Triple") ){
            roomService.insertRoom(dto);
            return new ResponseEntity<>(dto,HttpStatus.CREATED);
        } else {
            throw new NotAllowedException("Room type is not allowed");
        }
    }

    @Operation(summary = "Delete a room by its number. Available to Admin and SuperAdmin")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "Found the room and deleted"),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "405", description = "Failed to delete room because they already have a transaction history"),
            @ApiResponse(responseCode = "404", description = "Customer not found",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "403", description = "Not authorized",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})
    })
    @DeleteMapping("/delete/{roomNumber}")
    public ResponseEntity<Object> deleteRoom(@PathVariable String roomNumber){
        Room room = null;
        room = roomService.findById(roomNumber);
        if(room == null){
            throw new NotFoundException("Room with number "+roomNumber+" not Found");
        }

        if(room.getIsReserved() || room.getIsOccupied()){
            throw new NotAllowedException("The room is still reserved or occupied");
        }

        if(room.getTransactionList().size() == 0){
            roomService.deleteById(roomNumber);
            return ResponseEntity.status(HttpStatus.OK).body(roomNumber);
        } else {
            throw new NotAllowedException("The room already has a transaction history");
        }
    }

    @Operation(summary = "Update a room by its number. Available to Admin and SuperAdmin")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "Found the Room and updated",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UpdateRoomDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Customer not found",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "403", description = "Not authorized",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})
    })
    @PutMapping("/update/{roomNumber}")
    public ResponseEntity<Object> updateRoom(@PathVariable String roomNumber,
                                             @Valid @RequestBody UpdateRoomDTO dto){
        Room room = null;
        room = roomService.findById(roomNumber);
        if(room == null){
            throw new NotFoundException("Room with number "+roomNumber+" not Found");
        }
        if(room.getIsReserved() || room.getIsOccupied()){
            throw new NotAllowedException("The room is still reserved or occupied");
        }

        roomService.updateRoom(dto);

        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    @Operation(summary = "Show all vacant rooms with paging details, can search their type. Only available to Customer")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "Room list found",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = RoomGridDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid page supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Page not found",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "403", description = "Not authorized",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})
    })
    @GetMapping("/vacantRoom")
    public ResponseEntity<Page<RoomGridDTO>> getAllVacantRooms(@RequestParam(defaultValue = "1") Integer page,
                                                         @RequestParam(defaultValue = "") String roomType){
        Pageable pageable = PageRequest.of(page - 1,10, Sort.by("roomNumber"));
        Page<RoomGridDTO> roomPage = roomService.findAllVacantDTO(pageable, roomType);
        if(page > roomPage.getTotalPages()){
            throw new NotFoundException("Page not found");
        }
        return new ResponseEntity<>(roomPage, HttpStatus.OK);
    }

    @Operation(summary = "Get a vacant room by its number. Only available to customer")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "Found the room",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = RoomGridDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Room not found",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "403", description = "Not authorized",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})
    })
    @GetMapping("/get/vacant/{roomNumber}")
    public ResponseEntity<Object> getAVacantRoom(@PathVariable String roomNumber){
        Room room = null;
        room = roomService.findById(roomNumber);
        if(room == null){
            throw new NotFoundException("Room with number "+roomNumber+" not Found");
        }
        if(room.getIsReserved() || room.getIsReserved()){
            throw new NotAuthorizedException("Room is still reserved or occupied");
        }
        return new ResponseEntity<>(room, HttpStatus.OK);
    }


}
