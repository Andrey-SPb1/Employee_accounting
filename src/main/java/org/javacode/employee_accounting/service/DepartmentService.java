package org.javacode.employee_accounting.service;

import org.javacode.employee_accounting.model.dto.create.DepartmentCreateEditDto;
import org.javacode.employee_accounting.model.dto.response.DepartmentResponseDto;
import org.javacode.employee_accounting.model.entity.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface DepartmentService {

    Optional<DepartmentResponseDto> findById(Integer id);

    Page<DepartmentResponseDto> findAll(Pageable pageable);

    DepartmentResponseDto create(DepartmentCreateEditDto departmentDto);

    Optional<DepartmentResponseDto> update(Integer id, DepartmentCreateEditDto departmentDto);

    boolean delete(Integer id);

    Department findOrCreateDepartment(String name);
}
