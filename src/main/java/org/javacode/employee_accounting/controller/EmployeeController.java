package org.javacode.employee_accounting.controller;

import lombok.RequiredArgsConstructor;
import org.javacode.employee_accounting.exception.ResourceNotFoundException;
import org.javacode.employee_accounting.model.dto.create.EmployeeCreateEditDto;
import org.javacode.employee_accounting.model.dto.response.EmployeeResponseDto;
import org.javacode.employee_accounting.model.projections.EmployeeProjection;
import org.javacode.employee_accounting.service.EmployeeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.http.ResponseEntity.notFound;

@RestController
@RequestMapping("/api/v1/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping("/{id}")
    public EmployeeResponseDto getById(@PathVariable Long id) {
        return employeeService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee with id " + id + " not found"));
    }

    @GetMapping("/all/employees_projection")
    public List<EmployeeProjection> getAllEmployeeProjection() {
        return employeeService.findAllEmployeeProjection();
    }

    @GetMapping("/all")
    public Page<EmployeeResponseDto> getAll(Pageable pageable) {
        return employeeService.findAllEmployeeProjection(pageable);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MODERATOR')")
    public EmployeeResponseDto create(@Validated @RequestBody EmployeeCreateEditDto employee) {
        return employeeService.create(employee);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MODERATOR')")
    public EmployeeResponseDto update(@PathVariable("id") Long id,
                                      @Validated @RequestBody EmployeeCreateEditDto employee) {
        return employeeService.update(id, employee)
                .orElseThrow(() -> new ResourceNotFoundException("Employee with id " + id + " not found"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        return employeeService.delete(id) ? noContent().build() : notFound().build();
    }
}
