package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class AddFieldsCommand implements Command {
	
	@Override
	public boolean execute(Context context) throws Exception {
		
		//Have to be converted to batch insert
		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.MODULE_FIELD_LIST);
		
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		long moduleId = (long) context.get(FacilioConstants.ContextNames.MODULE_ID);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean", ((FacilioContext) context).getConnectionWithTransaction());
		
		for(FacilioField field : fields) {
			
			field.setModuleId(moduleId);
			field.setModuleName(moduleName);
			
			long fieldId = modBean.addField(field);
			field.setFieldId(fieldId);
		}
		
		return false;
	}	
}