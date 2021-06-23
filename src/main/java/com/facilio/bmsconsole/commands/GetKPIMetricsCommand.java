package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.KPIUtil;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;

public class GetKPIMetricsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		String moduleName = (String) context.get(ContextNames.MODULE_NAME);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> fields = KPIUtil.getKPIMetrics(modBean.getModule(moduleName));
		context.put(ContextNames.FIELDS, fields);
		
		return false;
	}

}
