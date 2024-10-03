package org.javacode.employee_accounting.mapper.create;

import lombok.RequiredArgsConstructor;
import org.javacode.employee_accounting.mapper.Mapper;
import org.javacode.employee_accounting.model.dto.create.EmployeeCreateEditDto;
import org.javacode.employee_accounting.model.entity.Employee;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmployeeCreateEditMapper implements Mapper<EmployeeCreateEditDto, Employee> {

    private final DepartmentCreateEditMapper departmentCreateEditMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Employee map(EmployeeCreateEditDto employee) {
        return Employee.builder()
                .firstName(employee.firstname())
                .lastName(employee.lastname())
                .email(employee.email())
                .username(employee.username())
                .password(employee.password())
                .password(passwordEncoder.encode(employee.password()))
                .role(employee.role())
                .salary(employee.salary())
                .position(employee.position())
                .isAccountNonLocked(true)
                .department(departmentCreateEditMapper.map(employee.department()))
                .build();
    }

    public Employee map(EmployeeCreateEditDto employeeDto, Employee employee) {
        employee.setFirstName(employeeDto.firstname());
        employee.setLastName(employeeDto.lastname());
        employee.setEmail(employeeDto.email());
        employee.setUsername(employeeDto.username());
        employee.setPassword(employeeDto.password());
        employee.setRole(employeeDto.role());
        employee.setSalary(employeeDto.salary());
        employee.setPosition(employeeDto.position());
        employee.setDepartment(departmentCreateEditMapper.map(employeeDto.department()));
        return employee;
    }

}
