package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.constants.FacilioConstants;

public class LoadAllFieldsCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		
		if(moduleName == null || moduleName.isEmpty()) {
			throw new IllegalArgumentException("Module Name is not set for the module");
		}
 		
		context.put(FacilioConstants.ContextNames.EXISTING_FIELD_LIST, FieldUtil.getAllFields(moduleName, ((FacilioContext) context).getConnectionWithoutTransaction()));
		
		return false;
	}
}
