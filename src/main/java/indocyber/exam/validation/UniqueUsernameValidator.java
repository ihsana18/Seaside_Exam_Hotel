package indocyber.exam.validation;

import indocyber.exam.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, String > {

    @Autowired
    private AccountService accountService;

    @Override
    public boolean isValid(String username, ConstraintValidatorContext constraintValidatorContext) {
        return !accountService.checkExistingUsername(username);
    }
}
