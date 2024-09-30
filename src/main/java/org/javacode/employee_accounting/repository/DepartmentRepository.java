package org.javacode.employee_accounting.repository;

import org.javacode.employee_accounting.model.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Integer> {

    Department findByName(String name);
}
