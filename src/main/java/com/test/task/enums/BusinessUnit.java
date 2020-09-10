package com.test.task.enums;

public enum BusinessUnit {
    ENGINEERING("ENGINEERING"),
    BUSINESS("BUSINESS"),
    HR("HR"),
    MARKETING("MARKETING"),
    MANAGEMENT("MANAGEMENT");

    String value;

    BusinessUnit(String value) {
        this.value = value;
    }

    public static BusinessUnit getBusinessUnitByValue(String value) {
        return BusinessUnit.valueOf(value);
    }
}
