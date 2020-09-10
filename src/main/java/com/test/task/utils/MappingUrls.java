package com.test.task.utils;

import lombok.experimental.UtilityClass;

public interface MappingUrls {

    public final String apiVersionV1 = "/api/v1";

    public interface Employee {
        public final String BASE = "/employee";

        public final String getEmployee = BASE + "/hierarchy/{id}";
        public final String getEmployeesForPlace = BASE + "/place/{place}";
        public final String updateSalaryForAllEmployeeByPlace = getEmployeesForPlace + "/salary/{percentage}";
        public final String getTotalSalary = BASE + "/salary/total";
        public final String getSalaryRangeByTitle = BASE + "/title/{title}/salary/range";

    }

    public interface Cache {
        public final String BASE = "/cache";

        public final String clearAll = BASE + "/clear";
    }
}
