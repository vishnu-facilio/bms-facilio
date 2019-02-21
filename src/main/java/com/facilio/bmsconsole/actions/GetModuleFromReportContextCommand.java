package com.facilio.bmsconsole.actions;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.struts2.components.Bean;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.report.context.ReportContext;

public class GetModuleFromReportContextCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		ReportContext reportContext = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
		if (reportContext.getModuleId() > 0) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule facilioModule = modBean.getModule(reportContext.getModuleId());
			context.put(FacilioConstants.ContextNames.MODULE, facilioModule);
		}
		return false;
	}

}
