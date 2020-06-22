package com.facilio.bmsconsoleV3.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ClientContext;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.V3Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class V3RecordAPI {

    public static void addRecord(boolean isLocalIdNeeded, List<? extends V3Context> list, FacilioModule module, List<FacilioField> fields) throws Exception {
        addRecord(isLocalIdNeeded, list, module, fields, false);
    }

    public static Map<Long, List<UpdateChangeSet>> addRecord(boolean isLocalIdNeeded, List<? extends V3Context> list, FacilioModule module, List<FacilioField> fields, Boolean isChangeSetNeeded) throws Exception {

        InsertRecordBuilder insertRecordBuilder = new InsertRecordBuilder<>()
                .module(module)
                .fields(fields);
        if(isLocalIdNeeded) {
            insertRecordBuilder.withLocalId();
        }
        if (isChangeSetNeeded != null && isChangeSetNeeded) {
            insertRecordBuilder.withChangeSet();
        }

        insertRecordBuilder.ignoreSplNullHandling();
        insertRecordBuilder.addRecords(list);
        insertRecordBuilder.save();
        if (isChangeSetNeeded != null && isChangeSetNeeded) {
            return insertRecordBuilder.getChangeSet();
        }
        return null;
    }

    public static void updateRecord(V3Context data, FacilioModule module, List<FacilioField> fields) throws Exception {
        updateRecord(data, module, fields, false);
    }

    public static Map<Long, List<UpdateChangeSet>> updateRecord(V3Context data, FacilioModule module, List<FacilioField> fields, Boolean isChangeSetNeeded) throws Exception {
        UpdateRecordBuilder updateRecordBuilder = new UpdateRecordBuilder<ModuleBaseWithCustomFields>()
                .module(module)
                .fields(fields)
                .andCondition(CriteriaAPI.getIdCondition(data.getId(), module));
        if(isChangeSetNeeded != null && isChangeSetNeeded) {
            updateRecordBuilder.withChangeSet(ModuleBaseWithCustomFields.class);
        }
        updateRecordBuilder.ignoreSplNullHandling();
        updateRecordBuilder.update(data);
        if (isChangeSetNeeded != null && isChangeSetNeeded) {
            return updateRecordBuilder.getChangeSet();
        }

        return null;

    }

    public static boolean checkChangeSet(List<UpdateChangeSet> changes, String fieldName, String moduleName) throws Exception {

        if(CollectionUtils.isNotEmpty(changes)) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

            FacilioField field = modBean.getField(fieldName, moduleName);
            if(field == null) {
                throw new IllegalArgumentException("Invalid Field");
            }
            for(UpdateChangeSet change : changes) {
                if(change.getFieldId() == field.getFieldId()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void handleCustomLookup(Map<String, Object> data, String modName) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields(modName);
        CommonCommandUtil.handleLookupFormData(fields, data);

    }


    public static FacilioField getField(String fieldName, String modName) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        return modBean.getField(fieldName, modName);
    }

    public static ModuleBaseWithCustomFields getRecord (String modName, Long recId, Class beanClass) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(modName);
        List<FacilioField> fields = modBean.getAllFields(modName);

        Class beanClassName = FacilioConstants.ContextNames.getClassFromModule(module);
        if (beanClassName == null) {
            beanClassName = V3Context.class;
        }
        SelectRecordsBuilder<? extends V3Context> builder = new SelectRecordsBuilder<V3Context>()
                .module(module)
                .beanClass(beanClass)
                .select(fields)
                .andCondition(CriteriaAPI.getIdCondition(Long.valueOf(recId), module))
                ;

        List<? extends V3Context> records = builder.get();
        if(CollectionUtils.isNotEmpty(records)) {
            return records.get(0);
        }
        else {
            return null;
        }
    }


}
