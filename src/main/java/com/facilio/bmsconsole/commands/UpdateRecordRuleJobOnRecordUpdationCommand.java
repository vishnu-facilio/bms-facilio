package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.SingleRecordRuleAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.UpdateChangeSet;

public class UpdateRecordRuleJobOnRecordUpdationCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		Map<Long, List<UpdateChangeSet>> changeSet = (Map<Long, List<UpdateChangeSet>>)context.get(FacilioConstants.ContextNames.CHANGE_SET);
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if(CollectionUtils.isEmpty(recordIds)) {
			Long recordId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
			if(recordId != null) {
				recordIds = Collections.singletonList(recordId);
			}
		}
		if(CollectionUtils.isNotEmpty(recordIds) && changeSet != null && !changeSet.isEmpty()) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			for(Long recordId : recordIds) {
				SingleRecordRuleAPI.updateRuleJobDuringRecordUpdation(recordId, module, changeSet.get(recordId));
			}
		}
		return false;
	}

}
