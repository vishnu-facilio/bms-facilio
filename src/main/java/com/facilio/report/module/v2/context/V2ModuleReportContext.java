package com.facilio.report.module.v2.context;

import com.facilio.analytics.v2.context.V2AnalyticsGroupByContext;
import com.facilio.analytics.v2.context.V2DimensionContext;
import com.facilio.analytics.v2.context.V2MeasuresContext;
import com.facilio.analytics.v2.context.V2TimeFilterContext;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldUtil;
import com.facilio.report.context.ReportBaseLineContext;
import com.facilio.report.context.ReportDrilldownPathContext;
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
public class V2ModuleReportContext
{
    private long id=-1;
    private long appId=-1;
    private long reportId=-1;
    private String name;
    private String description;
    private long  folderId;
    private String  moduleName;
    private String chartState;
    private V2ModuleDimensionContext dimensions;
    private List<V2ModuleMeasureContext> measures;
    private V2ModuleGroupByContext groupBy;
    private V2ModuleTimeFilterContext timeFilter;
    private V2ModuleFilterContext filters;
    private ReportDrilldownPathContext drillDown;
    private String baseLines;
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
        this.dimensions = FieldUtil.getAsBeanFromJson(jsonObject, V2ModuleDimensionContext.class);
    }
    public void setTimeFilterJson(String timeFilter) throws Exception {
        JSONObject jsonObject = FacilioUtil.parseJson(timeFilter);
        this.timeFilter = FieldUtil.getAsBeanFromJson(jsonObject, V2ModuleTimeFilterContext.class);
    }
    public void setGroupByJson(String groupBy) throws Exception {
        JSONObject jsonObject = FacilioUtil.parseJson(groupBy);
        this.groupBy = FieldUtil.getAsBeanFromJson(jsonObject, V2ModuleGroupByContext.class);
    }
    public void setMeasuresJson(String measures) throws Exception
    {
        JSONArray json_arr = FacilioUtil.parseJsonArray(measures);
        for(Object measure:  json_arr)
        {
            V2ModuleMeasureContext measure_context = FieldUtil.getAsBeanFromJson((JSONObject) measure, V2ModuleMeasureContext.class);
            if(this.measures == null){
                this.measures = new ArrayList<>();
            }
            this.measures.add(measure_context);
        }
    }
}
