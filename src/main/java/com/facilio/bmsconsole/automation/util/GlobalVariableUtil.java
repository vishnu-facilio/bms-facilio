package com.facilio.bmsconsole.automation.util;

import com.facilio.bmsconsole.automation.context.GlobalVariableContext;
import com.facilio.bmsconsole.automation.context.GlobalVariableGroupContext;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

import java.util.List;

public class GlobalVariableUtil {

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
}
