package org.test.calendar.validation.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;
import java.time.ZonedDateTime;

public class SameDayValidator implements ConstraintValidator<SameDay, Object> {
    
    private String fromField;
    private String toField;
    
    @Override
    public void initialize(SameDay constraintAnnotation) {
        this.fromField = constraintAnnotation.fromField();
        this.toField = constraintAnnotation.toField();
    }
    
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        
        try {
            ZonedDateTime fromDate = getFieldValue(value, fromField);
            ZonedDateTime toDate = getFieldValue(value, toField);
            
            if (fromDate == null || toDate == null) {
                return true;
            }
            
            boolean sameDay = fromDate.toLocalDate().equals(toDate.toLocalDate());
            boolean fromBeforeTo = fromDate.isBefore(toDate);
            
            return sameDay && fromBeforeTo;
            
        } catch (Exception e) {
            return false;
        }
    }
    
    private ZonedDateTime getFieldValue(Object object, String fieldName) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return (ZonedDateTime) field.get(object);
    }
}
