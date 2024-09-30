package org.javacode.employee_accounting.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.javacode.employee_accounting.model.dto.create.DepartmentCreateEditDto;
import org.javacode.employee_accounting.model.dto.create.EmployeeCreateEditDto;
import org.javacode.employee_accounting.model.entity.Employee;
import org.javacode.employee_accounting.repository.EmployeeRepository;
import org.javacode.employee_accounting.service.DepartmentService;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
    private final DepartmentService departmentService;
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    EmployeeControllerTest(EmployeeRepository employeeRepository, DepartmentService departmentService, MockMvc mockMvc, ObjectMapper objectMapper) {
        this.employeeRepository = employeeRepository;
        this.departmentService = departmentService;
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }


    @BeforeEach
    void setUp() {
        loadTestData();
    }

    @Test
    @Order(1)
    void getById() throws Exception {
        mockMvc.perform(get("/api/v1/employee/1"))
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
        mockMvc.perform(get("/api/v1/employee/all/employees_projection"))
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
        mockMvc.perform(get("/api/v1/employee/all?page=1&size=2&sort=id,asc"))
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
                "security guard",
                900.00,
                new DepartmentCreateEditDto("security")
        );

        mockMvc.perform(post("/api/v1/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("department.name").value("security"))
                .andExpect(jsonPath("firstname").value("Dmitry"))
                .andExpect(jsonPath("lastname").value("Ivanov"))
                .andExpect(jsonPath("salary").value(900.00));
    }

    @Test
    void update() throws Exception {
        EmployeeCreateEditDto employee = new EmployeeCreateEditDto(
                "Ivan",
                "Ivanov",
                "chef",
                2000.00,
                new DepartmentCreateEditDto("cafe")
        );

        mockMvc.perform(put("/api/v1/employee/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("firstname").value("Ivan"))
                .andExpect(jsonPath("lastname").value("Ivanov"))
                .andExpect(jsonPath("position").value("chef"))
                .andExpect(jsonPath("salary").value(2000.00))
                .andExpect(jsonPath("department.name").value("cafe"));
    }

    @Test
    void deleteTest() throws Exception {
        Employee test = Employee.builder()
                .firstName("test")
                .lastName("test")
                .salary(1000.00)
                .position("test")
                .department(departmentService.findOrCreateAuthor("test"))
                .build();

        Long testId = employeeRepository.save(test).getId();

        mockMvc.perform(delete("/api/v1/employee/" + testId))
                .andExpect(status().isNoContent());
    }

    private void loadTestData() {
        if (employeeRepository.count() != 0) return;

        Employee cook1 = Employee.builder()
                .firstName("Ivan")
                .lastName("Ivanov")
                .salary(1000.00)
                .position("cook")
                .department(departmentService.findOrCreateAuthor("cafe"))
                .build();

        Employee cook2 = Employee.builder()
                .firstName("Semen")
                .lastName("Ivanov")
                .salary(1100.00)
                .position("cook")
                .department(departmentService.findOrCreateAuthor("cafe"))
                .build();

        Employee salesman = Employee.builder()
                .firstName("Semen")
                .lastName("Semenov")
                .salary(900.00)
                .position("salesman")
                .department(departmentService.findOrCreateAuthor("sales"))
                .build();

        Employee hr_officer = Employee.builder()
                .firstName("Maria")
                .lastName("Ivanova")
                .salary(1200.00)
                .position("hr officer")
                .department(departmentService.findOrCreateAuthor("hr"))
                .build();

        Employee security_guard = Employee.builder()
                .firstName("Petr")
                .lastName("Petrov")
                .salary(1100.00)
                .position("security guard")
                .department(departmentService.findOrCreateAuthor("security"))
                .build();

        employeeRepository.saveAll(List.of(cook1, cook2, salesman, hr_officer, security_guard));
    }
}