package org.javacode.employee_accounting.model.dto.response;

public record EmployeeResponseDto(
        String firstname,
        String lastname,
        String position,
        Double salary,
        DepartmentResponseDto department) {
}
