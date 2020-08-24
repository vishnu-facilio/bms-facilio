package com.facilio.v3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.*;

public class DeleteSubModuleRecordCommand extends FacilioCommand {
    //TODO handle bulk case
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List<Long>> deleteRecordIdMap = Constants.getDeleteRecordIdMap(context);
        if (MapUtils.isEmpty(deleteRecordIdMap)) {
            return false;
        }

        Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);
        String moduleName = Constants.getModuleName(context);

        List<ModuleBaseWithCustomFields> record = recordMap.get(moduleName);
        long id = record.get(0).getId();

        Set<String> subModNameList = deleteRecordIdMap.keySet();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        for (String subModName: subModNameList) {
            FacilioModule module = modBean.getModule(subModName);
            Map<String, LookupField> allLookupFields = getAllLookupFields(modBean, module);
            LookupField lookupField = allLookupFields.get(moduleName);

            DeleteRecordBuilder<ModuleBaseWithCustomFields> builder = new DeleteRecordBuilder<>()
                            .module(module)
                            .andCondition(CriteriaAPI.getIdCondition(deleteRecordIdMap.get(subModName), module))
                            .andCondition(CriteriaAPI.getCondition(lookupField, id+"", NumberOperators.EQUALS));
            if (module.getTypeEnum() == FacilioModule.ModuleType.SUB_ENTITY) {
                builder.delete();
            } else {
                builder.markAsDelete();
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
