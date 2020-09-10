package com.test.task;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.task.utils.CacheNames;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.PreDestroy;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@EnableCaching
@SpringBootApplication
@ComponentScan(basePackages = {"com.test.task.configuration", "com.test.task"})
public class TaskApplication {
    @Value("${employees.cache.file.path}")
    private String employeesCacheFile;

    @Value("${employee.cache.file.path}")
    private String employeeCacheFile;

    public static void main(String[] args) {
        SpringApplication.run(TaskApplication.class, args);
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, true);

        return mapper;
    }

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(CacheNames.EMPLOYEES, CacheNames.EMPLOYEE);
    }

    @PreDestroy
    public void saveCacheBeforeExit() {
        try {
            persistEmployeesCache(employeesCacheFile, CacheNames.EMPLOYEES);
            persistEmployeesCache(employeeCacheFile, CacheNames.EMPLOYEE);
            log.info("Cache persist to local storage before shutdown");
        } catch (Exception e) {
            log.error("Unable to persist cache before shutdown due to {}", e.getMessage());
        }
    }

    private void persistEmployeesCache(String cacheFile, String cacheName) throws Exception {
        ConcurrentMapCache inMemoryCache = (ConcurrentMapCache) cacheManager().getCache(cacheName);
        assert inMemoryCache != null;
        ConcurrentMap<Object, Object> cache = inMemoryCache.getNativeCache();
        File file = new File(cacheFile);
        FileOutputStream fileOutputStream;
        if (!file.exists()) {
            if (file.createNewFile()) {
                fileOutputStream = new FileOutputStream(file);
            } else {
                throw new Exception("Unable to create cache file");
            }
        } else {
            fileOutputStream = new FileOutputStream(file);
        }
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(cache);
        objectOutputStream.close();
    }

}
