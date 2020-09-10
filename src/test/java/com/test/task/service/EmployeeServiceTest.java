package com.test.task.service;

import com.test.task.entity.Employee;
import com.test.task.enums.BusinessUnit;
import com.test.task.repository.EmployeeRepository;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.atLeast;

@SpringBootTest
public class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private IEmployeeService employeeService;

    private Employee employee;

    @Test
    public void whenGetById_ShouldReturnEmployee() {
        Mockito.when(employeeRepository.findByEmployeeId(Mockito.any())).thenReturn(getEmployee(1L));
        Employee employee = employeeService.getEmployee(1L);
        Mockito.verify(employeeRepository, atLeast(1)).findByEmployeeId(1L);
        assertThat(employee.getPlace()).isEqualTo("DELHI");
        assertThat(employee.getSalary()).isEqualTo(10000);
        assertThat(employee.getBusinessUnit()).isEqualTo(BusinessUnit.ENGINEERING);
        assertThat(employee.getCompetencies()).isEqualTo("Test");
    }

    @Test
    public void whenGetById_ButIdNotPresent_ShouldThrowException() {
        Mockito.when(employeeRepository.findByEmployeeId(Mockito.any())).thenReturn(null);
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> employeeService.getEmployee(1L));
        Mockito.verify(employeeRepository, atLeast(1)).findByEmployeeId(1L);
        String expectedMessage = "Employee Not Found";
        assertTrue(exception.getStatus().is4xxClientError());
        assertTrue(Objects.requireNonNull(exception.getMessage()).contains(expectedMessage));
    }

    @Test
    public void whenFindByPlace_shouldReturnEmployees() {
        Mockito.when(employeeRepository.findByPlace(Mockito.any())).thenReturn(new ArrayList<>(getEmployees()));
        List<Employee> employeeList = employeeService.getEmployeeList("DELHI");
        Mockito.verify(employeeRepository, atLeast(1)).findByPlace("DELHI");
        assertThat(employeeList).hasSize(2);
        assertThat(employeeList.get(0).getPlace()).isEqualTo("DELHI");
        assertThat(employeeList.get(1).getPlace()).isEqualTo("DELHI");
    }

    @Test
    public void whenFindByPlace_ButPlaceIsNotPresent_shouldThrowException() {
        Mockito.when(employeeRepository.findByPlace(Mockito.any())).thenReturn(new ArrayList<>());
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> employeeService.getEmployeeList("DELHI"));
        Mockito.verify(employeeRepository, atLeast(1)).findByPlace("DELHI");
        String expectedMessage = "No Employees found for this place";
        assertTrue(exception.getStatus().is4xxClientError());
        assertTrue(Objects.requireNonNull(exception.getMessage()).contains(expectedMessage));
    }

    @Test
    public void shouldCalculateTotalSalaryForEmployeesByPlace() {
        Mockito.when(employeeRepository.findByPlace(Mockito.any())).thenReturn(new ArrayList<>(getEmployees()));
        Integer expected = employeeService.getTotalSalaryBy("DELHI", null, null);
        Mockito.verify(employeeRepository, atLeast(1)).findByPlace("DELHI");
        assertThat(expected).isEqualTo(20000);
    }

    @Test
    public void shouldCalculateTotalSalaryForEmployeesBySupervisorId() {
        employee = getEmployee(1L);
        employee.setReportees(getEmployees());
        Mockito.when(employeeRepository.findByEmployeeId(Mockito.any())).thenReturn(employee);
        Integer expected = employeeService.getTotalSalaryBy(null, 1L, null);
        Mockito.verify(employeeRepository, atLeast(1)).findByEmployeeId(1L);
        assertThat(expected).isEqualTo(20000);
    }

    @Test
    public void whenCalculateTotalSalaryForEmployeesBySupervisorId_ButEmployeeIsNotPresent_ShouldThrowException() {
        Mockito.when(employeeRepository.findByEmployeeId(Mockito.any())).thenReturn(null);
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> employeeService.getTotalSalaryBy(null, 1L, null));
        Mockito.verify(employeeRepository, atLeast(1)).findByEmployeeId(1L);
        String expectedMessage = "Invalid SupervisorID";
        assertTrue(exception.getStatus().is4xxClientError());
        assertTrue(Objects.requireNonNull(exception.getMessage()).contains(expectedMessage));
    }

    @Test
    public void shouldCalculateTotalSalaryForEmployeesByBusinessUnit() {
        Mockito.when(employeeRepository.findByBusinessUnit(Mockito.any())).thenReturn(new ArrayList<>(getEmployees()));
        Integer expected = employeeService.getTotalSalaryBy(null, null, BusinessUnit.ENGINEERING);
        Mockito.verify(employeeRepository, atLeast(1)).findByBusinessUnit(BusinessUnit.ENGINEERING);
        assertThat(expected).isEqualTo(20000);
    }

    @Test
    public void whenCalculateTotalSalaryForEmployees_ButNoFilterParameterGiven_ShouldThrowException() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> employeeService.getTotalSalaryBy(null, null, null));
        String expectedMessage = "Invalid filter parameter";
        assertTrue(exception.getStatus().is4xxClientError());
        assertTrue(Objects.requireNonNull(exception.getMessage()).contains(expectedMessage));
    }

    @Test
    public void shouldGetSalaryRangeByTitle() {
        Mockito.when(employeeRepository.findByTitle(Mockito.any())).thenReturn(new ArrayList<>(getEmployees()));
        Map<String, Integer> result = employeeService.getSalaryRangeByTitle("Developer");
        assertThat(result).isNotNull();
        assertThat(result.get("min")).isEqualTo(10000);
        assertThat(result.get("max")).isEqualTo(10000);
    }

    @Test
    public void whenGetSalaryRangeByTitle_ButTitleIsNotPresent_ShouldThrowException() {
        Mockito.when(employeeRepository.findByTitle(Mockito.any())).thenReturn(new ArrayList<>());
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> employeeService.getSalaryRangeByTitle("Developer"));
        String expectedMessage = "Invalid Title";
        assertTrue(exception.getStatus().is4xxClientError());
        assertTrue(Objects.requireNonNull(exception.getMessage()).contains(expectedMessage));
    }

    @Test
    public void shouldUpdateSalaryOfEmployeeByPercentage() {
        ArgumentCaptor<List> argument = ArgumentCaptor.forClass(List.class);
        Mockito.when(employeeRepository.findByPlace(Mockito.any())).thenReturn(new ArrayList<>(getEmployees()));
        employeeService.updateSalaryForEmployees("DELHI", 10);
        Mockito.verify(employeeRepository, atLeast(1)).findByPlace("DELHI");
        Mockito.verify(employeeRepository).saveAll(argument.capture());
        List<Employee> expected = argument.getValue();
        assertThat(expected).hasSize(2);
        assertThat(expected.get(0).getSalary()).isEqualTo(11000);
        assertThat(expected.get(1).getSalary()).isEqualTo(11000);
    }

    @Test
    public void whenUpdateSalaryOfEmployeeByPercentage_ButPlaceIsNotPresent_ShouldThrowException() {
        Mockito.when(employeeRepository.findByPlace(Mockito.any())).thenReturn(new ArrayList<>());
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> employeeService.updateSalaryForEmployees("DELHI", 10));
        Mockito.verify(employeeRepository, atLeast(1)).findByPlace("DELHI");
        String expectedMessage = "Invalid place";
        assertTrue(exception.getStatus().is4xxClientError());
        assertTrue(Objects.requireNonNull(exception.getMessage()).contains(expectedMessage));
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
