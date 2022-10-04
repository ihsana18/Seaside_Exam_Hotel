package indocyber.exam.dto.room;


import indocyber.exam.utility.Helper;
import lombok.*;
import java.math.BigDecimal;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Setter @Getter
public class RoomGridDTO {

    private String roomNumber;

    private String type;

    private BigDecimal price;

    private Boolean isOccupied;

    private Boolean isReserved;

    public String getStatus(){
        if(this.isOccupied == true){
            return "Occupied";
        } else if(this.isReserved == true){
            return "Reserved";
        } else{ return "Vacant"; }
    }

    public String getPriceFormatRupiah(){
        return Helper.formatRupiah(this.price);
    }
}
