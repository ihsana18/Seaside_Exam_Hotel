package indocyber.exam.dto.customer;

import lombok.*;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Setter @Getter
public class CustomerGridDTO {

    private Integer id;

    private String name;

    private String email;

    private String address;

    private String username;

    public CustomerGridDTO(Integer id, String name, String email, String address) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.address = address;
    }
}
