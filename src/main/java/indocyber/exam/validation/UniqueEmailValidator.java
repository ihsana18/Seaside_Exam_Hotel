package indocyber.exam.validation;

import indocyber.exam.entity.Customer;
import indocyber.exam.service.CustomerService;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, Object> {

    private String username;
    private String email;

    @Autowired
    private CustomerService customerService;

    @Override
    public void initialize(UniqueEmail constraintAnnotation) {
        this.username = constraintAnnotation.username();
        this.email = constraintAnnotation.email();
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext constraintValidatorContext) {
        String usernameValue = new BeanWrapperImpl(object).getPropertyValue(username).toString();
        String emailValue = new BeanWrapperImpl(object).getPropertyValue(email).toString();
        return !customerService.checkExistingEmail(usernameValue, emailValue);
    }

}
