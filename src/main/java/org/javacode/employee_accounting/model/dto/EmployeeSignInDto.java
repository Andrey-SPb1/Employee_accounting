package org.javacode.employee_accounting.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EmployeeSignInDto(
        @NotBlank
        String username,
        @Size(min = 8, max = 50)
        String password) {
}
