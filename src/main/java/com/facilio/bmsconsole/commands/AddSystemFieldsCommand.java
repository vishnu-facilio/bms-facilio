package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioModule.ModuleType;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

public class AddSystemFieldsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.MODULE_FIELD_LIST);
		FacilioModule module = (FacilioModule) context.get(FacilioConstants.ContextNames.MODULE);
		if (module != null) {
			// add only for custom module
			if (module.getTypeEnum() != ModuleType.CUSTOM) {
				return false;
			}
			
			if (fields == null) {
				fields = new ArrayList<>();
				context.put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, fields);
			}
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			
			Boolean supportStateFlow = (Boolean) context.get(FacilioConstants.ContextNames.SUPPORT_STATEFLOW);
			if (supportStateFlow != null && supportStateFlow) {
				LookupField moduleStateField = (LookupField) FieldFactory.getField("moduleState", "Status", "MODULE_STATE", module, FieldType.LOOKUP);
				moduleStateField.setLookupModule(modBean.getModule("ticketstatus"));
				fields.add(moduleStateField);
				
				fields.add(FieldFactory.getField("stateFlowId", "State Flow Id", "STATE_FLOW_ID", module, FieldType.NUMBER));
			}
		}
		return false;
	}

}
