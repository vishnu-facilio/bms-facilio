package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;

import java.util.List;

public class GetModulesWithDateFieldCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioModule> moduleList = modBean.getCustomModulesWithDateFieldList();

		context.put(FacilioConstants.ContextNames.MODULE_LIST, moduleList);
		return false;
	}

}
