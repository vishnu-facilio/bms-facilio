package com.facilio.readingrule.util;


import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import org.apache.commons.chain.Context;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.ns.NamespaceAPI;
import com.facilio.ns.context.NSType;
import com.facilio.ns.context.NameSpaceContext;
import com.facilio.ns.context.NameSpaceField;
import com.facilio.ns.factory.NamespaceModuleAndFieldFactory;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.readingrule.context.RuleAlarmDetails;
import com.facilio.relation.context.RelationMappingContext;
import com.facilio.relation.util.RelationUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import lombok.NonNull;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.common.protocol.types.Field;

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

    private static void fetchAndUpdateAlarmDetails(NewReadingRuleContext readingRule) throws Exception {
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
                CriteriaAPI.getIdCondition(ruleId,modBean.getModule(FacilioConstants.ReadingRules.NEW_READING_RULE))});
        return CollectionUtils.isNotEmpty(rules) ? rules.get(0) : null;
    }

    public static List<NewReadingRuleContext> getAllRules(Map<String, Object> paramsMap) throws Exception {
        String moduleName = FacilioConstants.ReadingRules.NEW_READING_RULE;
        String search = null;
        int page = 0, perPage = 50;
        String orderBy = null, orderType = null;
        if(paramsMap != null){
            page = (int) paramsMap.get("page");
            perPage = (int) paramsMap.get("perPage");
            search = (String) paramsMap.get("search");
            if (paramsMap.containsKey("orderBy")) {
                orderBy = (String) paramsMap.get("orderBy");
                orderType = (String) paramsMap.get("orderType");
            }
        }
        FacilioContext fetch = V3Util.fetchList(moduleName, true, null, null, false, null, orderBy, orderType, search, page, perPage, true, null, null);
        Map<String, Object> newReadingRuleContexts = (Map<String, Object>) fetch.get(Constants.RECORD_MAP);

        List<NewReadingRuleContext> rules = (List<NewReadingRuleContext>) newReadingRuleContexts.get(moduleName);
        return rules;
    }

    public static List<NewReadingRuleContext> getReadingRules(List<Long> recordId) throws Exception {
        String moduleName = FacilioConstants.ReadingRules.NEW_READING_RULE;
        FacilioContext summary = V3Util.getSummary(moduleName, recordId);
        List<NewReadingRuleContext> readingRules = Constants.getRecordListFromContext(summary, moduleName);
        return readingRules;
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

    public static Long getPrimaryFieldId(NewReadingRuleContext readingRule) {
        List<NameSpaceField> fields = readingRule.getNs().getFields();
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
}
