package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;

public class UpdateFieldCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		FacilioField field = (FacilioField) context.get(FacilioConstants.ContextNames.MODULE_FIELD);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		context.put(FacilioConstants.ContextNames.ROWS_UPDATED, modBean.updateField(field));
		return false;
	}

}
