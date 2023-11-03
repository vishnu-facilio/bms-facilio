package com.facilio.analytics.v2.command;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.analytics.v2.V2AnalyticsOldUtil;
import com.facilio.analytics.v2.context.V2AnalyticsCardWidgetContext;
import com.facilio.analytics.v2.context.V2CardContext;
import com.facilio.analytics.v2.context.V2MeasuresContext;
import com.facilio.analytics.v2.context.V2TimeFilterContext;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.BaseLineAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.BooleanField;
import com.facilio.modules.fields.EnumField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.report.formatter.DecimalFormatter;
import com.facilio.service.FacilioService;
import com.facilio.time.DateRange;
import com.facilio.unitconversion.Unit;
import com.facilio.unitconversion.UnitsUtil;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.*;

public class V2ConstructCardCommand extends FacilioCommand {
    private static final Logger LOGGER = Logger.getLogger(V2ConstructCardCommand.class.getName());
    @Override
    public boolean executeCommand(Context context) throws Exception
    {
        V2CardContext cardContext = (V2CardContext) context.get("cardContext");
        if(cardContext != null)
        {
            this.getReadingCardValue(cardContext.getCardParams());
        }
        return false;
    }
    private void getReadingCardValue(V2AnalyticsCardWidgetContext cardContext)throws Exception
    {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Long fieldId = cardContext.getFieldId();
        if(cardContext != null && fieldId > 0)
        {
            FacilioField field = modBean.getField(fieldId);
            FacilioModule baseModule = field.getModule();
            FacilioField parentModuleIdField = FieldFactory.getIdField(modBean.getModule(cardContext.getParentModuleName()));
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(baseModule.getName()));

            FacilioModule parentModuleForCriteria = modBean.getModule(cardContext.getParentModuleName());
            if(field != null)
            {
                V2TimeFilterContext timeFilter = cardContext.getTimeFilter();
                if(timeFilter != null)
                {
                    DateRange range = null;
                    if(timeFilter.getDateOperator() > 0){
                        range = timeFilter.getOffset() != null &&  timeFilter.getOffset() > 0 ? ((DateOperators) Operator.getOperator(timeFilter.getDateOperator())).getRange(timeFilter.getOffset().toString()) : ((DateOperators) Operator.getOperator(timeFilter.getDateOperator())).getRange(String.valueOf(timeFilter.getStartTime()));
                    }else{
                        range = new DateRange(timeFilter.getStartTime(), timeFilter.getEndTime());
                    }
                    FacilioField child_field = fieldMap.get("parentId");
                    AggregateOperator aggr = AggregateOperator.getAggregateOperator(cardContext.getAggr());
                    /**
                     * Select Builder construction to fetch card data starts here
                     */

                    Object result = this.fetchCardData(Collections.singletonList(aggr.getSelectField(field).clone()), baseModule, range, cardContext, fieldMap, parentModuleIdField, child_field, parentModuleForCriteria);
                    cardContext.getResult().put("value", this.setResultJson(timeFilter.getLabel(), result, field, null));
                    Object baseline_result = null;
                    if(cardContext.getBaseline() != null)
                    {
                        BaseLineContext baseline = BaseLineAPI.getBaseLine(cardContext.getBaseline());
                        baseline.setAdjustType(BaseLineContext.AdjustType.NONE);
                        DateRange baseline_range = baseline.calculateBaseLineRange(range, baseline.getAdjustTypeEnum());
                        cardContext.getTimeFilter().setBaselineRange(baseline_range);
                        cardContext.getTimeFilter().setBaselinePeriod(cardContext.getBaseline());
                        baseline_result = this.fetchCardData(Collections.singletonList(aggr.getSelectField(field).clone()) ,baseModule, baseline_range, cardContext, fieldMap, parentModuleIdField, child_field, parentModuleForCriteria);
                        cardContext.getResult().put("baseline_value", this.setResultJson(baseline.getName(), baseline_result, field, cardContext.getBaselineTrend()));
                    }
                    /**
                     * Select Builder construction to fetch card data ends here
                     */
                }
            }
        }
    }
    private Map<String, Object> setResultJson(String period, Object value, FacilioField field, String trend)throws Exception
    {
        Map<String, Object> result_json = new HashMap<>();
        result_json.put("dataType", field.getDataTypeEnum());
        result_json.put("value", value);
        result_json.put("actualValue", value);
        result_json.put("period",period);
        if(trend != null && !trend.equals("")){
            result_json.put("baselineTrend",trend);
        }
//        if(range != null){
//            result_json.put("dateRange", range);
//        }
        if (field instanceof NumberField) {
            NumberField numberField = (NumberField)field;
            result_json.put("unit", numberField.getUnit());
            if(numberField.getUnitId() > 0) {
                result_json.put("value", UnitsUtil.convertToSiUnit(value, Unit.valueOf(numberField.getUnitId())));
            }
        }
        else if (field.getDataTypeEnum() == FieldType.BOOLEAN && value != null)
        {
            BooleanField boolField = (BooleanField) field;
            HashMap<String, String> enumMap = new HashMap<>();
            if (boolField.getTrueVal() != null && !boolField.getTrueVal().isEmpty()) {
                enumMap.put("1", boolField.getTrueVal());
                enumMap.put("0", boolField.getFalseVal());
            }
            else {
                enumMap.put("1", "True");
                enumMap.put("0", "False");
            }
            String str_value = value.toString();
            if (str_value.equals("1") && boolField.getTrueVal() != null) {
                result_json.put("value", boolField.getTrueVal());
            } else if (str_value.equals("0") && boolField.getFalseVal() != null) {
                result_json.put("value", boolField.getFalseVal());
            }else{
                result_json.put("value", enumMap.get(str_value));
            }
        }
        else if (field instanceof EnumField && value != null) {
            Map<Integer, Object> enumMap = ((EnumField) field).getEnumMap();
            if(enumMap.containsKey(Integer.parseInt(value.toString()))){
                result_json.put("value", enumMap.get(Integer.parseInt(value.toString())));
            }
        }
        return result_json;
    }
    private SelectRecordsBuilder<ModuleBaseWithCustomFields> fetchCardDataSelectBuilder(List<FacilioField> fields, FacilioModule baseModule, DateRange range, V2AnalyticsCardWidgetContext cardContext, Map<String, FacilioField> fieldMap, FacilioField parentModuleIdField, FacilioField child_field, FacilioModule parentModuleForCriteria)throws Exception
    {
        SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder = setBaseModuleAggregation(baseModule);
        selectBuilder.select(fields);
        selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("ttime"), range.toString(), DateOperators.BETWEEN));
        if(cardContext.getCriteriaType() == V2MeasuresContext.Criteria_Type.CRITERIA.getIndex())
        {
            Criteria parent_criteria = V2AnalyticsOldUtil.setFieldInCriteria(cardContext.getCriteria(), parentModuleForCriteria);
            if (parent_criteria != null) {
                V2AnalyticsOldUtil.applyJoin(null, baseModule, new StringBuilder(parentModuleIdField.getCompleteColumnName()).append(" = ").append(child_field.getCompleteColumnName()).toString(), parentModuleForCriteria, selectBuilder);
                selectBuilder.andCriteria(parent_criteria);
            }
        }
        selectBuilder.limit(50);
        return selectBuilder;
    }
    private Object fetchCardData(List<FacilioField> fields, FacilioModule baseModule, DateRange range, V2AnalyticsCardWidgetContext cardContext, Map<String, FacilioField> fieldMap, FacilioField parentModuleIdField, FacilioField child_field, FacilioModule parentModuleForCriteria)throws Exception
    {
        List<Map<String, Object>> props = null;
        SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder = this.fetchCardDataSelectBuilder(fields, baseModule, range, cardContext, fieldMap, parentModuleIdField, child_field, parentModuleForCriteria);
        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.CLICKHOUSE))
        {
            try {
                props = FacilioService.runAsServiceWihReturn(FacilioConstants.Services.CLICKHOUSE, () -> selectBuilder.getAsProps());
            }
            catch (Exception e)
            {
                LOGGER.debug("CLICKHOUSE SELECT BUILDER Object for getting card data ---" + selectBuilder);
                SelectRecordsBuilder<ModuleBaseWithCustomFields> mysql_selectBuilder = this.fetchCardDataSelectBuilder(fields, baseModule, range, cardContext, fieldMap, parentModuleIdField, child_field, parentModuleForCriteria);
                props = mysql_selectBuilder.getAsProps();
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
    private SelectRecordsBuilder<ModuleBaseWithCustomFields> setBaseModuleAggregation(FacilioModule baseModule)throws Exception
    {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder =  new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
                .module(baseModule) //Assuming X to be the base module
                .setAggregation();
        FacilioField marked = modBean.getField("marked", baseModule.getName());
        if(marked != null) {
            selectBuilder.andCondition(CriteriaAPI.getCondition(marked, "false", BooleanOperators.IS));
        }
        return selectBuilder;
    }

}
