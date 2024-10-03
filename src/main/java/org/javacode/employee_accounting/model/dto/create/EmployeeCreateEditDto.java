package org.javacode.employee_accounting.model.dto.create;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.javacode.employee_accounting.model.entity.Role;

public record EmployeeCreateEditDto(
        @Size(min = 1, max = 50)
        String firstname,
        @Size(min = 1, max = 50)
        String lastname,
        @Email
        String email,
        @Size(min = 8, max = 50)
        String password,
        @NotBlank
        String username,
        @NotBlank
        String position,
        @NotNull
        Role role,
        @NotNull
        Double salary,
        @NotNull
        DepartmentCreateEditDto department) {
}
