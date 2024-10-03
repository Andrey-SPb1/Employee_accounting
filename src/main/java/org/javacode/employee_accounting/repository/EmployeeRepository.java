package org.javacode.employee_accounting.repository;

import org.javacode.employee_accounting.model.entity.Employee;
import org.javacode.employee_accounting.model.projections.EmployeeProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("SELECT e FROM Employee e")
    List<EmployeeProjection> findAllEmployeeProjection();

    Optional<Employee> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
