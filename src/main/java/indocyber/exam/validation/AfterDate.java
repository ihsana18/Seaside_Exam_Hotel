package indocyber.exam.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Constraint(validatedBy = AfterDateValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(AfterDates.class)
public @interface AfterDate {
    public Class<?>[] groups() default {};
    public Class<? extends Payload>[] payload() default {};
    public String message();
    public String dateChecked();
    public String dateCheckedAfter();

}