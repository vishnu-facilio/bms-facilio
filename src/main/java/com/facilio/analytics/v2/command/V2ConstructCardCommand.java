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
                    cardContext.getResult().put("value", this.setResultJson(range, result, field));
                    Object baseline_result = null;
                    if(cardContext.getBaseline() != null)
                    {
                        BaseLineContext baseline = BaseLineAPI.getBaseLine(cardContext.getBaseline());
                        baseline.setAdjustType(BaseLineContext.AdjustType.NONE);
                        DateRange baseline_range = baseline.calculateBaseLineRange(range, baseline.getAdjustTypeEnum());
                        cardContext.getTimeFilter().setBaselineRange(baseline_range);
                        baseline_result = this.fetchCardData(Collections.singletonList(aggr.getSelectField(field).clone()) ,baseModule, baseline_range, cardContext, fieldMap, parentModuleIdField, child_field, parentModuleForCriteria);
                        cardContext.getResult().put("baseline_value", this.setResultJson(baseline_range, baseline_result, field));
                    }
                    /**
                     * Select Builder construction to fetch card data ends here
                     */
                }
            }
        }
    }
    private Map<String, Object> setResultJson(DateRange range, Object value, FacilioField field)throws Exception
    {
        Map<String, Object> result_json = new HashMap<>();
        result_json.put("dataType", field.getDataTypeEnum());
        result_json.put("value", value);
        result_json.put("actualValue", value);
        if(range != null){
            result_json.put("dateRange", range);
        }
        if (field instanceof NumberField) {
            NumberField numberField = (NumberField)field;
            result_json.put("unit", numberField.getUnit());
            if(numberField.getUnitId() > 0) {
                result_json.put("value", UnitsUtil.convertToSiUnit(value, Unit.valueOf(numberField.getUnitId())));
            }
        }

//        if (field.getDataTypeEnum() == FieldType.BOOLEAN) {
//            if (value == true && field.get.trueVal != null) {
//                valueMap["value"] = fieldMapInfo.trueVal;
//            } else if (cardValue == false && fieldMapInfo.falseVal != null) {
//                valueMap["value"] = fieldMapInfo.falseVal;
//            }
//            if (baselineCardValue == true && fieldMapInfo.trueVal != null) {
//                baselineValueMap["value"] = fieldMapInfo.trueVal;
//            } else if (baselineCardValue == false && fieldMapInfo.falseVal != null) {
//                baselineValueMap["value"] = fieldMapInfo.falseVal;
//            }
//        } else if (enumMap != null) {
//            if (cardValue != null && enumMap.get(cardValue) != null) {
//                valueMap["value"] = enumMap.get(cardValue);
//            }
//            if (baselineCardValue != null && enumMap.get(baselineCardValue) != null) {
//                baselineValueMap["value"] = enumMap.get(baselineCardValue);
//            }
//        }
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
                selectBuilder.innerJoin(cardContext.getParentModuleName()).on(new StringBuilder(parentModuleIdField.getCompleteColumnName()).append(" = ").append(child_field.getCompleteColumnName()).toString());
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
