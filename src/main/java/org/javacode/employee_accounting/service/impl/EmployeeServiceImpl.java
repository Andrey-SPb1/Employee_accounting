package org.javacode.employee_accounting.service.impl;

import lombok.RequiredArgsConstructor;
import org.javacode.employee_accounting.exception.AlreadyExistsException;
import org.javacode.employee_accounting.exception.ResourceNotFoundException;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    public Optional<EmployeeResponseDto> findByUsername(String username) {
        return employeeRepository.findByUsername(username)
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
        checkUsernameAndEmail(employeeDto);
        return Optional.of(employeeDto)
                .map(employeeCreateEditMapper::map)
                .map(employee -> {
                    employee.setDepartment(departmentService.findOrCreateDepartment(employee.getDepartment().getName()));
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
                    employee.setDepartment(departmentService.findOrCreateDepartment(employee.getDepartment().getName()));
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

    @Override
    public UserDetails loadUserByUsername(String username) {
        return employeeRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }

    @Override
    public boolean userIsLocked(Long id) {
        return employeeRepository.findById(id)
                .map(Employee::isAccountNonLocked)
                .orElseThrow(() -> new ResourceNotFoundException("Employee with id " + id + " not found"));
    }

    @Override
    public void setBlock(Long id, Boolean block) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee with id " + id + " not found"));
        employee.setAccountNonLocked(block);
    }

    private void checkUsernameAndEmail(EmployeeCreateEditDto employeeDto) {
        if (employeeRepository.existsByUsername(employeeDto.username())) {
            throw new AlreadyExistsException("User with username " + employeeDto.username() + " already exists");
        }

        if (employeeRepository.existsByEmail(employeeDto.email())) {
            throw new AlreadyExistsException("User with email " + employeeDto.email() + " already exists");
        }
    }
}
