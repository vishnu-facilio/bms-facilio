package com.facilio.classification.util;

import com.facilio.attribute.context.ClassificationAttributeContext;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.classification.context.ClassificationDataContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ClassificationUtil {


    public static List<ClassificationAttributeContext> getClassificationAttributesByIds(Collection<Long> recordIds) throws Exception {
        if(CollectionUtils.isEmpty(recordIds)){
            return new ArrayList<>();
        }
        FacilioContext summaryContext = V3Util.getSummary(FacilioConstants.ContextNames.CLASSIFICATION_ATTRIBUTE, new ArrayList<>(recordIds));
        List<ClassificationAttributeContext> list = Constants.getRecordListFromContext(summaryContext, FacilioConstants.ContextNames.CLASSIFICATION_ATTRIBUTE);
        return list;
    }

    public static Set<Long> getClassificationRelatedAttributesIds(Long classificationId) throws Exception {
        Set<Long> relatedIds = null;

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = new ArrayList<>();
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(FieldFactory.getClassificationAttributeRelFields());
        FacilioField field = fieldsMap.get("attributeId");
        fields.add(field);

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getClassificationAttributeRelModule().getTableName())
                .select(fields)
                .andCondition(CriteriaAPI.getCondition("CLASSIFICATION_ID", "classificationId", String.valueOf(classificationId), NumberOperators.EQUALS));
        List<Map<String, Object>> maps = builder.get();

        if (CollectionUtils.isNotEmpty(maps)) {
            relatedIds = new HashSet<>();
            for (Map<String, Object> map : maps) {
                Long id = (Long) map.get("attributeId");
                relatedIds.add(id);
            }
        }
        return relatedIds;
    }

    public static  Map<Long, Set<Long>> getClassificationRelatedAttributesIds(Set<Long> classificationIds) throws Exception {
        Map<Long, Set<Long>> classificationAttributesMap = new HashMap<>();

        if (CollectionUtils.isEmpty(classificationIds)) {
            return classificationAttributesMap;
        }



        List<FacilioField> fields = new ArrayList<>();
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(FieldFactory.getClassificationAttributeRelFields());
        FacilioField attributeIdField = fieldsMap.get("attributeId");
        FacilioField classificationIdField = fieldsMap.get("classificationId");
        fields.add(attributeIdField);
        fields.add(classificationIdField);
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getClassificationAttributeRelModule().getTableName())
                .select(fields)
                .andCondition(CriteriaAPI.getCondition("CLASSIFICATION_ID", "classificationId", StringUtils.join(classificationIds, ","), NumberOperators.EQUALS));
        List<Map<String, Object>> maps = builder.get();

        if (CollectionUtils.isNotEmpty(maps)) {
            for (Map<String, Object> map : maps) {
                Long classificationId = (Long) map.get("classificationId");
                Long attributeId = (Long) map.get("attributeId");

                if (!classificationAttributesMap.containsKey(classificationId)) {
                    Set<Long> relatedAttributeIds = new HashSet<>();
                    relatedAttributeIds.add(attributeId);
                    classificationAttributesMap.put(classificationId, relatedAttributeIds);
                } else {
                    classificationAttributesMap.get((classificationId)).add(attributeId);
                }

            }
        }
        return classificationAttributesMap;
    }

    public static Map<Long, List<ClassificationAttributeContext>> getAttributeMap(Set<Long> classificationIds) throws Exception {

        Map<Long, List<ClassificationAttributeContext>> collectionModuleMap = new HashMap<>();
        for (Long classificationId : classificationIds) {
            Set<Long> classificationRelatedAttributeIds = getClassificationRelatedAttributesIds(classificationId);
            List<ClassificationAttributeContext> classificationAttributes = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(classificationRelatedAttributeIds)) {
                classificationAttributes = getClassificationAttributesByIds(classificationRelatedAttributeIds);

            }
            collectionModuleMap.put(classificationId, classificationAttributes);
        }
        return collectionModuleMap;
    }

    public static Map<Long, List<ClassificationAttributeContext>> getAttributeMapVsClassificationId(Set<Long> classificationIds) throws Exception {
        Map<Long, List<ClassificationAttributeContext>> classifcationRelAttributesMap = new HashMap<>();
        Map<Long, Set<Long>> classificationRelAttributeIdsMap = getClassificationRelatedAttributesIds(classificationIds);
        Set<Long> attributeIds=new HashSet<>();
        if(CollectionUtils.isNotEmpty(classificationRelAttributeIdsMap.values())){
            classificationRelAttributeIdsMap.values().forEach(ids->attributeIds.addAll(ids));
        }
        List<ClassificationAttributeContext> attributesList=getClassificationAttributesByIds(attributeIds);
        Map<Long ,ClassificationAttributeContext> attributeMapByAttributeId= attributesList.stream().collect(Collectors.toMap(ClassificationAttributeContext::getId, Function.identity()));

        for(Long classificationId:classificationIds){
            Set<Long> relatedAttributeIds=classificationRelAttributeIdsMap.get(classificationId);
            List<ClassificationAttributeContext> classificationAttributes = new ArrayList<>();
            if(CollectionUtils.isNotEmpty(relatedAttributeIds)){
                for(Long relatedAttributeId:relatedAttributeIds){
                    ClassificationAttributeContext dbAttribute=attributeMapByAttributeId.get(relatedAttributeId);
                    ClassificationAttributeContext attribute=FieldUtil.cloneBean(dbAttribute,ClassificationAttributeContext.class);
                    classificationAttributes.add(attribute);
                }
            }
            classifcationRelAttributesMap.put(classificationId,classificationAttributes);
        }
        return classifcationRelAttributesMap;

    }

    public static List<ClassificationAttributeContext> getClassificationRelatedAttributes(Long classificationId) throws Exception {
        List<ClassificationAttributeContext> classificationAttributes = new ArrayList<>();

        Set<Long> classificationRelatedAttributeIds = getClassificationRelatedAttributesIds(classificationId);

        if (CollectionUtils.isNotEmpty(classificationRelatedAttributeIds)) {
            classificationAttributes = getClassificationAttributesByIds(classificationRelatedAttributeIds);

        }
        return classificationAttributes;
    }

    public static void associateAttributesToClassification(long classificationId, Set<Long> relatedAttributeIds) throws Exception {

        if (CollectionUtils.isEmpty(relatedAttributeIds)) return;

        GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getClassificationAttributeRelModule().getTableName())
                .fields(FieldFactory.getClassificationAttributeRelFields());
        for (Long attributeId : relatedAttributeIds) {
            Map<String, Object> map = new HashMap<>();
            map.put("attributeId", attributeId);
            map.put("classificationId", classificationId);
            builder.addRecord(map);
        }
        builder.save();
    }

    public static void addClassificationData(List<ClassificationDataContext> classificationDataList, FacilioModule classificationDataModule) throws Exception {

        if (CollectionUtils.isEmpty(classificationDataList)) {
            return;
        }

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields(classificationDataModule.getName());
        InsertRecordBuilder<ClassificationDataContext> insert = new InsertRecordBuilder<ClassificationDataContext>()
                .module(classificationDataModule)
                .fields(fields)
                .addRecords(classificationDataList);
        insert.save();

    }

    public static void deleteClassificationDatabyRecordIds(Set<Long> recordIds, FacilioModule classificationDataModule) throws Exception {
        if (CollectionUtils.isEmpty(recordIds)) {
            return;
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioField recordIdField = modBean.getField("record", classificationDataModule.getName());

        DeleteRecordBuilder<ClassificationDataContext> deleteBuilder = new DeleteRecordBuilder<ClassificationDataContext>()
                .module(classificationDataModule)
                .andCondition(CriteriaAPI.getCondition(recordIdField, StringUtils.join(recordIds, ","), NumberOperators.EQUALS));
        deleteBuilder.delete();


    }

    public static Map<Long, List<ClassificationDataContext>> getClassificationDataMap(Set<Long> recordIds, String currentModuleName) throws Exception {
        Map<Long, List<ClassificationDataContext>> classificationDataMap = new HashMap<>();

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule currentModule = modBean.getModule(currentModuleName);
        List<FacilioModule> classificationDataModuleList = modBean.getSubModules(currentModule.getModuleId(), FacilioModule.ModuleType.CLASSIFICATION_DATA);


        if (CollectionUtils.isEmpty(classificationDataModuleList) || classificationDataModuleList.size() > 1) {
            return classificationDataMap;
        }


        FacilioModule classificationDataModule = classificationDataModuleList.get(0);
        String classificationDataModuleName = classificationDataModule.getName();
        List<FacilioField> fields = modBean.getAllFields(classificationDataModuleName);

        FacilioField recordIdField = modBean.getField("record", classificationDataModuleName);

        SelectRecordsBuilder<ClassificationDataContext> selectRecordsBuilder = new SelectRecordsBuilder<ClassificationDataContext>()
                .select(fields)
                .module(classificationDataModule)
                .andCondition(CriteriaAPI.getCondition(recordIdField, StringUtils.join(recordIds, ","), NumberOperators.EQUALS))
                .beanClass(ClassificationDataContext.class);
        List<ClassificationDataContext> list = selectRecordsBuilder.get();
        if (CollectionUtils.isEmpty(list)) {
            return classificationDataMap;
        }

        list.forEach(var -> {
                    if (!classificationDataMap.containsKey(var.getRecord().getId())) {
                        List<ClassificationDataContext> classificationDataList = new ArrayList<>();
                        classificationDataList.add(var);
                        classificationDataMap.put(var.getRecord().getId(), classificationDataList);
                    } else {
                        classificationDataMap.get(var.getRecord().getId()).add(var);
                    }
                }
        );

        return classificationDataMap;
    }

    public static FacilioModule getNewClassificationDataModule(FacilioModule parentModule, String tableName) throws Exception {
        FacilioModule newClassificationDataModule = new FacilioModule();
        newClassificationDataModule.setName(parentModule.getName() + "_" + "classification_data");
        newClassificationDataModule.setType(FacilioModule.ModuleType.CLASSIFICATION_DATA);
        newClassificationDataModule.setTableName(tableName);

        if(parentModule.isCustom()){
            newClassificationDataModule.setCustom(true);
            newClassificationDataModule.setDisplayName(parentModule.getDisplayName() +" CMD Classifications");
        }
        newClassificationDataModule.setDisplayName(parentModule.getDisplayName() +" Classifications");

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule attributeModule = modBean.getModule(FacilioConstants.ContextNames.CLASSIFICATION_ATTRIBUTE);

        newClassificationDataModule.setFields(getClassificationDataFields(newClassificationDataModule, attributeModule, parentModule));
        return newClassificationDataModule;


    }

    public static long addClassificationDataModule(FacilioModule parentModule, String tableName) throws Exception {
        FacilioModule classificationDataModule = getNewClassificationDataModule(parentModule, tableName);


        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        long classificationDataModuleId = modBean.addModule(classificationDataModule);

        classificationDataModule.setModuleId(classificationDataModuleId);

        FacilioChain addFieldsChain = TransactionChainFactory.getAddFieldsChain();
        FacilioContext context = addFieldsChain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, classificationDataModule.getName());
        context.put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, classificationDataModule.getFields());
        addFieldsChain.execute();

        return classificationDataModuleId;
    }

    public static Set<Long> getAllExistingCustomModuleId() {
        return null;
    }

    public static List<FacilioField> getClassificationDataFields(FacilioModule module, FacilioModule attributeModule, FacilioModule parentModule) {
        List<FacilioField> fields = new ArrayList<>();

        LookupField recordField = new LookupField();
        recordField.setDataType(FieldType.LOOKUP);
        recordField.setName("record");
        recordField.setDisplayName("Record Id");
        recordField.setColumnName("RECORD_ID");
        recordField.setModule(module);
        recordField.setLookupModule(parentModule);
        fields.add(recordField);

        LookupField attributeIdField = new LookupField();
        attributeIdField.setDataType(FieldType.LOOKUP);
        attributeIdField.setName("attribute");
        attributeIdField.setDisplayName("Attribute Id");
        attributeIdField.setColumnName("ATTRIBUTE_ID");
        attributeIdField.setModule(module);
        attributeIdField.setLookupModule(attributeModule);
        fields.add(attributeIdField);

        fields.add(FieldFactory.getNumberField("numberValue", "NUMBER_VALUE", module));
        fields.add(FieldFactory.getField("decimalValue", "DECIMAL_VALUE", module, FieldType.DECIMAL));
        fields.add(FieldFactory.getStringField("textValue", "TEXT_VALUE", module));
        fields.add(FieldFactory.getField("textAreaValue", "TEXT_AREA_VALUE", module, FieldType.BIG_STRING));
        fields.add(FieldFactory.getBooleanField("booleanValue", "BOOLEAN_VALUE", module));
        fields.add(FieldFactory.getDateField("dateTimeValue", "DATETIME_VALUE", module));

        return fields;
    }
}
