package indocyber.exam.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "Admin")
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Setter @Getter
public class Admin {

    @Id
    @Column(name = "Id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "Username")
    private String username;

    @OneToOne
    @JoinColumn(name = "Username", insertable = false, updatable = false)
    private Account account;

    @Column(name = "JobTitle")
    private String jobTitle;


    public Admin(String username, String jobTitle) {
        this.username = username;
        this.jobTitle = jobTitle;
    }

    public Admin(Integer id, String username, String jobTitle) {
        this.id = id;
        this.username = username;
        this.jobTitle = jobTitle;
    }
}
