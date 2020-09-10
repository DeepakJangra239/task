package com.test.task.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.opencsv.bean.CsvBindByName;
import com.test.task.enums.BusinessUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Employee implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "EmployeeID")
    private Long employeeId;

    @Column(name = "EmployeeName")
    private String employeeName;

    @Column(name = "Title")
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "BusinessUnit")
    private BusinessUnit businessUnit;

    @Column(name = "Place")
    private String place;

    @Column(name = "SupervisorId")
    private Long supervisorId;

    @Column(name = "Competencies")
    private String competencies;

    @Column(name = "Salary")
    private Integer salary;

    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    private List<Employee> reportees = new ArrayList<>();
}
