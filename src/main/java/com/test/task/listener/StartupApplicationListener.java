package com.test.task.listener;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.test.task.entity.Employee;
import com.test.task.pojo.EmployeeData;
import com.test.task.repository.EmployeeRepository;
import com.test.task.utils.CacheNames;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class StartupApplicationListener {

    private final EmployeeRepository employeeRepository;
    private final CacheManager cacheManager;

    @Value("${employees.cache.file.path}")
    private String employeesCacheFile;

    @Value("${employee.cache.file.path}")
    private String employeeCacheFile;

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("Loading data in database from CSV...");
        try {
            List<Employee> employees = getEmployeeList();
            employees.forEach(this::insertEmployee);
            log.info("CSV data loaded in database");
        } catch (Exception e) {
            log.error("Error while loading data in DB : {}", e.getMessage());
        }
        updateCacheFromFile(employeesCacheFile, CacheNames.EMPLOYEES);
        updateCacheFromFile(employeeCacheFile, CacheNames.EMPLOYEE);
        log.info("Local cache updated into memory");
    }

    private void updateCacheFromFile(String cacheFilePath, String cacheName) {
        try {
            File file = new File(cacheFilePath);
            if (file.exists()) {
                FileInputStream f = new FileInputStream(file);
                ObjectInputStream s = new ObjectInputStream(f);
                ConcurrentHashMap<Object, Object> concurrentHashMap = (ConcurrentHashMap<Object, Object>) s.readObject();
                s.close();
                writeCache(concurrentHashMap, cacheName);
            }
        } catch (IOException | ClassNotFoundException e) {
            log.error("Unable to update cache from local storage");
        }
    }

    private void writeCache(ConcurrentHashMap<Object, Object> concurrentHashMap, String cacheName) {
        switch (cacheName) {
            case CacheNames.EMPLOYEES:
                for (Map.Entry<Object, Object> obj : concurrentHashMap.entrySet()) {
                    List<Employee> employees = (List<Employee>) obj.getValue();
                    Objects.requireNonNull(cacheManager.getCache(cacheName)).putIfAbsent(obj.getKey(), employees);
                }
                break;
            case CacheNames.EMPLOYEE:
                for (Object obj : concurrentHashMap.values()) {
                    Employee employee = (Employee) obj;
                    Objects.requireNonNull(cacheManager.getCache(cacheName)).putIfAbsent(employee.getEmployeeId(), employee);
                }
                break;
            default:
                log.debug("Invalid Cache Name");
        }
    }

    private List<Employee> getEmployeeList() throws IOException {
        File file = new ClassPathResource("/employee.csv").getFile();
        try (Reader reader = new FileReader(file)) {
            CsvToBean<EmployeeData> employees = new CsvToBeanBuilder<EmployeeData>(reader).withType(EmployeeData.class)
                    .withIgnoreLeadingWhiteSpace(Boolean.TRUE)
                    .build();
            return EmployeeData.getEmployeeListFromData(employees.parse());
        } catch (Exception e) {
            log.error("Failed to parse CSV file due to {}", e.getMessage());
            throw e;
        }
    }

    private void insertEmployee(Employee employee) {
        if (Objects.nonNull(employee.getSupervisorId())) {
            Employee supervisor = employeeRepository.getOne(employee.getSupervisorId());
            Hibernate.initialize(supervisor.getReportees());
            List<Employee> reportees = Optional.ofNullable(supervisor.getReportees()).orElseGet(ArrayList::new);
            reportees.add(employee);
            supervisor.setReportees(reportees);
            employeeRepository.save(supervisor);
        }
        employeeRepository.save(employee);
    }
}
