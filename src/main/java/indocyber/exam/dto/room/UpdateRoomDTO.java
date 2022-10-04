package indocyber.exam.dto.room;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Setter @Getter
public class UpdateRoomDTO {

    private String roomNumber;

    private String type;

    @Positive(message = "Price must be a positive number")
    @NotNull(message = "Price is required")
    private BigDecimal price;

    private Boolean isReserved;

    private Boolean isOccupied;

}
