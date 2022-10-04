package indocyber.exam.entity;


import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "Room")
@Setter @Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class Room {

    @Id
    @Column(name = "RoomNumber")
    private String roomNumber;

    @Column(name = "RoomType")
    private String type;

    @Column(name = "PricePerDay")
    private BigDecimal price;

    @Column(name = "IsReserved")
    private Boolean isReserved;

    @Column(name = "IsOccupied")
    private Boolean isOccupied;

    @OneToMany(mappedBy = "room", fetch = FetchType.EAGER)
    private List<Transaction> transactionList;

    public Room(String roomNumber, String type, BigDecimal price, Boolean isReserved, Boolean isOccupied) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.price = price;
        this.isReserved = isReserved;
        this.isOccupied = isOccupied;
    }
    public void addTransaction(Transaction transaction){
        if(this.transactionList == null){
            this.transactionList = new LinkedList<>();
        }
        this.transactionList.add(transaction);
    }
}
