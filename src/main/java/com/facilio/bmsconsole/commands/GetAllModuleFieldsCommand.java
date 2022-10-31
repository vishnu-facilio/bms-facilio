package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GetAllModuleFieldsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        boolean handleStateField = (boolean) context.getOrDefault("handleStateField", false);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
        String searchString = (String) context.get(FacilioConstants.ContextNames.SEARCH);

        if(moduleName != null && !moduleName.isEmpty()) {
            List<FacilioField> fields = new ArrayList<>();
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(moduleName);
            fields.addAll(modBean.getAllFields(moduleName, pagination, searchString));

            List<FacilioField> restrictedFields = new ArrayList<FacilioField>();
            for(int i=0; i<fields.size(); i++) {
                if(fields.get(i).getName().equals("tenant")) {
                    if(!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.TENANTS)) {
                        continue;
                    }
                }
                if(moduleName.equals("workorder") && fields.get(i).getName().equals("safetyPlan")) {
                    if(!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.SAFETY_PLAN)) {
                        continue;
                    }
                }
                else if (handleStateField) {
                    fields.removeIf(field -> field.getName().equals("stateFlowId") || (field.getName().equals("moduleState") && !module.isStateFlowEnabled()));
                }
                restrictedFields.add(fields.get(i));
            }
            context.put(FacilioConstants.ContextNames.FIELDS, restrictedFields);
        }

        return false;
    }
}
