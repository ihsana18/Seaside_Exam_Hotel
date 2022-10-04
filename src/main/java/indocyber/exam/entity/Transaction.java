package indocyber.exam.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "Transactions")
@Setter @Getter
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class Transaction {

    @Id
    @Column(name = "Id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "CheckInDate")
    private LocalDate checkIn;

    @Column(name = "CheckOutDate")
    private LocalDate checkOut;

    @Column(name = "RoomNumber")
    private String roomNumber;

    @ManyToOne
    @JoinColumn(name = "RoomNumber", insertable = false, updatable = false)
    private Room room;

    @Column(name = "CustomerId")
    private Integer customerId;

    @ManyToOne
    @JoinColumn(name = "CustomerId", insertable = false, updatable = false)
    private Customer customer;

    @Column(name = "Bill")
    private BigDecimal bill;

    @Column(name = "IsPending")
    private Boolean isPending;

    @Column(name = "IsExpired")
    private Boolean isExpired;

    @Column(name = "IsConfirmed")
    private Boolean isConfirmed;

    public Transaction(Integer id, LocalDate checkIn, LocalDate checkOut, String roomNumber, Integer customerId, BigDecimal bill, Boolean isPending, Boolean isExpired) {
        this.id = id;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.roomNumber = roomNumber;
        this.customerId = customerId;
        this.bill = bill;
        this.isPending = isPending;
        this.isExpired = isExpired;
    }

    public Transaction(LocalDate checkIn, LocalDate checkOut, String roomNumber, Integer customerId, BigDecimal bill, Boolean isPending, Boolean isExpired) {
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.roomNumber = roomNumber;
        this.customerId = customerId;
        this.bill = bill;
        this.isPending = isPending;
        this.isExpired = isExpired;
    }


}
