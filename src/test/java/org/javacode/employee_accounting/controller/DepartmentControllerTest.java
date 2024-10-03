package org.javacode.employee_accounting.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.javacode.employee_accounting.model.dto.create.DepartmentCreateEditDto;
import org.javacode.employee_accounting.model.entity.Department;
import org.javacode.employee_accounting.model.entity.Employee;
import org.javacode.employee_accounting.model.entity.Role;
import org.javacode.employee_accounting.repository.DepartmentRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DepartmentControllerTest {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final DepartmentService departmentService;
    private final EmployeeService employeeService;
    private final JdbcTemplate jdbcTemplate;
    private final JwtUtil jwtUtil;
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private String adminToken;
    private String userToken;
    private String moderatorToken;

    DepartmentControllerTest(DepartmentRepository departmentRepository, EmployeeRepository employeeRepository, DepartmentService departmentService, EmployeeService employeeService, JdbcTemplate jdbcTemplate, JwtUtil jwtUtil, MockMvc mockMvc, ObjectMapper objectMapper) {
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
        this.departmentService = departmentService;
        this.employeeService = employeeService;
        this.jdbcTemplate = jdbcTemplate;
        this.jwtUtil = jwtUtil;
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @BeforeEach
    void setUp() {
        loadTestData();
        loadTokens();
    }

    @Test
    @Order(1)
    void getById() throws Exception {
        mockMvc.perform(get("/api/v1/department/2")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("name").value("restaurant"));
    }

    @Test
    @Order(2)
    void getByIdWithoutToken() throws Exception {
        mockMvc.perform(get("/api/v1/department/2"))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(3)
    void getAll() throws Exception {
        mockMvc.perform(get("/api/v1/department/all?page=1&size=2&sort=id,asc")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("content.length()").value(2))
                .andExpect(jsonPath("content[0].name").value("hr"))
                .andExpect(jsonPath("content[1].name").value("sales"));
    }

    @Test
    void create() throws Exception {
        DepartmentCreateEditDto logistics = new DepartmentCreateEditDto("logistics");

        mockMvc.perform(post("/api/v1/department")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(logistics)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("name").value("logistics"));
    }

    @Test
    void update() throws Exception {
        DepartmentCreateEditDto cafe = new DepartmentCreateEditDto("cafe");
        mockMvc.perform(put("/api/v1/department/2")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cafe)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("name").value("cafe"));
    }

    @Test
    void updateWithUserToken() throws Exception {
        DepartmentCreateEditDto cafe = new DepartmentCreateEditDto("cafe");
        mockMvc.perform(put("/api/v1/department/2")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cafe)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteTest() throws Exception {
        Department test = Department.builder()
                .name("test2")
                .build();

        Integer testId = departmentRepository.save(test).getId();

        mockMvc.perform(delete("/api/v1/department/" + testId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteTestWithModeratorToken() throws Exception {
        Department test = Department.builder()
                .name("test2")
                .build();

        Integer testId = departmentRepository.save(test).getId();

        mockMvc.perform(delete("/api/v1/department/" + testId)
                        .header("Authorization", "Bearer " + moderatorToken))
                .andExpect(status().isForbidden());
    }

    private void loadTestData() {
        updateData();

        loadEmployees();

        Department restaurant = Department.builder()
                .name("restaurant")
                .build();

        Department hr = Department.builder()
                .name("hr")
                .build();

        Department sales = Department.builder()
                .name("sales")
                .build();

        Department security = Department.builder()
                .name("security")
                .build();

        Department cleaning = Department.builder()
                .name("cleaning")
                .build();

        departmentRepository.saveAll(List.of(restaurant, hr, sales, security, cleaning));
    }

    private void updateData() {
        jdbcTemplate.update("ALTER TABLE employees ALTER COLUMN id RESTART WITH 1;");
        jdbcTemplate.update("ALTER TABLE departments ALTER COLUMN id RESTART WITH 1;");
        jdbcTemplate.update("DELETE FROM employees;");
        jdbcTemplate.update("DELETE FROM departments;");
    }

    private void loadTokens() {
        this.adminToken = jwtUtil.generateJwtToken(employeeService.loadUserByUsername("testAdmin"));
        this.userToken = jwtUtil.generateJwtToken(employeeService.loadUserByUsername("testUser"));
        this.moderatorToken = jwtUtil.generateJwtToken(employeeService.loadUserByUsername("testModerator"));
    }

    private void loadEmployees() {
        Employee testAdmin = Employee.builder()
                .firstName("test")
                .lastName("test")
                .salary(1000.00)
                .position("test")
                .email("test@gmail.com")
                .username("testAdmin")
                .password("test0123")
                .role(Role.ADMIN)
                .department(departmentService.findOrCreateDepartment("testDepartment"))
                .build();

        Employee testUser = Employee.builder()
                .firstName("test2")
                .lastName("test2")
                .salary(1000.00)
                .position("test2")
                .email("test2@gmail.com")
                .username("testUser")
                .password("test0123")
                .role(Role.USER)
                .department(departmentService.findOrCreateDepartment("testDepartment"))
                .build();

        Employee testModerator = Employee.builder()
                .firstName("test3")
                .lastName("test3")
                .salary(1000.00)
                .position("test3")
                .email("test3@gmail.com")
                .username("testModerator")
                .password("test0123")
                .role(Role.MODERATOR)
                .department(departmentService.findOrCreateDepartment("testDepartment"))
                .build();

        employeeRepository.saveAll(List.of(testAdmin, testUser, testModerator));
    }
}