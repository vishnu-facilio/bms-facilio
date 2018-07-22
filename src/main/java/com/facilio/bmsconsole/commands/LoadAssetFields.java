package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class LoadAssetFields implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		List<FacilioField> fields = new ArrayList(modBean.getAllFields(moduleName));
		if (!moduleName.equals(FacilioConstants.ContextNames.ASSET)) {
			List<FacilioField> customFields = modBean.getAllCustomFields("asset");
			if (customFields != null) {
				fields.addAll(modBean.getAllCustomFields("asset"));
			}
		}
		context.put(FacilioConstants.ContextNames.EXISTING_FIELD_LIST, fields);

		
//		fields.addAll(modBean.getAllCustomFields("asset"));
		
		return false;
	}

}
