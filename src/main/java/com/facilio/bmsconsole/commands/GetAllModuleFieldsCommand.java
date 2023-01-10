package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetAllModuleFieldsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        boolean skipStateField = (boolean) context.getOrDefault("skipStateField", false);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
        String searchString = (String) context.get(FacilioConstants.ContextNames.SEARCH);

        if(moduleName != null && !moduleName.isEmpty()) {
            Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(FieldFactory.getFieldFields());
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(moduleName);

            Criteria filterCriteria = new Criteria();

            if (!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.TENANTS)) {
                Criteria tenantCriteria = new Criteria();
                tenantCriteria.addAndCondition(CriteriaAPI.getCondition(fieldsMap.get("name"), "tenant", StringOperators.ISN_T));
                filterCriteria.andCriteria(tenantCriteria);
            }

            if (moduleName.equals("workorder") && !AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.SAFETY_PLAN)) {
                Criteria safetyPlanCriteria = new Criteria();
                safetyPlanCriteria.addAndCondition(CriteriaAPI.getCondition(fieldsMap.get("name"), "safetyPlan", StringOperators.ISN_T));
                filterCriteria.andCriteria(safetyPlanCriteria);
            }

            if (skipStateField) {
                Criteria stateFieldCriteria = new Criteria();
                stateFieldCriteria.addAndCondition(CriteriaAPI.getCondition(fieldsMap.get("name"), "stateFlowId", StringOperators.ISN_T));
                if (!module.isStateFlowEnabled()) {
                    stateFieldCriteria.addAndCondition(CriteriaAPI.getCondition(fieldsMap.get("name"), "moduleState", StringOperators.ISN_T));
                }
                filterCriteria.andCriteria(stateFieldCriteria);
            }

            List<FacilioField> fields = new ArrayList<>(modBean.getAllFields(moduleName, pagination, searchString, filterCriteria));

            context.put(FacilioConstants.ContextNames.FIELDS, fields);
        }

        return false;
    }
}
