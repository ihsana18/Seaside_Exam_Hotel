package indocyber.exam.entity;

import lombok.*;
import org.hibernate.annotations.GeneratorType;

import javax.persistence.*;

@Entity
@Table(name = "Account")
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Setter @Getter
public class Account {

    @Id
    @Column(name = "Username")
    private String username;

    @Column(name = "Password")
    private String password;

    @Column(name = "Role")
    private String role;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL)
    private Admin admin;


    public Account(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

}
