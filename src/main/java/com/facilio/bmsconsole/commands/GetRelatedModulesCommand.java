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
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetRelatedModulesCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        Integer moduleType = (Integer) context.get(FacilioConstants.ContextNames.MODULE_TYPE);
        JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
        String searchString = (String) context.get(FacilioConstants.ContextNames.SEARCH);

        if (moduleType == null || moduleType <= 0) {
            moduleType = FacilioModule.ModuleType.BASE_ENTITY.getValue();
        }

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioModule> relatedModules = modBean.getSubModules(moduleName, pagination, searchString, FacilioModule.ModuleType.valueOf(moduleType));

        List<Map<String, Object>> relatedFieldsVsModulesMap = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(relatedModules)) {
            FacilioModule currModule = modBean.getModule(moduleName);
            for (FacilioModule relatedModule : relatedModules) {
                List<FacilioField> allFields = modBean.getAllFields(relatedModule.getName(), null, null, fetchLookupFieldsCriteria());
                allFields.stream()
                    .filter(field -> ((LookupField) field).getLookupModuleId() == currModule.getModuleId())
                    .forEach(field -> {
                        Map<String, Object> fieldVsModuleMap = new HashMap<>();
                        fieldVsModuleMap.put("fieldName", field.getDisplayName());
                        fieldVsModuleMap.put("module", relatedModule);
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
