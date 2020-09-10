package com.test.task.service;

import com.test.task.entity.Employee;
import com.test.task.enums.BusinessUnit;
import com.test.task.repository.EmployeeRepository;
import com.test.task.utils.CacheNames;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class IEmployeeService implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Override
    @CachePut(value = CacheNames.EMPLOYEES, unless = "#result.isEmpty()", key = "#place")
    public List<Employee> updateSalaryForEmployees(String place, double percentage) {
        log.debug("Updating salary by {}% for all employees from {}", percentage, place);
        List<Employee> employees = employeeRepository.findByPlace(place);
        if (employees.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid place");
        employees.forEach(employee -> salaryIncrement(employee, percentage));
        log.debug("Salaries updated");
        return employeeRepository.saveAll(employees);
    }

    @Override
    @Cacheable(value = CacheNames.EMPLOYEES, key = "#place", unless = "#result.isEmpty()")
    public List<Employee> getEmployeeList(String place) {
        log.debug("Fetching Employee list of {}", place);
        List<Employee> employees = employeeRepository.findByPlace(place);
        if (employees.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No Employees found for this place");
        return employees;
    }

    @Override
    @Cacheable(value = CacheNames.EMPLOYEE, key = "#id", condition = "#id > 0")
    public Employee getEmployee(Long id) {
        log.debug("Fetching Employee & Reportee list for Employee {}", id);
        Employee employee = Optional.ofNullable(employeeRepository.findByEmployeeId(id))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee Not Found"));
        Hibernate.initialize(employee.getReportees());
        return employee;
    }

    @Override
    public Integer getTotalSalaryBy(String place, Long supervisorId, BusinessUnit businessUnit) {
        List<Employee> employees;
        if (nonNull(place)) {
            employees = employeeRepository.findByPlace(place);
        } else if (nonNull(supervisorId)) {
            Employee employee = Optional.ofNullable(employeeRepository.findByEmployeeId(supervisorId))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid SupervisorID"));
            Hibernate.initialize(employee.getReportees());
            employees = employee.getReportees();
        } else {
            employees = employeeRepository.findByBusinessUnit(businessUnit);
        }
        if (employees.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid filter parameter");
        return employees.parallelStream().mapToInt(Employee::getSalary).sum();
    }

    public Map<String, Integer> getSalaryRangeByTitle(String title) {
        log.debug("Fetching salary range for {}", title);
        Map<String, Integer> range = new HashMap<>();
        List<Employee> employees = employeeRepository.findByTitle(title);
        if (employees.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid Title");
        try {
            range.put("min", Collections.min(employees, Comparator.comparing(Employee::getSalary)).getSalary());
            range.put("max", Collections.max(employees, Comparator.comparing(Employee::getSalary)).getSalary());
        } catch (Exception e) {
            log.error("Error occurred while generating Salary min/max range due to {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Please try again after sometime!");
        }
        return range;
    }

    private void salaryIncrement(Employee employee, double percentage) {
        Integer increasedSalary = Math.toIntExact(Math.round(employee.getSalary() + (percentage / 100d) * employee.getSalary()));
        employee.setSalary(increasedSalary);
    }
}
