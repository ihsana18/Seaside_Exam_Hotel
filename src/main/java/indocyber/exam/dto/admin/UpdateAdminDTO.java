package indocyber.exam.dto.admin;

import com.fasterxml.jackson.annotation.JsonIgnore;
import indocyber.exam.validation.EqualPassword;
import indocyber.exam.validation.UniqueUsername;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@EqualPassword(message = "Password and Password Confirmation doesn't match", password = "password", passwordConfirmation = "passwordConfirmation")
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Setter @Getter
public class UpdateAdminDTO {
    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Job Title is required")
    private String jobTitle;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Password Confirmation is required")
    private String passwordConfirmation;

    public UpdateAdminDTO(String username, String jobTitle) {
        this.username = username;
        this.jobTitle = jobTitle;
    }
}
