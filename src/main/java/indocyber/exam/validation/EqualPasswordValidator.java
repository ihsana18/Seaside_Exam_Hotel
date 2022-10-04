package indocyber.exam.validation;


import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EqualPasswordValidator implements ConstraintValidator<EqualPassword, Object> {
    private String password;
    private String passwordConfirmation;


    @Override
    public void initialize(EqualPassword constraintAnnotation) {
        this.password = constraintAnnotation.password();
        this.passwordConfirmation = constraintAnnotation.passwordConfirmation();
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        String pass = new BeanWrapperImpl(o).getPropertyValue(password).toString();
        String passConfirm = new BeanWrapperImpl(o).getPropertyValue(passwordConfirmation).toString();

        if(pass.equals(passConfirm)){
            return true;
        } else{
            return false;
        }
    }
}
