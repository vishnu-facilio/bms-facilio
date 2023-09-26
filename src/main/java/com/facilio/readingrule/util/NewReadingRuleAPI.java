package com.facilio.readingrule.util;


import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.WorkflowRuleLoggerContext;
import com.facilio.bmsconsole.enums.RuleJobType;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.bmsconsole.util.WorkflowRuleLoggerAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.connected.CommonConnectedUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.ns.NamespaceAPI;
import com.facilio.ns.context.NSType;
import com.facilio.ns.context.NameSpaceCacheContext;
import com.facilio.ns.context.NameSpaceContext;
import com.facilio.ns.context.NameSpaceField;
import com.facilio.readingkpi.context.IConnectedRule;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.readingrule.context.RuleAlarmDetails;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import lombok.NonNull;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Log4j
public class NewReadingRuleAPI {

    // TODO refactor the entire api for NewReadingRules module
    public static class RuleReadingsConstant {
        public static String RULE_READING_RESULT = "ruleResult";
        public static String RULE_READING_INFO = "info";
        public static String RULE_READING_ENERGY_IMPACT = "energyImpact";
        public static String RULE_READING_COST_IMPACT = "costImpact";
    }

    public static final String READING_RULE_FIELD_TABLE_NAME = "Rule_Readings";

    private static void updateModuleAndFields(NewReadingRuleContext ruleCtx) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule ruleModule = modBean.getModule(ruleCtx.getReadingModuleId());
        List<FacilioField> field = modBean.getAllFields(ruleModule.getName());
        List<FacilioField> customFields = field.stream().filter(f -> f.getDisplayName().equals(ruleCtx.getName())).collect(Collectors.toList());
        if (!customFields.isEmpty()) {
            ruleCtx.setReadingField(customFields.get(0));
            ruleCtx.setReadingFieldId(customFields.get(0).getFieldId());
        }
    }

    public static List<NewReadingRuleContext> getRules() throws Exception {
        return getRules(new Condition[]{});
    }

    public static List<NewReadingRuleContext> getRules(@NonNull Condition[] conditions) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        GenericSelectRecordBuilder select = new GenericSelectRecordBuilder();
        select.select(modBean.getModuleFields(FacilioConstants.ReadingRules.NEW_READING_RULE))
                .table(modBean.getModule(FacilioConstants.ReadingRules.NEW_READING_RULE).getTableName());
        if (conditions.length > 0) {
            Arrays.stream(conditions).forEach(c -> select.andCondition(c));
        }
        List<Map<String, Object>> maps = select.get();
        if (maps == null) {
            return null;
        }
        return getReadingRulesFromMap(maps);
    }

    public static List<NewReadingRuleContext> getRules(Collection<Long> ruleIds) throws Exception {
        GenericSelectRecordBuilder select = new GenericSelectRecordBuilder();
        select.select(FieldFactory.getNewReadingRuleFields())
                .table(ModuleFactory.getNewReadingRuleModule().getTableName())
                .andCondition(CriteriaAPI.getIdCondition(ruleIds, ModuleFactory.getNewReadingRuleModule()));

        List<Map<String, Object>> maps = select.get();
        if (CollectionUtils.isEmpty(maps)) {
            return null;
        }
        return getReadingRulesFromMap(maps);
    }

    public static List<NewReadingRuleContext> getReadingRulesFromMap(@NonNull List<Map<String, Object>> rulesMap) throws Exception {
        return getReadingRulesFromMap(rulesMap, true);
    }

    public static List<NewReadingRuleContext> getReadingRulesFromMap(@NonNull List<Map<String, Object>> rulesMap, boolean fetchChildren) throws Exception {
        List<NewReadingRuleContext> rules = new ArrayList<>();
        for (Map<String, Object> ruleProps : rulesMap) {
            NewReadingRuleContext readingRule = FieldUtil.getAsBeanFromMap(ruleProps, NewReadingRuleContext.class);
            updateModuleAndFields(readingRule);
            if (fetchChildren) {
                fetchAndUpdateAlarmDetails(readingRule);
                updateNamespaceAndFields(readingRule);
                // updateWorkflow(readingRule);
//                updateFaultImpact(readingRule);
            }
            rules.add(readingRule);
        }
        return rules;
    }

    private static void updateNamespaceAndFields(NewReadingRuleContext readingRule) throws Exception {
        NameSpaceContext nsCtx = NamespaceAPI.getNameSpaceByRuleId(readingRule.getId(), NSType.READING_RULE);
        readingRule.setAssets(NamespaceAPI.getMatchedResources(nsCtx, readingRule));
        readingRule.setNs(nsCtx);
    }

    public static void fetchAndUpdateAlarmDetails(NewReadingRuleContext readingRule) throws Exception {
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder();
        selectBuilder.select(FieldFactory.getRuleAlarmDetailsFields())
                .table(ModuleFactory.getRuleAlarmDetailsModule().getTableName()) //Table:New_Reading_Rule_AlarmDetails
                .andCondition(CriteriaAPI.getCondition("RULE_ID", "ruleId", String.valueOf(readingRule.getId()), NumberOperators.EQUALS));
        Map<String, Object> props = selectBuilder.fetchFirst();
        if (props != null) {
            RuleAlarmDetails alarmDetails = FieldUtil.getAsBeanFromMap(props, RuleAlarmDetails.class);
            alarmDetails.setSeverity(AlarmAPI.getAlarmSeverity(alarmDetails.getSeverityId()).getSeverity());
            readingRule.setAlarmDetails(alarmDetails);
        }
    }

    public static NewReadingRuleContext getRule(Long ruleId) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        List<NewReadingRuleContext> rules = getRules(new Condition[]{
                CriteriaAPI.getIdCondition(ruleId, modBean.getModule(FacilioConstants.ReadingRules.NEW_READING_RULE))});
        if (CollectionUtils.isNotEmpty(rules)) {
            return rules.get(0);
        }
        throw new IllegalArgumentException("Invalid Rule Id");
    }

    public static List<NewReadingRuleContext> getAllRules(Map<String, Object> paramsMap) throws Exception {
        String moduleName = FacilioConstants.ReadingRules.NEW_READING_RULE;
        String search = null;
        int page = 0, perPage = 50;
        String orderBy = null, orderType = null;
        if (paramsMap != null) {
            page = (int) paramsMap.get("page");
            perPage = (int) paramsMap.get("perPage");
            search = (String) paramsMap.get("search");
            if (paramsMap.containsKey("orderBy")) {
                orderBy = (String) paramsMap.get("orderBy");
                orderType = (String) paramsMap.get("orderType");
            }
        }
        FacilioContext fetch = V3Util.fetchList(moduleName, true, null, null, false, null, orderBy, orderType, search, page, perPage, true, null, null, null);
        Map<String, Object> newReadingRuleContexts = (Map<String, Object>) fetch.get(Constants.RECORD_MAP);

        List<NewReadingRuleContext> rules = (List<NewReadingRuleContext>) newReadingRuleContexts.get(moduleName);
        return rules;
    }

    public static List<NewReadingRuleContext> getReadingRules(List<Long> recordId) throws Exception {
        String moduleName = FacilioConstants.ReadingRules.NEW_READING_RULE;
        FacilioContext summary = V3Util.getSummary(moduleName, recordId);
        List<NewReadingRuleContext> readingRules = Constants.getRecordListFromContext(summary, moduleName);
        if (CollectionUtils.isNotEmpty(readingRules)) {
            return readingRules;
        }
        throw new IllegalArgumentException("No such rules");
    }

    // only id, fieldId and ns will be set in the context, this is used in storm exec, to form dependency graph
    public static List<IConnectedRule> getReadingRules(Map<Long, Long> ruleIdVsNsId) throws Exception {
        Map<Long, Long> ruleIdVsFieldId = getRuleIdVsReadingFieldId(new ArrayList<>(ruleIdVsNsId.keySet()));
        List<IConnectedRule> rules = new ArrayList<>();
        for (Map.Entry<Long, Long> entry : ruleIdVsNsId.entrySet()) {
            NameSpaceCacheContext ns = Constants.getNsBean().getNamespace(entry.getValue());
            Long readingFieldId = ruleIdVsFieldId.get(entry.getValue());
            rules.add(new NewReadingRuleContext(entry.getKey(), readingFieldId, ns));
        }
        return rules;
    }


    public static Map<String, Object> getMatchedResourcesWithCount(NewReadingRuleContext readingRule) throws Exception {
        Map<String, Object> resourcesWithCount = new HashMap<>();
        List<Long> matchedResourceIds = NamespaceAPI.fetchMatchedResourceIds(readingRule.getNs().getId());
        resourcesWithCount.put("count", matchedResourceIds.size());
        resourcesWithCount.put("resourceIds", matchedResourceIds);

        return resourcesWithCount;
    }

    public static List<Long> getRCARulesForReadingRule(Long ruleId) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getReadingRuleRCAMapping().getTableName())
                .select(FieldFactory.getReadingRuleRCAMapping())
                .andCondition(CriteriaAPI.getCondition("RULE_ID", "ruleId", String.valueOf(ruleId), NumberOperators.EQUALS));
        List<Map<String, Object>> resList = builder.get();
        resList = resList == null ? new ArrayList<>() : resList;

        List<Long> rcaRuleIds = resList.stream().map(el -> (Long) el.get("rcaRuleId")).collect(Collectors.toList());
        return rcaRuleIds;
    }

    public static List<NewReadingRuleContext> destructureRuleFromRecordMap(Context context) {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<NewReadingRuleContext> readingRules = (List<NewReadingRuleContext>) recordMap.get(moduleName);
        if (CollectionUtils.isNotEmpty(readingRules)) {
            return readingRules;
        }
        return new ArrayList<>();
    }

    public static Map<Long, String> getReadingRuleNamesByIds(List<Long> ruleIds) throws Exception {

        Map<Long, String> nameMap = new HashMap<>();
        List<NewReadingRuleContext> resList = getReadingRules(ruleIds);
        if (resList != null) {
            for (NewReadingRuleContext props : resList) {
                nameMap.put(props.getId(), props.getName());
            }
        }
        return nameMap;
    }

    public static Long getPrimaryFieldId(NameSpaceContext ns) {
        List<NameSpaceField> fields = ns.getFields();
        if (CollectionUtils.isNotEmpty(fields)) {
            for (NameSpaceField field : fields) {
                if (field.getResourceId() == null || field.getResourceId() == -1) {
                    return field.getFieldId();
                }
            }
            return fields.get(0).getFieldId(); //As default returns first field id. This is hack for now. Need to define primary field flag for fields.
        }
        return -1L;
    }

    public static void setModuleNameForCriteria(Criteria criteria, String moduleName) {
        Map<String, Condition> conditions = criteria.getConditions();
        for (Map.Entry<String, Condition> entry : conditions.entrySet()) {
            entry.getValue().setModuleName(moduleName);
        }
    }

    public static Map<Long, Map<String, Object>> getReadingRuleNameAndImpactByIds(List<Long> ruleIds) throws Exception {

        Map<Long, Map<String, Object>> nameMap = new HashMap<>();
        List<NewReadingRuleContext> resList = getReadingRules(ruleIds);
        if (resList != null) {
            for (NewReadingRuleContext props : resList) {
                Map<String, Object> prop = new HashMap<>();
                prop.put("name", props.getName());
                prop.put("impact", props.getImpact());
                nameMap.put(props.getId(), prop);
            }
        }
        return nameMap;
    }


    public static Map<Long, Long> getRuleIdVsReadingFieldId(List<Long> ruleIds) throws Exception {
        return getReadingFieldIdVsRuleId(ruleIds).entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
    }

    public static Map<Long, Long> getReadingFieldIdVsRuleId(List<Long> ruleIds) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule ruleModule = modBean.getModule(FacilioConstants.ReadingRules.NEW_READING_RULE);
        FacilioField fieldIdField = FieldFactory.getNumberField("readingFieldId", "READING_FIELD_ID", ruleModule);

        SelectRecordsBuilder<NewReadingRuleContext> selectRecordsBuilder = new SelectRecordsBuilder<NewReadingRuleContext>()
                .module(ruleModule)
                .beanClass(NewReadingRuleContext.class)
                .select(Collections.singletonList(fieldIdField));

        if (CollectionUtils.isNotEmpty(ruleIds)) {
            selectRecordsBuilder.andCondition(CriteriaAPI.getIdCondition(ruleIds, ruleModule));
        }
        return selectRecordsBuilder.get().stream().collect(Collectors.toMap(NewReadingRuleContext::getReadingFieldId, NewReadingRuleContext::getId));
    }

    public static long insertLog(Long ruleId, Long intervalStartTime, Long intervalEndTime, Integer resourceCount) throws Exception {
        WorkflowRuleLoggerContext ruleLogger = new WorkflowRuleLoggerContext();
        ruleLogger.setRuleId(ruleId);
        ruleLogger.setNoOfResources(resourceCount);
        ruleLogger.setResolvedResourcesCount(0L);
        ruleLogger.setStatus(WorkflowRuleLoggerContext.Status.IN_PROGRESS.getIndex());
        ruleLogger.setRuleJobType(RuleJobType.READING_ALARM.getIndex());
        ruleLogger.setStartTime(intervalStartTime);
        ruleLogger.setEndTime(intervalEndTime);
        ruleLogger.setCreatedBy(AccountUtil.getCurrentUser().getId());
        ruleLogger.setCreatedTime(DateTimeUtil.getCurrenTime(new Boolean[0]));
        ruleLogger.setCalculationStartTime(DateTimeUtil.getCurrenTime(new Boolean[0]));
        WorkflowRuleLoggerAPI.addWorkflowRuleLogger(ruleLogger);
        return ruleLogger.getId();
    }

    public static Map<String, Long> getImpactFieldIdsForRule(Long ruleId) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        Map<String, Long> resultMap = new HashMap<>();
        List<NewReadingRuleContext> rules = getReadingRules(Collections.singletonList(ruleId));
        for (NewReadingRuleContext rule : rules) {
            FacilioModule module = modBean.getModule(rule.getReadingModuleId());
            List<FacilioField> fields = modBean.getAllFields(module.getName());
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
            resultMap.put(FacilioConstants.ReadingRules.COST_IMPACT_ID, fieldMap.get("costImpact").getFieldId());
            resultMap.put(FacilioConstants.ReadingRules.ENERGY_IMPACT_ID, fieldMap.get("energyImpact").getFieldId());
        }
        return resultMap;
    }

    public static List<Long[]> getBooleanChartData(Long ruleId, Long resourceId, Long startTime, Long endTime) throws Exception {
        IConnectedRule rule = CommonConnectedUtil.fetchConnectedRule(ruleId, NSType.READING_RULE);
        ModuleBean modBean = Constants.getModBean();
        FacilioField result = modBean.getField(rule.getReadingFieldId());
        FacilioModule readingModule = modBean.getModule(result.getModuleId());
        Long readingModuleId = readingModule.getModuleId();
        FacilioField ttime = FieldFactory.getField("ttime", "TTIME", FieldType.NUMBER);

        String ruleReadingsTableName = NewReadingRuleAPI.READING_RULE_FIELD_TABLE_NAME;
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .select(Arrays.asList(ttime, result))
                .table(ruleReadingsTableName)
                .andCondition(CriteriaAPI.getCondition(ruleReadingsTableName + ".PARENT_ID", "parentId", resourceId.toString(), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("MODULEID", "moduleId", String.valueOf(readingModuleId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("TTIME", "ttime", startTime + "," + endTime, DateOperators.BETWEEN))
                .orderBy("TTIME");
        List<Map<String, Object>> maps = builder.get();

        ReadingDataMeta rdm = ReadingsAPI.getReadingDataMeta(resourceId, result);
        List<Long[]> data = new ArrayList<>();
        if (CollectionUtils.isEmpty(maps)) {
            if ((Boolean) rdm.getValue()) {
                Long currentTime = DateTimeUtil.utcTimeToOrgTime(System.currentTimeMillis());
                data.add(new Long[]{startTime, currentTime, currentTime - startTime});
            }
            return data;
        }

        Boolean lastValue = null;
        boolean started = false;
        Long[] p = new Long[3];
        for (Map<String, Object> map : maps) {
            Boolean ruleResult = (Boolean) map.get("ruleResult");
            Long ttimeVal = (Long) map.get("ttime");
            if (started) {
                if (ruleResult != lastValue) {
                    if (ruleResult && p[0] == null) {
                        p[0] = ttimeVal;
                    } else {
                        p[1] = ttimeVal;
                        p[2] = p[1] - p[0];
                        data.add(p);
                        p = new Long[3];
                    }
                }
            } else if (ruleResult) {
                p[0] = ttimeVal;
                started = true;
            }
            lastValue = ruleResult;
        }
        if (p[0] != null && p[1] == null && Boolean.TRUE.equals(lastValue)) {
            p[1] = System.currentTimeMillis();
            p[2] = p[1] - p[0];
            data.add(p);
        }
        return data;
    }
}
