package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import freemarker.template.utility.StringUtil;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.CommissioningLogContext;
import com.facilio.bmsconsole.util.CommissioningApi;
import com.facilio.constants.FacilioConstants.ContextNames;
import org.apache.commons.lang3.StringUtils;

public class UpdateCommissioningCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {

		CommissioningLogContext log = (CommissioningLogContext) context.get(ContextNames.LOG);
		CommissioningLogContext oldLog = CommissioningApi.commissioniongDetails(log.getId(), false);
		
		log.setControllerType(oldLog.getControllerType());
		CommissioningApi.setPointContext(log);
		CommissioningApi.filterAndValidatePointsOnUpdate(log, oldLog);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(ContextNames.COMMISSIONING_LOG);
		if(module == null){
			log.setSysModifiedTime(System.currentTimeMillis());
		}
		CommissioningApi.updateLog(log);

		return false;
	}
	
}
