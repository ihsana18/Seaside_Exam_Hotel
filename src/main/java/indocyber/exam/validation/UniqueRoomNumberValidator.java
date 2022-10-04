package indocyber.exam.validation;

import indocyber.exam.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueRoomNumberValidator implements ConstraintValidator<UniqueRoomNumber, String> {

    @Autowired
    private RoomService roomService;

    @Override
    public boolean isValid(String roomNumber, ConstraintValidatorContext constraintValidatorContext) {
        return !roomService.checkExistingRoomNumber(roomNumber);
    }
}
