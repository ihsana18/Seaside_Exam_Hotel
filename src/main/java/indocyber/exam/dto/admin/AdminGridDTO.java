package indocyber.exam.dto.admin;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Setter @Getter
public class AdminGridDTO {
    private Integer id;

    private String username;

    private String jobTitle;

    public AdminGridDTO(String username, String jobTitle) {
        this.username = username;
        this.jobTitle = jobTitle;
    }
}
