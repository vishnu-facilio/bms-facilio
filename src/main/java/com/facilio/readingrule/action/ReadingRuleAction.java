package com.facilio.readingrule.action;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.ReadingAlarm;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.connected.ResourceType;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.readingrule.rca.context.RCAScoreReadingContext;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import com.facilio.storm.InstructionType;
import com.facilio.v3.V3Action;
import com.facilio.workflowv2.util.WorkflowV2Util;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.*;
import java.util.stream.Collectors;

import static com.facilio.bmsconsole.util.NewAlarmAPI.getReadingAlarms;
import static com.facilio.connected.CommonConnectedUtil.postConRuleHistoryInstructionToStorm;
import static com.facilio.readingrule.rca.util.ReadingRuleRcaAPI.getAlarmDurationAndCount;


@Log4j
@Setter
@Getter
public class ReadingRuleAction extends V3Action {
    private String moduleName;
    private Long alarmId;
    private Integer dateOperator;
    private String dateOperatorValue;
    private String filters;
    private Integer resourceType;
    List<Object> paramList;
    String widgetName;

    public String rcaRuleList() throws Exception {
        resourceType = resourceType == null ? ResourceType.ASSET_CATEGORY.getIndex() : resourceType;
        FacilioChain chain = TransactionChainFactory.fetchRcaRules();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.ReadingRules.RESOURCE_TYPE, ResourceType.valueOf(resourceType));
        chain.execute();
        List<NewReadingRuleContext> newReadingRuleContext = (List<NewReadingRuleContext>) context.get(FacilioConstants.ReadingRules.NEW_READING_RULE_LIST);
        setData("result", newReadingRuleContext);
        return SUCCESS;
    }

    public String rcaReadingsFetch() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.fetchRcaReadingsChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID, alarmId);
        context.put(FacilioConstants.ContextNames.PAGE, getPage());
        context.put(FacilioConstants.ContextNames.PER_PAGE, getPerPage());
        context.put(FacilioConstants.ContextNames.DATE_OPERATOR, getDateOperator());
        context.put(FacilioConstants.ContextNames.DATE_OPERATOR_VALUE, getDateOperatorValue());
        context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ReadingRules.RCA.RCA_SCORE_READINGS_MODULE);
        String filters = getFilters();
        if (filters != null && !filters.isEmpty()) {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(filters);
            context.put(FacilioConstants.ContextNames.FILTERS, json);
        }
        chain.execute();
        List<RCAScoreReadingContext> rcaScoreReadingContexts = (List<RCAScoreReadingContext>) context.get(FacilioConstants.ReadingRules.RCA.RCA_SCORE_READINGS);
        JSONObject result = new JSONObject();
        result.put(FacilioConstants.ContextNames.RECORD_LIST, rcaScoreReadingContexts);
        result.put(FacilioConstants.ContextNames.RECORD_COUNT, context.get(FacilioConstants.ContextNames.COUNT));
        setData(FacilioConstants.ContextNames.RESULT, result);
        setData("result", result);
        return SUCCESS;
    }

    private Long recordId;
    private Long startTime;
    private Long endTime;
    private List<Long> assetIds;

    public String runHistorical() throws Exception {
        NewReadingRuleContext rule = NewReadingRuleAPI.getReadingRules(Collections.singletonList(getRecordId())).get(0);
        postConRuleHistoryInstructionToStorm(Collections.singletonList(rule), startTime, endTime, getAssetIds(), false, InstructionType.READING_RULE_HISTORICAL, false);
        setData("success", "Instruction Processing has begun");

        return SUCCESS;
    }

    private Long ruleId;

    public String rootCauseFetch() throws Exception {
        FacilioChain chain = TransactionChainFactory.fetchRootCause();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.RULE_ID, getRuleId());
        context.put(FacilioConstants.ContextNames.PAGE, getPage());
        context.put(FacilioConstants.ContextNames.PER_PAGE, getPerPage());
        chain.execute();
        List<Map<String, Object>> ruleDetails = (List<Map<String, Object>>) context.get(FacilioConstants.ReadingRules.RCA.RCA_RULE_DETAILS);
        JSONObject result = new JSONObject();
        result.put(FacilioConstants.ContextNames.RECORD_LIST, ruleDetails);
        result.put(FacilioConstants.ContextNames.RECORD_COUNT, context.get(FacilioConstants.ContextNames.COUNT));
        setData("result", result);
        return SUCCESS;
    }

    public String fetchImpactFieldIds() throws Exception {
        FacilioChain chain = TransactionChainFactory.getImpactFieldsForRuleChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.RULE_ID, getRuleId());
        chain.execute();
        Map<String, Long> impactFieldIds = (Map<String, Long>) context.get(FacilioConstants.ContextNames.RECORD);
        JSONObject result = new JSONObject();
        result.put(FacilioConstants.ReadingRules.COST_IMPACT_ID, impactFieldIds.get(FacilioConstants.ReadingRules.COST_IMPACT_ID));
        result.put(FacilioConstants.ReadingRules.ENERGY_IMPACT_ID, impactFieldIds.get(FacilioConstants.ReadingRules.ENERGY_IMPACT_ID));
        setData(FacilioConstants.ContextNames.RESULT, result);
        return SUCCESS;
    }

    private Long parentId;
    Boolean isCostImpact;


    public String impactFetch() throws Exception {
        FacilioChain chain = TransactionChainFactory.getImpactForRuleChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.RULE_ID, getRuleId());
        context.put(FacilioConstants.ContextNames.PARENT_ID, getParentId());
        context.put("isCostImpact", getIsCostImpact());
        chain.execute();
        JSONObject result = new JSONObject();
        result.put("lastMonth", context.getOrDefault("lastMonth", 0D));
        result.put("thisMonth", context.getOrDefault("thisMonth", 0D));
        setData("result", result);
        return SUCCESS;
    }

    public String getFddDefaultWorkflow() throws Exception {
        FacilioChain chain = TransactionChainFactory.getFDDDefaultWorkFlowChain();

        FacilioContext context = chain.getContext();
        context.put(WorkflowV2Util.WORKFLOW_PARAMS, paramList);
        context.put(FacilioConstants.ContextNames.MODULE_NAME,moduleName);
        context.put(FacilioConstants.ContextNames.WIDGET_NAME,widgetName);

        chain.execute();

        setData(WorkflowV2Util.WORKFLOW_CONTEXT, context.get(WorkflowV2Util.WORKFLOW_CONTEXT));
        setData(WorkflowV2Util.WORKFLOW_SYNTAX_ERROR, context.get(WorkflowV2Util.WORKFLOW_SYNTAX_ERROR));
        return SUCCESS;
    }

    public String getRuleInsights() throws Exception {

        List<ReadingAlarm> readingAlarms = getReadingAlarms(getRuleId());
        if(CollectionUtils.isEmpty(readingAlarms)){
            return SUCCESS;
        }
        List<Map<String, Object>> alarmDurationAndCount = getAlarmDurationAndCount(readingAlarms.stream().map(ModuleBaseWithCustomFields::getId).collect(Collectors.toList()), startTime, endTime);

        Map<Long, Map<String, Object>> result = Optional.ofNullable(alarmDurationAndCount).orElseGet(ArrayList::new).stream().collect(Collectors.toMap(map -> {
            Map<String, Object> m = (Map<String, Object>) map.get("resource");
            return (Long) m.get("id");
        }, map -> map));
        for (Map.Entry<Long, Map<String, Object>> entry : result.entrySet()) {
            Long resId = entry.getKey();
            Map<String, Object> resMap = entry.getValue();
            resMap.put("booleanChartData", NewReadingRuleAPI.getBooleanChartData(getRuleId(), resId, getStartTime(), getEndTime()));
        }
        if (!result.isEmpty()) {
            addAssetNames(result);
        }
        setData("result", result);
        return SUCCESS;
    }

    public static void addAssetNames(Map<Long, Map<String, Object>> result) throws Exception {

        Set<Long> assetIds = result.keySet();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET);
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));
        SelectRecordsBuilder<AssetContext> selectBuilder = new SelectRecordsBuilder<AssetContext>()
                .moduleName(module.getName())
                .beanClass(AssetContext.class)
                .select(Arrays.asList(FieldFactory.getIdField(module), fieldsMap.get("name")))
                .table(module.getTableName())
                .andCondition(CriteriaAPI.getIdCondition(assetIds, module));

        Optional.ofNullable(selectBuilder.get())
                .orElseGet(ArrayList::new).forEach(asset -> {
                    Map<String, Object> resMap = result.get(asset.getId());
                    resMap.put("assetName", asset.getName());
                });
    }

}
