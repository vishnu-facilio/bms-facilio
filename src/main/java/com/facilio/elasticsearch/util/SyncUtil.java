package com.facilio.elasticsearch.util;

import com.facilio.beans.ModuleBean;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.elasticsearch.context.SyncContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class SyncUtil {

    public static SyncContext getSyncContext(Long moduleId) throws Exception {
        if (moduleId == null) {
            return null;
        }

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getESSyncContextModule().getTableName())
                .select(FieldFactory.getESSyncContextFields())
                .andCondition(CriteriaAPI.getCondition("SYNC_MODULE_ID", "syncModuleId",
                        String.valueOf(moduleId), NumberOperators.EQUALS));
        SyncContext syncContext = FieldUtil.getAsBeanFromMap(builder.fetchFirst(), SyncContext.class);
        if (syncContext != null) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            syncContext.setSyncModule(modBean.getModule(syncContext.getSyncModuleId()));
        }
        return syncContext;
    }

    public static void updateSync(SyncContext syncContext) throws Exception {
        if (syncContext == null) {
            return;
        }

        if (syncContext.getId() == null) {
            GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                    .table(ModuleFactory.getESSyncContextModule().getTableName())
                    .fields(FieldFactory.getESSyncContextFields());
            builder.insert(FieldUtil.getAsProperties(syncContext));
        }
        else {
            GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                    .table(ModuleFactory.getESSyncContextModule().getTableName())
                    .fields(FieldFactory.getESSyncContextFields())
                    .andCondition(CriteriaAPI.getIdCondition(syncContext.getId(), ModuleFactory.getESSyncContextModule()));
            builder.update(FieldUtil.getAsProperties(syncContext));
        }
    }
}
