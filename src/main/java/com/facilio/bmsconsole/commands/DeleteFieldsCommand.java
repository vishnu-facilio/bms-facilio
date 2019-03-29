package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;

public class DeleteFieldsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<Long> fieldIds = (List<Long>) context.get(FacilioConstants.ContextNames.MODULE_FIELD_IDS);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		context.put(FacilioConstants.ContextNames.ROWS_UPDATED, modBean.deleteFields(fieldIds));
		return false;
	}

}
