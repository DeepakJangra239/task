package com.test.task.controller;

import com.test.task.utils.CacheNames;
import com.test.task.utils.MappingUrls;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(MappingUrls.apiVersionV1)
public class CacheController {

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(MappingUrls.Cache.clearAll)
    @CacheEvict(cacheNames = {CacheNames.EMPLOYEES}, allEntries = true)
    public void clearAllCache() {
        log.info("All cache cleared");
    }
}
