package com.facilio.v3.commands;

import java.io.File;
import java.util.*;

import com.facilio.bmsconsole.commands.FacilioCommand;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.ModuleLocalIdUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.FileField;
import com.facilio.modules.fields.LookupField;

public class SaveSubFormCommand extends FacilioCommand {

    private List<ModuleBaseWithCustomFields> getRecord(Context context) {
        Map<String, List<ModuleBaseWithCustomFields>> recordMap = (Map<String, List<ModuleBaseWithCustomFields>>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        return recordMap.get(moduleName);
    }

    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<ModuleBaseWithCustomFields> records = getRecord(context);
        if (CollectionUtils.isEmpty(records)) {
            return false;
        }

        Map<String, List<ModuleBaseWithCustomFields>> recordMap = (Map<String, List<ModuleBaseWithCustomFields>>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        String mainModuleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

        for (ModuleBaseWithCustomFields record: records) {
            Map<String, List<Map<String, Object>>> subFormMap = record.getSubForm();
            if (MapUtils.isEmpty(subFormMap)) {
                continue;
            }

            for (String moduleName : subFormMap.keySet()) {
                List<Map<String, Object>> subForm = subFormMap.get(moduleName);
                List<ModuleBaseWithCustomFields> beanList = insert(mainModuleName, moduleName, subForm, record.getId());
                List<ModuleBaseWithCustomFields> list = recordMap.get(moduleName);
                if (list == null) {
                    recordMap.put(moduleName, new ArrayList<>());
                }
                recordMap.get(moduleName).addAll(beanList);
            }
        }

        context.put(FacilioConstants.ContextNames.RECORD_MAP, recordMap);
        return false;
    }

    private List<ModuleBaseWithCustomFields> insert(String mainModuleName, String moduleName, List<Map<String, Object>> subForm, long recordId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        List<FacilioField> fields = modBean.getAllFields(moduleName);

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

        List<Map<String, Object>> maps = subForm;
        List<ModuleBaseWithCustomFields> beanList = new ArrayList<>();
        Class contextClass = FacilioConstants.ContextNames.getClassFromModule(module);
        Map<String, Object> parentObject = new HashMap<>();
        parentObject.put("id", recordId);

        Map<String, LookupField> allLookupFields = getAllLookupFields(modBean, module);
        LookupField lookupField = allLookupFields.get(mainModuleName);

        for (Map<String, Object> map : maps) {
            map.put(lookupField.getName(), parentObject);
            Map<FacilioField, File> fileMap = new HashMap<>();
            for (FacilioField f : fileFields) {
                Object remove = map.remove(f.getName());
                if (remove != null) {
                   fileMap.put(f, (File) remove);
                }
            }
            ModuleBaseWithCustomFields moduleRecord = (ModuleBaseWithCustomFields) FieldUtil.getAsBeanFromMap(map, contextClass);
            for (FacilioField field : fileMap.keySet()) {
                File file = fileMap.get(field);
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

        return beanList;
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
}
