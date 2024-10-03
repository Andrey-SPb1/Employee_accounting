package org.javacode.employee_accounting.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.javacode.employee_accounting.model.dto.EmployeeSignInDto;
import org.javacode.employee_accounting.model.dto.create.DepartmentCreateEditDto;
import org.javacode.employee_accounting.model.dto.create.EmployeeCreateEditDto;
import org.javacode.employee_accounting.model.entity.Employee;
import org.javacode.employee_accounting.model.entity.Role;
import org.javacode.employee_accounting.repository.EmployeeRepository;
import org.javacode.employee_accounting.service.DepartmentService;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthControllerTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final EmployeeRepository employeeRepository;
    private final JdbcTemplate jdbcTemplate;
    private final DepartmentService departmentService;

    AuthControllerTest(MockMvc mockMvc, ObjectMapper objectMapper, EmployeeRepository employeeRepository, JdbcTemplate jdbcTemplate, DepartmentService departmentService) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.employeeRepository = employeeRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.departmentService = departmentService;
    }

    @BeforeEach
    void setUp() {
        loadTestData();
    }

    @Test
    @Order(1)
    void signUp() throws Exception {
        EmployeeCreateEditDto employee = new EmployeeCreateEditDto(
                "Dmitry",
                "Ivanov",
                "dmitry@gmail.com",
                "password99",
                "dmitry123",
                "security guard",
                Role.ADMIN,
                900.00,
                new DepartmentCreateEditDto("security")
        );

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee)))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @Order(3)
    void signIn() throws Exception {
        EmployeeSignInDto employeeSignInDto = new EmployeeSignInDto("test123", "wrong password");

        for (int i = 0; i < 4; i++) {
            mockMvc.perform(post("/auth/signin")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(employeeSignInDto)));
        }

        mockMvc.perform(post("/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeSignInDto)))
                .andExpect(status().isLocked());
    }

    @Test
    @Order(2)
    void refreshToken() throws Exception {
        EmployeeSignInDto employeeSignInDto = new EmployeeSignInDto("test123", "test0123");

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeSignInDto)))
                .andExpect(status().is2xxSuccessful());
    }

    private void loadTestData() {
        updateData();

        Employee testEmployee = Employee.builder()
                .firstName("test")
                .lastName("test")
                .salary(1000.00)
                .position("test")
                .email("test@gmail.com")
                .username("test123")
                .password("test0123")
                .isAccountNonLocked(true)
                .role(Role.ADMIN)
                .department(departmentService.findOrCreateDepartment("testDepartment"))
                .build();

        employeeRepository.save(testEmployee);
    }

    private void updateData() {
        jdbcTemplate.update("ALTER TABLE employees ALTER COLUMN id RESTART WITH 1;");
        jdbcTemplate.update("ALTER TABLE departments ALTER COLUMN id RESTART WITH 1;");
        jdbcTemplate.update("DELETE FROM employees;");
        jdbcTemplate.update("DELETE FROM departments;");
    }
}