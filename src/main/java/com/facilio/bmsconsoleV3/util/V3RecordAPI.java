package com.facilio.bmsconsoleV3.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.*;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.V3Context;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

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
                throw new RESTException(ErrorCode.RESOURCE_NOT_FOUND, "Invalid Field");
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
        SelectRecordsBuilder<? extends ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
                .module(module)
                .beanClass(beanClass)
                .select(fields)
                .andCondition(CriteriaAPI.getIdCondition(Long.valueOf(recId), module))
                ;

        List<? extends ModuleBaseWithCustomFields> records = builder.get();
        if(CollectionUtils.isNotEmpty(records)) {
            return records.get(0);
        }
        else {
            return null;
        }
    }

    public static List<? extends ModuleBaseWithCustomFields> getRecordsList (String modName, List<Long> recordIds) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(modName);
        List<FacilioField> fields = modBean.getAllFields(modName);

        Class beanClassName = FacilioConstants.ContextNames.getClassFromModule(module);
        if (beanClassName == null) {
            beanClassName = V3Context.class;
        }
        SelectRecordsBuilder<? extends ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
                .module(module)
                .beanClass(beanClassName)
                .select(fields)
                .andCondition(CriteriaAPI.getIdCondition(StringUtils.join(recordIds, ","), module))
                ;

        List<? extends ModuleBaseWithCustomFields> records = builder.get();
        if(CollectionUtils.isNotEmpty(records)) {
            return records;
        }
        else {
            return null;
        }
    }

    public static List<? extends V3Context> getTransactionRecordsList (String modName, String sourceModName, Long sourceId) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(modName);
        List<FacilioField> fields = modBean.getAllFields(modName);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        if(MapUtils.isNotEmpty(fieldsAsMap)) {
            FacilioField sourceModNameField = fieldsAsMap.get("transactionSourceModuleName");
            FacilioField sourceRecId = fieldsAsMap.get("transactionSourceRecordId");


            Class beanClassName = FacilioConstants.ContextNames.getClassFromModule(module);
            if (beanClassName == null) {
                beanClassName = V3Context.class;
            }
            SelectRecordsBuilder<? extends V3Context> builder = new SelectRecordsBuilder<V3Context>()
                    .module(module)
                    .beanClass(beanClassName)
                    .select(fields);

            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition(sourceModNameField, sourceModName, StringOperators.IS));
            criteria.addAndCondition(CriteriaAPI.getCondition(sourceRecId, String.valueOf(sourceId), NumberOperators.EQUALS));

            builder.andCriteria(criteria);

            List<? extends V3Context> records = builder.get();
            if (CollectionUtils.isNotEmpty(records)) {
                return records;
            } else {
                return null;
            }
        }
        return null;
    }



}
