package com.facilio.trigger.command;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.trigger.context.Trigger;
import com.facilio.trigger.util.TriggerUtil;

public class DeleteTriggerCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		Trigger trigger = (Trigger)context.get(TriggerUtil.TRIGGER_CONTEXT);
		
		GenericDeleteRecordBuilder delete = new GenericDeleteRecordBuilder()
				.table(ModuleFactory.getTriggerModule().getTableName())
				.andCondition(CriteriaAPI.getIdCondition(trigger.getId(), ModuleFactory.getTriggerModule()));
		
		delete.delete();
		
		return false;
	}

}
