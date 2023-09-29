package com.facilio.analytics.v2.context;

import com.facilio.modules.AggregateOperator;
import com.facilio.report.context.ReadingAnalysisContext;
import com.facilio.report.context.ReportBaseLineContext;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class V2ReportContext {

    public V2DimensionContext dimensions = new V2DimensionContext();
    public List<V2MeasuresContext> measures = new ArrayList<>();
    public V2ReportFiltersContext g_criteria = null;
    public V2TimeFilterContext timeFilter = null;
    public V2AnalyticsGroupByContext groupBy = new V2AnalyticsGroupByContext();
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

}
