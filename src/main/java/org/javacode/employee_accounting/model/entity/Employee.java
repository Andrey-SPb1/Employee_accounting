package org.javacode.employee_accounting.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String position;

    @Column(nullable = false)
    private Double salary;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getDepartmentName() {
        return department.getName();
    }
}

