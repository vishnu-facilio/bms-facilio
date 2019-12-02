package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.ModuleLocalIdUtil;
import com.facilio.bmsconsole.view.CustomModuleData;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.*;

public class GenericAddSubModuleDataCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBaseWithCustomFields record = (ModuleBaseWithCustomFields) context.get(FacilioConstants.ContextNames.RECORD);
        if(record != null) {
            if (record instanceof CustomModuleData) {
                CustomModuleData customModuleData = (CustomModuleData) record;
                Map<String, List<Map<String, Object>>> subForm = customModuleData.getSubForm();

                Map<String, Object> parentObject = new HashMap<>();
                parentObject.put("id", record.getId());

                if (MapUtils.isNotEmpty(subForm)) {
                    Map<String, List> recordMap = new HashMap<>();
                    String mainModuleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
                    recordMap.put(mainModuleName, Collections.singletonList(record));

                    ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

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

                        List<Map<String, Object>> maps = subForm.get(moduleName);
                        for (Map<String, Object> map : maps) {
                            map.put(lookupField.getName(), parentObject);
                        }

                        List beanList = FieldUtil.getAsBeanListFromMapList(maps, FacilioConstants.ContextNames.getClassFromModule(module));
                        insertRecordBuilder.addRecords(beanList);
                        insertRecordBuilder.save();

                        recordMap.put(moduleName, beanList);
                    }

                    context.put(FacilioConstants.ContextNames.RECORD_MAP, recordMap);
                 }
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
}
