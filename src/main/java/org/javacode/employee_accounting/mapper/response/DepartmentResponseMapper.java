package org.javacode.employee_accounting.mapper.response;

import org.javacode.employee_accounting.mapper.Mapper;
import org.javacode.employee_accounting.model.dto.response.DepartmentResponseDto;
import org.javacode.employee_accounting.model.entity.Department;
import org.springframework.stereotype.Component;

@Component
public class DepartmentResponseMapper implements Mapper<Department, DepartmentResponseDto> {

    @Override
    public DepartmentResponseDto map(Department department) {
        return new DepartmentResponseDto(department.getName());
    }
}
