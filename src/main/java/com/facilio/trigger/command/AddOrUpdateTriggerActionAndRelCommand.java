package com.facilio.trigger.command;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.trigger.context.BaseTriggerContext;
import com.facilio.trigger.context.TriggerAction;
import com.facilio.trigger.util.TriggerUtil;
import org.apache.commons.chain.Context;

import java.util.Map;

public class AddOrUpdateTriggerActionAndRelCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		BaseTriggerContext trigger = (BaseTriggerContext)context.get(TriggerUtil.TRIGGER_CONTEXT);
		if(trigger.getTriggerActions() != null) {
			for(TriggerAction action : trigger.getTriggerActions()) {
				if(action.getId() < 0) {
					action.setTriggerId(trigger.getId());
					Map<String, Object> props = FieldUtil.getAsProperties(action);
					GenericInsertRecordBuilder insert = new GenericInsertRecordBuilder()
							.table(ModuleFactory.getTriggerActionModule().getTableName())
							.fields(FieldFactory.getTriggerActionFields())
							.addRecord(props);
					insert.save();
					action.setId((long)props.get("id"));
				}
				else {
					GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
							.table(ModuleFactory.getTriggerActionModule().getTableName())
							.fields(FieldFactory.getTriggerActionFields())
							.andCondition(CriteriaAPI.getIdCondition(action.getId(), ModuleFactory.getTriggerActionModule()));
					builder.update(FieldUtil.getAsProperties(action));
				}
			}
		}
		return false;
	}

}
