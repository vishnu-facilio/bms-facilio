package com.facilio.report.context;

public enum ReportPivotTableColumnType {
    ROW(1),
    DATA(2),
    FORMULA(3)
    ;

    private int enumValue;

    ReportPivotTableColumnType(int enumValue) {
        this.enumValue = enumValue;

    }
}
