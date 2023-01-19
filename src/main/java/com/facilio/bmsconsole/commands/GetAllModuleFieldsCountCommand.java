package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetAllModuleFieldsCountCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        boolean handleStateField = (boolean) context.getOrDefault("handleStateField", false);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        String searchString = (String) context.get(FacilioConstants.ContextNames.SEARCH);

        long count = 0;
        if(moduleName != null && !moduleName.isEmpty()) {
            if(LookupSpecialTypeUtil.isSpecialType(moduleName)) {
                List<FacilioField> allFields = LookupSpecialTypeUtil.getAllFields(moduleName);
                count = CollectionUtils.isNotEmpty(allFields) ? allFields.size() : 0;
                context.put(FacilioConstants.ContextNames.COUNT, count);
                return false;
            }

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(moduleName);
            FacilioUtil.throwIllegalArgumentException(module == null, "Invalid module while getting module");
            List<Long> extendedModuleIds = module.getExtendedModuleIds();

            List<FacilioField> moduleFields = FieldFactory.getFieldFields();
            Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(moduleFields);

            GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                    .table("Fields")
                    .select(FieldFactory.getCountField())
                    .andCondition(CriteriaAPI.getCondition("MODULEID", "moduleId", StringUtils.join(extendedModuleIds, ","), NumberOperators.EQUALS));

            if (StringUtils.isNotEmpty(searchString)) {
                selectBuilder.andCondition(CriteriaAPI.getCondition("DISPLAY_NAME", "displayName", searchString, StringOperators.CONTAINS));
            }

            if (!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.TENANTS)) {
                Criteria tenantCriteria = new Criteria();
                tenantCriteria.addAndCondition(CriteriaAPI.getCondition(fieldsMap.get("name"), "tenant", StringOperators.ISN_T));
                selectBuilder.andCriteria(tenantCriteria);
            }
            if (moduleName.equals("workorder")) {
                if (!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.SAFETY_PLAN)) {
                    Criteria safetyPlanCriteria = new Criteria();
                    safetyPlanCriteria.addAndCondition(CriteriaAPI.getCondition(fieldsMap.get("name"), "safetyPlan", StringOperators.ISN_T));
                    selectBuilder.andCriteria(safetyPlanCriteria);
                }
            }

            if (handleStateField) {
                Criteria stateFieldCriteria = new Criteria();
                stateFieldCriteria.addAndCondition(CriteriaAPI.getCondition(fieldsMap.get("name"), "stateFlowId", StringOperators.ISN_T));
                if (!module.isStateFlowEnabled()) {
                    stateFieldCriteria.addAndCondition(CriteriaAPI.getCondition(fieldsMap.get("name"), "moduleState", StringOperators.ISN_T));
                }
                selectBuilder.andCriteria(stateFieldCriteria);
            }

            Map<String, Object> modulesMap = selectBuilder.fetchFirst();
            count = MapUtils.isNotEmpty(modulesMap) ? (long) modulesMap.get("count") : 0;

            context.put(FacilioConstants.ContextNames.COUNT, count);
        }
        return false;
    }
}
