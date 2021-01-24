package com.facilio.trigger.command;

import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.ModuleFactory;
import com.facilio.trigger.context.BaseTriggerContext;
import com.facilio.trigger.util.TriggerUtil;

public class DeleteTriggerCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		long triggerId = (long) context.get(FacilioConstants.ContextNames.ID);
		
		GenericDeleteRecordBuilder delete = new GenericDeleteRecordBuilder()
				.table(ModuleFactory.getTriggerModule().getTableName())
				.andCondition(CriteriaAPI.getIdCondition(triggerId, ModuleFactory.getTriggerModule()));
		
		delete.delete();
		
		return false;
	}

}
