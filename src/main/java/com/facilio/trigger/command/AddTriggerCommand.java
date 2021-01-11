package com.facilio.trigger.command;

import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.trigger.context.BaseTriggerContext;
import com.facilio.trigger.util.TriggerUtil;

public class AddTriggerCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		BaseTriggerContext trigger = (BaseTriggerContext)context.get(TriggerUtil.TRIGGER_CONTEXT);
		
		Map<String, Object> props = FieldUtil.getAsProperties(trigger);
		
		GenericInsertRecordBuilder insert = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getTriggerModule().getTableName())
				.fields(FieldFactory.getTriggerFields())
				.addRecord(props);
		
		insert.save();
		
		trigger.setId((long)props.get("id"));
		
		return false;
	}

}
