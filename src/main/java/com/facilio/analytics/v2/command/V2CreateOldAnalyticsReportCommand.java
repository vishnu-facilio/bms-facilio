package com.facilio.analytics.v2.command;

import com.facilio.analytics.v2.V2AnalyticsOldUtil;
import com.facilio.analytics.v2.context.V2AnalyticsContextForDashboardFilter;
import com.facilio.analytics.v2.context.V2DimensionContext;
import com.facilio.analytics.v2.context.V2MeasuresContext;
import com.facilio.analytics.v2.context.V2ReportContext;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.report.ReportDynamicKpiContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.ns.context.NameSpaceField;
import com.facilio.readingkpi.context.ReadingKPIContext;
import com.facilio.report.context.*;
import com.facilio.report.util.ReportUtil;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.facilio.readingkpi.ReadingKpiAPI.getReadingKpi;

public class V2CreateOldAnalyticsReportCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context)throws Exception
    {
        V2ReportContext report = (V2ReportContext) context.get("report_v2");
        V2AnalyticsContextForDashboardFilter db_filter = (V2AnalyticsContextForDashboardFilter) context.get("db_filter");
        ReportContext report_context = context.get("report")  != null ? (ReportContext) context.get("report") : new ReportContext();
        ReportContext reportContext = V2AnalyticsOldUtil.constructReportOld(report, report_context);
        reportContext.setType(ReportContext.ReportType.READING_REPORT);
        setModeWiseXAggr(reportContext, ReadingAnalysisContext.ReportMode.valueOf(report.getReportMode()));
        if(report.getReportTTimeFilter() != null) {
            reportContext.setReportTTimeFilter(report.getReportTTimeFilter());
        }
        if(db_filter != null && db_filter.getDb_user_filter() != null){
            context.put(FacilioConstants.ContextNames.REPORT_USER_FILTER_VALUE, db_filter.getDb_user_filter());
        }
        this.constructDataPoints(report, reportContext);
        context.put(FacilioConstants.ContextNames.REPORT, reportContext);
        return false;
    }
    private void constructDataPoints(V2ReportContext report, ReportContext reportContext)throws Exception
    {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<V2MeasuresContext> measures = report.getMeasures();
        JSONObject resourceAlias = new JSONObject();
        List<ReportDataPointContext> dataPoints = new ArrayList<>();
        boolean sortdataPointFlag=true;
        for(V2MeasuresContext measure : measures)
        {
            ReportDataPointContext dataPoint = new ReportDataPointContext();
            ReportYAxisContext yAxis = new ReportYAxisContext();
            if(measure.getDynamicKpiId() != null && measure.getDynamicKpiId() > 0)
            {
                ReportDynamicKpiContext dynamic_kpi = new ReportDynamicKpiContext();
                dynamic_kpi.setDynamicKpi(measure.getDynamicKpiId().toString());
                dynamic_kpi.setCategory(measure.getCategory());
                dynamic_kpi.setParentId(null);
                dynamic_kpi.setV2Analytics(true);
                dataPoint.setDynamicKpi(dynamic_kpi);
                ReadingKPIContext dynKpi = getReadingKpi(Long.valueOf(dynamic_kpi.getDynamicKpi()));
                for (NameSpaceField nsField : dynKpi.getNs().getFields())
                {
                    FacilioField field = nsField.getField();
                    List<FacilioField> allFields = modBean.getAllFields(field.getModule().getName());
                    Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(allFields);
                    V2AnalyticsOldUtil.setXAndDateFields(dataPoint, ReadingAnalysisContext.ReportMode.valueOf(report.getReportMode()), fieldMap);
                    break;
                }
                yAxis.setUnitStr(dynKpi.getUnitLabel());
                yAxis.setLabel(measure.getName());
                yAxis.setDataType(FieldType.DECIMAL);
                dataPoint.setyAxis(yAxis);
                dataPoint.setduplicateDataPoint(Boolean.FALSE);
                dataPoint.addMeta(FacilioConstants.ContextNames.PARENT_ID_LIST, null);
            }
            else
            {
                FacilioField measureField = modBean.getField(measure.getFieldId());
                yAxis.setField(measureField.getModule(), measureField);
                yAxis.setAggr(measure.getAggr());
                dataPoint.setModuleName(measure.getModuleName());
                dataPoint.setyAxis(yAxis);
                dataPoint.setCriteriaType(measure.getCriteriaType() > 0 ? measure.getCriteriaType() : V2MeasuresContext.Criteria_Type.ALL.getIndex());
                Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(measureField.getModule().getName()));
                V2AnalyticsOldUtil.setXAndDateFields(dataPoint, report.getReportModeEnum(), fieldMap);
                if (measure.getModuleName() == null) {
                    dataPoint.setModuleName(FacilioConstants.ContextNames.RESOURCE);
                }
                if (dataPoint.isFetchResource() || dataPoint.isFetchMetersWithResource()) {
                    resourceAlias.put(reportContext.getxAlias(), FacilioConstants.ContextNames.RESOURCE);
                    reportContext.addToReportState(FacilioConstants.ContextNames.REPORT_RESOURCE_ALIASES,  resourceAlias);
                    if(dataPoint.isFetchMetersWithResource()){
                        dataPoint.setModuleName(FacilioConstants.Meter.METER);
                    }
                }
                else if(dataPoint.isFetchMeters()){
                    resourceAlias.put(reportContext.getxAlias(), FacilioConstants.Meter.METER);
                    reportContext.addToReportState(FacilioConstants.ContextNames.REPORT_RESOURCE_ALIASES,  resourceAlias);
                    dataPoint.setModuleName(FacilioConstants.Meter.METER);
                }
                dataPoint.setName(dataPoint.getyAxis().getField().getDisplayName());
                dataPoint.setResourceName("");
                /**
                    below portion of code is used to set the propoperty for (ENUM, BOOLEAN Type field), based on this
                    Data Aggregation Calculation happenes
                 */
                if(dataPoint.getxAxis() != null && (dataPoint.getxAxis().getDataTypeEnum() == FieldType.DATE_TIME || dataPoint.getxAxis().getDataTypeEnum() == FieldType.DATE) && dataPoint.getyAxis() != null && (dataPoint.getyAxis().getDataTypeEnum() == FieldType.BOOLEAN || dataPoint.getyAxis().getDataTypeEnum() == FieldType.ENUM) && (dataPoint.getyAxis().getAggr() == BmsAggregateOperators.CommonAggregateOperator.COUNT.getValue()))
                {
                    dataPoint.setHandleEnum(true);
                }
                else if (dataPoint.getxAxis() != null && (dataPoint.getxAxis().getDataTypeEnum() == FieldType.DATE_TIME || dataPoint.getxAxis().getDataTypeEnum() == FieldType.DATE) && (dataPoint.getyAxis() != null && (dataPoint.getyAxis().getDataTypeEnum() == FieldType.BOOLEAN || dataPoint.getyAxis().getDataTypeEnum() == FieldType.ENUM)) &&  (dataPoint.getyAxis().getAggrEnum() == null || dataPoint.getyAxis().getAggr() == BmsAggregateOperators.CommonAggregateOperator.ACTUAL.getValue())) {
                    dataPoint.getyAxis().setAggr(null);
                    dataPoint.setHandleEnum(true);
                }
                if(measure.getRelationship_id() != null && measure.getRelationship_id() > 0 && measure.getParent_measure_alias() != null){
                    dataPoint.setRelationship_id(measure.getRelationship_id());
                    dataPoint.setParent_measure_alias(measure.getParent_measure_alias());
                }
            }
            if(measure != null && measure.getBaseLineString() != null)
            {
                List<ReportBaseLineContext> measureBaseLines = V2AnalyticsOldUtil.fetchBaseLinesForReport(measure.getBaseLineString());
                if(measureBaseLines != null && measureBaseLines.size() > 0){
                    dataPoint.setBaseLine(measureBaseLines.get(0));
                }
            }
            dataPoint.setMultiMeasureChartType(report.getDimensions().isMultiMeasureChartType());
            dataPoint.setDefaultSortPoint(measure.isDefaultSortPoint());
            dataPoint.setAssetCategoryId(measure.getCategory() != null ? measure.getCategory(): -1);
            dataPoint.setType(ReportDataPointContext.DataPointType.MODULE);
            dataPoint.setAliases(measure.getAliases());
            dataPoint.setV2Criteria(measure.getCriteria());
            if (measure.getParentModuleName() != null) {
                FacilioModule parentModule = modBean.getModule(measure.getParentModuleName());
                dataPoint.setParentReadingModule(parentModule);
            }
            int orderByFn = measure.getOrderByFunction() != null ? Integer.valueOf(measure.getOrderByFunction()) : -1;
            if (dataPoint.getxAxis().getField() != null && orderByFn > 0 && orderByFn != ReportDataPointContext.OrderByFunction.NONE.getValue()) {
                dataPoint.setDefaultSortPoint(true);
                dataPoint.setOrderByFunc(orderByFn);
                List<String> orderBy = new ArrayList<>();
                orderBy.add(ReportUtil.getAggrFieldName(dataPoint.getyAxis().getField(), dataPoint.getyAxis().getAggrEnum()));
                dataPoint.setOrderBy(orderBy);
                if(measure.getLimit() != -1){
                    dataPoint.setLimit(measure.getLimit());
                }
                sortdataPointFlag = false;
            }
            else if(sortdataPointFlag && report.getDimensions().getDimension_type() != V2DimensionContext.DimensionType.TIME.getIndex())
            {
                sortdataPointFlag = false;
                dataPoint.setDefaultSortPoint(true);
                dataPoint.setOrderByFunc(ReportDataPointContext.OrderByFunction.valueOf(3));
                List<String> orderBy = new ArrayList<>();
                orderBy.add(ReportUtil.getAggrFieldName(dataPoint.getyAxis().getField(), dataPoint.getyAxis().getAggrEnum()));
                dataPoint.setOrderBy(orderBy);
                dataPoint.setLimit(20);
            }

            dataPoints.add(dataPoint);
            /**
             * below conditions to check for heatmap case
             */
            if(measure.getHmAggr() != null && !"".equals(measure.getHmAggr()))
            {
                reportContext.setAnalyticsType(ReadingAnalysisContext.AnalyticsType.HEAT_MAP);
            }
        }
        reportContext.setDataPoints(dataPoints);
    }
    private void setModeWiseXAggr (ReportContext report, ReadingAnalysisContext.ReportMode mode) {
        AggregateOperator aggr = null;
        switch (mode) {
            case SITE:
                aggr = BmsAggregateOperators.SpaceAggregateOperator.SITE;
                break;
            case BUILDING:
                aggr = BmsAggregateOperators.SpaceAggregateOperator.BUILDING;
                break;
            case FLOOR:
                aggr = BmsAggregateOperators.SpaceAggregateOperator.FLOOR;
                break;
            case SPACE:
                aggr = BmsAggregateOperators.SpaceAggregateOperator.SPACE;
                break;
            default:
                break;
        }
        if(aggr != null){
            report.setxAggr(aggr);
        }
    }
}
