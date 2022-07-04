package com.facilio.report.context;

public enum PivotValueColumnType {
    DATA("DATA"),
    FORMULA("FORMULA");

    private String enumValue;

    PivotValueColumnType(String enumValue) {
        this.enumValue = enumValue;
    }
}
