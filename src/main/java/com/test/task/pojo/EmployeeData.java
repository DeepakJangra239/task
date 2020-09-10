package com.test.task.pojo;

import com.opencsv.bean.CsvBindByName;
import com.test.task.entity.Employee;
import com.test.task.enums.BusinessUnit;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class EmployeeData {

    @CsvBindByName(column = "EmployeeID")
    private Long employeeId;

    @CsvBindByName
    private String employeeName;

    @CsvBindByName
    private String title;

    @CsvBindByName
    private String businessUnit;

    @CsvBindByName
    private String place;

    @CsvBindByName(column = "supervisorID")
    private Long supervisorId;

    @CsvBindByName
    private String competencies;

    @CsvBindByName
    private Integer salary;

    public static List<Employee> getEmployeeListFromData(List<EmployeeData> employeeDataList) {
        List<Employee> employeeList = new ArrayList<>();
        employeeDataList.forEach(employeeData ->
                employeeList.add(Employee.builder()
                        .employeeId(employeeData.employeeId)
                        .employeeName(employeeData.employeeName)
                        .title(employeeData.title)
                        .businessUnit(BusinessUnit.getBusinessUnitByValue(employeeData.businessUnit.toUpperCase()))
                        .place(employeeData.place)
                        .supervisorId(employeeData.supervisorId)
                        .competencies(employeeData.competencies)
                        .salary(employeeData.salary)
                        .build())
        );
        return employeeList;
    }
}
