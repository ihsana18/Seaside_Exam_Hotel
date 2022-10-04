package indocyber.exam.rest;

import indocyber.exam.dto.admin.AdminGridDTO;
import indocyber.exam.dto.admin.InsertAdminDTO;
import indocyber.exam.dto.admin.UpdateAdminDTO;
import indocyber.exam.dto.customer.InsertCustomerDTO;
import indocyber.exam.exception.ErrorResponse;
import indocyber.exam.exception.NotAuthorizedException;
import indocyber.exam.exception.NotFoundException;
import indocyber.exam.service.AdminService;
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

@Tag(
        description = "Admin resources to create, read, update, or delete admin data ",
        name = "Admin Resources"
)
@RestController
@RequestMapping("/api/admins")
public class AdminRestController {

    @Autowired
    private AdminService adminService;

    @Operation(summary = "Show all admins (except superAdmin) with paging details. Only available to SuperAdmin")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "Admin list found",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AdminGridDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid page supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Page not found",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "403", description = "Not authorized",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})
    })
    @GetMapping("/index")
    public ResponseEntity<Page<AdminGridDTO>> getAllAdmins(@RequestParam(defaultValue = "1") Integer page){
        Pageable pageable = PageRequest.of(page - 1,10, Sort.by("username"));

        Page<AdminGridDTO> adminPage = adminService.findAllDTO(pageable);

        if(page > adminPage.getTotalPages()){
            throw new NotFoundException("Page not found");
        }

        return new ResponseEntity<>(adminPage, HttpStatus.OK);
    }

    @Operation(summary = "Adding an admin. Only available to SuperAdmin")
    @ApiResponses(value={
            @ApiResponse( responseCode = "201", description = "Successfully added the Admin",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = InsertAdminDTO.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "403", description = "Not authorized",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})
    })
    @PostMapping("/add")
    public ResponseEntity<InsertAdminDTO> postAdmin(@Valid @RequestBody InsertAdminDTO dto){
        adminService.insertAdmin(dto);
        return new ResponseEntity<>(dto,HttpStatus.CREATED);
    }

    @Operation(summary = "Delete an admin by its username. Only available to SuperAdmin")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "Found the admin and deleted"),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "405", description = "Method not allowed"),
            @ApiResponse(responseCode = "404", description = "Admin not found",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "403", description = "Not authorized",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})
    })
    @DeleteMapping("/delete/{username}")
    public ResponseEntity<Object> deleteAdmin(@PathVariable String username){
        adminService.deleteByUsername(username);
        return ResponseEntity.status(HttpStatus.OK).body(username);
    }


    @Operation(summary = "Get an admin by its username. Available to Admin and SuperAdmin")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "Found the admin",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AdminGridDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Admin not found",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "403", description = "Not authorized",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})
    })
    @GetMapping("/get/{username}")
    public ResponseEntity<Object> getAnAdmin(@PathVariable String username, Authentication authentication){
        AdminGridDTO dto = null;
        if(!username.equals(authentication.getName()) && authentication.getAuthorities().toString().equals("[Admin]")){
            throw new NotAuthorizedException("You are not authorized to see other admin's profile");
        }
        dto = adminService.findByUsername(username);
        if(dto == null){
            throw new NotFoundException("Admin with username "+username+" is not found");
        }
        return new ResponseEntity<>(dto,HttpStatus.OK);
    }

    @Operation(summary = "Update an admin by its username. Only available to Admin")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "Found the admin and updated",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UpdateAdminDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Admin not found",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "403", description = "Not authorized",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})
    })
    @PutMapping("/update/{username}")
    public ResponseEntity<Object> updateAdmin(@PathVariable String username, @Valid @RequestBody UpdateAdminDTO dto, Authentication authentication){
        if (!username.equals(authentication.getName())){
            throw new NotAuthorizedException("You're not allowed to change other's profile");
        }
        adminService.updateAdminFromDTO(dto);
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }


}
