package com.facilio.analytics.v2.command;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.analytics.v2.V2AnalyticsOldUtil;
import com.facilio.analytics.v2.chain.V2AnalyticsTransactionChain;
import com.facilio.analytics.v2.context.*;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.BaseLineAPI;
import com.facilio.bmsconsoleV3.context.report.ReportDynamicKpiContext;
import com.facilio.ch.ClickhouseUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.*;
import com.facilio.report.context.ReportDataPointContext;
import com.facilio.readingkpi.ReadingKpiAPI;
import com.facilio.readingkpi.context.ReadingKPIContext;
import com.facilio.report.formatter.DecimalFormatter;
import com.facilio.report.module.v2.chain.V2TransactionChainFactory;
import com.facilio.report.module.v2.context.V2ModuleContextForDashboardFilter;
import com.facilio.report.module.v2.context.V2ModuleMeasureContext;
import com.facilio.report.module.v2.context.V2ModuleReportContext;
import com.facilio.report.module.v2.context.V2ModuleTimeFilterContext;
import com.facilio.service.FacilioService;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;
import com.facilio.unitconversion.Unit;
import com.facilio.unitconversion.UnitsUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.*;
import java.util.stream.Collectors;

public class V2ConstructCardCommand extends FacilioCommand {
    private static final Logger LOGGER = Logger.getLogger(V2ConstructCardCommand.class.getName());
    private boolean clickhouse=false;
    @Override
    public boolean executeCommand(Context context) throws Exception
    {
        clickhouse = context.get("clickhouse") != null ? (Boolean) context.get("clickhouse") : false;
        V2CardContext cardContext = (V2CardContext) context.get("cardContext");
        V2AnalyticsContextForDashboardFilter db_filter = (V2AnalyticsContextForDashboardFilter) context.get("db_filter");
        V2ModuleContextForDashboardFilter db_user_filter = (V2ModuleContextForDashboardFilter) context.get("db_user_filter");
        if(cardContext != null)
        {
            V2AnalyticsCardWidgetContext cardParams = cardContext.getCardParams();
            Boolean isModuleKpi = cardParams!=null ? cardParams.getIsModuleKpi() : false;
            if(cardParams.getDynamicKpiId() != null && cardParams.getDynamicKpiId() > 0){
                this.getDynamicKpiValue(cardParams, db_filter);
            }
            else if(isModuleKpi){
                this.getModuleCardValue(cardParams,db_user_filter);
            }
            else{
                this.getReadingCardValue(cardContext.getCardParams(),db_filter);
            }
        }
        return false;
    }
    private void getReadingCardValue(V2AnalyticsCardWidgetContext cardContext, V2AnalyticsContextForDashboardFilter db_filter)throws Exception
    {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Long fieldId = cardContext.getFieldId();
        ReadingKPIContext readingKPI=null;
        if(cardContext != null && fieldId > 0)
        {
            FacilioField field = modBean.getField(fieldId);
            FacilioModule baseModule = field.getModule();
            if(baseModule.getTypeEnum() == FacilioModule.ModuleType.LIVE_FORMULA){
                Map<Long, Long> field_vs_kpi = ReadingKpiAPI.getReadingFieldIdVsKpiId(new ArrayList<>());
                if(field_vs_kpi != null){
                    Long kpiId = field_vs_kpi.get(field.getFieldId());
                    if(kpiId != null) {
                        readingKPI = ReadingKpiAPI.getReadingKpi(kpiId);
                    }
                }
            }
            FacilioField parentModuleIdField = FieldFactory.getIdField(modBean.getModule(cardContext.getParentModuleName()));
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(baseModule.getName()));

            FacilioModule parentModuleForCriteria = modBean.getModule(cardContext.getParentModuleName());
            if(field != null)
            {
                V2TimeFilterContext timeFilter = cardContext.getTimeFilter();
                if(timeFilter != null)
                {
                    DateRange range = null;
                    long startTime = timeFilter.getStartTime();
                    long endTime = timeFilter.getEndTime();
                    if(db_filter != null && db_filter.getTimeFilter() != null) {
                        V2TimeFilterContext dbTimeFilter = db_filter.getTimeFilter();
                        range =  new DateRange(dbTimeFilter.getStartTime(), dbTimeFilter.getEndTime());
                        timeFilter.setDateLabel(dbTimeFilter.getDateLabel());
                        timeFilter.setDateOperator(dbTimeFilter.getDateOperator());
                        timeFilter.setDateOperatorEnum(dbTimeFilter.getDateOperatorEnum());
                    }
                    else if(timeFilter.getDateOperator() > 0){
                        range = timeFilter.getOffset() != null &&  timeFilter.getOffset() > 0 ? ((DateOperators) Operator.getOperator(timeFilter.getDateOperator())).getRange(timeFilter.getOffset().toString()) : ((DateOperators) Operator.getOperator(timeFilter.getDateOperator())).getRange(String.valueOf(timeFilter.getStartTime()));
                    }else{
                        range = new DateRange(timeFilter.getStartTime(), timeFilter.getEndTime());
                    }
                    if(startTime == -1 && endTime == -1 && range !=null){
                        timeFilter.setStartTime(range.getStartTime());
                        timeFilter.setEndTime(range.getEndTime());
                    }
                    FacilioField child_field = fieldMap.get("parentId").clone();
                    AggregateOperator aggr = AggregateOperator.getAggregateOperator(cardContext.getAggr());
                    /**
                     * Select Builder construction to fetch card data starts here
                     */

                    Object result = this.fetchCardData(Collections.singletonList(aggr.getSelectField(field).clone()), baseModule, range, cardContext, fieldMap, parentModuleIdField, child_field, parentModuleForCriteria,db_filter, aggr, field);
                    cardContext.getResult().put("value", this.setResultJson(timeFilter.getDateLabel(), result, field, null,aggr, readingKPI != null ? readingKPI.getUnitLabel() : null));
                    Object baseline_result = null;
                    if(cardContext.getBaseline() != null)
                    {
                        BaseLineContext baseline = BaseLineAPI.getBaseLine(cardContext.getBaseline());
                        baseline.setAdjustType(BaseLineContext.AdjustType.NONE);
                        DateRange baseline_range = baseline.calculateBaseLineRange(range, baseline.getAdjustTypeEnum());
                        cardContext.getTimeFilter().setBaselineRange(baseline_range);
                        cardContext.getTimeFilter().setBaselinePeriod(cardContext.getBaseline());
                        baseline_result = this.fetchCardData(Collections.singletonList(aggr.getSelectField(field).clone()) ,baseModule, baseline_range, cardContext, fieldMap, parentModuleIdField, child_field, parentModuleForCriteria,db_filter,aggr, field);
                        cardContext.getResult().put("baseline_value", this.setResultJson(baseline.getName(), baseline_result, field, cardContext.getBaselineTrend(),aggr, readingKPI != null ? readingKPI.getUnitLabel() : null));
                    }
                    /**
                     * Select Builder construction to fetch card data ends here
                     */
                }
            }
        }
    }
    private Map<String, Object> setResultJson(String period, Object value, FacilioField field, String trend,AggregateOperator aggr)throws Exception
    {
        return setResultJson(period, value, field, trend, aggr, null);
    }
    private Map<String, Object> setResultJson(String period, Object value, FacilioField field, String trend,AggregateOperator aggr, String unitSymbol)throws Exception
    {
        Map<String, Object> result_json = new HashMap<>();
        result_json.put("dataType", field != null ? field.getDataTypeEnum() : null);
        result_json.put("value", value);
        result_json.put("actualValue", value);
        result_json.put("period",period);
        if(trend != null && !trend.equals("")){
            result_json.put("baselineTrend",trend);
        }
        if (field != null && field instanceof NumberField) {
            NumberField numberField = (NumberField)field;
            result_json.put("unit", numberField.getUnit());
            Unit unitMap = Unit.getUnitFromSymbol(numberField.getUnit());
            if(unitMap != null) {
                result_json.put("unit_map", FieldUtil.getAsJSON(unitMap));
            }
            if(numberField.getUnitId() > 0) {
                result_json.put("value", UnitsUtil.convertToSiUnit(value, Unit.valueOf(numberField.getUnitId())));
            }
        }
        else if (field != null && field.getDataTypeEnum() == FieldType.BOOLEAN && value != null)
        {
            if(aggr != null && aggr instanceof BmsAggregateOperators.SpecialAggregateOperator) {
                Map<String, Integer> counts = (Map<String, Integer>) value;
                String actualString = "";
                if(counts.keySet().size() > 0) {
                    Object firstValue = counts.keySet().iterator().next();
                    if(counts != null && counts.keySet().size() < 2 && counts.get(firstValue) < 2){
                        actualString = getBooleanValue(field,firstValue);
                    }
                    else {
                        actualString = counts.entrySet().stream().map(e -> {
                            try {
                                return e.getValue() + " " + getBooleanValue(field, e.getKey());
                            } catch (Exception ex) {
                                throw new RuntimeException(ex);
                            }
                        }).collect(Collectors.joining(" & "));
                    }
                }
                result_json.put("actualValue",actualString);
            } else {
                result_json.put("value", getBooleanValue(field, value));
            }
        }
        else if (field != null && field instanceof EnumField && value != null) {
            if(aggr != null && aggr instanceof BmsAggregateOperators.SpecialAggregateOperator) {
                Map<Integer, Object> enumMap = ((EnumField) field).getEnumMap();
                Map<String, Integer> counts = (Map<String, Integer>) value;
                String actualString = "";
                if(enumMap != null && counts.keySet().size() > 0){
                    Object firstValue = counts.keySet().iterator().next();
                    if(counts != null && counts.keySet().size() < 2 && counts.get(firstValue) < 2){
                        actualString = (String) enumMap.get(Integer.parseInt((String) firstValue));
                    }
                    else {
                        actualString = counts.entrySet().stream().map(e ->e.getValue() + " " + enumMap.get(Integer.parseInt(e.getKey()))).collect(Collectors.joining(" & "));
                    }
                }
                result_json.put("actualValue",actualString);
            } else {
                Map<Integer, Object> enumMap = ((EnumField) field).getEnumMap();
                if(enumMap.containsKey(Integer.parseInt(value.toString()))){
                    result_json.put("value", enumMap.get(Integer.parseInt(value.toString())));
                }
            }
        }
        if(unitSymbol != null){
            result_json.put("unit", unitSymbol);
        }
        return result_json;
    }
    private SelectRecordsBuilder<ModuleBaseWithCustomFields> fetchCardDataSelectBuilder(List<FacilioField> fields, FacilioModule baseModule, DateRange range, V2AnalyticsCardWidgetContext cardContext, Map<String, FacilioField> fieldMap, FacilioField parentModuleIdField, FacilioField child_field, FacilioModule parentModuleForCriteria, V2AnalyticsContextForDashboardFilter db_filter, AggregateOperator aggr, FacilioField field)throws Exception
    {
        Set<FacilioModule> addedModules= new HashSet<>();
        FacilioModule aggr_base_Module = null;
        if(clickhouse && AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.CLICKHOUSE) && aggr != null && !(aggr instanceof BmsAggregateOperators.SpecialAggregateOperator))
        {
            aggr_base_Module = getAggrTableNameFromBaseModule(baseModule);
            if (aggr_base_Module != null)
            {
                baseModule = aggr_base_Module;
                fields = Collections.singletonList(V2AnalyticsOldUtil.getAggregatedYField(field.clone(), baseModule, aggr.getStringValue().toUpperCase(Locale.ROOT)));
            }
        }
        addedModules.add(baseModule);
        SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder = setBaseModuleAggregation(baseModule);
        if(aggr != null && aggr instanceof BmsAggregateOperators.SpecialAggregateOperator  && (field instanceof EnumField || field instanceof BooleanField)) {
            List<FacilioField> newFields = new ArrayList<>();
            for(FacilioField curr_fields : fields) {
                newFields.add(curr_fields);
            }
            FacilioField rowNumberField = new FacilioField();
            rowNumberField.setName("rn");
            rowNumberField.setColumnName("ROW_NUMBER() OVER (PARTITION BY " + baseModule.getTableName() + "." +  fieldMap.get("parentId").getColumnName() + "  ORDER BY " + "TTIME DESC)");
            newFields.add(rowNumberField);
            newFields.add(fields.get(0));

            newFields.add(fieldMap.get("ttime"));
            newFields.add(AccountConstants.getOrgIdField(baseModule));
            selectBuilder.select(newFields);
        }else {
            selectBuilder.select(fields);
        }
        if(aggr_base_Module != null)
        {
            if(range != null)
            {
                String startDate = DateTimeUtil.getFormattedTime(range.getStartTime(), "yyyy-MM-dd");
                String endDate = DateTimeUtil.getFormattedTime(range.getEndTime(), "yyyy-MM-dd");
                if(startDate != null && endDate != null)
                {
                    FacilioField aggr_XField = FieldFactory.getDefaultField("ttime", "Date", new StringBuilder(aggr_base_Module.getTableName()).append(".DATE").toString(), FieldType.DATE);
                    if(range.getStartTime() == range.getEndTime()){
                        selectBuilder.andCustomWhere(new StringBuilder(" ").append(aggr_XField.getColumnName()).append(" >= '").append(startDate).append("' AND ").append(aggr_XField.getColumnName()).append(" < '").append(endDate).append("'").toString());
                    }else {
                        selectBuilder.andCustomWhere(new StringBuilder(" ").append(aggr_XField.getColumnName()).append(" >= '").append(startDate).append("' AND ").append(aggr_XField.getColumnName()).append(" <= '").append(endDate).append("'").toString());
                    }
                }
            }
        }
        else
        {
            if(range != null && range.getStartTime() == range.getEndTime()){
                FacilioField time_field = fieldMap.get("ttime").clone();
                selectBuilder.andCustomWhere(new StringBuilder(" ").append(time_field.getCompleteColumnName()).append(" >= '").append(range.getStartTime()).append("' AND ").append(time_field.getCompleteColumnName()).append(" < '").append(range.getEndTime()).append("'").toString());
            }
            else {
                selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("ttime"), range.toString(), DateOperators.BETWEEN));
            }
        }
        if(cardContext.getCriteriaType() == V2MeasuresContext.Criteria_Type.CRITERIA.getIndex())
        {
            Criteria parent_criteria = V2AnalyticsOldUtil.setFieldInCriteria(cardContext.getCriteria(), parentModuleForCriteria);
            if (parent_criteria != null) {
                /**
                 * code added for handling aggregated table name which is defined in clickhouse for basemodule
                 */
                if(baseModule.getName().equals(child_field.getModule().getName()) && !baseModule.getTableName().equals(child_field.getModule().getTableName()))
                {
                    child_field.setModule(baseModule);
                }
                V2AnalyticsOldUtil.applyJoin(null, baseModule, new StringBuilder(parentModuleIdField.getCompleteColumnName()).append(" = ").append(child_field.getCompleteColumnName()).toString(), parentModuleForCriteria, selectBuilder, addedModules);
                selectBuilder.andCriteria(parent_criteria);
            }
        }
        if(db_filter != null && db_filter.getDb_user_filter() != null) {
            applyDashboardUserFilterCriteria(baseModule, db_filter.getDb_user_filter(), cardContext,selectBuilder,addedModules );
        }
        if(addedModules.size() == 1){
            V2AnalyticsOldUtil.checkAndApplyJoinForScopingCriteria(selectBuilder, addedModules, baseModule);
        }else{
            V2AnalyticsOldUtil.addDeletedCriteria(addedModules , selectBuilder);
        }
        selectBuilder.limit(50);
        return selectBuilder;
    }
    private Object fetchCardData(List<FacilioField> fields, FacilioModule baseModule, DateRange range, V2AnalyticsCardWidgetContext cardContext, Map<String, FacilioField> fieldMap, FacilioField parentModuleIdField, FacilioField child_field, FacilioModule parentModuleForCriteria, V2AnalyticsContextForDashboardFilter db_filter, AggregateOperator aggr, FacilioField y_field)throws Exception
    {
        List<Map<String, Object>> props = null;
        SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder = this.fetchCardDataSelectBuilder(fields, baseModule, range, cardContext, fieldMap, parentModuleIdField, child_field, parentModuleForCriteria,db_filter, aggr, y_field);
        if(aggr != null && aggr instanceof BmsAggregateOperators.SpecialAggregateOperator && (y_field instanceof EnumField || y_field instanceof BooleanField)) {
            props = getLastValues(selectBuilder, fields, baseModule);
            Map<String, Integer> counts = new HashMap<String, Integer>();
            for(Map<String, Object> prop : props) {
                if(prop.get(fields.get(0).getName()) != null) {
                    String value = (String) prop.get(fields.get(0).getName()).toString();
                    if(counts.containsKey(value)){
                        Integer count = counts.get(value);
                        count = count + 1;
                        counts.put(value,count);
                    }else {
                        counts.put(value,1);
                    }
                }
            }
            return counts;
        }else if(aggr != null && aggr instanceof BmsAggregateOperators.SpecialAggregateOperator) {
            selectBuilder.limit(1);
            selectBuilder.orderBy("TTIME desc");
        }
        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.CLICKHOUSE))
        {
            try {
                props = FacilioService.runAsServiceWihReturn(FacilioConstants.Services.CLICKHOUSE, () -> selectBuilder.getAsProps());
                LOGGER.debug("CLICKHOUSE SELECT BUILDER " + selectBuilder);
            }
            catch (Exception e)
            {
                LOGGER.error("CLICKHOUSE SELECT BUILDER Object for getting card data ---" + selectBuilder, e);
                LOGGER.error("ERROR while executing clickhouse card query", e);
            }
        }
        else
        {
            props = selectBuilder.getAsProps();
        }
        LOGGER.debug("SELECT BUILDER Object for getting card data ---" + selectBuilder);
        if(props != null && props.size() > 0)
        {
            FacilioField field = fields.get(0);
            HashMap<String, Object> prop =  (HashMap<String, Object>) props.get(0);
            return prop.get(field.getName());
        }
        return null;
    }

    private void getDynamicKpiValue(V2AnalyticsCardWidgetContext cardParams, V2AnalyticsContextForDashboardFilter db_filter)throws Exception
    {
        ReadingKPIContext dynKpi = ReadingKpiAPI.getReadingKpi(cardParams.getDynamicKpiId());
        if(dynKpi != null)
        {
            DateRange dateRange = this.constructDateRange(cardParams.getTimeFilter(), db_filter);
            AggregateOperator aggr = AggregateOperator.getAggregateOperator(cardParams.getAggr());
            Criteria criteria = new Criteria();
            if (cardParams.getCriteria() != null) {
                criteria = cardParams.getCriteria();
            }
            if (db_filter != null && db_filter.getDb_user_filter() != null) {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                JSONObject userFilter = db_filter.getDb_user_filter();
                for (Object field : userFilter.keySet()) {
                    List<Map<String, JSONObject>> filterMappings = (List<Map<String, JSONObject>>) userFilter.get(field);
                    for (Map<String, JSONObject> filterMap : filterMappings) {
                        for (String alias : filterMap.keySet()) {
                            HashMap selected_dp_map = (HashMap) filterMap.get(String.valueOf(alias));
                            Condition condition = new Condition();
                            if (selected_dp_map.get("moduleName") != null) {
                                ReportDataPointContext dataPoint = new ReportDataPointContext();
                                dataPoint.setParentReadingModule(modBean.getModule(cardParams.getParentModuleName()));
                                dataPoint.setModuleName(cardParams.getType());
                                FacilioField filterField = V2AnalyticsOldUtil.isFilterApplicable(modBean, dataPoint, (String) selected_dp_map.get("moduleName"));
                                if (filterField == null) {
                                    continue;
                                }
                                condition.setField(filterField);
                            }
                            else{
                                if(alias.equals("parentId")) {
                                   condition.setField(FieldFactory.getIdField(modBean.getModule(cardParams.getParentModuleName())));
                                }else {
                                    condition.setField(modBean.getField(alias, cardParams.getParentModuleName()));
                                }
                            }
                            condition.setOperatorId(((Long) selected_dp_map.get("operatorId")).intValue());
                            List<String> value = (List<String>) selected_dp_map.get("value");
                            StringJoiner joiner = new StringJoiner(",");
                            value.forEach(val -> joiner.add(val));
                            condition.setValue(String.valueOf(joiner));
                            condition.setModuleName(cardParams.getParentModuleName());
                            criteria.addAndCondition(condition);
                        }
                    }
                }
            }
            List<Long> parentIds = V2AnalyticsOldUtil.getAssetIdsFromCriteria(cardParams.getParentModuleName(), criteria);
            if(parentIds != null && parentIds.size() > 0)
            {
                Map<Long, List<Map<String, Object>>> resultForDynamicKpi = ReadingKpiAPI.getResultForDynamicKpi(Collections.singletonList(parentIds.get(0)), dateRange, null, dynKpi.getNs(), clickhouse, true);
                cardParams.getResult().put("value", this.setResultJson(cardParams.getTimeFilter() != null ? cardParams.getTimeFilter().getDateLabel() : null, this.getDynamicKpiFinalResult(resultForDynamicKpi, parentIds.get(0)), null, null,aggr, dynKpi.getUnitLabel()));
                if(cardParams.getBaseline() != null)
                {
                    BaseLineContext baseline = BaseLineAPI.getBaseLine(cardParams.getBaseline());
                    baseline.setAdjustType(BaseLineContext.AdjustType.NONE);
                    DateRange baseline_range = baseline.calculateBaseLineRange(dateRange, baseline.getAdjustTypeEnum());
                    cardParams.getTimeFilter().setBaselineRange(baseline_range);
                    cardParams.getTimeFilter().setBaselinePeriod(cardParams.getBaseline());
                    Map<Long, List<Map<String, Object>>> baseline_dkpi_result = ReadingKpiAPI.getResultForDynamicKpi(Collections.singletonList(parentIds.get(0)), dateRange, null, dynKpi.getNs(), clickhouse, true);
                    cardParams.getResult().put("baseline_value", this.setResultJson(cardParams.getTimeFilter() != null ? cardParams.getTimeFilter().getDateLabel() : null, this.getDynamicKpiFinalResult(baseline_dkpi_result, parentIds.get(0)), null, cardParams.getBaselineTrend(),aggr, dynKpi.getUnitLabel()));
                }
                Map<String, Object> parentIdMap = new HashMap<>();
                parentIdMap.put("parentId", parentIds.get(0));
                cardParams.getResult().put("parentIds", parentIdMap);
            }
            else {
                cardParams.getResult().put("value", this.setResultJson(cardParams.getTimeFilter() != null ? cardParams.getTimeFilter().getDateLabel() : null, null, null, null,aggr, dynKpi.getUnitLabel()));
                if (cardParams.getBaseline() != null) {
                    BaseLineContext baseline = BaseLineAPI.getBaseLine(cardParams.getBaseline());
                    baseline.setAdjustType(BaseLineContext.AdjustType.NONE);
                    DateRange baseline_range = baseline.calculateBaseLineRange(dateRange, baseline.getAdjustTypeEnum());
                    cardParams.getTimeFilter().setBaselineRange(baseline_range);
                    cardParams.getTimeFilter().setBaselinePeriod(cardParams.getBaseline());
                    cardParams.getResult().put("baseline_value", this.setResultJson(cardParams.getTimeFilter() != null ? cardParams.getTimeFilter().getDateLabel() : null, null, null, cardParams.getBaselineTrend(),aggr, dynKpi.getUnitLabel()));
                }
            }
        }
    }
    private SelectRecordsBuilder<ModuleBaseWithCustomFields> setBaseModuleAggregation(FacilioModule baseModule)throws Exception
    {
        SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder =  new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
                .module(baseModule) //Assuming X to be the base module
                .setAggregation();
        return selectBuilder;
    }
    public static void applyDashboardUserFilterCriteria(FacilioModule baseModule, JSONObject dbUserFilter,V2AnalyticsCardWidgetContext cardContext, SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder,Set<FacilioModule> addedModules)throws Exception
    {
        if(dbUserFilter != null)
        {
            List<Map<String, JSONObject>> filterMappings = (List<Map<String, JSONObject>>) dbUserFilter.get(cardContext.getType());
            if(filterMappings != null && filterMappings.size() > 0) {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioField appliedField = new FacilioField();
                FacilioField yField = modBean.getField(cardContext.getFieldId());
                Criteria criteria = new Criteria();
                for(Map<String, JSONObject> filterMap : filterMappings) {
                    for (String alias : filterMap.keySet()) {
                        Condition condition = new Condition();
                        HashMap selected_dp_map = (HashMap) filterMap.get(String.valueOf(alias));
                        condition.setOperatorId(((Long) selected_dp_map.get("operatorId")).intValue());
                        List<String> value = (List<String>) selected_dp_map.get("value");
                        StringJoiner joiner = new StringJoiner(",");
                        value.forEach(val -> joiner.add(val));
                        condition.setValue(String.valueOf(joiner));
                        Boolean picklistJoin = false;
                        String filterModule =  null;
                        if(alias.equals("parentId")) {
                            appliedField = modBean.getField("parentId",baseModule.getName()).clone();
                        }else{
                            if(selected_dp_map.get("moduleName") != null) {
                                picklistJoin = true;
                                filterModule = (String) selected_dp_map.get("moduleName");
                                appliedField = modBean.getField(alias, (String) selected_dp_map.get("moduleName")).clone();
                            } else {
                                appliedField = modBean.getField(alias, cardContext.getType()).clone();
                            }
                        }
                        if (appliedField != null && appliedField.getModule() != null && appliedField.getModule().getName().equals(baseModule.getName()) && !appliedField.getModule().getTableName().equals(baseModule.getTableName()))
                        {
                            appliedField = appliedField.clone();
                            appliedField.setModule(baseModule);
                        }
                        condition.setField(appliedField);
                        criteria.addAndCondition(condition);
                        applyFilterCriteria(baseModule,cardContext,selectBuilder,addedModules,criteria,yField,picklistJoin, filterModule);
                    }
                }
            }
        }
    }
    public static void applyFilterCriteria(FacilioModule baseModule, V2AnalyticsCardWidgetContext cardContext, SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder, Set<FacilioModule> addedModules, Criteria criteria, FacilioField yField, Boolean picklistJoin, String filterModule)throws Exception
    {
        LinkedHashMap<String, String> moduleVsAlias = new LinkedHashMap<String, String>();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String parentReadingModule = cardContext.getParentModuleName();
        FacilioModule parentModule = modBean.getModule(parentReadingModule);
            if(parentReadingModule != null)
            {
                FacilioField parent_field = FieldFactory.getIdField(modBean.getModule(parentReadingModule));
                if(yField != null)
                {
                    Map<String,FacilioField> fieldsMap = FieldFactory.getAsMap(modBean.getAllFields(yField.getModule().getName()));
                    FacilioField child_field = fieldsMap.get("parentId").clone();
                    /**
                     * code added for handling aggregated table name which is defined in clickhouse for basemodule
                     */
                    if(baseModule.getName().equals(child_field.getModule().getName()) && !baseModule.getTableName().equals(child_field.getModule().getTableName()))
                    {
                        child_field.setModule(baseModule);
                    }
                    V2AnalyticsOldUtil.applyJoin(moduleVsAlias,  baseModule, new StringBuilder(parent_field.getCompleteColumnName()).append("=").append(child_field.getCompleteColumnName()).toString(), parentModule, selectBuilder, addedModules);
                    if(picklistJoin) {
                        ReportDataPointContext dataPoint = new ReportDataPointContext();
                        dataPoint.setParentReadingModule(parentModule);
                        dataPoint.setModuleName(cardContext.getType());
                        V2AnalyticsOldUtil.applyPicklistJoin(moduleVsAlias, baseModule, dataPoint, selectBuilder,addedModules, criteria, filterModule);

                    }else {
                        Criteria parent_criteria = V2AnalyticsOldUtil.setFieldInCriteria(criteria, parentModule);
                        if(parent_criteria != null)
                        {
                            selectBuilder.andCriteria(parent_criteria);
                        }
                    }
                }
            }
    }

    private void getModuleCardValue(V2AnalyticsCardWidgetContext cardParams, V2ModuleContextForDashboardFilter db_filter) throws Exception {
        Long reportId = cardParams.getReportId();
        if(reportId!=null && reportId>0){
            FacilioChain chain = V2TransactionChainFactory.getKpiDataChain();
            FacilioContext context = chain.getContext();
            context.put(FacilioConstants.ContextNames.REPORT_ID,cardParams.getReportId());
            V2TimeFilterContext timeFilter = cardParams.getTimeFilter();
            DateRange range = null;
            if(timeFilter!=null){
                if(db_filter != null && db_filter.getTimeFilter() != null) {
                    V2TimeFilterContext dbTimeFilter = db_filter.getTimeFilter();
                    context.put(FacilioConstants.ContextNames.CARD_PERIOD,DateOperators.BETWEEN);
                    range =  new DateRange(dbTimeFilter.getStartTime(), dbTimeFilter.getEndTime());
                    timeFilter.setDateLabel(dbTimeFilter.getDateLabel());
                    timeFilter.setDateOperator(DateOperators.BETWEEN.getOperatorId());
                    timeFilter.setDateOperatorEnum(DateOperators.BETWEEN);
                }else {
                    context.put(FacilioConstants.ContextNames.CARD_PERIOD,timeFilter.getDateOperatorEnum());
                }

                long dateOperator = timeFilter.getDateOperator();
                if( dateOperator > 0 && dateOperator!=20){
                    range = ((DateOperators) Operator.getOperator(timeFilter.getDateOperator())).getRange(String.valueOf(timeFilter.getStartTime()));
                }
                if(range!=null){
                    timeFilter.setStartTime(range.getStartTime());
                    timeFilter.setEndTime(range.getEndTime());
                }
            }
            if(db_filter != null) {
                context.put("db_filter",db_filter);
            }
            if(cardParams.getBaseline()!=null){
                context.put(FacilioConstants.ContextNames.BASE_LINE,cardParams.getBaseline());
            }
            chain.execute();
            Object cardValue = context.get(FacilioConstants.ContextNames.CARD_VALUE);
            Object baseLineValue = context.get(FacilioConstants.ContextNames.BASE_LINE_VALUE);
            if(cardValue!=null){
                Map<String,Object> resultMap = new HashMap<>();
                String period = null;
                if(db_filter != null && db_filter.getTimeFilter() != null) {
                    period = db_filter.getTimeFilter().getDateLabel();
                }
                else if(cardParams.getTimeFilter()!=null){
                    period = cardParams.getTimeFilter().getDateLabel();
                }
                V2ModuleReportContext reportContext = (V2ModuleReportContext) context.get("v2_report");
                V2ModuleMeasureContext measureContext = reportContext.getMeasures()!=null ? reportContext.getMeasures().get(0) : null;
                Criteria criteriaObj = null;
                if(measureContext!=null){
                    String criteria = measureContext.getCriteria();
                    if(StringUtils.isNotEmpty(criteria)) {
                        JSONParser parser = new JSONParser();
                        JSONObject criteriaJson = (JSONObject) parser.parse(criteria);
                        criteriaObj = FieldUtil.getAsBeanFromJson(criteriaJson,Criteria.class);
                    }
                    ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                    String fieldName = measureContext.getFieldName();
                    String moduleName = measureContext.getModuleName();
                    if(fieldName!=null && moduleName!=null) {
                        FacilioField measureField = modBean.getField(fieldName,moduleName);
                        if(measureField!=null && measureField instanceof NumberField){
                            NumberField numberField = (NumberField) measureField;
                            if(numberField!=null){
                                resultMap.put("unit", numberField.getUnit());
                                Unit unitMap = Unit.getUnitFromSymbol(numberField.getUnit());
                                if(unitMap != null) {
                                    resultMap.put("unit_map", FieldUtil.getAsJSON(unitMap));
                                }
                            }
                        }
                    }
                }
                V2ModuleTimeFilterContext timeFilterContext = reportContext.getTimeFilter();
                String dateFieldName = null;
                if(timeFilterContext!=null){
                    dateFieldName = timeFilterContext.getFieldName();
                }
                resultMap = setResultModuleCard(resultMap,cardValue,period,criteriaObj,dateFieldName,null);
                cardParams.getResult().put(FacilioConstants.ContextNames.CARD_VALUE,resultMap);
            }
            if(baseLineValue!=null){
                Map<String,Object> resultMap = new HashMap<>();
                String baseLinePeriod = cardParams.getBaseline();
                String baseLineTrend = cardParams.getBaselineTrend();
                resultMap = setResultModuleCard(resultMap,baseLineValue,baseLinePeriod,null,null,baseLineTrend);
                cardParams.getResult().put("baseline_value",resultMap);
            }
        }
    }
    private Map<String,Object> setResultModuleCard(Map<String,Object> resultMap, Object cardValue, String period, Criteria criteria,String dateField, String baseLineTrend){
        resultMap.put(FacilioConstants.ContextNames.CARD_VALUE,cardValue);
        resultMap.put(FacilioConstants.ContextNames.ACTUAL_VALUE,cardValue);
        if(period!=null){
            resultMap.put(FacilioConstants.ContextNames.CARD_PERIOD,period);
        }
        if(baseLineTrend!=null && !baseLineTrend.isEmpty()){
            resultMap.put(FacilioConstants.ContextNames.BASE_LINE_TREND,baseLineTrend);
        }
        if(criteria!=null){
            resultMap.put(FacilioConstants.ContextNames.CRITERIA,criteria);
        }
        if(dateField!=null){
            resultMap.put(FacilioConstants.ContextNames.DATE_FIELD,dateField);
        }
        return resultMap;
    }

    private DateRange constructDateRange(V2TimeFilterContext timeFilter, V2AnalyticsContextForDashboardFilter db_filter)throws Exception
    {
        DateRange range = null;
        if(timeFilter != null)
        {
            long startTime = timeFilter.getStartTime();
            long endTime = timeFilter.getEndTime();
            if (db_filter != null && db_filter.getTimeFilter() != null) {
                V2TimeFilterContext dbTimeFilter = db_filter.getTimeFilter();
                range = new DateRange(dbTimeFilter.getStartTime(), dbTimeFilter.getEndTime());
                timeFilter.setDateLabel(dbTimeFilter.getDateLabel());
                timeFilter.setDateOperator(dbTimeFilter.getDateOperator());
                timeFilter.setDateOperatorEnum(dbTimeFilter.getDateOperatorEnum());
            } else if (timeFilter.getDateOperator() > 0) {
                range = timeFilter.getOffset() != null && timeFilter.getOffset() > 0 ? ((DateOperators) Operator.getOperator(timeFilter.getDateOperator())).getRange(timeFilter.getOffset().toString()) : ((DateOperators) Operator.getOperator(timeFilter.getDateOperator())).getRange(String.valueOf(timeFilter.getStartTime()));
            } else {
                range = new DateRange(timeFilter.getStartTime(), timeFilter.getEndTime());
            }
            if(startTime == -1 && endTime == -1 && range !=null){
                timeFilter.setStartTime(range.getStartTime());
                timeFilter.setEndTime(range.getEndTime());
            }
        }
        return range;
    }

    private Object getDynamicKpiFinalResult(Map<Long, List<Map<String, Object>>> d_kpi_result, Long parentId)throws Exception
    {
        if(d_kpi_result != null)
        {
            List<Map<String, Object>> baseline_resultList = d_kpi_result.get(parentId);
            if (baseline_resultList != null && baseline_resultList.size() > 0) {
                Map<String, Object> valueMap = baseline_resultList.get(0);
                if (valueMap != null && !valueMap.isEmpty()) {
                    return valueMap.get("result");
//                    cardParams.getResult().put("baseline_value", this.setResultJson(baseline.getName(), valueMap.get("result"), null, cardParams.getBaselineTrend()));
                }
            }
        }
        return null;
    }

    private FacilioModule getAggrTableNameFromBaseModule(FacilioModule baseModule)throws Exception
    {
        String aggr_table_name=null;
        if(baseModule != null) {
            aggr_table_name = ClickhouseUtil.getAggregatedTableName(baseModule.getTableName(), AccountUtil.getCurrentOrg().getTimezone(), "daily");
            if(aggr_table_name != null)
            {
                return V2AnalyticsOldUtil.constructAndGetAggregatedModule(baseModule, aggr_table_name);
            }
        }
        return null;
    }
    private List<Map<String, Object>> getLastValues( SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder,List<FacilioField> fields, FacilioModule baseModule) throws Exception {
        List<FacilioField> cloneFields = new ArrayList<>();
        List<FacilioField> wrapperFields = new ArrayList<>();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        cloneFields.add(modBean.getField("ttime",baseModule.getName()));
        String tableName = selectBuilder.constructQueryString();

        FacilioField rowNumberField = new FacilioField();
        rowNumberField.setName("rn");
        rowNumberField.setColumnName("rn");
        cloneFields.add(rowNumberField);

        cloneFields.add(fields.get(0));
        for (FacilioField field : cloneFields) {
            if (field.getColumnName().equals("ORGID") || field.getColumnName().contains("ROW_NUMBER") || field.getColumnName() == null) {
                continue;
            }
            FacilioField temp_field = field.clone();
            temp_field.setColumnName(temp_field.getName());
            temp_field.setTableAlias("subquery");
            wrapperFields.add(temp_field);
        }
        GenericSelectRecordBuilder newselectBuilder = new GenericSelectRecordBuilder()
                .table(new StringBuilder("( ").append(tableName).append(" )").toString())
                .select(wrapperFields)
                .tableAlias("subquery");
        newselectBuilder.andCustomWhere("rn = 1");
        newselectBuilder.limit(2000);
        return newselectBuilder.get();
    }
    public String getBooleanValue(FacilioField field, Object value) throws Exception {
        BooleanField boolField = (BooleanField) field;
        HashMap<String, String> enumMap = new HashMap<>();
        if (boolField.getTrueVal() != null && !boolField.getTrueVal().isEmpty()) {
            enumMap.put("true", boolField.getTrueVal());
            enumMap.put("false", boolField.getFalseVal());
        }
        else {
            enumMap.put("true", "True");
            enumMap.put("false", "False");
        }
        String str_value = value.toString();
        if (str_value.equals("true") && boolField.getTrueVal() != null) {
            return boolField.getTrueVal();
        } else if (str_value.equals("false") && boolField.getFalseVal() != null) {
            return boolField.getFalseVal();
        }else if(enumMap.get(str_value) != null){
            return enumMap.get(str_value);
        }else {
            return str_value;
        }
    }
}
