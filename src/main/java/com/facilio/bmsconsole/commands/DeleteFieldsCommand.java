package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class DeleteFieldsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<Long> fieldIds = (List<Long>) context.get(FacilioConstants.ContextNames.MODULE_FIELD_IDS);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		context.put(FacilioConstants.ContextNames.ROWS_UPDATED, modBean.deleteFields(fieldIds));
		return false;
	}

}
