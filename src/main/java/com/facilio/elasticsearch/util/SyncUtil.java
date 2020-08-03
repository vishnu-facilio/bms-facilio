package com.facilio.elasticsearch.util;

import com.facilio.beans.ModuleBean;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.elasticsearch.context.SyncContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

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
            long id = builder.insert(FieldUtil.getAsProperties(syncContext));
            syncContext.setId(id);
        }
        else {
            GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                    .table(ModuleFactory.getESSyncContextModule().getTableName())
                    .fields(FieldFactory.getESSyncContextFields())
                    .andCondition(CriteriaAPI.getIdCondition(syncContext.getId(), ModuleFactory.getESSyncContextModule()));
            builder.update(FieldUtil.getAsProperties(syncContext));
        }
    }

    public static List<SyncContext> getAllSync() throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getESSyncContextModule().getTableName())
                .select(FieldFactory.getESSyncContextFields());
        List<SyncContext> syncContexts = FieldUtil.getAsBeanListFromMapList(builder.get(), SyncContext.class);
        if (CollectionUtils.isNotEmpty(syncContexts)) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            for (SyncContext syncContext : syncContexts) {
                syncContext.setSyncModule(modBean.getModule(syncContext.getSyncModuleId()));
            }
        }
        return syncContexts;
    }

    public static List<FacilioModule> getModulesToSearch() throws Exception {
        List<SyncContext> allSync = getAllSync();
        if (CollectionUtils.isNotEmpty(allSync)) {
            return allSync.stream().map(SyncContext::getSyncModule).collect(Collectors.toList());
        }
        return null;
    }

    public static void deleteSyncContext(Long id) throws Exception {
        if (id == null) {
            return;
        }

        GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
                .table(ModuleFactory.getESSyncContextModule().getTableName())
                .andCondition(CriteriaAPI.getIdCondition(id, ModuleFactory.getESSyncContextModule()));
        builder.delete();
    }
}
