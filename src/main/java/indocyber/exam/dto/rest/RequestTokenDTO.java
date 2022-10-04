package indocyber.exam.dto.rest;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Setter @Getter
public class RequestTokenDTO {

    private String username;

    private String password;

    private String subject;


    private String secretKey;

    private String audience;


}
