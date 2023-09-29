package com.facilio.analytics.v2.command;

import com.facilio.analytics.v2.V2AnalyticsOldUtil;
import com.facilio.analytics.v2.context.V2MeasuresContext;
import com.facilio.analytics.v2.context.V2ReportContext;
import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.AggregateOperator;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.report.context.ReadingAnalysisContext;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportDataPointContext;
import com.facilio.report.context.ReportYAxisContext;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class V2CreateOldAnalyticsReportCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context)throws Exception
    {
        V2ReportContext report = (V2ReportContext) context.get("report_v2");
        ReportContext report_context = context.get("report")  != null ? (ReportContext) context.get("report") : new ReportContext();
        ReportContext reportContext = V2AnalyticsOldUtil.constructReportOld(report, report_context);
        reportContext.setType(ReportContext.ReportType.READING_REPORT);
        setModeWiseXAggr(reportContext, ReadingAnalysisContext.ReportMode.valueOf(report.getReportMode()));
        if(report.getReportTTimeFilter() != null) {
            reportContext.setReportTTimeFilter(report.getReportTTimeFilter());
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
        for(V2MeasuresContext measure : measures)
        {
            ReportDataPointContext dataPoint = new ReportDataPointContext();
            ReportYAxisContext yAxis = new ReportYAxisContext();
            FacilioField measureField = modBean.getField(measure.getFieldId());
            yAxis.setField(measureField.getModule(), measureField);
            yAxis.setAggr(measure.getAggr());
            dataPoint.setyAxis(yAxis);

            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(measureField.getModule().getName()));
            V2AnalyticsOldUtil.setXAndDateFields(dataPoint, report.getReportModeEnum(), fieldMap);
            String moduleName = measure.getModuleName();
            if (moduleName == null) {
                moduleName = FacilioConstants.ContextNames.RESOURCE;
            }
            if (dataPoint.isFetchResource()) {
                resourceAlias.put(reportContext.getxAlias(), moduleName);
            }
            dataPoint.setDefaultSortPoint(measure.isDefaultSortPoint());
            dataPoint.setModuleName(moduleName);
            dataPoint.setName(dataPoint.getyAxis().getField().getDisplayName());
            dataPoint.setResourceName("");
            dataPoint.setAssetCategoryId(-1);
            dataPoint.setType(ReportDataPointContext.DataPointType.MODULE);
            dataPoint.setAliases(measure.getAliases());
            dataPoint.setV2Criteria(measure.getCriteria());
            if(measure.getParentModuleName() != null) {
                FacilioModule parentModule = modBean.getModule(measure.getParentModuleName());
                dataPoint.setParentReadingModule(parentModule);
            }
            dataPoints.add(dataPoint);
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
        if (aggr != null) {
            if (report.getxAggrEnum() != null) {
                if (report.getxAggrEnum() != BmsAggregateOperators.CommonAggregateOperator.ACTUAL || report.getxAggrEnum() != BmsAggregateOperators.DateAggregateOperator.HOURSOFDAYONLY) {
                    throw new IllegalArgumentException("Report X Aggr cannot be specified explicitly for these modes");
                }
            }
            report.setxAggr(aggr);
        }
    }
}
