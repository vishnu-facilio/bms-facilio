package com.facilio.bmsconsole.automation.util;

import com.facilio.bmsconsole.automation.context.GlobalVariableGroupContext;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

import java.sql.SQLException;
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

    public static void addGlobalVariableGroup(GlobalVariableGroupContext variableGroup) throws Exception {
        GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getGlobalVariableGroupModule().getTableName())
                .fields(FieldFactory.getGlobalVariableGroupFields());
        long id = builder.insert(FieldUtil.getAsProperties(variableGroup));
        variableGroup.setId(id);
    }

    public static void updateGlobalVariableGroup(GlobalVariableGroupContext variableGroup) throws Exception {
        if (variableGroup.getId() <= 0) {
            throw new IllegalArgumentException("Cannot update the record");
        }
        GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getGlobalVariableGroupModule().getTableName())
                .fields(FieldFactory.getGlobalVariableGroupFields())
                .andCondition(CriteriaAPI.getIdCondition(variableGroup.getId(), ModuleFactory.getGlobalVariableGroupModule()));
        builder.update(FieldUtil.getAsProperties(variableGroup));
    }
}
