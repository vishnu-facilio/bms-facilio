package com.facilio.bmsconsole.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.struts2.dispatcher.multipart.StrutsUploadedFile;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.ModuleLocalIdUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.FileField;
import com.facilio.modules.fields.LookupField;

public class GenericAddSubModuleDataCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBaseWithCustomFields record = (ModuleBaseWithCustomFields) context.get(FacilioConstants.ContextNames.RECORD);
        if(record != null) {
            Map<String, List<Map<String, Object>>> subForm = record.getSubForm();
            List<ModuleBaseWithCustomFields.SubFormDataContext> subFormData = record.getSubFormData();
            // to be removed once it is handled in client
            if (MapUtils.isNotEmpty(subForm)) {
                Map<String, Object> parentObject = new HashMap<>();
                parentObject.put("id", record.getId());

                Map<String, List> recordMap = new HashMap<>();
                String mainModuleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule mainModule = modBean.getModule(mainModuleName);
                SelectRecordsBuilder<ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<>()
                        .module(mainModule)
                        .select(modBean.getAllFields(mainModuleName))
                        .beanClass(FacilioConstants.ContextNames.getClassFromModule(mainModule))
                        .andCondition(CriteriaAPI.getIdCondition(record.getId(), mainModule));
                ModuleBaseWithCustomFields updatedRecord = builder.fetchFirst();
                recordMap.put(mainModuleName, Collections.singletonList(updatedRecord));


                for (String moduleName : subForm.keySet()) {
                    FacilioModule module = modBean.getModule(moduleName);
                    List<FacilioField> fields = modBean.getAllFields(moduleName);

                    Map<String, LookupField> allLookupFields = getAllLookupFields(modBean, module);
                    LookupField lookupField = allLookupFields.get(mainModuleName);

                    InsertRecordBuilder<ModuleBaseWithCustomFields> insertRecordBuilder = new InsertRecordBuilder<>()
                            .module(module)
                            .fields(fields)
                            ;
                    insertRecordBuilder.withChangeSet();

                    boolean moduleWithLocalId = ModuleLocalIdUtil.isModuleWithLocalId(module);
                    if (moduleWithLocalId) {
                        insertRecordBuilder.withLocalId();
                    }

                    List<FacilioField> fileFields = new ArrayList<>();
                    for (FacilioField f : fields) {
                        if (f instanceof FileField) {
                            fileFields.add(f);
                        }
                    }

                    List<Map<String, Object>> maps = subForm.get(moduleName);
                    List<ModuleBaseWithCustomFields> beanList = new ArrayList<>();
                    Class contextClass = FacilioConstants.ContextNames.getClassFromModule(module);
                    for (Map<String, Object> map : maps) {
                        map.put(lookupField.getName(), parentObject);
                        Map<FacilioField, StrutsUploadedFile> fileMap = new HashMap<>();
                        for (FacilioField f : fileFields) {
                            Object remove = map.remove(f.getName());
                            if (remove != null) {
                                fileMap.put(f, (StrutsUploadedFile) remove);
                            }
                        }
                        ModuleBaseWithCustomFields moduleRecord = (ModuleBaseWithCustomFields) FieldUtil.getAsBeanFromMap(map, contextClass);
                        for (FacilioField field : fileMap.keySet()) {
                            File file = fileMap.get(field).getContent();
                            if (field.isDefault()) {
                                PropertyUtils.setProperty(moduleRecord, field.getName(), file);
                            }
                            else {
                                moduleRecord.setDatum(field.getName(), file);
                            }
                        }
                        moduleRecord.parseFormData();
                        beanList.add(moduleRecord);
                    }
                    insertRecordBuilder.addRecords(beanList);
                    insertRecordBuilder.save();

                    recordMap.put(moduleName, beanList);
                }

                context.put(FacilioConstants.ContextNames.RECORD_MAP, recordMap);
            }
            else if (CollectionUtils.isNotEmpty(subFormData)) {
                Map<String, Object> parentObject = new HashMap<>();
                parentObject.put("id", record.getId());

                Map<String, List> recordMap = new HashMap<>();
                String mainModuleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule mainModule = modBean.getModule(mainModuleName);

                SelectRecordsBuilder<ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<>()
                        .module(mainModule)
                        .select(modBean.getAllFields(mainModuleName))
                        .beanClass(FacilioConstants.ContextNames.getClassFromModule(mainModule))
                        .andCondition(CriteriaAPI.getIdCondition(record.getId(), mainModule));
                ModuleBaseWithCustomFields updatedRecord = builder.fetchFirst();
                recordMap.put(mainModuleName, Collections.singletonList(updatedRecord));


                for (ModuleBaseWithCustomFields.SubFormDataContext subFormD : subFormData) {
                    String moduleName = subFormD.getModuleName();
                    FacilioModule module = modBean.getModule(moduleName);
                    List<FacilioField> fields = modBean.getAllFields(moduleName);

                    Map<String, LookupField> allLookupFields = getAllLookupFields(modBean, module);
                    LookupField lookupField = allLookupFields.get(mainModuleName);

                    InsertRecordBuilder<ModuleBaseWithCustomFields> insertRecordBuilder = new InsertRecordBuilder<>()
                            .module(module)
                            .fields(fields)
                            ;
                    insertRecordBuilder.withChangeSet();

                    boolean moduleWithLocalId = ModuleLocalIdUtil.isModuleWithLocalId(module);
                    if (moduleWithLocalId) {
                        insertRecordBuilder.withLocalId();
                    }

                    List<FacilioField> fileFields = new ArrayList<>();
                    for (FacilioField f : fields) {
                        if (f instanceof FileField) {
                            fileFields.add(f);
                        }
                    }

                    List<ModuleBaseWithCustomFields.Data> maps = subFormD.getData();
                    List<ModuleBaseWithCustomFields> beanList = new ArrayList<>();
                    Class contextClass = FacilioConstants.ContextNames.getClassFromModule(module);
                    for (ModuleBaseWithCustomFields.Data datum : maps) {
                        Map<String, Object> map = datum.getDatumMap();

                        map.put(lookupField.getName(), parentObject);
                        Map<FacilioField, Map<String, Object>> fileMap = new HashMap<>();
                        for (FacilioField f : fileFields) {
                        		Map<String, Object> fileDetails = new HashMap<>();
                            Object remove = map.remove(f.getName());
                            if (remove != null) {
	                            	fileDetails.put(f.getName(), remove);
	                            	fileDetails.put(f.getName()+"ContentType", map.remove(f.getName()+"ContentType"));
	                            	fileDetails.put(f.getName()+"FileName", map.remove(f.getName()+"FileName"));
	                            	fileMap.put(f, fileDetails);
                            }
                        }
                        ModuleBaseWithCustomFields moduleRecord = (ModuleBaseWithCustomFields) FieldUtil.getAsBeanFromMap(map, contextClass);
                        for (FacilioField field : fileMap.keySet()) {
                        	    Map<String, Object> fileDetails = fileMap.get(field);
                            File file = ((StrutsUploadedFile) fileDetails.get(field.getName())).getContent();
                            String  contentType = (String) fileDetails.get(field.getName()+"ContentType");
                            String  fileName = (String) fileDetails.get(field.getName()+"FileName");
                            if (field.isDefault()) {
                                PropertyUtils.setProperty(moduleRecord, field.getName(), file);
                                PropertyUtils.setProperty(moduleRecord, field.getName()+"ContentType", contentType);
                                PropertyUtils.setProperty(moduleRecord, field.getName()+"FileName", fileName);
                            }
                            else {
                                moduleRecord.setDatum(field.getName(), file);
                                moduleRecord.setDatum(field.getName()+"ContentType", contentType);
                                moduleRecord.setDatum(field.getName()+"FileName", fileName);
                            }
                        }
                        moduleRecord.parseFormData();
                        beanList.add(moduleRecord);
                    }
                    insertRecordBuilder.addRecords(beanList);
                    insertRecordBuilder.save();

                    recordMap.put(moduleName, beanList);
                }
                context.put(FacilioConstants.ContextNames.RECORD_MAP, recordMap);
            }
        }
        return false;
    }

    private Map<String, LookupField> getAllLookupFields(ModuleBean modBean, FacilioModule module) throws Exception {
        List<LookupField> lookupFields = new ArrayList<>();
        List<FacilioField> allFields = modBean.getAllFields(module.getName());
        if (CollectionUtils.isNotEmpty(allFields)) {
            for (FacilioField f : allFields) {
                if (f instanceof LookupField) {
                    lookupFields.add((LookupField) f);
                }
            }
        }

        Map<String, LookupField> lookupFieldMap = new HashMap<>();
        for (LookupField l : lookupFields) {
            FacilioModule lookupModule = l.getLookupModule();
            if (lookupModule != null) {
                lookupFieldMap.put(lookupModule.getName(), l);
            }
        }
        return lookupFieldMap;
    }

    private void sample() {
//        if (MapUtils.isNotEmpty(subForm)) {
//            Map<String, List> recordMap = new HashMap<>();
//            String mainModuleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
//            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
//            FacilioModule mainModule = modBean.getModule(mainModuleName);
//            SelectRecordsBuilder<ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<>()
//                    .module(mainModule)
//                    .select(modBean.getAllFields(mainModuleName))
//                    .beanClass(FacilioConstants.ContextNames.getClassFromModule(mainModule))
//                    .andCondition(CriteriaAPI.getIdCondition(record.getId(), mainModule));
//            ModuleBaseWithCustomFields updatedRecord = builder.fetchFirst();
//            recordMap.put(mainModuleName, Collections.singletonList(updatedRecord));
//
//
//            for (String moduleName : subForm.keySet()) {
//                FacilioModule module = modBean.getModule(moduleName);
//                List<FacilioField> fields = modBean.getAllFields(moduleName);
//
//                Map<String, LookupField> allLookupFields = getAllLookupFields(modBean, module);
//                LookupField lookupField = allLookupFields.get(mainModuleName);
//
//                InsertRecordBuilder<ModuleBaseWithCustomFields> insertRecordBuilder = new InsertRecordBuilder<>()
//                        .module(module)
//                        .fields(fields)
//                        ;
//                insertRecordBuilder.withChangeSet();
//
//                boolean moduleWithLocalId = ModuleLocalIdUtil.isModuleWithLocalId(module);
//                if (moduleWithLocalId) {
//                    insertRecordBuilder.withLocalId();
//                }
//
//                List<FacilioField> fileFields = new ArrayList<>();
//                for (FacilioField f : fields) {
//                    if (f instanceof FileField) {
//                        fileFields.add(f);
//                    }
//                }
//
//                List<Map<String, Object>> maps = subForm.get(moduleName);
//                List<ModuleBaseWithCustomFields> beanList = new ArrayList<>();
//                Class contextClass = FacilioConstants.ContextNames.getClassFromModule(module);
//                for (Map<String, Object> map : maps) {
//                    map.put(lookupField.getName(), parentObject);
//                    Map<FacilioField, StrutsUploadedFile> fileMap = new HashMap<>();
//                    for (FacilioField f : fileFields) {
//                        Object remove = map.remove(f.getName());
//                        if (remove != null) {
//                            fileMap.put(f, (StrutsUploadedFile) remove);
//                        }
//                    }
//                    ModuleBaseWithCustomFields moduleRecord = (ModuleBaseWithCustomFields) FieldUtil.getAsBeanFromMap(map, contextClass);
//                    for (FacilioField field : fileMap.keySet()) {
//                        File file = fileMap.get(field).getContent();
//                        if (field.isDefault()) {
//                            PropertyUtils.setProperty(moduleRecord, field.getName(), file);
//                        }
//                        else {
//                            moduleRecord.setDatum(field.getName(), file);
//                        }
//                    }
//                    moduleRecord.parseFormData();
//                    beanList.add(moduleRecord);
//                }
//                insertRecordBuilder.addRecords(beanList);
//                insertRecordBuilder.save();
//
//                recordMap.put(moduleName, beanList);
//            }
//
//            context.put(FacilioConstants.ContextNames.RECORD_MAP, recordMap);
//        }
    }
}
