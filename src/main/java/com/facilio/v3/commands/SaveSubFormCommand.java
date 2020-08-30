package com.facilio.v3.commands;

import java.io.File;
import java.util.*;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.v3.util.ChainUtil;
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

import javax.validation.constraints.NotNull;

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
                if (CollectionUtils.isEmpty(subForm)) {
                    continue;
                }
                List<ModuleBaseWithCustomFields> beanList = insert(context, mainModuleName, moduleName, subForm, record.getId());
                recordMap.computeIfAbsent(moduleName, k -> new ArrayList<>());
                recordMap.get(moduleName).addAll(beanList);
            }
        }

        context.put(FacilioConstants.ContextNames.RECORD_MAP, recordMap);
        return false;
    }

    /*
    * Finds lookup field relation in child module
    *  if parent module is workorder (workorder extends ticket) and child module is ticketattachment,
    *  the look up is present for ticket module in ticketattachment.
    */
    private LookupField findLookupFieldInChildModule(@NotNull FacilioModule parentModule, @NotNull Map<String, LookupField> childLookupFields) throws Exception {
        LookupField lookupField = childLookupFields.get(parentModule.getName());
        if (lookupField == null) {
            FacilioModule extendModule = parentModule.getExtendModule();
            if (extendModule == null) {
                throw new RESTException(ErrorCode.VALIDATION_ERROR);
            }
            return findLookupFieldInChildModule(extendModule, childLookupFields);
        }
        return lookupField;
    }

    private List<ModuleBaseWithCustomFields> insert(Context context, String mainModuleName, String moduleName, List<Map<String, Object>> subForm, long recordId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        FacilioModule mainModule = modBean.getModule(mainModuleName);

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

        List<ModuleBaseWithCustomFields> beanList = new ArrayList<>();
        Class contextClass = ChainUtil.getBeanClass(null, module);

        Map<String, Object> parentObject = new HashMap<>();
        parentObject.put("id", recordId);
        LookupField lookupField;
        try {
            lookupField = findLookupFieldInChildModule(mainModule, getAllLookupFields(modBean, module));
        } catch (RESTException ex) {
            throw new RESTException(ex.getErrorCode(), "There is no relationship between " + mainModuleName + " and " + moduleName);
        }

        for (Map<String, Object> map : subForm) {
            // ignoring patched records
            if (map.get("id") != null) {
                continue;
            }
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
