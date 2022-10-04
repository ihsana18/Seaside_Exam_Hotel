package indocyber.exam.validation;

import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class AfterDateValidator implements ConstraintValidator<AfterDate, Object> {

    private String dateChecked;
    private String dateCheckedAfter;

    @Override
    public void initialize(AfterDate constraintAnnotation) {
        this.dateChecked = constraintAnnotation.dateChecked();
        this.dateCheckedAfter = constraintAnnotation.dateCheckedAfter();
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {

        LocalDate dateCheckedValue = (LocalDate)(new BeanWrapperImpl(o).getPropertyValue(dateChecked));
        LocalDate dateCheckedAfterValue = (LocalDate)(new BeanWrapperImpl(o).getPropertyValue(dateCheckedAfter));

        if(dateCheckedValue.isEqual(dateCheckedAfterValue)){
            return true;
        }

        return dateCheckedValue.isBefore(dateCheckedAfterValue);
    }
}