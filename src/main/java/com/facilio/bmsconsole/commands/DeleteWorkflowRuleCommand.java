package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericDeleteRecordBuilder;

public class DeleteWorkflowRuleCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<Long> ruleIds = (List<Long>) context.get(FacilioConstants.ContextNames.ID);
		FacilioModule module = ModuleFactory.getWorkflowRuleModule();
		
		GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getIdCondition(ruleIds, module))
														;
		
		deleteBuilder.delete();
		context.put(FacilioConstants.ContextNames.RESULT, true);
		return false;
	}

}
