package com.facilio.report.context;

import com.facilio.db.criteria.Criteria;
import org.json.simple.JSONObject;

public class PivotColumnContext {
    private ReportPivotTableColumnType columnType;
    private String alias;
    private ReportPivotFieldContext field;
    private JSONObject formatting;
    private String moduleName;
    private Criteria criteria;

    private Criteria dataFilter;
    public ReportPivotTableColumnType getColumnType() {
        return columnType;
    }

    public void setColumnType(ReportPivotTableColumnType columnType) {
        this.columnType = columnType;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public ReportPivotFieldContext getField() {
        return field;
    }

    public void setField(ReportPivotFieldContext field) {
        this.field = field;
    }

    public JSONObject getFormatting() {
        return formatting;
    }

    public void setFormatting(JSONObject formatting) {
        this.formatting = formatting;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public Criteria getCriteria() {
        return criteria;
    }

    public void setCriteria(Criteria criteria) {
        this.criteria = criteria;
    }

    public Criteria getDataFilter() {
        return dataFilter;
    }

    public void setDataFilter(Criteria dataFilter) {
        this.dataFilter = dataFilter;
    }
}
