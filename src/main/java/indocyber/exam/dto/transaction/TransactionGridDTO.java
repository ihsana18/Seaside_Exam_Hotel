package indocyber.exam.dto.transaction;

import indocyber.exam.utility.Helper;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Setter @Getter
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class TransactionGridDTO {

    private Integer id;

    private String customerName;

    private String roomNumber;

    private LocalDate checkIn;

    private LocalDate checkOut;

    private BigDecimal bill;

    private Boolean isPending;

    private Boolean isExpired;

    private Boolean isConfirmed;


    public String getBillFormatRupiah(){
        return Helper.formatRupiah(this.bill);
    }

    public String getStatus(){
        if(this.isConfirmed == true && this.isExpired == true){
            return "Past Transaction";
        }else if(this.isPending == true){
            return "Pending";
        } else if(this.isExpired == true){
            return "Expired";
        } else if(this.isConfirmed == true){
            return "Confirmed";
        } else{
            return "You're not supposed to see it";
        }
    }

}
