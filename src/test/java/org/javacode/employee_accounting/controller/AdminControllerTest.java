package org.javacode.employee_accounting.controller;

import org.javacode.employee_accounting.model.entity.Employee;
import org.javacode.employee_accounting.model.entity.Role;
import org.javacode.employee_accounting.repository.EmployeeRepository;
import org.javacode.employee_accounting.security.JwtUtil;
import org.javacode.employee_accounting.service.DepartmentService;
import org.javacode.employee_accounting.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AdminControllerTest {

    private final EmployeeRepository employeeRepository;
    private final EmployeeService employeeService;
    private final DepartmentService departmentService;
    private final JdbcTemplate jdbcTemplate;
    private final MockMvc mockMvc;
    private final JwtUtil jwtUtil;
    private String adminToken;
    private String moderatorToken;

    AdminControllerTest(EmployeeRepository employeeRepository, EmployeeService employeeService, DepartmentService departmentService, JdbcTemplate jdbcTemplate, MockMvc mockMvc, JwtUtil jwtUtil) {
        this.employeeRepository = employeeRepository;
        this.employeeService = employeeService;
        this.departmentService = departmentService;
        this.jdbcTemplate = jdbcTemplate;
        this.mockMvc = mockMvc;
        this.jwtUtil = jwtUtil;
    }


    @BeforeEach
    void setUp() {
        loadTestData();
        loadToken();
    }

    @Test
    void isUserBlock() throws Exception {
        mockMvc.perform(get("/api/v1/admin/block/1")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string("false"));
    }

    @Test
    void isUserBlockWithModeratorToken() throws Exception {
        mockMvc.perform(get("/api/v1/admin/block/1")
                        .header("Authorization", "Bearer " + moderatorToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateUserBlock() throws Exception {
        mockMvc.perform(put("/api/v1/admin/block/2")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("false"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("false"));
    }

    @Test
    void updateUserBlockWithModeratorToken() throws Exception {
        mockMvc.perform(put("/api/v1/admin/block/2")
                        .header("Authorization", "Bearer " + moderatorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("false"))
                .andExpect(status().isForbidden());
    }

    private void loadTestData() {
        updateData();

        Employee hr_officer = Employee.builder()
                .firstName("Maria")
                .lastName("Ivanova")
                .salary(1200.00)
                .position("hr officer")
                .email("maria@gmail.com")
                .username("Maria123")
                .password("password4")
                .role(Role.ADMIN)
                .department(departmentService.findOrCreateDepartment("hr"))
                .build();

        Employee security_guard = Employee.builder()
                .firstName("Petr")
                .lastName("Petrov")
                .salary(1100.00)
                .position("security guard")
                .email("petr@gmail.com")
                .username("Petr123")
                .password("password5")
                .role(Role.MODERATOR)
                .department(departmentService.findOrCreateDepartment("security"))
                .build();

        employeeRepository.saveAll(List.of(hr_officer, security_guard));
    }

    private void updateData() {
        jdbcTemplate.update("ALTER TABLE employees ALTER COLUMN id RESTART WITH 1;");
        jdbcTemplate.update("ALTER TABLE departments ALTER COLUMN id RESTART WITH 1;");
        jdbcTemplate.update("DELETE FROM employees;");
        jdbcTemplate.update("DELETE FROM departments;");
    }

    private void loadToken() {
        this.adminToken = jwtUtil.generateJwtToken(employeeService.loadUserByUsername("Maria123"));
        this.moderatorToken = jwtUtil.generateJwtToken(employeeService.loadUserByUsername("Petr123"));
    }
}