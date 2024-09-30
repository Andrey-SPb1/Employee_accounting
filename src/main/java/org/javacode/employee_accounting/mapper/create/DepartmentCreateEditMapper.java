package org.javacode.employee_accounting.mapper.create;

import org.javacode.employee_accounting.mapper.Mapper;
import org.javacode.employee_accounting.model.dto.create.DepartmentCreateEditDto;
import org.javacode.employee_accounting.model.entity.Department;
import org.springframework.stereotype.Component;

@Component
public class DepartmentCreateEditMapper implements Mapper<DepartmentCreateEditDto, Department> {

    @Override
    public Department map(DepartmentCreateEditDto department) {
        return Department.builder()
                .name(department.name())
                .build();
    }
    @Override
    public Department map(DepartmentCreateEditDto departmentDto, Department department) {
        department.setName(departmentDto.name());
        return department;
    }
}
