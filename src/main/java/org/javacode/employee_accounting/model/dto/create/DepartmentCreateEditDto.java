package org.javacode.employee_accounting.model.dto.create;

import jakarta.validation.constraints.Size;

public record DepartmentCreateEditDto(
        @Size(min = 3, max = 50)
        String name) {

}
