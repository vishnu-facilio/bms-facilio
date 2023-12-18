package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.AssetDepreciationAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetChartParamsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long recordId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
        Long widgetId=(Long) context.get("widgetId");
        PageSectionWidgetContext pageWidget = (PageSectionWidgetContext) context.get(FacilioConstants.CustomPage.PAGE_SECTION_WIDGET);
        PageWidget.WidgetType widgetType = (PageWidget.WidgetType) context.get("widgetType");
        if(recordId != null && recordId > 0) {
            FacilioUtil.throwIllegalArgumentException(widgetId<=-1 && pageWidget == null, "pageSectionWidget can't be null while fetching chartParams");
            ChartParamWidget chartParamWidget = new ChartParamWidget();
            if(pageWidget != null){
                chartParamWidget.setChartParams(getChartParams(recordId,pageWidget.getWidgetType()));
            }
            else{
                chartParamWidget.setChartParams(getChartParams(recordId,widgetType));
            }

            context.put(FacilioConstants.CustomPage.WIDGET_DETAIL, chartParamWidget);
        }

        return false;
    }

    private static Map<String, Object> getChartParams(long recordId , PageWidget.WidgetType widgetType) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Criteria criteria = new Criteria();
        switch (widgetType.getName()) {
            case "avgRepairTime":
                PageWidget cardWidget = new PageWidget(PageWidget.WidgetType.AVERAGE_REPAIR_TIME, "avgTtr");
                Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.ContextNames.ASSET_BREAKDOWN));
                criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("asset"), String.valueOf(recordId), NumberOperators.EQUALS));
                return addChartParams(cardWidget, "fromtime", "duration", criteria);
            case "failureRate":
                PageWidget failureWidget = new PageWidget(PageWidget.WidgetType.FAILURE_RATE, "failureRate");
                Map<String, FacilioField> failureFieldMap = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.ContextNames.ASSET_BREAKDOWN));
                criteria.addAndCondition(CriteriaAPI.getCondition(failureFieldMap.get("asset"), String.valueOf(recordId), NumberOperators.EQUALS));
                return addChartParams(failureWidget, "fromtime", "timeBetweenFailure", criteria);
            case "maintenanceCostTrend":
                PageWidget maintenanceCostWidget = new PageWidget(PageWidget.WidgetType.MAINTENANCE_COST_TREND, "maintenanceCostTrend");
                Map<String, FacilioField> woFieldMap = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER));
                criteria.addAndCondition(CriteriaAPI.getCondition(woFieldMap.get("resource"), String.valueOf(recordId), NumberOperators.EQUALS));
                return addChartParams(maintenanceCostWidget, "createdTime","Created Time", "totalCost","Total Cost","plannedvsunplanned", criteria);
            case "costBreakup":
                PageWidget costBreakupWidget = new PageWidget(PageWidget.WidgetType.COST_BREAKUP, "costBreakup");
                Map<String, FacilioField> woCostFieldMap = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.ContextNames.WORKORDER_COST));
                criteria.addAndCondition(CriteriaAPI.getCondition(woCostFieldMap.get("parentId"), String.valueOf(""), NumberOperators.EQUALS));
                return addChartParams(null,costBreakupWidget, "costType", "cost", criteria);
            case "depreciationCostTrend":
                PageWidget depreciationCostWidget = new PageWidget(PageWidget.WidgetType.DEPRECIATION_COST_TREND, "depreciationCostTrend");
                AssetContext asset= V3RecordAPI.getRecord("asset",recordId);
                return addDepreciationCostTrendWidget(depreciationCostWidget,asset,modBean);



        }

        return null;
    }

    private static Map<String, Object> addChartParams(String groupByFieldName,PageWidget widget, String xFieldName, String yFieldName , Criteria criteria) {
      return addChartParams(widget, "line", BmsAggregateOperators.DateAggregateOperator.MONTHANDYEAR, xFieldName,null, BmsAggregateOperators.NumberAggregateOperator.SUM, yFieldName,null, groupByFieldName , DateOperators.CURRENT_YEAR, null, criteria);
    }
    private static Map<String, Object> addChartParams(PageWidget widget, String xFieldName,String xDisplayName, String yFieldName,String yDisplayName,String groupByFieldName, Criteria criteria) {
       return addChartParams(widget, "line", BmsAggregateOperators.DateAggregateOperator.MONTHANDYEAR, xFieldName, xDisplayName, BmsAggregateOperators.NumberAggregateOperator.AVERAGE, yFieldName,yDisplayName, groupByFieldName , DateOperators.CURRENT_YEAR, null, criteria);
    }

    private static Map<String, Object> addChartParams(PageWidget widget, String xFieldName, String yFieldName, Criteria criteria) {
        return addChartParams(widget, xFieldName, yFieldName, null, criteria);
    }
    public static  Map<String, Object> addChartParams(PageWidget widget, String xFieldName, String yFieldName,String groupByFieldName, Criteria criteria) {
        return addChartParams(widget, "line", BmsAggregateOperators.DateAggregateOperator.MONTHANDYEAR, xFieldName, null, BmsAggregateOperators.NumberAggregateOperator.AVERAGE, yFieldName,null, groupByFieldName , DateOperators.CURRENT_YEAR, null, criteria);
    }

    private static Map<String, Object> addChartParams(String xFieldName, String xDisplayName, String yFieldName, String yDisplayName, String groupByFieldName, Criteria criteria) throws Exception {
        return addChartParams("line", BmsAggregateOperators.DateAggregateOperator.MONTHANDYEAR, xFieldName, xDisplayName, BmsAggregateOperators.NumberAggregateOperator.AVERAGE, yFieldName,yDisplayName, groupByFieldName , DateOperators.CURRENT_YEAR, null, criteria);
    }

    public static  Map<String, Object> addChartParams(PageWidget widget, String chartType, AggregateOperator xAggr, String xFieldName,String xDisplayName, AggregateOperator yAggr,
                                         String yFieldName,String yDisplayName,String groupByFieldName, DateOperators dateOperator, String dateOperatorValue, Criteria criteria) {
        Map<String, Object> obj = new HashMap<>();
        obj.put("chartType", chartType);

        Map<String, Object> xField = new HashMap<>();
        xField.put("aggr", xAggr.getValue());
        xField.put("fieldName", xFieldName);
        xField.put("displayName",xDisplayName);
        obj.put("xField", xField);

        Map<String, Object> yField = new HashMap<>();
        yField.put("aggr", yAggr.getValue());
        yField.put("fieldName", yFieldName);
        yField.put("displayName",yDisplayName);
        obj.put("yField", yField);

        Map<String, Object> groupBy = new HashMap<>();
        groupBy.put("fieldName", groupByFieldName);
        obj.put("groupBy", groupBy);

        obj.put("dateOperator", dateOperator.getOperatorId());
        obj.put("dateOperatorValue", dateOperatorValue);
        obj.put("criteria", criteria);

        return obj;
    }

    private static Map<String, Object> addChartParams(String chartType, AggregateOperator xAggr, String xFieldName, String xDisplayName, AggregateOperator yAggr,
                                         String yFieldName, String yDisplayName, String groupByFieldName, DateOperators dateOperator, String dateOperatorValue, Criteria criteria) throws Exception {
        Map<String, Object> obj = new HashMap<>();
        obj.put("chartType", chartType);

        Map<String, Object> xField = new HashMap<>();
        xField.put("aggr", xAggr.getValue());
        xField.put("fieldName", xFieldName);
        xField.put("displayName",xDisplayName);
        obj.put("xField", xField);

        Map<String, Object> yField = new HashMap<>();
        yField.put("aggr", yAggr.getValue());
        yField.put("fieldName", yFieldName);
        yField.put("displayName",yDisplayName);
        obj.put("yField", yField);

        Map<String, Object> groupBy = new HashMap<>();
        groupBy.put("fieldName", groupByFieldName);
        obj.put("groupBy", groupBy);

        obj.put("dateOperator", dateOperator.getOperatorId());
        obj.put("dateOperatorValue", dateOperatorValue);
        obj.put("criteria", criteria);

        return obj;
    }

    private static Map<String, Object> addChartParams(PageWidget widget, String chartType, AggregateOperator xAggr, String xFieldName, AggregateOperator yAggr,
                                         List<String> yFieldNameArray,String groupByFieldName, DateOperators dateOperator, List<Long> dateOperatorValue, Criteria criteria) {
        Map<String, Object> obj = new HashMap<>();
        obj.put("chartType", chartType);

        Map<String, Object> xField = new HashMap<>();
        xField.put("aggr", xAggr.getValue());
        xField.put("fieldName", xFieldName);
        obj.put("xField", xField);

        if( yFieldNameArray != null) {

            JSONArray yFields = new JSONArray();

            for(String yFieldName:yFieldNameArray) {
                if(yFieldName != null) {
                    Map<String, Object> yField = new HashMap<>();
                    yField.put("aggr", yAggr.getValue());
                    yField.put("fieldName", yFieldName);
                    yFields.add(yField);
                } else {
                    yFields.add(null);
                }
            }

            obj.put("yField", yFields);
            obj.put("isMultipleMetric",true);

        } else {
            obj.put("yField", null);
        }

        Map<String, Object> groupBy = new HashMap<>();
        groupBy.put("fieldName", groupByFieldName);
        obj.put("groupBy", groupBy);

        obj.put("dateOperator", dateOperator.getOperatorId());
        obj.put("dateOperatorValue", dateOperatorValue);
        obj.put("criteria", criteria);

        return obj;
    }

    private static Map<String, Object> addDepreciationCostTrendWidget(PageWidget widget, Criteria criteria, DateOperators dateoperator, List<Long> dateOperatorValue, BmsAggregateOperators.DateAggregateOperator xAggr) {

        List<String> yFields = new ArrayList<>();
        yFields.add("currentPrice");
        yFields.add("depreciatedAmount");
        return addChartParams(widget, "line", xAggr, "calculatedDate", BmsAggregateOperators.NumberAggregateOperator.SUM, yFields, null , dateoperator, dateOperatorValue, criteria);

    }

    private static Map<String, Object> addDepreciationCostTrendWidget(PageWidget widget,AssetContext asset, ModuleBean modBean) throws Exception {
        if (AssetDepreciationAPI.getDepreciationOfAsset(asset.getId()) != null) {
            AssetDepreciationContext assetDepreciation = AssetDepreciationAPI.getDepreciationOfAsset(asset.getId());
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.ContextNames.ASSET_DEPRECIATION_CALCULATION));
            Criteria depreciationCostCriteria = new Criteria();
            depreciationCostCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("asset"), String.valueOf(asset.getId()), NumberOperators.EQUALS));
            FacilioModule assetModule = modBean.getModule(FacilioConstants.ContextNames.ASSET);
            Long startDate = (long) FieldUtil.getValue(asset, assetDepreciation.getStartDateFieldId(), assetModule);
            if (startDate == null || startDate == -1) {
                throw new IllegalArgumentException("Start date cannot be empty");
            } else {
                startDate = DateTimeUtil.getMonthStartTimeOf(startDate);
                ZonedDateTime endDate = DateTimeUtil.getDateTime(startDate);
                List<Long> DateValue = new ArrayList<>();
                DateValue.add(startDate);
                switch(assetDepreciation.getFrequencyTypeEnum()) {
                    case MONTHLY:
                        endDate = DateTimeUtil.getMonthEndTimeOf(endDate.plus(1*assetDepreciation.getFrequency(), ChronoUnit.MONTHS));
                        DateValue.add(endDate.toInstant().toEpochMilli());
                        return addDepreciationCostTrendWidget(widget, depreciationCostCriteria,DateOperators.CURRENT_N_MONTH,DateValue, BmsAggregateOperators.DateAggregateOperator.MONTHANDYEAR);
                    case QUARTERLY:
                        endDate = DateTimeUtil.getMonthEndTimeOf(endDate.plus(3*assetDepreciation.getFrequency(), ChronoUnit.MONTHS));
                        DateValue.add(endDate.toInstant().toEpochMilli());
                        return addDepreciationCostTrendWidget(widget, depreciationCostCriteria,DateOperators.CURRENT_N_QUARTER,DateValue, BmsAggregateOperators.DateAggregateOperator.QUARTERLY);

                    case HALF_YEARLY:
                        endDate = DateTimeUtil.getMonthEndTimeOf(endDate.plus(6*assetDepreciation.getFrequency(), ChronoUnit.MONTHS));
                        DateValue.add(endDate.toInstant().toEpochMilli());
                        return addDepreciationCostTrendWidget(widget, depreciationCostCriteria,DateOperators.CURRENT_N_QUARTER,DateValue, BmsAggregateOperators.DateAggregateOperator.QUARTERLY);

                    case YEARLY:
                        endDate = DateTimeUtil.getMonthEndTimeOf(endDate.plus(1*assetDepreciation.getFrequency(), ChronoUnit.YEARS));
                        DateValue.add(endDate.toInstant().toEpochMilli());
                        return addDepreciationCostTrendWidget(widget, depreciationCostCriteria,DateOperators.CURRENT_N_YEAR,DateValue, BmsAggregateOperators.DateAggregateOperator.YEAR);

                    default:
                        endDate = DateTimeUtil.getMonthEndTimeOf(endDate.plus(1*assetDepreciation.getFrequency(), ChronoUnit.MONTHS));
                        DateValue.add(endDate.toInstant().toEpochMilli());
                        return addDepreciationCostTrendWidget(widget, depreciationCostCriteria,DateOperators.CURRENT_N_MONTH,DateValue, BmsAggregateOperators.DateAggregateOperator.MONTHANDYEAR);

                }
            }
        }
        return null;

    }

}
