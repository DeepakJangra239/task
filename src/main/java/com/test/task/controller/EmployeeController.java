package com.test.task.controller;


import com.test.task.entity.Employee;
import com.test.task.enums.BusinessUnit;
import com.test.task.service.EmployeeService;
import com.test.task.utils.MappingUrls;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(MappingUrls.apiVersionV1)
public class EmployeeController {

    private final EmployeeService employeeService;

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PutMapping(MappingUrls.Employee.updateSalaryForAllEmployeeByPlace)
    public List<Employee> updateSalaryForAllEmployeeByPlace(@PathVariable String place, @PathVariable double percentage) {
        return employeeService.updateSalaryForEmployees(place, percentage);
    }

    @GetMapping(MappingUrls.Employee.getEmployeesForPlace)
    public List<Employee> getEmployeesForPlace(@PathVariable String place) {
        return employeeService.getEmployeeList(place);
    }

    @GetMapping(MappingUrls.Employee.getEmployee)
    public Employee getEmployee(@PathVariable Long id) {
        return employeeService.getEmployee(id);
    }

    @ResponseBody
    @GetMapping(MappingUrls.Employee.getTotalSalary)
    public Integer getTotalSalary(String place, Long supervisorId, BusinessUnit unit) {
        return employeeService.getTotalSalaryBy(place, supervisorId, unit);
    }

    @ResponseBody
    @GetMapping(MappingUrls.Employee.getSalaryRangeByTitle)
    public Map<String, Integer> getSalaryRangeByTitle(@PathVariable String title) {
        return employeeService.getSalaryRangeByTitle(title);
    }
}
