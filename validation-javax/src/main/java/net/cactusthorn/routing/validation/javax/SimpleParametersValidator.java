package net.cactusthorn.routing.validation.javax;

import java.lang.reflect.Method;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.executable.ExecutableValidator;

import net.cactusthorn.routing.validate.ParametersValidator;
import net.cactusthorn.routing.validate.ParametersValidationException;

public class SimpleParametersValidator implements ParametersValidator {

    ExecutableValidator executableValidator = Validation.buildDefaultValidatorFactory().getValidator().forExecutables();

    @Override //
    public void validate(Object object, Method method, Object[] parameters) throws ParametersValidationException {

        Set<ConstraintViolation<Object>> violations = executableValidator.validateParameters(object, method, parameters);

        if (violations.isEmpty()) {
            return;
        }

        String message = "";
        for (ConstraintViolation<Object> violation : violations) {
            message += violation.getPropertyPath() + " :: " + violation.getMessage() + ";";
        }
        throw new ParametersValidationException(message);
    }
}
