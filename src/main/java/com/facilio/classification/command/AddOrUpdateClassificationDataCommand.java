package com.facilio.classification.command;

import com.facilio.attribute.context.ClassificationAttributeContext;
import com.facilio.beans.ModuleBean;
import com.facilio.chain.FacilioContext;
import com.facilio.classification.context.ClassificationContext;
import com.facilio.classification.context.ClassificationDataContext;
import com.facilio.classification.util.ClassificationCache;
import com.facilio.classification.util.ClassificationUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.Constants;
import com.facilio.v3.context.V3Context;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AddOrUpdateClassificationDataCommand extends FacilioCommand {
    private boolean add;
    private ClassificationCache cache = new ClassificationCache();

    public AddOrUpdateClassificationDataCommand() {
    }

    public AddOrUpdateClassificationDataCommand(boolean add) {
        this.add = add;
    }

    @Override
    public boolean executeCommand(Context context) throws Exception {

        List<V3Context> records = Constants.getRecordList((FacilioContext) context);
        if (CollectionUtils.isEmpty(records)) {
            return false;
        }

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule parentModule = modBean.getModule(Constants.getModuleName(context));
        List<FacilioModule> classificationDataModuleList = modBean.getSubModules(parentModule.getModuleId(), FacilioModule.ModuleType.CLASSIFICATION_DATA);

        Set<Long> deleteRecordIds = new HashSet<>();

        if (add) {
            records = records.stream().filter(record -> record.getClassification() != null).collect(Collectors.toList()); //filter clasification holding record only

            if (CollectionUtils.isEmpty(records)) {
                return false;
            }

            if (CollectionUtils.isEmpty(classificationDataModuleList) || classificationDataModuleList.size() > 1) {  //there should only one ClassificationData module
                throw new IllegalArgumentException("ClassificationData module may be empty or more than one entry");
            }
            FacilioModule classificationDataModule = classificationDataModuleList.get(0);

            List<ClassificationDataContext> classificationDataList = new ArrayList<>();
            addClassificationDatatoClassificationDatList(records, parentModule, deleteRecordIds, classificationDataList, classificationDataModule);

            ClassificationUtil.addClassificationData(classificationDataList, classificationDataModule);

        } else {
            Map<Long, V3Context> oldRecordMap = Constants.getOldRecordMap(context);

            if(oldRecordMap==null){
                return false;
            }
            if (records.size() != oldRecordMap.values().size()) {
                throw new RESTException(ErrorCode.VALIDATION_ERROR, "Few records doesn't have data");
            }
            addDeletableRecordsIds(records, oldRecordMap, deleteRecordIds);

            List<V3Context> oldRecords = oldRecordMap.values().stream().filter(oldRecord -> oldRecord.getClassification() != null).collect(Collectors.toList());
            records = records.stream().filter(record -> record.getClassification() != null).collect(Collectors.toList()); //filter clasification holding record only

            if (CollectionUtils.isEmpty(oldRecords) && CollectionUtils.isEmpty(records)) {
                return false;
            }
            if (CollectionUtils.isEmpty(classificationDataModuleList) || classificationDataModuleList.size() > 1) {  //there should only one ClassificationData module
                throw new IllegalArgumentException("ClassificationData module may be empty or more than one entry");
            }
            FacilioModule classificationDataModule = classificationDataModuleList.get(0);

            List<ClassificationDataContext> classificationDataList = new ArrayList<>();
            addClassificationDatatoClassificationDatList(records, parentModule, deleteRecordIds, classificationDataList, classificationDataModule);

            ClassificationUtil.deleteClassificationDatabyRecordIds(deleteRecordIds, classificationDataModule);
            ClassificationUtil.addClassificationData(classificationDataList, classificationDataModule);
        }


        return false;
    }

    private void addClassificationDatatoClassificationDatList(List<V3Context> records, FacilioModule parentModule, Set<Long> deleteRecordIds, List<ClassificationDataContext> classificationDataList, FacilioModule classificationDataModule) throws Exception {
        List<ClassificationContext> classificationList = records.stream().map(V3Context::getClassification).collect(Collectors.toList());    //For add we need to fetch attributes for all classificationId
        Set<Long> classificationIds = classificationList.stream().map(ClassificationContext::getId).collect(Collectors.toSet());             //if it update ,attributes for classificatoion are available in oldRecordMap in context
        Map<Long, List<ClassificationAttributeContext>> attributeMap = ClassificationUtil.getAttributeMap(classificationIds);


        for (V3Context record : records) {

            validateRecordClassification(record, parentModule);

            long recordId = record.getId();
            deleteRecordIds.add(recordId);

            ClassificationContext classificationContext = record.getClassification();
            long classificationId = classificationContext.getId();
            List<ClassificationAttributeContext> dbClassificationAttributes = attributeMap.get(classificationId);
            List<ClassificationAttributeContext> classificationAttributeContextList = classificationContext.getAttributes();

            if (CollectionUtils.isNotEmpty(dbClassificationAttributes) && CollectionUtils.isNotEmpty(classificationAttributeContextList)) {
                addAttributeValuesToClassificationData(classificationDataList, record, dbClassificationAttributes, classificationDataModule);
            }

        }
    }

    private void addAttributeValuesToClassificationData(List<ClassificationDataContext> classificationDataList, V3Context record, List<ClassificationAttributeContext> dbClassificationAttributes, FacilioModule classificationDataModule) throws Exception {

        ClassificationContext classificationContext = record.getClassification();
        List<ClassificationAttributeContext> classificationAttributeContextList = classificationContext.getAttributes();
        Map<Long, ClassificationAttributeContext> dbAttributeMap = dbClassificationAttributes.stream().collect(Collectors.toMap(ClassificationAttributeContext::getId, Function.identity()));
        Set<Long> dbAttributeIds = dbClassificationAttributes.stream().map(ClassificationAttributeContext::getId).collect(Collectors.toSet());
        Set<Long> inputAttributeIds = classificationAttributeContextList.stream().map(ClassificationAttributeContext::getId).collect(Collectors.toSet());

        long classificationId = classificationContext.getId();
        ClassificationContext dbClassification = cache.getFromCache(classificationId);
        if (!dbAttributeIds.containsAll(inputAttributeIds)) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid attributes for this classification -" + dbClassification.getName());
        }


        for (ClassificationAttributeContext attribute : classificationAttributeContextList) {
            ClassificationDataContext classificationDataContext = new ClassificationDataContext();
            //DP attribute
            ClassificationAttributeContext dbAttribute = dbAttributeMap.get(attribute.getId());

            attribute.setName(dbAttribute.getName());
            attribute.setFieldType(dbAttribute.getFieldType());

            if (attribute.getValue() == null) {
                continue;
            }
            attribute.setClassificationData(classificationDataContext);         //validating value and set classification data

            classificationDataContext.setModuleId(classificationDataModule.getModuleId());
            classificationDataContext.setAttribute(attribute);
            classificationDataContext.setRecord(record);

            classificationDataList.add(classificationDataContext);
        }
    }

    private void addDeletableRecordsIds(List<V3Context> records, Map<Long, V3Context> oldRecordMap, Set<Long> deleteRecordIds) throws RESTException {
        records.forEach(record -> {
            V3Context oldRecord = oldRecordMap.get(record.getId());
            if (oldRecord.getClassification() != null && record.getClassification() == null) { //if record have classification null but old record have classification means we need to delete in classificationModule data
                deleteRecordIds.add(record.getId());
            }
        });

    }

    private void validateRecordClassification(V3Context record, FacilioModule parentModule) throws Exception {
        ClassificationContext classificationContext = record.getClassification();
        long classificationId = classificationContext.getId();


        ClassificationContext dbClassification = cache.getFromCache(classificationId); //validate classification exits or not
        if (dbClassification == null) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid ClassificationId -" + classificationId);
        }
        if (!dbClassification.getAppliedModuleIds().contains(parentModule.getModuleId())) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, dbClassification.getName().toUpperCase() + " -this Classification doesn't applied for this module -" + parentModule.getName());
        }

    }
}
