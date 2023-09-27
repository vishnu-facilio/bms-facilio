package com.facilio.analytics.v2;

import com.facilio.analytics.v2.context.V2ReportContext;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.BaseLineAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.report.context.*;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class V2AnalyticsOldUtil {

    public static ReportContext constructReportOld(V2ReportContext report, ReportContext report_context)throws Exception
    {
        report_context.setName(report.getName());
        report_context.setDateOperator(DateOperators.BETWEEN);
        report_context.setDateValue(new StringBuilder().append(report.getTimeFilter().getStartTime()).append(", ").append(report.getTimeFilter().getEndTime()).toString());
        String baseLinesString = report.getBaseLinesString();
        V2AnalyticsOldUtil.fetchBaseLines(report, baseLinesString,report_context);
        report_context.setChartState(report.getChartState());
        if(report.getTimeFilter().getDateOperator() != -1)
        {
            report_context.setDateOperator(report.getTimeFilter().getDateOperator());
        }
        report_context.setxAlias(FacilioConstants.ContextNames.REPORT_DEFAULT_X_ALIAS);
        report_context.setxAggr(report.getDimensions().getxAggr());
        report_context.setAnalyticsType(ReadingAnalysisContext.AnalyticsType.PORTFOLIO);
        if(report.getGroupBy() != null && report.getGroupBy().getTime_aggr() > 0)
        {
            report_context.setgroupByTimeAggr(report.getGroupBy().getTime_aggrEnum());
            report_context.addToReportState(FacilioConstants.ContextNames.REPORT_GROUP_BY_TIME_AGGR, report.getGroupBy().getTime_aggr());
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        report_context.setModuleId(modBean.getModule("energydata").getModuleId());

        return report_context;
    }

    public static void fetchBaseLines (V2ReportContext report, String baseLineString, ReportContext reportContext) throws Exception
    {
        if(baseLineString != null)
        {
            JSONParser parser = new JSONParser();
            JSONArray reportBaseLine_arr = (JSONArray) parser.parse(baseLineString);
            List<ReportBaseLineContext> reportBaseLines = FieldUtil.getAsBeanListFromJsonArray(reportBaseLine_arr, ReportBaseLineContext.class);
            if (reportBaseLines != null && !reportBaseLines.isEmpty()) {
                List<Long> baseLineIds = new ArrayList<>();
                for (ReportBaseLineContext baseLine : reportBaseLines) {
                    if (baseLine.getBaseLineId() != -1) {
                        baseLineIds.add(baseLine.getBaseLineId());
                    } else {
                        throw new IllegalArgumentException("Give valid baseline id");
                    }
                }
                Map<Long, BaseLineContext> baseLines = BaseLineAPI.getBaseLinesAsMap(baseLineIds);

                if (baseLines == null || baseLines.isEmpty()) {
                    throw new IllegalArgumentException("Give valid baseline id");
                }

                for (ReportBaseLineContext baseLine : reportBaseLines) {
                    baseLine.setBaseLine(baseLines.get(baseLine.getBaseLineId()));

                    if (baseLine.getAdjustTypeEnum() == null) {
                        baseLine.setAdjustType(BaseLineContext.AdjustType.WEEK);
                    }

                    if (baseLine.getBaseLine() == null) {
                        throw new IllegalArgumentException("Give valid baseline id. " + baseLine.getBaseLineId() + " is invalid");
                    }
                }
                reportContext.setBaseLines(reportBaseLines);
            } else {
                reportContext.setBaseLines(null);
            }
        }
    }

    public static void setXAndDateFields(ReportDataPointContext dataPoint, ReadingAnalysisContext.ReportMode mode, Map<String, FacilioField> fieldMap) throws Exception {
        FacilioField xField = null;
        ModuleBean bean = (ModuleBean)BeanFactory.lookup("ModuleBean");
        switch (mode) {
            case SERIES:
                xField = fieldMap.get("parentId");
                break;
            case SITE:
            case BUILDING:
            case FLOOR:
            case SPACE:
            case RESOURCE:
                if(fieldMap.get("parentId")!= null) {
                    xField = fieldMap.get("parentId");
                }else {
                    FacilioModule module = bean.getModule(mode.getStringVal());
                    xField = FieldFactory.getIdField(module);
                }
                dataPoint.setFetchResource(true);
                break;
            case TIMESERIES:
            case CONSOLIDATED:
            case TIME_CONSOLIDATED:
            case TIME_SPLIT:
            case TIME_DURATION:
                xField = fieldMap.get("ttime");
                break;
        }
        ReportFieldContext xAxis = new ReportFieldContext();
        xAxis.setField(xField.getModule(), xField);
        dataPoint.setxAxis(xAxis);
        if(fieldMap.get("ttime")!= null) {
            ReportFieldContext dateField = new ReportFieldContext();
            dateField.setField(fieldMap.get("ttime").getModule(), fieldMap.get("ttime"));
            dataPoint.setDateField(dateField);
        }
    }
    public static Criteria setFieldInCriteria(Criteria criteria, FacilioModule module)throws Exception
    {
        if(criteria != null)
        {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            for (String key :  criteria.getConditions().keySet())
            {
                Condition condition = criteria.getConditions().get(key);
                if (module == null || condition == null || condition.getFieldName() == null) continue;
                FacilioField field = modBean.getField(condition.getFieldName(), module.getName());
                if(field == null) continue;
                condition.setField(field);
            }
            return criteria;
        }
        return null;
    }
}
