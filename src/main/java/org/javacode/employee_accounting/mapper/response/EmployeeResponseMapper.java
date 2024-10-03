package org.javacode.employee_accounting.mapper.response;

import lombok.RequiredArgsConstructor;
import org.javacode.employee_accounting.mapper.Mapper;
import org.javacode.employee_accounting.model.dto.response.EmployeeResponseDto;
import org.javacode.employee_accounting.model.entity.Employee;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmployeeResponseMapper implements Mapper<Employee, EmployeeResponseDto> {

    private final DepartmentResponseMapper departmentResponseMapper;

    @Override
    public EmployeeResponseDto map(Employee employee) {
        return new EmployeeResponseDto(
                employee.getFirstName(),
                employee.getLastName(),
                employee.getUsername(),
                employee.getPosition(),
                employee.getSalary(),
                departmentResponseMapper.map(employee.getDepartment())
        );
    }
}
