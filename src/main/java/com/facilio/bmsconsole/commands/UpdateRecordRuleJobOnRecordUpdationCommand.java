package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.SingleRecordRuleAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.UpdateChangeSet;

public class UpdateRecordRuleJobOnRecordUpdationCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		Map<Long, List<UpdateChangeSet>> changeSet = (Map<Long, List<UpdateChangeSet>>)context.get(FacilioConstants.ContextNames.CHANGE_SET_MAP);
		Long recordId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		Long moduleId = (Long) context.get(FacilioConstants.ContextNames.MODULE_ID);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleId);
		SingleRecordRuleAPI.updateRuleJobDuringRecordUpdation(recordId, module, changeSet);
		
		return false;
	}

}
