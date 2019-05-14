package com.facilio.workflowv2.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.workflowv2.contexts.DBParamContext;
import com.facilio.workflowv2.contexts.Value;

public class WorkflowV2Util {
	
	
	public static String getModuleClassNameFromModuleName(String moduleName) {
		return "com.facilio.workflowv2.modulefunctions.Facilio"+moduleName+"ModuleFunctions";
	}

	public static void fillExtraInfo(Value paramValue,FacilioModule module,ModuleBean modBean) throws Exception {
		
		if(paramValue.asObject() instanceof DBParamContext) {
			DBParamContext dbParamContext = (DBParamContext)paramValue.asObject();
			for(String key :dbParamContext.getCriteria().getConditions().keySet()) {
				Condition condition = dbParamContext.getCriteria().getConditions().get(key);
				FacilioField field = modBean.getField(condition.getFieldName(), module.getName());
				condition.setField(field);
			}
		}
		
	}
	
}
