package com.example.demo.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class DobValidator implements ConstraintValidator<DobConstraint, LocalDate>{

    private int min;
    // Hàm xử lý data có đúng hay khng
    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        if (Objects.isNull(localDate)) {
            return true;
        }
        long years = ChronoUnit.YEARS.between(localDate, LocalDate.now());
        return years >= min;
    }

    // Khởi tạo các giá trị được config rồi tra len cho ham tren
    @Override
    public void initialize(DobConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        min = constraintAnnotation.min();
    }
}
