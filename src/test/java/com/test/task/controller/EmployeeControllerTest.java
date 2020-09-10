package com.test.task.controller;

import com.test.task.entity.Employee;
import com.test.task.enums.BusinessUnit;
import com.test.task.repository.EmployeeRepository;
import com.test.task.service.IEmployeeService;
import com.test.task.utils.MappingUrls;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class EmployeeControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private IEmployeeService employeeService;

    @MockBean
    private EmployeeRepository employeeRepository;

    @Before
    public void setup() {
        Mockito.when(employeeService.updateSalaryForEmployees(Mockito.anyString(), Mockito.anyDouble()))
                .thenReturn(getEmployees());
    }

    @BeforeEach
    public void setMockMvc() {
        mockMvc = MockMvcBuilders.standaloneSetup(new EmployeeController(employeeService)).build();
    }

    @Test
    public void whenUpdateSalary_ShouldReturnRefreshedEmployeeList() throws Exception {
        mockMvc.perform(put(MappingUrls.apiVersionV1 + MappingUrls.Employee.updateSalaryForAllEmployeeByPlace, "DELHI", "20"))
                .andDo(print())
                .andExpect(status().isAccepted());
        Mockito.verify(employeeService, Mockito.atLeast(1)).updateSalaryForEmployees("DELHI", Double.parseDouble("20"));
    }

    @Test
    public void whenUpdateSalaryWithInvalidParameters_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(put(MappingUrls.apiVersionV1 + MappingUrls.Employee.updateSalaryForAllEmployeeByPlace, "20", null))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void whenGetEmployeesForPlace_ShouldReturnEmployeeList() throws Exception {
        mockMvc.perform(get(MappingUrls.apiVersionV1 + MappingUrls.Employee.getEmployeesForPlace, "DELHI"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void whenGetEmployeesForPlace_AndNoEmployeeFound_ShouldReturnEmptyList() throws Exception {
        mockMvc.perform(get(MappingUrls.apiVersionV1 + MappingUrls.Employee.getEmployeesForPlace, "DELHI"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(new ArrayList<>().toString()));
    }

    @Test
    public void whenGetEmployeesForPlaceWithInvalidParam_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get(MappingUrls.apiVersionV1 + MappingUrls.Employee.getEmployeesForPlace, ""))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void whenGetEmployee_shouldReturnListOfEmployeeWithHierarchy() throws Exception {
        mockMvc.perform(get(MappingUrls.apiVersionV1 + MappingUrls.Employee.getEmployee, 1L))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void whenGetEmployeeWithInvalidParam_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get(MappingUrls.apiVersionV1 + MappingUrls.Employee.getEmployee, ""))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void whenGetTotalSalaryByPlace_shouldReturnTotalSalaryOfAllEmployeesFromPlace() throws Exception {
        mockMvc.perform(get(MappingUrls.apiVersionV1 + MappingUrls.Employee.getTotalSalary, "DELHI", null, null))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("0"));
    }

    @Test
    public void whenGetTotalSalaryBySupervisorId_shouldReturnTotalSalaryOfAllEmployeesFromPlace() throws Exception {
        mockMvc.perform(get(MappingUrls.apiVersionV1 + MappingUrls.Employee.getTotalSalary, null, 1L, null))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("0"));;
    }

    @Test
    public void whenGetTotalSalaryByBusinessUnit_shouldReturnTotalSalaryOfAllEmployeesFromPlace() throws Exception {
        mockMvc.perform(get(MappingUrls.apiVersionV1 + MappingUrls.Employee.getTotalSalary, null, null, BusinessUnit.ENGINEERING.name()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("0"));
    }

    @Test
    public void whenGetSalaryRangeByTitle_shouldReturnSalaryRange() throws Exception {
        mockMvc.perform(get(MappingUrls.apiVersionV1 + MappingUrls.Employee.getSalaryRangeByTitle, "Developer"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Ignore
    private List<Employee> getEmployees() {
        return Stream.of(getEmployee(1L), getEmployee(2L)).collect(Collectors.toList());

    }

    @Ignore
    private static Employee getEmployee(Long id) {
        return Employee.builder()
                .employeeId(id)
                .businessUnit(BusinessUnit.ENGINEERING)
                .title("Developer")
                .competencies("Test")
                .place("DELHI")
                .salary(10000)
                .build();
    }


}
