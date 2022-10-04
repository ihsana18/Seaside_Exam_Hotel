package indocyber.exam.dto.room;

import indocyber.exam.validation.UniqueRoomNumber;
import lombok.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWebApplication;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Setter @Getter
public class InsertRoomDTO {

    @UniqueRoomNumber(message = "Room number already existed")
    @NotBlank(message = "Room number is required")
    @Positive(message = "Room number must be a positive number")
    private String roomNumber;

    private String type;

    @Positive(message = "Price must be a positive number")
    @NotNull(message = "Price is required")
    private BigDecimal price;

    private Boolean isReserved;

    private Boolean isOccupied;


}
