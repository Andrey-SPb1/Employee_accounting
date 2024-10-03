package org.javacode.employee_accounting.service.impl;

import lombok.RequiredArgsConstructor;
import org.javacode.employee_accounting.mapper.create.DepartmentCreateEditMapper;
import org.javacode.employee_accounting.mapper.response.DepartmentResponseMapper;
import org.javacode.employee_accounting.model.dto.create.DepartmentCreateEditDto;
import org.javacode.employee_accounting.model.dto.response.DepartmentResponseDto;
import org.javacode.employee_accounting.model.entity.Department;
import org.javacode.employee_accounting.repository.DepartmentRepository;
import org.javacode.employee_accounting.service.DepartmentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentResponseMapper departmentResponseMapper;
    private final DepartmentCreateEditMapper departmentCreateEditMapper;

    @Override
    public Optional<DepartmentResponseDto> findById(Integer id) {
        return departmentRepository.findById(id)
                .map(departmentResponseMapper::map);
    }

    @Override
    public Page<DepartmentResponseDto> findAll(Pageable pageable) {
        Page<Department> page = departmentRepository.findAll(pageable);
        return new PageImpl<>(page.getContent().stream()
                .map(departmentResponseMapper::map)
                .toList(), pageable, page.getTotalElements());
    }

    @Override
    public DepartmentResponseDto create(DepartmentCreateEditDto departmentDto) {
        return Optional.of(departmentDto)
                .map(departmentCreateEditMapper::map)
                .map(departmentRepository::save)
                .map(departmentResponseMapper::map)
                .orElseThrow();
    }

    @Override
    public Optional<DepartmentResponseDto> update(Integer id, DepartmentCreateEditDto departmentDto) {
        return departmentRepository.findById(id)
                .map(department -> departmentCreateEditMapper.map(departmentDto, department))
                .map(departmentRepository::saveAndFlush)
                .map(departmentResponseMapper::map);
    }

    @Override
    public boolean delete(Integer id) {
        return departmentRepository.findById(id)
                .map(entity -> {
                    departmentRepository.deleteById(id);
                    departmentRepository.flush();
                    return true;
                })
                .orElse(false);
    }

    @Override
    public Department findOrCreateDepartment(String name) {
        return Objects.requireNonNullElseGet(departmentRepository.findByName(name),
                () -> departmentRepository.save(Department.builder().name(name).build()));
    }
}
