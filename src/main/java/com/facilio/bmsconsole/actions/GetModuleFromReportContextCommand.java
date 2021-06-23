package com.facilio.bmsconsole.actions;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.report.context.ReportContext;

public class GetModuleFromReportContextCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		ReportContext reportContext = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
		if (reportContext.getModuleId() > 0) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule facilioModule = modBean.getModule(reportContext.getModuleId());
			context.put(FacilioConstants.ContextNames.MODULE, facilioModule);
		}
		return false;
	}

}
