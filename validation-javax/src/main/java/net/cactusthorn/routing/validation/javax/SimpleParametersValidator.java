package net.cactusthorn.routing.validation.javax;

import java.lang.reflect.Method;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.executable.ExecutableValidator;
import javax.ws.rs.BadRequestException;

import net.cactusthorn.routing.validate.ParametersValidator;

public class SimpleParametersValidator implements ParametersValidator {

    ExecutableValidator executableValidator = Validation.buildDefaultValidatorFactory().getValidator().forExecutables();

    @Override //
    public void validate(Object object, Method method, Object[] parameters) {

        Set<ConstraintViolation<Object>> violations = executableValidator.validateParameters(object, method, parameters);

        if (violations.isEmpty()) {
            return;
        }

        String message = "";
        for (ConstraintViolation<Object> violation : violations) {
            message += violation.getPropertyPath() + " :: " + violation.getMessage() + ";";
        }
        throw new BadRequestException(message);
    }
}
