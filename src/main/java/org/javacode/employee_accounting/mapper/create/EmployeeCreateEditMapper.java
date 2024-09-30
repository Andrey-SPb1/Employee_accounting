package org.javacode.employee_accounting.mapper.create;

import lombok.RequiredArgsConstructor;
import org.javacode.employee_accounting.mapper.Mapper;
import org.javacode.employee_accounting.model.dto.create.EmployeeCreateEditDto;
import org.javacode.employee_accounting.model.entity.Employee;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmployeeCreateEditMapper implements Mapper<EmployeeCreateEditDto, Employee> {

    private final DepartmentCreateEditMapper departmentCreateEditMapper;

    @Override
    public Employee map(EmployeeCreateEditDto employee) {
        return Employee.builder()
                .firstName(employee.firstname())
                .lastName(employee.lastname())
                .salary(employee.salary())
                .position(employee.position())
                .department(departmentCreateEditMapper.map(employee.department()))
                .build();
    }

    public Employee map(EmployeeCreateEditDto employeeDto, Employee employee) {
        employee.setFirstName(employeeDto.firstname());
        employee.setLastName(employeeDto.lastname());
        employee.setSalary(employeeDto.salary());
        employee.setPosition(employeeDto.position());
        employee.setDepartment(departmentCreateEditMapper.map(employeeDto.department()));
        return employee;
    }

}
