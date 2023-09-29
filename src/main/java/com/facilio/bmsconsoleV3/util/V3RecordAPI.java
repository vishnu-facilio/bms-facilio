package com.facilio.bmsconsoleV3.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsoleV3.context.V3TransactionContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.DBUtil;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.*;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.util.CurrencyUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.context.Constants;
import com.facilio.v3.context.V3Context;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.v3.util.ChainUtil;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class V3RecordAPI {

    public static void addRecord(boolean isLocalIdNeeded, List<? extends V3Context> list, FacilioModule module, List<FacilioField> fields) throws Exception {
        addRecord(isLocalIdNeeded, list, module, fields, false);
    }

    public static Map<Long, List<UpdateChangeSet>> addRecord(boolean isLocalIdNeeded, List<? extends V3Context> list, FacilioModule module, List<FacilioField> fields, Boolean isChangeSetNeeded) throws Exception {

        InsertRecordBuilder insertRecordBuilder = new InsertRecordBuilder<>()
                .module(module)
                .fields(fields);
        if (isLocalIdNeeded) {
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
        return updateRecord(data, module, fields, isChangeSetNeeded, false);
    }

    public static Map<Long, List<UpdateChangeSet>> updateRecord(V3Context data, FacilioModule module, List<FacilioField> fields, Boolean isChangeSetNeeded, Boolean updateSupplements) throws Exception {
        UpdateRecordBuilder updateRecordBuilder = new UpdateRecordBuilder<ModuleBaseWithCustomFields>()
                .module(module)
                .fields(fields)
                .andCondition(CriteriaAPI.getIdCondition(data.getId(), module));
        if (isChangeSetNeeded != null && isChangeSetNeeded) {
            updateRecordBuilder.withChangeSet(ModuleBaseWithCustomFields.class);
        }
        if (updateSupplements != null && updateSupplements) {
            List<SupplementRecord> supplements = new ArrayList<>();
            CommonCommandUtil.handleFormDataAndSupplement(fields, data.getData(), supplements);
            if (!supplements.isEmpty()) {
                updateRecordBuilder.updateSupplements(supplements);
            }
        }
        updateRecordBuilder.ignoreSplNullHandling();
        updateRecordBuilder.update(data);
        if (isChangeSetNeeded != null && isChangeSetNeeded) {
            return updateRecordBuilder.getChangeSet();
        }

        return null;

    }

    public static boolean checkChangeSet(List<UpdateChangeSet> changes, String fieldName, String moduleName) throws Exception {

        if (CollectionUtils.isNotEmpty(changes)) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

            FacilioField field = modBean.getField(fieldName, moduleName);
            if (field == null) {
                throw new RESTException(ErrorCode.RESOURCE_NOT_FOUND, "Invalid Field");
            }
            for (UpdateChangeSet change : changes) {
                if (change.getFieldId() == field.getFieldId()) {
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

    public static <T extends ModuleBaseWithCustomFields> T getRecord(String modName, Long recId) throws Exception {
        return getRecord(modName, recId, null);
    }

    public static <T extends ModuleBaseWithCustomFields> T getRecord(Long moduleId, Long recId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        return getRecord(modBean.getModule(moduleId).getName(), recId, null);
    }

    public static <T extends ModuleBaseWithCustomFields> T getRecord(String modName, long recId, Class<T> beanClass) throws Exception {
        return getRecord(modName, recId, beanClass, false);
    }

    public static <T extends ModuleBaseWithCustomFields> T getRecord (String modName, long recId, Class<T> beanClass, Collection<SupplementRecord> supplements) throws Exception {
        List<T> records = constructBuilder(modName, Collections.singletonList(recId), beanClass, null, supplements).get();
        if(CollectionUtils.isNotEmpty(records)) {
            return records.get(0);
        }
        else {
            return null;
        }
    }
    public static <T extends ModuleBaseWithCustomFields> T getRecord (String modName, long recId, Class<T> beanClass, boolean skipScoping) throws Exception {
        List<T> records = constructBuilder(modName, Collections.singletonList(recId), beanClass, skipScoping).get();
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

    private static <T extends ModuleBaseWithCustomFields> SelectRecordsBuilder<T> constructBuilder(String modName, Collection<Long> recordIds, Class<T> beanClass, boolean skipScoping) throws Exception {
        return constructBuilder(modName, recordIds, beanClass, null, null, null, null, null, null, null, skipScoping);
    }

    private static <T extends ModuleBaseWithCustomFields> SelectRecordsBuilder<T> constructBuilder(String modName, Collection<Long> recordIds, Class<T> beanClass, Criteria criteria, Collection<SupplementRecord> supplements) throws Exception {
        return constructBuilder(modName, recordIds, beanClass, criteria, supplements, null, null, null, null, null, false);
    }
    private static <T extends ModuleBaseWithCustomFields> SelectRecordsBuilder<T> constructBuilder(String modName, Collection<Long> recordIds, Class<T> beanClass, Criteria criteria, Collection<SupplementRecord> supplements,String orderBy,String orderType,AggregateOperator aggregateOperator,FacilioField aggregateField,String groupBy, boolean skipScoping) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(modName);
        List<FacilioField> fields = modBean.getAllFields(modName);
        if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.MULTI_CURRENCY) && CurrencyUtil.isMultiCurrencyEnabledModule(module)) {
            fields.addAll(FieldFactory.getCurrencyPropsFields(module));
        }

        Class beanClassName = beanClass == null ? FacilioConstants.ContextNames.getClassFromModule(module) : beanClass;
        if (beanClassName == null) {
            beanClassName = V3Context.class;
        }
        SelectRecordsBuilder<T> builder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
                .module(module)
                .beanClass(beanClassName);

        if(CollectionUtils.isNotEmpty(recordIds)) {
                builder.andCondition(CriteriaAPI.getIdCondition(recordIds, module));
        }


        if (criteria != null && !criteria.isEmpty()) {
            builder.andCriteria(criteria);
        }
        if (CollectionUtils.isNotEmpty(supplements)) {
            builder.fetchSupplements(supplements);
        }
        if(StringUtils.isNotEmpty(orderBy)) {
            FacilioUtil.throwRunTimeException((StringUtils.isEmpty(orderType) || (!StringUtils.lowerCase(orderType).equalsIgnoreCase("desc") && !StringUtils.lowerCase(orderType).equalsIgnoreCase("asc"))), "Order Type Cannot be null");
            builder.orderBy(orderBy + StringUtils.SPACE + orderType);
        }

        if(aggregateOperator != null) {
            FacilioUtil.throwRunTimeException(aggregateField == null, "Aggregate field cannot be empty or null");
            builder.aggregate(aggregateOperator, aggregateField);
        }

	    if(groupBy != null) {
             builder.groupBy(groupBy);
        }

	    //Cannot group by all fields without an aggregate operator
	    if(groupBy == null && aggregateOperator != null) {
            builder.select(new HashSet<>());
	    }
	    else {
            builder.select(fields);
	    }


        if(skipScoping) {
            builder.skipScopeCriteria();
        }
        return builder;
    }

    public static <T extends ModuleBaseWithCustomFields> List<T> getRecordsList (String modName, Collection<Long> recordIds) throws Exception{
        return getRecordsList(modName, recordIds, null);
    }

    public static <T extends ModuleBaseWithCustomFields> List<T> getRecordsList (String modName, Collection<Long> recordIds, Class<T> beanClass) throws Exception{
        List<T> records = constructBuilder(modName, recordIds, beanClass, false).get();
        if(CollectionUtils.isNotEmpty(records)) {
            return records;
        }
        else {
            return null;
        }
    }
   
    
    public static <T extends ModuleBaseWithCustomFields> List<T> getRecordsListWithSupplements (String modName, Collection<Long> recordIds, Class<T> beanClass, Collection<SupplementRecord> supplements) throws Exception{
        List<T> records = constructBuilder(modName, recordIds, beanClass, null, supplements).get();
        if(CollectionUtils.isNotEmpty(records)) {
            return records;
        }
        else {
            return null;
        }
    }
    
    
    public static <T extends ModuleBaseWithCustomFields> List<T> getRecordsListWithSupplements (String modName, Collection<Long> recordIds, Class<T> beanClass, Criteria criteria, Collection<SupplementRecord> supplements) throws Exception{
        List<T> records = constructBuilder(modName, recordIds, beanClass, criteria, supplements).get();
        if(CollectionUtils.isNotEmpty(records)) {
            return records;
        }
        else {
            return null;
        }
    }

    public static <T extends ModuleBaseWithCustomFields> List<T> getRecordsListWithSupplements (String modName, Collection<Long> recordIds, Class<T> beanClass, Criteria criteria, Collection<SupplementRecord> supplements, String orderBy, String orderType) throws Exception{
        return getRecordsListWithSupplements(modName, recordIds, beanClass, criteria, supplements, orderBy, orderType, false);
    }

    public static <T extends ModuleBaseWithCustomFields> List<T> getRecordsListWithSupplements (String modName, Collection<Long> recordIds, Class<T> beanClass, Criteria criteria, Collection<SupplementRecord> supplements, String orderBy, String orderType, boolean skipScoping) throws Exception{
        List<T> records = constructBuilder(modName, recordIds, beanClass, criteria, supplements, orderBy, orderType, null, null, null, skipScoping).get();
        if(CollectionUtils.isNotEmpty(records)) {
            return records;
        }
        else {
            return null;
        }
    }

    public static <T extends ModuleBaseWithCustomFields> List<Map<String, Object>> getRecordsAggregateValue (String modName, Collection<Long> recordIds, Class<T> beanClass, Criteria criteria, AggregateOperator aggregateoperator, FacilioField aggregateField, String groupBy) throws Exception{

        List<Map<String, Object>> props = constructBuilder(modName, recordIds, beanClass, criteria, null,  null,  null,  aggregateoperator,  aggregateField, groupBy, false).getAsProps();
        if (CollectionUtils.isNotEmpty(props)) {
	        return props;
        }
		return null;
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

    public static V3TransactionContext getTransactionRecord (String modName, Long sourceModId, Long sourceId, long ruleId) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(modName);
        List<FacilioField> fields = modBean.getAllFields(modName);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        if(MapUtils.isNotEmpty(fieldsAsMap)) {
            FacilioField sourceModIdField = fieldsAsMap.get("transactionSourceModuleId");
            FacilioField sourceRecId = fieldsAsMap.get("transactionSourceRecordId");
            FacilioField ruleIdField=fieldsAsMap.get("ruleId");


            Class beanClassName = FacilioConstants.ContextNames.getClassFromModule(module);
            if (beanClassName == null) {
                beanClassName = V3Context.class;
            }
            SelectRecordsBuilder<? extends V3Context> builder = new SelectRecordsBuilder<V3Context>()
                    .module(module)
                    .beanClass(beanClassName)
                    .select(fields);

            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition(sourceModIdField, String.valueOf(sourceModId),NumberOperators.EQUALS));
            criteria.addAndCondition(CriteriaAPI.getCondition(sourceRecId, String.valueOf(sourceId), NumberOperators.EQUALS));
            criteria.addAndCondition(CriteriaAPI.getCondition(ruleIdField,String.valueOf(ruleId),NumberOperators.EQUALS));

            builder.andCriteria(criteria);

            V3TransactionContext record = (V3TransactionContext) builder.fetchFirst();
            if(record!=null){
                return record;
            }
        }
        return null;
    }

    public static int deleteRecords (String moduleName, Criteria criteria,boolean markAsDelete) throws Exception {
        DeleteRecordBuilder deleteRecordBuilder = new DeleteRecordBuilder<ModuleBaseWithCustomFields>().moduleName(moduleName).andCriteria(criteria);
        if(markAsDelete){
            return deleteRecordBuilder.markAsDelete();
        }
        return deleteRecordBuilder.delete();
    }
    public static int deleteRecordsById (String moduleName, List<Long> ids) throws Exception {
        return new DeleteRecordBuilder<ModuleBaseWithCustomFields>()
                    .moduleName(moduleName)
                    .batchDeleteById(ids);
    }

    public static <T extends ModuleBaseWithCustomFields> int batchUpdateRecords (String moduleName, Collection<T> records, List<FacilioField> updateFields) throws Exception {
        // Use with caution. Should be used only for system update. Default system fields and all aren't updated here
        FacilioUtil.throwRunTimeException(CollectionUtils.isEmpty(records), "Records cannot be empty for batch update");
        FacilioUtil.throwRunTimeException(CollectionUtils.isEmpty(updateFields), "Update fields cannot be empty for batch update");
        ModuleBean modBean = Constants.getModBean();
        FacilioModule module = Objects.requireNonNull(modBean.getModule(moduleName), "Invalid module specified for batch update of records");
        FacilioField moduleIdField = FieldFactory.getModuleIdField(module);
        FacilioField idField = FieldFactory.getIdField(module);

        List<FacilioField> whereFields = new ArrayList<>(2);
        whereFields.add(moduleIdField);
        whereFields.add(idField);

        List<GenericUpdateRecordBuilder.BatchUpdateContext> batchUpdateRecords = records.stream()
                                                                                    .map(r -> constructBatchUpdateRec(module, moduleIdField, idField, r))
                                                                                    .collect(Collectors.toList());

        return DBUtil.getUpdateBuilderWithJoin(module, updateFields)
                .batchUpdate(whereFields, batchUpdateRecords);
    }

    @SneakyThrows
    private static <T extends ModuleBaseWithCustomFields> GenericUpdateRecordBuilder.BatchUpdateContext constructBatchUpdateRec (FacilioModule module, FacilioField moduleIdField, FacilioField idField, T record) {
        GenericUpdateRecordBuilder.BatchUpdateContext batchUpdateRec = new GenericUpdateRecordBuilder.BatchUpdateContext();
        batchUpdateRec.setUpdateValue(FieldUtil.getAsProperties(
                Objects.requireNonNull(record, "Record cannot be null for batch update")
        ));
        batchUpdateRec.addWhereValue(moduleIdField.getName(), module.getModuleId());
        batchUpdateRec.addWhereValue(idField.getName(), record.getId());

        return batchUpdateRec;
    }

    public static String getModuleName(Long moduleId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        if(moduleId != null) {
            FacilioModule module = modBean.getModule(moduleId);
            if(module != null) {
                return module.getName();
            }
        }
        return null;
    }
    public static <T extends ModuleBaseWithCustomFields> int batchUpdateRecordsWithHandlingLookup (String moduleName, Collection<T> records, List<FacilioField> updateFields) throws Exception {
        // Use with caution. Should be used only for system update. Default system fields and all aren't updated here
        FacilioUtil.throwRunTimeException(CollectionUtils.isEmpty(records), "Records cannot be empty for batch update");
        FacilioUtil.throwRunTimeException(CollectionUtils.isEmpty(updateFields), "Update fields cannot be empty for batch update");
        ModuleBean modBean = Constants.getModBean();
        FacilioModule module = Objects.requireNonNull(modBean.getModule(moduleName), "Invalid module specified for batch update of records");
        FacilioField moduleIdField = FieldFactory.getModuleIdField(module);
        FacilioField idField = FieldFactory.getIdField(module);

        List<FacilioField> whereFields = new ArrayList<>(2);
        whereFields.add(moduleIdField);
        whereFields.add(idField);

        List<GenericUpdateRecordBuilder.BatchUpdateContext> batchUpdateRecords = records.stream()
                .map(r -> constructBatchUpdate(module, moduleIdField, idField, r,updateFields))
                .collect(Collectors.toList());

        return DBUtil.getUpdateBuilderWithJoin(module, updateFields)
                .batchUpdate(whereFields, batchUpdateRecords);
    }
    @SneakyThrows
    private static <T extends ModuleBaseWithCustomFields> GenericUpdateRecordBuilder.BatchUpdateContext constructBatchUpdate (FacilioModule module, FacilioField moduleIdField, FacilioField idField, T record,List<FacilioField> updateFields) {
        GenericUpdateRecordBuilder.BatchUpdateContext batchUpdateRec = new GenericUpdateRecordBuilder.BatchUpdateContext();
        Map<String,Object> moduleProps = FieldUtil.getAsProperties(record);
        handleLookupFields(moduleProps,updateFields);
        batchUpdateRec.setUpdateValue(Objects.requireNonNull(moduleProps, "Record cannot be null for batch update"));
        batchUpdateRec.addWhereValue(moduleIdField.getName(), module.getModuleId());
        batchUpdateRec.addWhereValue(idField.getName(), record.getId());

        return batchUpdateRec;
    }
    private static void handleLookupFields(Map<String, Object> moduleProps, List<FacilioField> fields) throws Exception {
        for(FacilioField field : fields) {
            if(field.getDataTypeEnum() == FieldType.LOOKUP) {
                Map<String, Object> lookupProps = (Map<String, Object>) moduleProps.get(field.getName());
                if(lookupProps != null && !lookupProps.isEmpty()) {
                    if(lookupProps.get("id") != null) {
                        moduleProps.put(field.getName(), lookupProps.get("id"));
                    } else  {
                        moduleProps.put(field.getName(), null);
                    }
                }
                else {
                    moduleProps.put(field.getName(), null);
                }
            }
        }
    }


    public static void updateRecord(V3Context data, FacilioModule module, List<FacilioField> fields,List<Long> ids) throws Exception {
        Criteria idCriteria = new Criteria();
        idCriteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(module),StringUtils.join(ids,","),NumberOperators.EQUALS));
        UpdateRecordBuilder updateRecordBuilder = new UpdateRecordBuilder<>()
                .module(module)
                .fields(fields);
        updateRecordBuilder.andCriteria(idCriteria);
        updateRecordBuilder.ignoreSplNullHandling();
        updateRecordBuilder.update(data);
    }

    public static boolean markAsDeleteEnabled(FacilioModule module)  {
        try {
            if (module != null && module.getName() != null) {
                V3Config v3Config = ChainUtil.getV3Config(module.getName());
                if (v3Config != null) {
                    V3Config.DeleteHandler deleteHandler = v3Config.getDeleteHandler();
                    if (deleteHandler != null && deleteHandler.isMarkAsDeleteByPeople()) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
