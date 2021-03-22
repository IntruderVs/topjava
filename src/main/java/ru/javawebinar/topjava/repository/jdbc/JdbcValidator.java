package ru.javawebinar.topjava.repository.jdbc;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Set;

class JdbcValidator {
    public static <V> void validate(Validator validator, V entity) {
        Set<ConstraintViolation<V>> validate = validator.validate(entity);
        if (validate.size() > 0) {
            //We need to throw ConstraintViolationException
            throw new ConstraintViolationException(validate);
        }
    }
}
