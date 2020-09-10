package com.test.task.service;

import com.test.task.entity.Employee;
import com.test.task.enums.BusinessUnit;

import java.util.List;
import java.util.Map;

public interface EmployeeService {
    List<Employee> updateSalaryForEmployees(String place, double percentage);

    List<Employee> getEmployeeList(String place);

    Employee getEmployee(Long id);

    Integer getTotalSalaryBy(String place, Long supervisorId, BusinessUnit unit);

    Map<String, Integer> getSalaryRangeByTitle(String title);
}
