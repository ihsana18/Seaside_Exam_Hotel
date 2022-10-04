package indocyber.exam.rest;

import indocyber.exam.dto.customer.InsertCustomerDTO;
import indocyber.exam.dto.rest.RequestTokenDTO;
import indocyber.exam.dto.rest.ResponseTokenDTO;
import indocyber.exam.dto.transaction.InsertTransactionDTORest;
import indocyber.exam.exception.ErrorResponse;
import indocyber.exam.service.AccountService;
import indocyber.exam.utility.JwtToken;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@Tag(
        description = "For authenticating/login and registering customer",
        name = "Account Resources"
)
@RestController
@RequestMapping("/api")
public class AccountRestController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private JwtToken jwtToken;

    @Operation(summary = "Authenticating users to get JWT Token")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "Successfully authenticated",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResponseTokenDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Fail to authenticate",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})
    })
    @PostMapping("/authenticate")
    public ResponseTokenDTO post(@RequestBody RequestTokenDTO dto){

        try{
            UsernamePasswordAuthenticationToken token =
                    new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword());

            Authentication authentication = authenticationManager.authenticate(token);

        } catch(Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can not authenticate", e);
        }


        String role = accountService.getAccountRole(dto.getUsername());

        String token = jwtToken.generateToken(
                dto.getSubject(),
                dto.getUsername(),
                role
        );

        ResponseTokenDTO responseTokenDTO = new ResponseTokenDTO(dto.getUsername(), role, token);

        return responseTokenDTO;

    }

    @Operation(summary = "Register to app as a customer")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "201", description = "Successfully register",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = InsertCustomerDTO.class))}),
            @ApiResponse(responseCode = "422", description = "Fail to register because of a validation error",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})
    })
    @PostMapping("/register")
    public ResponseEntity<Object> addAccount(@Valid @RequestBody InsertCustomerDTO dto){
        accountService.registerCustomer(dto);

        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

}
