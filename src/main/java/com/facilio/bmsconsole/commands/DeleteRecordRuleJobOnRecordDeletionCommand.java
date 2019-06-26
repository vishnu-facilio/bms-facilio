package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.SingleRecordRuleAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

public class DeleteRecordRuleJobOnRecordDeletionCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		Long recordId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		Long moduleId = (Long) context.get(FacilioConstants.ContextNames.MODULE_ID);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleId);
		SingleRecordRuleAPI.deleteRuleJobDuringRecordDeletion(recordId, module);
		return false;
	}

}
