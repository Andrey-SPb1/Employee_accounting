package org.javacode.employee_accounting.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.javacode.employee_accounting.model.dto.create.DepartmentCreateEditDto;
import org.javacode.employee_accounting.model.dto.create.EmployeeCreateEditDto;
import org.javacode.employee_accounting.model.entity.Employee;
import org.javacode.employee_accounting.model.entity.Role;
import org.javacode.employee_accounting.repository.EmployeeRepository;
import org.javacode.employee_accounting.security.JwtUtil;
import org.javacode.employee_accounting.service.DepartmentService;
import org.javacode.employee_accounting.service.EmployeeService;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EmployeeControllerTest {

    private final EmployeeRepository employeeRepository;
    private final EmployeeService employeeService;
    private final DepartmentService departmentService;
    private final JdbcTemplate jdbcTemplate;
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final JwtUtil jwtUtil;
    private String adminToken;
    private String userToken;
    private String moderatorToken;

    EmployeeControllerTest(EmployeeRepository employeeRepository, EmployeeService employeeService, DepartmentService departmentService, JdbcTemplate jdbcTemplate, MockMvc mockMvc, ObjectMapper objectMapper, JwtUtil jwtUtil) {
        this.employeeRepository = employeeRepository;
        this.employeeService = employeeService;
        this.departmentService = departmentService;
        this.jdbcTemplate = jdbcTemplate;
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.jwtUtil = jwtUtil;
    }

    @BeforeEach
    void setUp() {
        loadTestData();
        loadToken();
    }

    @Test
    @Order(1)
    void getById() throws Exception {
        mockMvc.perform(get("/api/v1/employee/1")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("firstname").value("Ivan"))
                .andExpect(jsonPath("lastname").value("Ivanov"))
                .andExpect(jsonPath("salary").value(1000.00))
                .andExpect(jsonPath("position").value("cook"))
                .andExpect(jsonPath("department.name").value("cafe"));
    }

    @Test
    @Order(2)
    void getAllEmployeeProjection() throws Exception {
        mockMvc.perform(get("/api/v1/employee/all/employees_projection")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("[0].fullName").value("Ivan Ivanov"))
                .andExpect(jsonPath("[0].position").value("cook"))
                .andExpect(jsonPath("[0].departmentName").value("cafe"))
                .andExpect(jsonPath("[2].fullName").value("Semen Semenov"))
                .andExpect(jsonPath("[3].position").value("hr officer"))
                .andExpect(jsonPath("[4].departmentName").value("security"));
    }

    @Test
    @Order(3)
    void getAll() throws Exception {
        mockMvc.perform(get("/api/v1/employee/all?page=1&size=2&sort=id,asc")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("content.length()").value(2))
                .andExpect(jsonPath("content[0].position").value("salesman"))
                .andExpect(jsonPath("content[0].department.name").value("sales"))
                .andExpect(jsonPath("content[1].firstname").value("Maria"));
    }

    @Test
    void create() throws Exception {
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

        mockMvc.perform(post("/api/v1/employee")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("department.name").value("security"))
                .andExpect(jsonPath("firstname").value("Dmitry"))
                .andExpect(jsonPath("lastname").value("Ivanov"))
                .andExpect(jsonPath("username").value("dmitry123"))
                .andExpect(jsonPath("salary").value(900.00));
    }

    @Test
    void update() throws Exception {
        EmployeeCreateEditDto employee = new EmployeeCreateEditDto(
                "Ivan",
                "Ivanov",
                "ivan@gmail.com",
                "password123",
                "Ivan123",
                "chef",
                Role.ADMIN,
                2000.00,
                new DepartmentCreateEditDto("cafe")
        );

        mockMvc.perform(put("/api/v1/employee/1")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("firstname").value("Ivan"))
                .andExpect(jsonPath("lastname").value("Ivanov"))
                .andExpect(jsonPath("position").value("chef"))
                .andExpect(jsonPath("username").value("Ivan123"))
                .andExpect(jsonPath("salary").value(2000.00))
                .andExpect(jsonPath("department.name").value("cafe"));
    }

    @Test
    void updateWithUserToken() throws Exception {
        EmployeeCreateEditDto employee = new EmployeeCreateEditDto(
                "Ivan",
                "Ivanov",
                "ivan@gmail.com",
                "password123",
                "Ivan123",
                "chef",
                Role.ADMIN,
                2000.00,
                new DepartmentCreateEditDto("cafe")
        );

        mockMvc.perform(put("/api/v1/employee/1")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteTest() throws Exception {
        Employee test = Employee.builder()
                .firstName("test")
                .lastName("test")
                .salary(1000.00)
                .position("test")
                .email("test@gmail.com")
                .username("test123")
                .password("test0123")
                .role(Role.ADMIN)
                .department(departmentService.findOrCreateDepartment("testDepartment"))
                .build();

        Long testId = employeeRepository.save(test).getId();

        mockMvc.perform(delete("/api/v1/employee/" + testId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteTestWithModeratorToken() throws Exception {
        Employee test = Employee.builder()
                .firstName("test")
                .lastName("test")
                .salary(1000.00)
                .position("test")
                .email("test@gmail.com")
                .username("test123")
                .password("test0123")
                .role(Role.ADMIN)
                .department(departmentService.findOrCreateDepartment("testDepartment"))
                .build();

        Long testId = employeeRepository.save(test).getId();

        mockMvc.perform(delete("/api/v1/employee/" + testId)
                        .header("Authorization", "Bearer " + moderatorToken))
                .andExpect(status().isForbidden());
    }

    private void loadTestData() {
        updateData();

        Employee cook1 = Employee.builder()
                .firstName("Ivan")
                .lastName("Ivanov")
                .salary(1000.00)
                .position("cook")
                .email("ivan@gmail.com")
                .username("Ivan123")
                .password("password1")
                .role(Role.USER)
                .department(departmentService.findOrCreateDepartment("cafe"))
                .build();

        Employee cook2 = Employee.builder()
                .firstName("Semen")
                .lastName("Ivanov")
                .salary(1100.00)
                .position("cook")
                .email("seemen@gmail.com")
                .username("Semen123")
                .password("password2")
                .role(Role.USER)
                .department(departmentService.findOrCreateDepartment("cafe"))
                .build();

        Employee salesman = Employee.builder()
                .firstName("Semen")
                .lastName("Semenov")
                .salary(900.00)
                .position("salesman")
                .email("seemen2@gmail.com")
                .username("Semen321")
                .password("password3")
                .role(Role.ADMIN)
                .department(departmentService.findOrCreateDepartment("sales"))
                .build();

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

        employeeRepository.saveAll(List.of(cook1, cook2, salesman, hr_officer, security_guard));
    }

    private void updateData() {
        jdbcTemplate.update("ALTER TABLE employees ALTER COLUMN id RESTART WITH 1;");
        jdbcTemplate.update("ALTER TABLE departments ALTER COLUMN id RESTART WITH 1;");
        jdbcTemplate.update("DELETE FROM employees;");
        jdbcTemplate.update("DELETE FROM departments;");
    }

    private void loadToken() {
        this.adminToken = jwtUtil.generateJwtToken(employeeService.loadUserByUsername("Maria123"));
        this.userToken = jwtUtil.generateJwtToken(employeeService.loadUserByUsername("Semen123"));
        this.moderatorToken = jwtUtil.generateJwtToken(employeeService.loadUserByUsername("Petr123"));
    }
}