package org.javacode.employee_accounting.controller;

import lombok.RequiredArgsConstructor;
import org.javacode.employee_accounting.exception.ResourceNotFoundException;
import org.javacode.employee_accounting.model.dto.create.DepartmentCreateEditDto;
import org.javacode.employee_accounting.model.dto.response.DepartmentResponseDto;
import org.javacode.employee_accounting.service.DepartmentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.http.ResponseEntity.notFound;

@RestController
@RequestMapping("/api/v1/department")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping("/{id}")
    public DepartmentResponseDto getById(@PathVariable Integer id) {
        return departmentService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department with id " + id + " not found"));
    }

    @GetMapping("/all")
    public Page<DepartmentResponseDto> getAll(Pageable pageable) {
        return departmentService.findAll(pageable);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MODERATOR')")
    public DepartmentResponseDto create(@Validated @RequestBody DepartmentCreateEditDto department) {
        return departmentService.create(department);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MODERATOR')")
    public DepartmentResponseDto update(@PathVariable("id") Integer id,
                                  @Validated @RequestBody DepartmentCreateEditDto department) {
        return departmentService.update(id, department)
                .orElseThrow(() -> new ResourceNotFoundException("Department with id " + id + " not found"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable("id") Integer id) {
        return departmentService.delete(id) ? noContent().build() : notFound().build();
    }
}
