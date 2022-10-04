package indocyber.exam.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "Customer")
@Setter @Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class Customer {


    @Id
    @Column(name = "Id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "Name")
    private String name;

    @Column(name = "Email")
    private String email;

    @Column(name = "Address")
    private String address;

    @Column(name = "Username")
    private String username;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "Username", insertable = false, updatable = false)
    private Account account;

    @OneToMany(mappedBy = "customer", fetch = FetchType.EAGER)
    private List<Transaction> listTransaction;

    public void addTransaction(Transaction transaction){
        if(this.listTransaction == null){
            this.listTransaction = new LinkedList<>();
        }
        this.listTransaction.add(transaction);
    }

    public Customer(String name, String email, String address, String username) {
        this.name = name;
        this.email = email;
        this.address = address;
        this.username = username;
    }

}
