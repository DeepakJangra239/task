package com.test.task.repository;

import com.test.task.entity.Employee;
import com.test.task.enums.BusinessUnit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findByPlace(String place);

    List<Employee> findByBusinessUnit(BusinessUnit unit);

    List<Employee> findByTitle(String title);

    Employee findByEmployeeId(Long employeeId);
}
