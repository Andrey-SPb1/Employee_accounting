package org.javacode.employee_accounting.model.dto.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record EmployeeCreateEditDto(
        @Size(min = 1, max = 50)
        String firstname,
        @Size(min = 1, max = 50)
        String lastname,
        @NotBlank
        String position,
        @NotNull
        Double salary,
        @NotNull
        DepartmentCreateEditDto department) {
}
