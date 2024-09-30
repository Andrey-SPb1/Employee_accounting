package org.javacode.employee_accounting.service.impl;

import lombok.RequiredArgsConstructor;
import org.javacode.employee_accounting.mapper.create.EmployeeCreateEditMapper;
import org.javacode.employee_accounting.mapper.response.EmployeeResponseMapper;
import org.javacode.employee_accounting.model.dto.create.EmployeeCreateEditDto;
import org.javacode.employee_accounting.model.dto.response.EmployeeResponseDto;
import org.javacode.employee_accounting.model.entity.Employee;
import org.javacode.employee_accounting.model.projections.EmployeeProjection;
import org.javacode.employee_accounting.repository.EmployeeRepository;
import org.javacode.employee_accounting.service.DepartmentService;
import org.javacode.employee_accounting.service.EmployeeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentService departmentService;
    private final EmployeeResponseMapper employeeResponseMapper;
    private final EmployeeCreateEditMapper employeeCreateEditMapper;

    @Override
    public Optional<EmployeeResponseDto> findById(Long id) {
        return employeeRepository.findById(id)
                .map(employeeResponseMapper::map);
    }

    @Override
    public List<EmployeeProjection> findAllEmployeeProjection() {
        return employeeRepository.findAllEmployeeProjection();
    }

    @Override
    public Page<EmployeeResponseDto> findAllEmployeeProjection(Pageable pageable) {
        Page<Employee> page = employeeRepository.findAll(pageable);
        return new PageImpl<>(page.getContent().stream()
                .map(employeeResponseMapper::map)
                .toList(), pageable, page.getTotalElements());
    }

    @Override
    public EmployeeResponseDto create(EmployeeCreateEditDto employeeDto) {
        return Optional.of(employeeDto)
                .map(employeeCreateEditMapper::map)
                .map(employee -> {
                    employee.setDepartment(departmentService.findOrCreateAuthor(employee.getDepartment().getName()));
                    return employee;
                })
                .map(employeeRepository::save)
                .map(employeeResponseMapper::map)
                .orElseThrow();
    }

    @Override
    public Optional<EmployeeResponseDto> update(Long id, EmployeeCreateEditDto employeeDto) {
        return employeeRepository.findById(id)
                .map(employee -> employeeCreateEditMapper.map(employeeDto, employee))
                .map(employee -> {
                    employee.setDepartment(departmentService.findOrCreateAuthor(employee.getDepartment().getName()));
                    return employee;
                })
                .map(employeeRepository::saveAndFlush)
                .map(employeeResponseMapper::map);
    }

    @Override
    public boolean delete(Long id) {
        return employeeRepository.findById(id)
                .map(entity -> {
                    employeeRepository.deleteById(id);
                    employeeRepository.flush();
                    return true;
                })
                .orElse(false);
    }
}
