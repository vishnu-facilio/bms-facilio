package com.facilio.bmsconsole.automation.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.automation.context.GlobalVariableContext;
import com.facilio.bmsconsole.automation.context.GlobalVariableGroupContext;
import com.facilio.cache.CacheUtil;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.cache.FacilioCache;
import com.facilio.fw.cache.LRUCache;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GlobalVariableUtil {

    private static final Logger LOGGER = LogManager.getLogger(GlobalVariableUtil.class.getName());

    public static List<GlobalVariableGroupContext> getAllGlobalVariableGroups() throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getGlobalVariableGroupModule().getTableName())
                .select(FieldFactory.getGlobalVariableGroupFields());
        List<GlobalVariableGroupContext> list = FieldUtil.getAsBeanListFromMapList(builder.get(), GlobalVariableGroupContext.class);
        return list;
    }

    public static GlobalVariableGroupContext getVariableGroup(long id) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getGlobalVariableGroupModule().getTableName())
                .select(FieldFactory.getGlobalVariableGroupFields())
                .andCondition(CriteriaAPI.getIdCondition(id, ModuleFactory.getGlobalVariableGroupModule()));
        GlobalVariableGroupContext variableGroup = FieldUtil.getAsBeanFromMap(builder.fetchFirst(), GlobalVariableGroupContext.class);
        return variableGroup;
    }
    public static GlobalVariableGroupContext getVariableGroupForLinkName(String linkName) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getGlobalVariableGroupModule().getTableName())
                .select(FieldFactory.getGlobalVariableGroupFields())
                .andCondition(CriteriaAPI.getCondition("LINK_NAME","linkName", linkName, StringOperators.IS));
        GlobalVariableGroupContext variableGroup = FieldUtil.getAsBeanFromMap(builder.fetchFirst(), GlobalVariableGroupContext.class);
        return variableGroup;
    }

    /**
     * Deletes variable group, along with variables assigned with this group.
     * @param id
     * @throws Exception
     */
    public static void deleteVariableGroup(Long id) throws Exception {
        GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
                .table(ModuleFactory.getGlobalVariableGroupModule().getTableName())
                .andCondition(CriteriaAPI.getIdCondition(id, ModuleFactory.getGlobalVariableGroupModule()));
        builder.delete();
    }

    public static void deleteVariable(Long id) throws Exception {
        GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
                .table(ModuleFactory.getGlobalVariableModule().getTableName())
                .andCondition(CriteriaAPI.getIdCondition(id, ModuleFactory.getGlobalVariableModule()));
        builder.delete();
    }

    public static List<GlobalVariableContext> getAllGlobalVariables(Long groupId) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getGlobalVariableModule().getTableName())
                .select(FieldFactory.getGlobalVariableFields())
                .andCondition(CriteriaAPI.getCondition("GROUP_ID", "groupId", String.valueOf(groupId), NumberOperators.EQUALS));
        List<GlobalVariableContext> list = FieldUtil.getAsBeanListFromMapList(builder.get(), GlobalVariableContext.class);
        return list;
    }
    public static List<GlobalVariableContext> getAllGlobalVariables() throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getGlobalVariableModule().getTableName())
                .select(FieldFactory.getGlobalVariableFields());
        List<GlobalVariableContext> list = FieldUtil.getAsBeanListFromMapList(builder.get(), GlobalVariableContext.class);
        return list;
    }

    public static Map<String, Map<String, Object>> getLiveVariables() {
        try {
            FacilioCache<String, Map<String, Map<String, Object>>> globalVariableCache = LRUCache.getGlobalVariableCache();
            String orgKey = CacheUtil.ORG_KEY(AccountUtil.getCurrentOrg().getOrgId());
            Map<String, Map<String, Object>> globalVariableMap = globalVariableCache.get(orgKey);
            if (globalVariableMap == null) {
                globalVariableMap = getLiveVariablesFromDB();
                globalVariableCache.put(orgKey, globalVariableMap);
            }
            return globalVariableMap;
        } catch (Exception ex) {
            LOGGER.error("Error in getting live variables", ex);
            return null;
        }
    }

    private static Map<String, Map<String, Object>> getLiveVariablesFromDB() throws Exception {
        Map<String, Map<String, Object>> allVariableMap = new HashMap<>();

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getGlobalVariableGroupModule().getTableName())
                .select(FieldFactory.getGlobalVariableGroupFields());
        List<GlobalVariableGroupContext> groupList = FieldUtil.getAsBeanListFromMapList(builder.get(), GlobalVariableGroupContext.class);
        if (CollectionUtils.isNotEmpty(groupList)) {
            Map<Long, String> variableGroupMap = groupList.stream().collect(Collectors.toMap(GlobalVariableGroupContext::getId, GlobalVariableGroupContext::getLinkName));

            GenericSelectRecordBuilder variableBuilder = new GenericSelectRecordBuilder()
                    .table(ModuleFactory.getGlobalVariableModule().getTableName())
                    .select(FieldFactory.getGlobalVariableFields());
            List<GlobalVariableContext> variables = FieldUtil.getAsBeanListFromMapList(variableBuilder.get(), GlobalVariableContext.class);
            if (CollectionUtils.isNotEmpty(variables)) {
                for (GlobalVariableContext variableContext : variables) {
                    String groupLinkName = variableGroupMap.get(variableContext.getGroupId());
                    if (!allVariableMap.containsKey(groupLinkName)) {
                        allVariableMap.put(groupLinkName, new HashMap<>());
                    }
                    Map<String, Object> variableMap = allVariableMap.get(groupLinkName);
                    variableMap.put(variableContext.getLinkName(), variableContext.getValue());
                }
            }
        }
        return allVariableMap;
    }
}
