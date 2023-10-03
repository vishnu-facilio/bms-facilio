package com.facilio.analytics.v2.context;

import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.AggregateOperator;
import com.facilio.modules.FieldUtil;
import com.facilio.report.context.ReadingAnalysisContext;
import com.facilio.report.context.ReportBaseLineContext;
import com.facilio.report.context.ReportFilterContext;
import com.facilio.util.FacilioUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.struts2.json.annotations.JSON;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class V2ReportContext {

    public long id=-1l;
    public Long reportId=-1l;
    public V2DimensionContext dimensions =null;
    public List<V2MeasuresContext> measures = null;
    public V2ReportFiltersContext g_criteria = null;
    public V2TimeFilterContext timeFilter = null;
    public V2AnalyticsGroupByContext groupBy = null;
    private List<ReportBaseLineContext> baseLines;
    private String baseLinesString;
    public int reportType;
    public String name;
    public String description;
    private String xAlias;
    private String chartState;
    public JSONObject reportTTimeFilter;
    public V2ReportType reportTypeEnum;
    public int reportMode;
    private long folderId;
    private Long criteriaId;
    public ReadingAnalysisContext.ReportMode reportModeEnum;

    public void setReportMode(int reportMode){
        this.reportMode = reportMode;
        this.setReportModeEnum(ReadingAnalysisContext.ReportMode.valueOf(reportMode));
    }
    public void setReportType(int reportType) {
        this.reportType = reportType;
        this.setReportTypeEnum(reportType);
    }
    public void setReportTypeEnum(int reportType) {
        this.reportTypeEnum = V2ReportType.valueOf(reportType);
    }
    public enum V2ReportType
    {
        READING_REPORT, WORKORDER_REPORT, REGRESSION_REPORT, TEMPLATE_REPORT, PIVOT_REPORT;

        public int getValue() {
            return ordinal() + 1;
        }

        public static V2ReportContext.V2ReportType valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }

    @JSON(serialize = false)
    public String getDimensionsJson() throws Exception {
        if (dimensions != null) {
            return FieldUtil.getAsJSON(dimensions).toJSONString();
        }
        return null;
    }
    @JSON(serialize = false)
    public String getMeasuresJson() throws Exception {
        if (measures != null) {
            return FieldUtil.getAsJSONArray(measures, V2MeasuresContext.class).toJSONString();
        }
        return null;
    }
    @JSON(serialize = false)
    public String getGroupByJson() throws Exception {
        if (groupBy != null) {
            return FieldUtil.getAsJSON(groupBy).toJSONString();
        }
        return null;
    }
    @JSON(serialize = false)
    public String getTimeFilterJson() throws Exception {
        if (timeFilter != null) {
            return FieldUtil.getAsJSON(timeFilter).toJSONString();
        }
        return null;
    }

    public void setDimensionsJson(String dimensions) throws ParseException, IOException {
        JSONObject jsonObject = FacilioUtil.parseJson(dimensions);
        this.dimensions = FieldUtil.getAsBeanFromJson(jsonObject, V2DimensionContext.class);
    }
    public void setTimeFilterJson(String timeFilter) throws Exception {
        JSONObject jsonObject = FacilioUtil.parseJson(timeFilter);
        this.timeFilter = FieldUtil.getAsBeanFromJson(jsonObject, V2TimeFilterContext.class);
    }
    public void setGroupByJson(String groupBy) throws Exception {
        JSONObject jsonObject = FacilioUtil.parseJson(groupBy);
        this.groupBy = FieldUtil.getAsBeanFromJson(jsonObject, V2AnalyticsGroupByContext.class);
    }
    public void setMeasuresJson(String measures) throws Exception
    {
        JSONArray json_arr = FacilioUtil.parseJsonArray(measures);
        for(Object measure:  json_arr)
        {
            V2MeasuresContext measure_context = FieldUtil.getAsBeanFromJson((JSONObject) measure, V2MeasuresContext.class);
            if(measure_context.getCriteriaId() > 0){
               measure_context.setCriteria(CriteriaAPI.getCriteria(measure_context.getCriteriaId()));
            }
            if(this.measures == null){
                this.measures = new ArrayList<>();
            }
            this.measures.add(measure_context);
        }
    }



}
