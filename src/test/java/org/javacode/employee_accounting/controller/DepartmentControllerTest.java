package org.javacode.employee_accounting.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.javacode.employee_accounting.model.dto.create.DepartmentCreateEditDto;
import org.javacode.employee_accounting.model.entity.Department;
import org.javacode.employee_accounting.repository.DepartmentRepository;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
@Order(1)
class DepartmentControllerTest {

    private final DepartmentRepository departmentRepository;
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    DepartmentControllerTest(DepartmentRepository departmentRepository, MockMvc mockMvc, ObjectMapper objectMapper) {
        this.departmentRepository = departmentRepository;
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
        mockMvc.perform(get("/api/v1/department/1"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("name").value("restaurant"));
    }

    @Test
    @Order(2)
    void getAll() throws Exception {
        mockMvc.perform(get("/api/v1/department/all?page=1&size=2&sort=id,asc"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("content.length()").value(2))
                .andExpect(jsonPath("content[0].name").value("sales"))
                .andExpect(jsonPath("content[1].name").value("security"));
    }

    @Test
    void create() throws Exception {
        DepartmentCreateEditDto logistics = new DepartmentCreateEditDto("logistics");

        mockMvc.perform(post("/api/v1/department")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(logistics)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("name").value("logistics"));
    }

    @Test
    void update() throws Exception {
        DepartmentCreateEditDto cafe = new DepartmentCreateEditDto("cafe");
        mockMvc.perform(put("/api/v1/department/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cafe)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("name").value("cafe"));
    }

    @Test
    void deleteTest() throws Exception {
        Department test = Department.builder()
                .name("test")
                .build();

        Integer testId = departmentRepository.save(test).getId();

        mockMvc.perform(delete("/api/v1/department/" + testId))
                .andExpect(status().isNoContent());
    }

    private void loadTestData() {
        if (departmentRepository.count() != 0) return;

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
}