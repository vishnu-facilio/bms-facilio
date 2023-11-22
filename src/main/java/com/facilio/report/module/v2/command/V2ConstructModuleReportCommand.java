package com.facilio.report.module.v2.command;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.BaseLineAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.report.context.*;
import com.facilio.report.module.v2.context.*;
import com.facilio.report.util.ReportUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class V2ConstructModuleReportCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception
    {
        V2ModuleReportContext v2_report = (V2ModuleReportContext)  context.get("v2_report");
        ReportContext reportContext = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
        String moduleName = v2_report.getModuleName();
        reportContext = reportContext == null ? new ReportContext() : reportContext;
        reportContext.setType(ReportContext.ReportType.WORKORDER_REPORT);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        context.put("ModuleDisplayName", module.getDisplayName());
        context.put(FacilioConstants.ContextNames.MODULE, module);
        reportContext.setxAggr(v2_report.getDimensions().getAggr() == -1 ? 0 : v2_report.getDimensions().getAggr());
        reportContext.setxAlias("X");
        reportContext.setId(v2_report.getId() > 0 ? v2_report.getId() : -1l);
        reportContext.setAppId(v2_report.getAppId() > 0 ? v2_report.getAppId() : AccountUtil.getCurrentApp().getId());
        reportContext.setModuleId(module.getModuleId());
        reportContext.setName(v2_report.getName());
        reportContext.setReportFolderId(v2_report.getFolderId());
        reportContext.setChartState(v2_report.getChartState());
        reportContext.setDescription(v2_report.getDescription());
        for(V2ModuleMeasureContext measureContext : v2_report.getMeasures())
        {
            constructModuleDataPoints(v2_report, v2_report.getDimensions(), measureContext, reportContext);
        }
        if(v2_report.getChartState() != null) {
            reportContext.setChartState(v2_report.getChartState());
        }
        context.put(FacilioConstants.ContextNames.REPORT, reportContext);


        return false;
    }


    private void constructModuleDataPoints(V2ModuleReportContext v2_report, V2ModuleDimensionContext dimension , V2ModuleMeasureContext measure, ReportContext reportContext)throws Exception
    {
        ReportDataPointContext dataPointContext = new ReportDataPointContext();
        ReportFieldContext xAxis = new ReportFieldContext();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioField xField = null;
        if(dimension != null && dimension.getFieldName() != null && dimension.getModuleName() != null)
        {
            String fieldName = dimension.getFieldName();
            xField = dimension.isSpecial() ? ReportUtil.getField(modBean, fieldName, modBean.getModule(dimension.getModuleName())): modBean.getField(fieldName, dimension.getModuleName());
        }
        if(dimension.getSelected_lookup_values()!= null){
            xAxis.setSelectValuesOnly(dimension.getSelected_lookup_values());
        }
        if(dimension.getLookupFieldName() != null)
        {
            FacilioField lookupField = modBean.getField(dimension.getLookupFieldName(),dimension.getLookupModuleName());
            xAxis.setLookupFieldId(lookupField != null ? lookupField.getId() : xAxis.getLookupFieldId());
        }
        int xAggr = dimension.getAggr();
        if (xAggr > -1)
        {
            AggregateOperator aggregateOperator = AggregateOperator.getAggregateOperator(xAggr);
            if (aggregateOperator instanceof BmsAggregateOperators.DateAggregateOperator && isDateField(xField)) {
                reportContext.setxAggr(aggregateOperator);
            } else if (aggregateOperator instanceof BmsAggregateOperators.SpaceAggregateOperator) {
                reportContext.setxAggr(aggregateOperator);
            }
        }
        xAxis.setField(modBean.getModule(dimension.getModuleName()), xField);
        dataPointContext.setxAxis(xAxis);

        if(v2_report.getTimeFilter() != null)
        {
            int operator = v2_report.getTimeFilter().getDateOperators() > -1 ? v2_report.getTimeFilter().getDateOperators() : DateOperators.BETWEEN.getOperatorId();
            if(operator > -1)
            {
                reportContext.setDateOperator(operator);
                String fieldName = v2_report.getTimeFilter().getFieldName();
                if(fieldName == null || fieldName.equals("")){
                    fieldName = v2_report.getDimensions().getFieldName();
                }
                String moduleName = v2_report.getTimeFilter().getModuleName();
                if(moduleName == null || moduleName.equals("")){
                    moduleName = v2_report.getDimensions().getModuleName();
                }
                reportContext.setDateValue(new StringBuilder().append(v2_report.getTimeFilter().getStartTime()).append(",").append(v2_report.getTimeFilter().getEndTime()).toString());
                ReportFieldContext reportFieldContext = new ReportFieldContext();
                FacilioModule dateFieldModule = modBean.getModule(moduleName);
                FacilioField field = modBean.getField(fieldName, dateFieldModule.getName());
                if(field == null)
                {
                    field = ReportFactory.getReportField(fieldName);
                    if(field == null) {
                        field = FieldFactory.getSystemField((String) fieldName, dateFieldModule);
                    }
                }
                reportFieldContext.setField(dateFieldModule, field);
                dataPointContext.setDateField(reportFieldContext);
            }
        }

        ReportYAxisContext yAxis = new ReportYAxisContext();
        FacilioModule yAxisModule = modBean.getModule(measure.getModuleName());
        FacilioField measure_field=null;
        if(measure.getFieldName().equals("id"))
        {
            measure_field = FieldFactory.getIdField(yAxisModule);
        }else{
            measure_field = ReportUtil.getField(modBean, measure.getFieldName(), yAxisModule);
        }
        yAxis.setAggr(measure.getAggr() > -1 ? AggregateOperator.getAggregateOperator(measure.getAggr()) : BmsAggregateOperators.CommonAggregateOperator.COUNT);
        yAxis.setField(yAxisModule, measure_field);
        dataPointContext.setyAxis(yAxis);

        if(measure.getSortOrder() > 0 && measure_field != null)
        {
            List<String> orderBy = new ArrayList<>();
            if(measure_field.getFieldId() == -1 )
            {
                if (measure_field != null && measure_field.getName().equals("id")) {
                    orderBy.add(yAxis.getAggrEnum().getSelectField(measure_field.clone()).getCompleteColumnName());
                } else {
                    if (yAxis.getAggr() > -1) {
                        AggregateOperator orderByAggr = AggregateOperator.getAggregateOperator(yAxis.getAggr());
                        orderBy.add(orderByAggr.getSelectField(measure_field.clone()).getCompleteColumnName());
                    } else{
                        orderBy.add(measure_field.getCompleteColumnName());
                    }
                }
            }
            else if(measure_field.getFieldId() > 0)
            {
                if (yAxis.getAggr() > -1) {
                    AggregateOperator orderByAggr = AggregateOperator.getAggregateOperator(yAxis.getAggr());
                    orderBy.add(orderByAggr.getSelectField(measure_field.clone()).getCompleteColumnName());
                } else{
                    orderBy.add(measure_field.clone().getCompleteColumnName());
                }
            }
            dataPointContext.setOrderBy(orderBy);
            if (measure.getSortOrder() > 0) {
                dataPointContext.setOrderByFunc(ReportDataPointContext.OrderByFunction.valueOf(measure.getSortOrder()));
            }
        }
        if (measure.getLimit() > 0) {
            dataPointContext.setLimit(measure.getLimit());
        }


        V2ModuleGroupByContext groupBy = v2_report.getGroupBy();
        if(groupBy != null && groupBy.getFieldName() != null)
        {
            List<ReportGroupByField> groupByFields = new ArrayList<>();
            ReportGroupByField groupByField = new ReportGroupByField();
            FacilioModule groupByModule = modBean.getModule(groupBy.getModuleName());
            FacilioField group_by_field = ReportUtil.getField(modBean, groupBy.getFieldName(), groupByModule);
            if(groupBy.getLookupFieldName() != null)
            {
                FacilioField lookupField = ReportUtil.getField(modBean, groupBy.getFieldName(), modBean.getModule(groupBy.getLookupModuleName()));
                if(lookupField != null){
                    groupByField.setLookupFieldId(lookupField.getId());
                }
            }
            groupByField.setField(groupByModule, group_by_field);
            groupByField.setAlias(group_by_field.getName());
            groupByField.setAggr(groupBy.getAggr() > -1 ? groupBy.getAggr() : groupByField.getAggr());
            groupByFields.add(groupByField);
            dataPointContext.setGroupByFields(groupByFields);
        }

        V2ModuleFilterContext filter = v2_report.getFilters();
        if(filter != null)
        {
            List<ReportHavingContext> data_filter_list = filter.getDataFilter();
            if (CollectionUtils.isNotEmpty(data_filter_list))
            {
                for (ReportHavingContext reportHavingContext : data_filter_list)
                {
                    FacilioField field = modBean.getField(reportHavingContext.getFieldName(), reportHavingContext.getModuleName());
                    if (field == null) {
                        field = ReportFactory.getReportField(reportHavingContext.getFieldName());
                    }
                    if (field == null) {
                        throw new IllegalArgumentException("Field cannot be empty in having");
                    }
                    reportHavingContext.setField(field);
                }
                dataPointContext.setHavingCriteria(data_filter_list);
            }
            List<ReportUserFilterContext> userFilters = filter.getUserFilters();
            if (CollectionUtils.isNotEmpty(userFilters)) {
                for (ReportUserFilterContext userFilterContext : userFilters) {
                    userFilterContext.setField(modBean.getField(userFilterContext.getFieldId()));
                }
                reportContext.setUserFilters(userFilters);
            }

            Criteria criteria  = filter.getGlobalCriteria();
            if(criteria != null){
                reportContext.setCriteria(criteria);
            }
        }

        Map<String, String> aliases = new HashMap<>();
        aliases.put("actual", measure_field.getDisplayName());
        dataPointContext.setAliases(aliases);
        dataPointContext.setName(xField.getName());

        String baseLines = v2_report.getBaseLines();
        if (baseLines != null && !baseLines.isEmpty()) {
            JSONParser parser = new JSONParser();
            List<ReportBaseLineContext> baseLineList = FieldUtil.getAsBeanListFromJsonArray((JSONArray) parser.parse(baseLines), ReportBaseLineContext.class);
            for (ReportBaseLineContext reportBaseLine : baseLineList) {
                BaseLineContext baseLineContext = BaseLineAPI.getBaseLine(reportBaseLine.getBaseLineId());
                reportBaseLine.setAdjustType(BaseLineContext.AdjustType.WEEK);
                reportBaseLine.setBaseLine(baseLineContext);
                aliases.put(baseLineContext.getName(), measure_field.getDisplayName() + '-' +baseLineContext.getName());
            }
            reportContext.setBaseLines(baseLineList);
        }
        reportContext.addDataPoint(dataPointContext);
    }
    private boolean isDateField(FacilioField field) {
        return field != null && (field.getDataType() == FieldType.DATE.getTypeAsInt() || field.getDataType() == FieldType.DATE_TIME.getTypeAsInt());
    }
}
