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
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.v3.context.V3Context;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Collections;
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

    public static <T extends ModuleBaseWithCustomFields> T getRecord (String modName, Long recId) throws Exception {
        return getRecord(modName, recId, null);
    }
    
    public static <T extends ModuleBaseWithCustomFields> T getRecord (Long moduleId, Long recId) throws Exception {
    	ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        return getRecord(modBean.getModule(moduleId).getName(), recId, null);
    }

    public static <T extends ModuleBaseWithCustomFields> T getRecord (String modName, long recId, Class<T> beanClass) throws Exception {
        List<T> records = constructBuilder(modName, Collections.singletonList(recId), beanClass).get();
        if(CollectionUtils.isNotEmpty(records)) {
            return records.get(0);
        }
        else {
            return null;
        }
    }

    private static <T extends ModuleBaseWithCustomFields> SelectRecordsBuilder<T> constructBuilder(String modName, Collection<Long> recordIds, Class<T> beanClass) throws Exception {
        return constructBuilder(modName, recordIds, beanClass, null, null);
    }

    private static <T extends ModuleBaseWithCustomFields> SelectRecordsBuilder<T> constructBuilder(String modName, Collection<Long> recordIds, Class<T> beanClass, Criteria criteria, Collection<SupplementRecord> supplements) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(modName);
        List<FacilioField> fields = modBean.getAllFields(modName);

        Class beanClassName = beanClass == null ? FacilioConstants.ContextNames.getClassFromModule(module) : beanClass;
        if (beanClassName == null) {
            beanClassName = V3Context.class;
        }
        SelectRecordsBuilder<T> builder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
                .module(module)
                .beanClass(beanClassName)
                .select(fields)
                .andCondition(CriteriaAPI.getIdCondition(recordIds, module))
                ;

        if (criteria != null && !criteria.isEmpty()) {
            builder.andCriteria(criteria);
        }
        if (CollectionUtils.isNotEmpty(supplements)) {
            builder.fetchSupplements(supplements);
        }

        return builder;
    }

    public static <T extends ModuleBaseWithCustomFields> List<T> getRecordsList (String modName, Collection<Long> recordIds) throws Exception{
        return getRecordsList(modName, recordIds, null);
    }

    public static <T extends ModuleBaseWithCustomFields> List<T> getRecordsList (String modName, Collection<Long> recordIds, Class<T> beanClass) throws Exception{
        List<T> records = constructBuilder(modName, recordIds, beanClass).get();
        if(CollectionUtils.isNotEmpty(records)) {
            return records;
        }
        else {
            return null;
        }
    }

    public static <T extends ModuleBaseWithCustomFields> Map<Long, T> getRecordsMap (String modName, Collection<Long> recordIds) throws Exception{
        return getRecordsMap(modName, recordIds, null, null);
    }

    public static <T extends ModuleBaseWithCustomFields> Map<Long, T> getRecordsMap (String modName, Collection<Long> recordIds, Collection<SupplementRecord> supplements) throws Exception{
        return getRecordsMap(modName, recordIds, null, null, supplements);
    }

    public static <T extends ModuleBaseWithCustomFields> Map<Long, T> getRecordsMap (String modName, Collection<Long> recordIds, Criteria criteria) throws Exception{
        return getRecordsMap(modName, recordIds, null, criteria);
    }

    public static <T extends ModuleBaseWithCustomFields> Map<Long, T> getRecordsMap (String modName, Collection<Long> recordIds, Class<T> beanClass) throws Exception{
        return getRecordsMap(modName, recordIds, beanClass, null);
    }

    public static <T extends ModuleBaseWithCustomFields> Map<Long, T> getRecordsMap (String modName, Collection<Long> recordIds, Class<T> beanClass, Criteria criteria) throws Exception{
        return getRecordsMap(modName, recordIds, beanClass, criteria, null);
    }

    public static <T extends ModuleBaseWithCustomFields> Map<Long, T> getRecordsMap (String modName, Collection<Long> recordIds, Class<T> beanClass, Criteria criteria, Collection<SupplementRecord> supplements) throws Exception{
        Map<Long, T> recordMap = constructBuilder(modName, recordIds, beanClass, criteria, supplements).getAsMap();
        return recordMap;
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

    public static int deleteRecordsById (String moduleName, List<Long> ids) throws Exception {
        return new DeleteRecordBuilder<ModuleBaseWithCustomFields>()
                    .moduleName(moduleName)
                    .batchDeleteById(ids);
    }

}
