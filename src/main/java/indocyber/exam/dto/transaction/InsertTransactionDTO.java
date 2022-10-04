package indocyber.exam.dto.transaction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import indocyber.exam.validation.AfterDate;
import lombok.*;
import org.hibernate.collection.internal.PersistentIdentifierBag;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.criteria.CriteriaBuilder;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

@AfterDate(message ="You cannot check out before you checked in" ,dateChecked = "checkIn", dateCheckedAfter = "checkOut")
@AfterDate(message ="You cannot check in in the past", dateChecked = "today", dateCheckedAfter = "checkIn")
@AfterDate(message ="You cannot check out in the past", dateChecked = "today", dateCheckedAfter = "checkOut")
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Setter @Getter
public class InsertTransactionDTO {

    private Integer id;

    @NotNull(message = "Check in date is required")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkIn;

    @NotNull(message = "Check out date is required")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkOut;

    @NotNull
    private String roomNumber;

    private Integer customerId;

    @Positive
    private BigDecimal bill;

    private Boolean isPending;

    private Boolean isExpired;

    private Boolean isConfirmed;

    @Positive
    private BigDecimal price;

    private String type;

    private LocalDate today = LocalDate.now();

    public String getStatus(){
        if(this.getIsPending() == true){
            return "Pending";
        } else if (this.getIsConfirmed() == true && this.getIsExpired() == false){
            return "Confirmed";
        } else {
            return "Error";
        }
    }
    public InsertTransactionDTO(Integer id, LocalDate checkIn, LocalDate checkOut, String roomNumber, Integer customerId, BigDecimal bill, Boolean isPending, Boolean isExpired) {
        this.id = id;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.roomNumber = roomNumber;
        this.customerId = customerId;
        this.bill = bill;
        this.isPending = isPending;
        this.isExpired = isExpired;
    }

}
