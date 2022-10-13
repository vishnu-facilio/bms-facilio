package com.facilio.readingrule.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AlarmSeverityContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
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
import com.facilio.readingrule.faultimpact.FaultImpactAPI;
import com.facilio.readingrule.faultimpact.FaultImpactContext;
import com.facilio.relation.context.RelationMappingContext;
import com.facilio.relation.util.RelationUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import lombok.NonNull;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Log4j
public class NewReadingRuleAPI {

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
        GenericSelectRecordBuilder select = new GenericSelectRecordBuilder();
        select.select(FieldFactory.getNewReadingRuleFields())
                .table(ModuleFactory.getNewReadingRuleModule().getTableName());
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
        readingRule.setAssets(NamespaceAPI.getMatchedResources(nsCtx,readingRule));
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

    public static NewReadingRuleContext getRuleForBuilderId(Long builderId) throws Exception {
        List<NewReadingRuleContext> rulesForBuilderId = getRulesByBuilderId(builderId);
        return CollectionUtils.isNotEmpty(rulesForBuilderId) ? rulesForBuilderId.get(0) : null;
    }

    public static List<NewReadingRuleContext> getRulesByBuilderId(Long builderId) throws Exception {
        FacilioModule builderConfigModule = ModuleFactory.getRuleBuilderConfigModule();
        FacilioModule ruleModule = ModuleFactory.getNewReadingRuleModule();
        GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
                .select(FieldFactory.getNewReadingRuleFields())
                .table(ruleModule.getTableName())
                .innerJoin(ModuleFactory.getRuleBuilderConfigModule().getTableName())
                .on(builderConfigModule.getTableName() + ".RULE_ID=" + ruleModule.getTableName() + ".ID")
                .andCondition(CriteriaAPI.getCondition(builderConfigModule.getTableName() + ".ID", "builderId", builderId.toString(), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("STATUS", "status", "true", BooleanOperators.IS));

        List<Map<String, Object>> maps = select.get();
        return (maps != null) ? getReadingRulesFromMap(maps) : null;
    }

    public static NewReadingRuleContext getRule(Long ruleId) throws Exception {
        List<NewReadingRuleContext> rules = getRules(new Condition[]{
                CriteriaAPI.getIdCondition(ruleId, ModuleFactory.getNewReadingRuleModule())});
        return CollectionUtils.isNotEmpty(rules) ? rules.get(0) : null;
    }

    public static List<NewReadingRuleContext> getAllRules() throws Exception {
        String moduleName = FacilioConstants.ReadingRules.NEW_READING_RULE;
        FacilioContext fetch=V3Util.fetchList(moduleName,true,null,null,false,null,null,null,null,0,50,false,null,null);
        Map<String,Object> newReadingRuleContexts= (Map<String, Object>) fetch.get(Constants.RECORD_MAP);

        List<NewReadingRuleContext> rules = (List<NewReadingRuleContext>) newReadingRuleContexts.get(moduleName);
        return rules;
    }

    public static List<NewReadingRuleContext> getReadingRules(List<Long> recordId) throws Exception {
        String moduleName = FacilioConstants.ReadingRules.NEW_READING_RULE;
        FacilioContext summary = V3Util.getSummary(moduleName, recordId);
        List<NewReadingRuleContext> readingRules = Constants.getRecordListFromContext(summary, moduleName);
        return readingRules;
    }

    public static NewReadingRuleContext getRuleByNSId(Long nsId) throws Exception {
        List<NewReadingRuleContext> rules = getRules(new Condition[]{
                CriteriaAPI.getCondition("NAMESPACE_ID", "nsId", String.valueOf(nsId), NumberOperators.EQUALS)});
        return CollectionUtils.isNotEmpty(rules) ? rules.get(0) : null;
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

        List<Long> rcaRuleIds = resList.stream().map(el -> (Long) el.get("rcaRule")).collect(Collectors.toList());
        return rcaRuleIds;
    }

    public static void addNamespaceFields(Long nsId, Map<Long, ResourceContext> assetsMap, List<NameSpaceField> fields) throws Exception {
        deleteFieldsIfAlreadyExists(nsId);
        List<Map<String, Object>> assetList = new ArrayList<>();

        for (NameSpaceField fld : fields) {
            Long resourceID = (fld.getResourceId() != null && fld.getResourceId() != -1) ? fld.getResourceId() : -1;
            if (resourceID == -1) {
                    if (fld.getRelMapContext() != null && fld.getRelMapContext().getMappingLinkName() != null) {
                        getRelationMappingFields(fld);
                        prepareNSField(fld, nsId, resourceID, false);
                        assetList.add(FieldUtil.getAsProperties(fld));
                    } else {
                        prepareNSField(fld, nsId, resourceID, true);
                        assetList.add(FieldUtil.getAsProperties(fld));
                    }

            } else {
                prepareNSField(fld, nsId, resourceID, false);
                assetList.add(FieldUtil.getAsProperties(fld));
            }
        }

        GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                .table(NamespaceModuleAndFieldFactory.getNamespaceFieldsModule().getTableName())
                .fields(NamespaceModuleAndFieldFactory.getNamespaceFieldFields())
                .addRecords(assetList);

        insertBuilder.save();
    }
    private static  void getRelationMappingFields(NameSpaceField nsField) throws Exception{
        RelationMappingContext mapping = RelationUtil.getRelationMapping(nsField.getRelMapContext().getMappingLinkName());
        nsField.setRelMapId(mapping.getId());
        nsField.setRelMapContext(mapping);
    }
    private static void deleteFieldsIfAlreadyExists(Long nsId) throws Exception {
        GenericDeleteRecordBuilder delBuilder = new GenericDeleteRecordBuilder()
                .table(NamespaceModuleAndFieldFactory.getNamespaceFieldsModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("NAMESPACE_ID", "nsId", String.valueOf(nsId), NumberOperators.EQUALS));
        delBuilder.delete();

    }

    private static void prepareNSField(NameSpaceField fld, long nsId, long resourceId, boolean isPrimary) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioField field = modBean.getField(fld.getFieldId());
        FacilioModule module = field.getModule();

        fld.setField(field);
        fld.setModule(module);
        fld.setNsId(nsId);
        fld.setResourceId(resourceId);
        fld.setPrimary(isPrimary);

    }

    public static Map<Long, String> getReadingRuleNamesByIds(List<Long> ruleIds) throws Exception {

        String s = "(" + StringUtils.join(ruleIds, ",") + ")";
        Map<Long, String> nameMap = new HashMap<>();

        List<NewReadingRuleContext> resList = getReadingRules(ruleIds);
        if (resList != null) {
            for (NewReadingRuleContext props : resList) {
                nameMap.put(props.getId(),  props.getName());
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
}
