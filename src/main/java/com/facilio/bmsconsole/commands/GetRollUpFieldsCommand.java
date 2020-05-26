package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.RollUpField;
import com.facilio.bmsconsole.util.RollUpFieldUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

public class GetRollUpFieldsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule parentModule = modBean.getModule(moduleName);
		if(parentModule == null) {
			throw new IllegalArgumentException("Please provide a valid parent Module.");
		}

		List<RollUpField> rollUpFields = RollUpFieldUtil.getRollUpFieldsByParentModuleId(parentModule, true);
		context.put(FacilioConstants.ContextNames.ROLL_UP_FIELDS, rollUpFields);

		return false;
	}

}
