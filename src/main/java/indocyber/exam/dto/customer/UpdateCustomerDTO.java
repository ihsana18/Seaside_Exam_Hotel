package indocyber.exam.dto.customer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import indocyber.exam.validation.EqualPassword;
import indocyber.exam.validation.UniqueEmail;
import indocyber.exam.validation.UniqueUsername;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@EqualPassword(message = "Password and Password Confirmation doesn't match", password = "password", passwordConfirmation = "passwordConfirmation")
@UniqueEmail(message = "Email already existed", username = "username", email = "email")
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Setter @Getter
public class UpdateCustomerDTO {

    private Integer id;
    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Username is required")
    @Email(message = "Must be a valid email address")
    private String email;

    private String address;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Password Confirmation is required")
    private String passwordConfirmation;

    public UpdateCustomerDTO(String username, String name, String email, String address) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.address = address;
    }
}
