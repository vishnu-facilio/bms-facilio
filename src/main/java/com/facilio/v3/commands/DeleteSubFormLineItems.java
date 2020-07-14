package com.facilio.v3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.context.SubFormContext;
import com.facilio.v3.context.V3Context;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class DeleteSubFormLineItems extends FacilioCommand {
    private List<ModuleBaseWithCustomFields> getRecord(Context context) {
        Map<String, List<ModuleBaseWithCustomFields>> recordMap = (Map<String, List<ModuleBaseWithCustomFields>>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        return recordMap.get(moduleName);
    }
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<ModuleBaseWithCustomFields> recordList = getRecord(context);
        String mainModuleName = Constants.getModuleName(context);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        for (ModuleBaseWithCustomFields record: recordList) {
            V3Context v3Context = (V3Context) record;
            Map<String, List<SubFormContext>> lineItems = v3Context.getRelations();
            if (MapUtils.isEmpty(lineItems)) {
                continue;
            }

            Set<String> moduleNameList = lineItems.keySet();
            for (String moduleName: moduleNameList) {
                List<SubFormContext> subFormContextList = lineItems.get(moduleName);
                for (SubFormContext subFormContext: subFormContextList) {
                    if (CollectionUtils.isEmpty(subFormContext.getDeleteIds())) {
                        continue;
                    }
                    FacilioModule module = modBean.getModule(moduleName);

                    Map<String, List<LookupField>> allLookupFields = getAllLookupFields(modBean, module);
                    List<LookupField> lookupFieldList = allLookupFields.get(mainModuleName);
                    Map<Long, FacilioField> fieldMap = new HashMap<>();
                    for (LookupField lookupField: lookupFieldList) {
                        fieldMap.put(lookupField.getId(), lookupField);
                    }

                    DeleteRecordBuilder<ModuleBaseWithCustomFields> builder = new DeleteRecordBuilder<>()
                            .module(module)
                            .andCondition(CriteriaAPI.getIdCondition(subFormContext.getDeleteIds(), module))
                            .andCondition(CriteriaAPI.getCondition(fieldMap.get(subFormContext.getFieldId()), record.getId()+"", NumberOperators.EQUALS));
                    builder.markAsDelete();
                }
            }
        }
        return false;
    }

    private Map<String, List<LookupField>> getAllLookupFields(ModuleBean modBean, FacilioModule module) throws Exception {
        List<LookupField> lookupFields = new ArrayList<>();
        List<FacilioField> allFields = modBean.getAllFields(module.getName());
        if (CollectionUtils.isNotEmpty(allFields)) {
            for (FacilioField f : allFields) {
                if (f instanceof LookupField) {
                    lookupFields.add((LookupField) f);
                }
            }
        }

        Map<String, List<LookupField>> lookupFieldMap = new HashMap<>();
        for (LookupField l : lookupFields) {
            FacilioModule lookupModule = l.getLookupModule();
            if (lookupModule != null) {
                lookupFieldMap.computeIfAbsent(lookupModule.getName(), k -> new ArrayList<>());
                lookupFieldMap.get(lookupModule.getName()).add(l);
            }
        }
        return lookupFieldMap;
    }
}
