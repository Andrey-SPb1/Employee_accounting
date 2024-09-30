package org.javacode.employee_accounting.service;

import org.javacode.employee_accounting.model.dto.create.EmployeeCreateEditDto;
import org.javacode.employee_accounting.model.dto.response.EmployeeResponseDto;
import org.javacode.employee_accounting.model.projections.EmployeeProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface EmployeeService {

    Optional<EmployeeResponseDto> findById(Long id);

    List<EmployeeProjection> findAllEmployeeProjection();

    Page<EmployeeResponseDto> findAllEmployeeProjection(Pageable pageable);

    EmployeeResponseDto create(EmployeeCreateEditDto employeeDto);

    Optional<EmployeeResponseDto> update(Long id, EmployeeCreateEditDto employeeDto);

    boolean delete(Long id);
}
