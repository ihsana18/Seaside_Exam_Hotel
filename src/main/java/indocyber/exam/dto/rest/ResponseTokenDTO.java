package indocyber.exam.dto.rest;

import lombok.*;

@Setter @Getter
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class ResponseTokenDTO {

    private String username;

    private String role;

    private String token;

}