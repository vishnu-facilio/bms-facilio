package com.facilio.bmsconsole.commands;

import java.util.*;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.BaseLineAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.modules.BaseLineContext;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.LookupField;
import com.facilio.report.context.*;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.json.annotations.JSON;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.AggregateOperator;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.BmsAggregateOperators.CommonAggregateOperator;
import com.facilio.modules.fields.FacilioField;
import com.facilio.report.context.ReportContext.ReportType;
import com.facilio.report.context.ReportDataPointContext.OrderByFunction;
import com.facilio.report.util.ReportUtil;
import com.facilio.time.DateRange;
import org.json.simple.parser.JSONParser;

;

public class ConstructTabularReportData extends FacilioCommand {
    private FacilioModule module;
    private LinkedHashMap<String, String> tableAlias = new LinkedHashMap<String, String>();
    private Context globalContext;
    private int maxLimit = 2000;

    private List<FacilioModule.ModuleType> READING_MODULE_TYPES = new ArrayList<>(Arrays.asList(FacilioModule.ModuleType.READING, FacilioModule.ModuleType.READING_RULE, FacilioModule.ModuleType.SYSTEM_SCHEDULED_FORMULA, FacilioModule.ModuleType.LIVE_FORMULA));
//    private Boolean isDataColumnSort=false;

    @Override
    public boolean executeCommand(Context context) throws Exception {

        ReportContext reportContext = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
        List<PivotRowColumnContext> rows = (List<PivotRowColumnContext>) context.get(FacilioConstants.Reports.ROWS);
        List<PivotDataColumnContext> data = (List<PivotDataColumnContext>) context.get(FacilioConstants.Reports.DATA);
        Criteria basecriteria = (Criteria) context.get(FacilioConstants.ContextNames.CRITERIA);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        JSONObject sortBy = (JSONObject) context.get(FacilioConstants.ContextNames.SORTING);
        long dateFieldId = (long) context.get(FacilioConstants.ContextNames.DATE_FIELD);
        long startTime = (long) context.get(FacilioConstants.ContextNames.START_TIME);
        long endTime = (long) context.get(FacilioConstants.ContextNames.END_TIME);
        globalContext = context;
        if (reportContext == null) {
            reportContext = new ReportContext();
            reportContext.setType(ReportType.PIVOT_REPORT);
        } else if (reportContext.getDataPoints() != null) {
            reportContext.getDataPoints().clear();
        }

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        module = null;
        if (StringUtils.isNotEmpty(moduleName)) {
            module = modBean.getModule(moduleName);
            reportContext.setModuleId(module.getModuleId());
            reportContext.setModule(module);
        }

        if (module == null) {
            throw new Exception("Module name should not be empty");
        }
        context.put("ModuleDisplayName", module.getDisplayName());
        context.put(FacilioConstants.ContextNames.MODULE, module);

        if (basecriteria != null) {
            reportContext.setCriteria(basecriteria);
        }

        List<String> rowHeaders = new ArrayList<>();
        List<String> dataHeaders = new ArrayList<>();
        Map<String, Object> rowAlias = new HashMap<>();
        Map<String, Object> dataAlias = new HashMap<>();
        if (reportContext.getDataPoints() == null || reportContext.getDataPoints().size() == 0) {
            for (int i = 0; i < data.size(); i++) {
                PivotDataColumnContext yData = data.get(i);
                addDataPointContext(modBean, reportContext, rows, yData, module, sortBy, dateFieldId, startTime,
                        endTime);
                dataHeaders.add(yData.getAlias());
                if (yData.getField() != null || yData.getReadingField() != null) {
                    Map<String, Object> dataDetails = new HashMap<>();
                    FacilioField yField = null;
                    if (yData.getReadingField() != null) {
                        if (yData.getReadingField().getId() > 0) {
                            yField = modBean.getField(yData.getReadingField().getId());
                        }
                    } else if (yData.getField() != null) {
                        if (yData.getField().getId() != -1) {
                            yField = modBean.getField(yData.getField().getId(), yData.getField().getModuleId());
                        } else {
                            FacilioModule yAxisModule = modBean.getModule(yData.getField().getModuleId());
                            yField = modBean.getField(yData.getField().getName(), yAxisModule.getName());
                        }
                    }
                    dataDetails.put("displayName", yField != null ? yField.getDisplayName() : "");
                    dataDetails.put(FacilioConstants.ContextNames.FIELD, yField);
                    dataDetails.put(FacilioConstants.ContextNames.FORMATTING, yData.getFormatting());
                    dataAlias.put(yData.getAlias(), dataDetails);
                }
            }

            if (rows != null && rows.size() > 0) {
                for (int i = 0; i < rows.size(); i++) {
                    PivotRowColumnContext groupByRow = rows.get(i);

                    ReportPivotFieldContext groupByRowField = groupByRow.getField();
                    FacilioModule groupByModule = null;
                    FacilioField gField = null;
                    if (groupByRowField != null) {
                        if (groupByRowField.getModuleId() != -1) {
                            groupByModule = modBean.getModule(groupByRowField.getModuleId());
                        }
                        if (groupByRowField.getId() != -1) {
                            gField = modBean.getField(groupByRowField.getId(), groupByRowField.getModuleId());
                        } else {
                            gField = modBean.getField(groupByRowField.getName(), groupByModule.getName());
                        }
                    }
                    rowHeaders.add(groupByRow.getAlias());
                    Map<String, Object> rowDetails = new HashMap<>();
                    rowDetails.put("displayName", gField.getDisplayName());
                    rowDetails.put(FacilioConstants.ContextNames.FIELD, gField);
                    rowDetails.put(FacilioConstants.ContextNames.FORMATTING, groupByRow.getFormatting());
                    rowAlias.put(groupByRow.getAlias(), rowDetails);
                }
            }
        }
        context.put(FacilioConstants.ContextNames.ROW_HEADERS, rowHeaders);
        context.put(FacilioConstants.ContextNames.DATA_HEADERS, dataHeaders);
        context.put(FacilioConstants.ContextNames.ROW_ALIAS, rowAlias);
        context.put(FacilioConstants.ContextNames.DATA_ALIAS, dataAlias);
        context.put(FacilioConstants.ContextNames.REPORT, reportContext);
        context.put(FacilioConstants.ContextNames.TABLE_ALIAS, tableAlias);
        return false;
    }

    private void addDataPointContext(ModuleBean modBean, ReportContext reportContext, List<PivotRowColumnContext> rows,
            PivotDataColumnContext data, FacilioModule module, JSONObject sortBy, long dateFieldId, long startTime,
            long endTime) throws Exception {
        ReportDataPointContext dataPointContext = new ReportDataPointContext();

        ReportFieldContext xAxis = new ReportFieldContext();
        PivotRowColumnContext firstRow = rows.get(0);

        FacilioField xField = null;
        FacilioModule xAxisModule = null;

        if (firstRow != null && firstRow.getField() != null) {
            ReportPivotFieldContext rowField = firstRow.getField();
            if (rowField.getModuleId() != -1) {
                xAxisModule = modBean.getModule(rowField.getModuleId());
            }
            if (rowField.getId() != -1) {
                xField = modBean.getField(rowField.getId(), rowField.getModuleId()).clone();
            } else if (xAxisModule != null) {
                xField = modBean.getField(rowField.getName(), xAxisModule.getName()).clone();
            } else {
                xField = modBean.getField(rowField.getName(), module.getName()).clone();
            }
        }

        if (firstRow.getLookupFieldId() != -1) {
            xAxis.setLookupFieldId(firstRow.getLookupFieldId());
        }

        if (firstRow.getSubModuleFieldId() != -1) {
            xAxis.setSubModuleFieldId(firstRow.getSubModuleFieldId());
        }

        if (xField == null) {
            throw new Exception("atleast one row mandatory");
        }
        if (firstRow.getLookupFieldId() > 0) {
            String fieldName;
            LookupField field = (LookupField) modBean.getField(firstRow.getLookupFieldId()).clone();
            fieldName = xAxisModule.getName() + "_" + xField.getName();
            // xField.setName(fieldName);
            xField.setTableAlias(getAndSetTableAlias(fieldName));
        } else {
            xField.setTableAlias(getAndSetTableAlias(xField.getModule().getName()));
        }
        xAxis.setField(xAxisModule, xField);
        if (firstRow.getAlias() != null) {
            xAxis.setAlias(firstRow.getAlias());
            if (firstRow.getAlias().equals(sortBy.get("alias"))) {
                dataPointContext.setOrderByFunc(OrderByFunction.valueOf(((Number) sortBy.get("order")).intValue()));
                List<String> orderBy = new ArrayList<>();
                orderBy.add(xField.getCompleteColumnName());
                dataPointContext.setOrderBy(orderBy);
                dataPointContext.setLimit(((Number) maxLimit).intValue());
            }else if(sortBy.get("limit") != null){
                dataPointContext.setLimit(((Number) maxLimit).intValue());
            }
        }
        if (firstRow.getModuleName() != null) {
            xAxis.setModule(modBean.getModule(firstRow.getModuleName()));
        }
        dataPointContext.setxAxis(xAxis);
        reportContext.setxAggr(firstRow.getSelectedTimeAggr() > 0 ? firstRow.getSelectedTimeAggr() : 0);
        reportContext.setxAlias(firstRow.getAlias());

        ReportYAxisContext yAxis = new ReportYAxisContext();
        FacilioField yField = null;

        AggregateOperator yAggr = CommonAggregateOperator.COUNT;
        FacilioModule yAxisModule = null;
        if (data != null) {
            if (data.getAggrEnum() != null) {
                yAggr = data.getAggrEnum();
            }
            if (data.getReadingField() != null) {
                if (data.getReadingField().getId() > 0) {
                    yField = modBean.getField(data.getReadingField().getId()).clone();
                } else if(data.getReadingField().getModuleId() > 0 && data.getReadingField().getName() != null){
                    FacilioModule facilioModule = modBean.getModule(data.getReadingField().getModuleId());
                    yField = modBean.getField(data.getReadingField().getName(),facilioModule.getName()).clone();
                }
                yAxisModule = modBean.getModule(data.getReadingField().getModuleId());
            } else if (data.getField() != null) {
                if (data.getField().getModuleId() > 0) {
                    yAxisModule = modBean.getModule(data.getField().getModuleId());
                }
                if (data.getField().getId() > 0) {
                    yField = modBean.getField(data.getField().getId(), data.getField().getModuleId()).clone();
                } else if (yAxisModule != null) {
                    yField = modBean.getField(data.getField().getName(), yAxisModule.getName()).clone();
                }
            }
            yField.setTableAlias(getAndSetTableAlias(yField.getModule().getName()));
            if (data.getAlias() != null) {
                yAxis.setAlias(data.getAlias());
                if (data.getAlias().equals(sortBy.get("alias"))) {

                    dataPointContext.setOrderByFunc(OrderByFunction.valueOf(((Number) sortBy.get("order")).intValue()));
                    List<String> orderBy = new ArrayList<>();
                    orderBy.add(getAggrCompleteColumnName(yField.getCompleteColumnName(), yAggr));
                    orderBy.add(xField.getCompleteColumnName());
                    dataPointContext.setOrderBy(orderBy);
                    dataPointContext.setLimit(((Number) maxLimit).intValue());
                    dataPointContext.setDefaultSortPoint(true);
                }
                else if(sortBy.get("limit") != null){
                    dataPointContext.setLimit(((Number) maxLimit).intValue());
                }
            }
            Criteria criteria = data.getCriteria();
            if (criteria != null) {
                dataPointContext.setCriteria(criteria);
            }
            boolean isTimeLineFilterApplied = true;
            boolean showTimelineFilter = false;
            int dateOperatorInt = -1;
            if(globalContext.containsKey(FacilioConstants.ContextNames.DATE_OPERATOR) && globalContext.get(FacilioConstants.ContextNames.DATE_OPERATOR) != null)
                dateOperatorInt = (int) globalContext.get(FacilioConstants.ContextNames.DATE_OPERATOR);
            if (globalContext.containsKey(FacilioConstants.ContextNames.IS_TIMELINE_FILTER_APPLIED))
                isTimeLineFilterApplied = (boolean) globalContext.get(FacilioConstants.ContextNames.IS_TIMELINE_FILTER_APPLIED);
            if(globalContext.containsKey(FacilioConstants.ContextNames.SHOW_TIME_LINE_FILTER) && globalContext.get(FacilioConstants.ContextNames.SHOW_TIME_LINE_FILTER) != null)
                showTimelineFilter = (boolean) globalContext.get(FacilioConstants.ContextNames.SHOW_TIME_LINE_FILTER);

            if (reportContext.getTypeEnum() == ReportType.PIVOT_REPORT) {
                if(data.getBaselineLabel() != null && ((data.getDateFieldId() > 0 && data.getDatePeriod() > 0) || (data.getReadingField() != null && data.getModuleType() != null && data.getModuleType().equals("2")))){
                    FacilioField dateField = null;
                    if(yField.getModule().getTypeEnum() != null && READING_MODULE_TYPES.contains(yField.getModule().getTypeEnum()))
                        dateField = FieldFactory.getDateField("ttime", "TTIME", yField.getModule()).clone();
                    else
                        dateField = modBean.getField(data.getDateFieldId(), yAxisModule.getName()).clone();
                    dateField.setTableAlias(getAndSetTableAlias(dateField.getModule().getName()));
                    BaseLineContext baseline = BaseLineAPI.getBaseLine(data.getBaselineLabel());
                    DateOperators dateOperator = (DateOperators) Operator.getOperator(data.getDatePeriod());
                    DateRange actualRange = null;
                    if(dateFieldId > 0 && startTime > -1 && endTime > -1 && !data.isExcludeFromTimelineFilter())
                        actualRange = new DateRange(startTime, endTime);
                    else if(data.getStartTime() > -1 && data.getEndTime() > -1 && data.getDatePeriod() == 20)
                        actualRange = new DateRange(data.getStartTime(), data.getEndTime());
                    else
                        actualRange = dateOperator.getRange(null);
                    DateRange baselineDateRange = baseline.calculateBaseLineRange(actualRange, baseline.getAdjustTypeEnum());
                    Criteria otherCrit = new Criteria();
                    Condition newCond = CriteriaAPI.getCondition(dateField, baselineDateRange.toString(), DateOperators.BETWEEN);
                    otherCrit.addAndCondition(newCond);
                    dataPointContext.setOtherCriteria(otherCrit);
                } else if (dateFieldId > 0 && startTime > 0 && endTime > 0 && isTimeLineFilterApplied && !data.isExcludeFromTimelineFilter() && data.getReadingField() == null) {
                    FacilioField dateField = modBean.getField(dateFieldId).clone();
                    dateField.setTableAlias(getAndSetTableAlias(dateField.getModule().getName()));
                    DateRange range = new DateRange(startTime, endTime);
                    Criteria otherCrit = new Criteria();
                    Condition newCond = CriteriaAPI.getCondition(dateField, range.toString(), DateOperators.BETWEEN);
                    otherCrit.addAndCondition(newCond);
                    dataPointContext.setOtherCriteria(otherCrit);
                }  else if (dateFieldId > 0 && dateOperatorInt > 0 && showTimelineFilter && !data.isExcludeFromTimelineFilter() && data.getReadingField() == null) {
                    FacilioField dateField = null;
                    try
                    {
                        dateField = modBean.getField(dateFieldId, yAxisModule.getName()).clone();
                    }
                    catch (Exception e)
                    {
                        if(module != null) {
                            dateField = modBean.getField(dateFieldId, module.getName()).clone();
                        }else{
                            throw e;
                        }
                    }
                    dateField.setTableAlias(getAndSetTableAlias(dateField.getModule().getName()));
                    Operator dateOperator = Operator.getOperator(dateOperatorInt);
                    Criteria otherCrit = new Criteria();
                    if(dateOperatorInt == DateOperators.CURRENT_N_YEAR.getOperatorId() || dateOperatorInt == DateOperators.CURRENT_N_DAY.getOperatorId() ||
                            dateOperatorInt == DateOperators.CURRENT_N_MONTH.getOperatorId() || dateOperatorInt == DateOperators.CURRENT_N_WEEK.getOperatorId())
                    {
                        String tabular_state = reportContext.getTabularState();
                        if(tabular_state != null){
                            JSONParser parser = new JSONParser();
                            JSONObject tabular_state_json = (JSONObject) parser.parse(tabular_state);
                            if(tabular_state_json != null && tabular_state_json.containsKey("dateValue"))
                            {
                                String dateValue = (String) tabular_state_json.get("dateValue");
                                if (dateValue != null && !dateValue.equals("") && !dateValue.equals("-1")) {
                                    String []date_value = dateValue.split(",");
                                    if(date_value.length>0 && date_value[0] != null && !date_value[0].equals("")) {
                                        Condition newCond = CriteriaAPI.getCondition(dateField, date_value[0], dateOperator);
                                        otherCrit.addAndCondition(newCond);
                                    }
                                }
                            }
                        }
                    }
                    else if( dateOperatorInt == DateOperators.LAST_N_DAYS.getOperatorId() ||
                            dateOperatorInt == DateOperators.LAST_N_MONTHS.getOperatorId() || dateOperatorInt == DateOperators.LAST_N_WEEKS.getOperatorId())
                    {
                        String tabular_state = reportContext.getTabularState();
                        if(tabular_state != null)
                        {
                            JSONParser parser = new JSONParser();
                            JSONObject tabular_state_json = (JSONObject) parser.parse(tabular_state);
                            if(tabular_state_json != null && tabular_state_json.containsKey("dateOffset") && tabular_state_json.get("dateOffset") != null)
                            {
                                String dateOffset = tabular_state_json.get("dateOffset").toString();
                                if (dateOffset != null) {
                                    DateRange dateRange = ((DateOperators)dateOperator).getRange(dateOffset);
                                    if(dateRange != null){
                                        Condition newCond = CriteriaAPI.getCondition(dateField, dateRange.toString(), DateOperators.BETWEEN);
                                        otherCrit.addAndCondition(newCond);
                                    }
                                }
                            }
                        }
                    }
                    else
                    {
                        Condition newCond = CriteriaAPI.getCondition(dateField, dateOperator);
                        otherCrit.addAndCondition(newCond);
                    }
                    dataPointContext.setOtherCriteria(otherCrit);
                }
                else if(data.getStartTime() > -1 && data.getEndTime() > -1 && data.getDatePeriod() == 20 && data.getReadingField() == null){
                    FacilioField dateField = modBean.getField(data.getDateFieldId(), yAxisModule.getName()).clone();
                    dateField.setTableAlias(getAndSetTableAlias(dateField.getModule().getName()));
                    DateRange range = new DateRange(data.getStartTime(), data.getEndTime());
                    Criteria otherCrit = new Criteria();
                    Condition newCond = CriteriaAPI.getCondition(dateField, range.toString(), DateOperators.BETWEEN);
                    otherCrit.addAndCondition(newCond);
                    dataPointContext.setOtherCriteria(otherCrit);
                } else if (data.getDateFieldId() > 0 && data.getDatePeriod() > 0 && data.getReadingField() == null) {
                    FacilioField dateField = modBean.getField(data.getDateFieldId(), yAxisModule.getName()).clone();
                    dateField.setTableAlias(getAndSetTableAlias(dateField.getModule().getName()));
                    Operator dateOperator = Operator.getOperator(data.getDatePeriod());
                    Criteria otherCrit = new Criteria();
                    Condition newCond = CriteriaAPI.getCondition(dateField, dateOperator);
                    otherCrit.addAndCondition(newCond);
                    dataPointContext.setOtherCriteria(otherCrit);
                } else if ((startTime > 0 && endTime > 0
                         && dateFieldId > 0 && !data.isExcludeFromTimelineFilter())
                        && READING_MODULE_TYPES.contains(yField.getModule().getTypeEnum())) {
                    FacilioField dateField = FieldFactory
                            .getDateField("ttime", "TTIME", yField.getModule()).clone();
                    dateField.setTableAlias(getAndSetTableAlias(dateField.getModule().getName()));
                    DateRange range = new DateRange(startTime, endTime);
                    Criteria otherCrit = new Criteria();
                    Condition newCond = CriteriaAPI.getCondition(dateField, range.toString(), DateOperators.BETWEEN);
                    otherCrit.addAndCondition(newCond);
                    dataPointContext.setOtherCriteria(otherCrit);
                }
                else if(data.isExcludeFromTimelineFilter() && data.getStartTime() > -1 && data.getEndTime() > -1 && data.getDatePeriod() == 20 && data.getReadingField() != null && READING_MODULE_TYPES.contains(yField.getModule().getTypeEnum())){
                    FacilioField dateField = FieldFactory
                            .getDateField("ttime", "TTIME", yField.getModule()).clone();
                    dateField.setTableAlias(getAndSetTableAlias(dateField.getModule().getName()));
                    DateRange range = new DateRange(data.getStartTime(), data.getEndTime());
                    Criteria otherCrit = new Criteria();
                    Condition newCond = CriteriaAPI.getCondition(dateField, range.toString(), DateOperators.BETWEEN);
                    otherCrit.addAndCondition(newCond);
                    dataPointContext.setOtherCriteria(otherCrit);
                }
                else if (data.getDatePeriod() > 0
                        && READING_MODULE_TYPES.contains(yField.getModule().getTypeEnum())) {
                    FacilioField dateField = FieldFactory
                            .getDateField("ttime", "TTIME", yField.getModule()).clone();
                    dateField.setTableAlias(getAndSetTableAlias(dateField.getModule().getName()));
                    Operator dateOperator = Operator.getOperator(data.getDatePeriod());
                    Criteria otherCrit = new Criteria();
                    Condition newCond = CriteriaAPI.getCondition(dateField, dateOperator);
                    otherCrit.addAndCondition(newCond);
                    dataPointContext.setOtherCriteria(otherCrit);
                }
            } else {
                if (dateFieldId > 0 && startTime > 0 && endTime > 0) {
                    FacilioField dateField = modBean.getField(dateFieldId);
                    DateRange range = new DateRange(startTime, endTime);
                    Criteria otherCrit = new Criteria();
                    Condition newCond = CriteriaAPI.getCondition(dateField, range.toString(), DateOperators.BETWEEN);
                    otherCrit.addAndCondition(newCond);
                    dataPointContext.setOtherCriteria(otherCrit);
                } else if (data.getDateFieldId() > 0 && data.getDatePeriod() > 0) {
                    FacilioField dateField = modBean.getField(data.getDateFieldId(), yAxisModule.getName()).clone();
                    dateField.setTableAlias(getAndSetTableAlias(dateField.getModule().getName()));
                    Operator dateOperator = Operator.getOperator(data.getDatePeriod());
                    Criteria otherCrit = new Criteria();
                    Condition newCond = CriteriaAPI.getCondition(dateField, dateOperator);
                    otherCrit.addAndCondition(newCond);
                    dataPointContext.setOtherCriteria(otherCrit);
                }
            }
            yAxis.setField(yAxisModule, yField);
            yAxis.setAggr(yAggr);
            if (data.getModuleName() != null) {
                yAxis.setModule(modBean.getModule(data.getModuleName()));
            }
            if (data.getSubModuleFieldId() > 0) {
                yAxis.setSubModuleFieldId(data.getSubModuleFieldId());
            }
            dataPointContext.setyAxis(yAxis);
            // dataPointContext.setModuleName(FacilioConstants.ContextNames.RESOURCE);
        }

        if (rows != null && rows.size() > 1) {
            List<ReportGroupByField> groupByFields = new ArrayList<>();
            for (int i = 1; i < rows.size(); i++) {
                PivotRowColumnContext groupByRow = rows.get(i);
                ReportGroupByField groupByField = new ReportGroupByField();

                ReportPivotFieldContext groupByRowField = groupByRow.getField();
                FacilioModule groupByModule = null;
                FacilioField gField = null;
                if (groupByRowField != null) {
                    if (groupByRowField.getModuleId() > 0) {
                        groupByModule = modBean.getModule(groupByRowField.getModuleId());
                    }
                    if (groupByRowField.getId() > 0) {
                        gField = modBean.getField(groupByRowField.getId(), groupByRowField.getModuleId()).clone();
                    } else if (groupByModule != null) {
                        gField = modBean.getField(groupByRowField.getName(), groupByModule.getName()).clone();
                    } else {
                        gField = modBean.getField(groupByRowField.getName(), module.getName()).clone();
                    }
                }

                if (groupByRow.getLookupFieldId() > 0) {
                    groupByField.setLookupFieldId(groupByRow.getLookupFieldId());
                }

                if (groupByRow.getSubModuleFieldId() > 0) {
                    groupByField.setSubModuleFieldId(groupByRow.getSubModuleFieldId());
                }

                if (groupByRow.getAlias() != null) {
                    groupByField.setAlias(groupByRow.getAlias());
                    if (groupByRow.getLookupFieldId() > 0) {
                        String fieldName;
                        LookupField field = (LookupField) modBean.getField(groupByRow.getLookupFieldId()).clone();
                        if (gField.getModule() != null) {
                            groupByModule = gField.getModule();
                        }

                        if (gField.equals(xField)) {
                            fieldName = groupByRow.getModuleName() + "_" + gField.getName();
                        } else {
                            if((AccountUtil.getCurrentOrg().getId() == 965)){
                                fieldName = groupByModule.getName() + "_" + field.getName() + "_"
                                        + groupByRow.getModuleName() + "_" + gField.getName();
                            }else {
                                fieldName = field.getLookupModule().getName() + "_" + field.getName() + "_"
                                        + groupByRow.getModuleName() + "_" + gField.getName();
                            }
                        }

                        // gField.setName(fieldName);
                        gField.setTableAlias(getAndSetTableAlias(fieldName));
                    } else {
                        gField.setTableAlias(getAndSetTableAlias(gField.getModule().getName()));
                    }
                    if (groupByField.getAlias().equals(sortBy.get("alias"))) {
                        dataPointContext
                                .setOrderByFunc(OrderByFunction.valueOf(((Number) sortBy.get("order")).intValue()));
                        List<String> orderBy = new ArrayList<>();
                        orderBy.add(gField.getCompleteColumnName());
                        dataPointContext.setOrderBy(orderBy);
                        dataPointContext.setLimit(((Number) maxLimit ).intValue());
                    }
                }
                groupByField.setField(groupByModule, gField);
                groupByField.setAlias(groupByRow.getAlias());
                if (groupByRow.getModuleName() != null
                        && !groupByRow.getModuleName().equalsIgnoreCase(module.getName())) {
                    groupByField.setModule(modBean.getModule(groupByRow.getModuleName()));
                }
                if (groupByRowField != null) {
                    groupByField.setAggr(groupByRowField.getAggr());
                }
                if(groupByRowField != null && groupByRow.getSelectedTimeAggr() > 0 && groupByField.getAggr() <= 0){
                    groupByField.setAggr(groupByRow.getSelectedTimeAggr());
                }
                groupByFields.add(groupByField);
            }
            dataPointContext.setGroupByFields(groupByFields);
        }

        Map<String, String> aliases = new HashMap<>();
        aliases.put("actual", data.getAlias());
        dataPointContext.setAliases(aliases);
        dataPointContext.setName(xField.getName());
        reportContext.addDataPoint(dataPointContext);
    }

    private static String getAggrCompleteColumnName(String name, AggregateOperator aggr) {
        return aggr == null || aggr == CommonAggregateOperator.ACTUAL ? name : aggr.getStringValue() + "(" + name + ")";
    }

    private String getAndSetTableAlias(String moduleName) {
        String alias = "";
        if (tableAlias.containsKey(moduleName)) {
            return tableAlias.get(moduleName);
        }

        if (tableAlias.values().size() > 0) {
            alias = ReportUtil.getAlias((String) tableAlias.values().toArray()[tableAlias.values().size() - 1]);
        } else {
            alias = ReportUtil.getAlias("");
        }

        tableAlias.put(moduleName, alias);

        return alias;
    }
}
