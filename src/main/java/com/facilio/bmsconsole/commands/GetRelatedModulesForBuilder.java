package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class GetRelatedModulesForBuilder extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        List<FacilioModule.ModuleType> moduleTypes = new ArrayList<>(Arrays.asList(FacilioModule.ModuleType.BASE_ENTITY, FacilioModule.ModuleType.Q_AND_A_RESPONSE, FacilioModule.ModuleType.Q_AND_A));

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioModule> relatedModules = modBean.getSubModules(moduleName, null, null,moduleTypes.toArray(new FacilioModule.ModuleType[]{}));

        List<Map<String, Object>> relatedFieldsVsModulesMap = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(relatedModules)) {
            FacilioModule currModule = modBean.getModule(moduleName);
            for (FacilioModule relatedModule : relatedModules) {
                List<FacilioField> allFields = modBean.getAllFields(relatedModule.getName(), null, null, fetchLookupFieldsCriteria());
                allFields.stream()
                        .filter(field -> ((LookupField) field).getLookupModuleId() == currModule.getModuleId())
                        .forEach(field -> {
                            Map<String, Object> fieldVsModuleMap = new HashMap<>();
                            fieldVsModuleMap.put("subModuleName", relatedModule.getName());
                            fieldVsModuleMap.put("displayName", relatedModule.getDisplayName());
                            fieldVsModuleMap.put("subModuleId", relatedModule.getModuleId());
                            fieldVsModuleMap.put("fieldId", field.getFieldId());
                            fieldVsModuleMap.put("fieldName", field.getName());
                            relatedFieldsVsModulesMap.add(fieldVsModuleMap);
                        });
            }
        }

        context.put(FacilioConstants.ContextNames.MODULE_LIST, relatedFieldsVsModulesMap);
        return false;

    }

    public static Criteria fetchLookupFieldsCriteria() {
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("DATA_TYPE", "dataType", String.valueOf(FieldType.LOOKUP.getTypeAsInt()), StringOperators.IS));
        return criteria;
    }
}
