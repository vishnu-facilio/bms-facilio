package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.controlaction.context.ControlGroupContext;
import com.facilio.controlaction.util.ControlActionUtil;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class AddControlGroupCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		List<ControlGroupContext> controlGroups = (List<ControlGroupContext>) context.get(ControlActionUtil.CONTROL_ACTION_GROUP_CONTEXTS);
		
		if(controlGroups != null) {
			for(ControlGroupContext controlGroup :controlGroups) {
				
				controlGroup.setOrgId(AccountUtil.getCurrentOrg().getId());
				
				Map<String, Object> props = FieldUtil.getAsProperties(controlGroup);
				
				GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
						.table(ModuleFactory.getControlGroupModule().getTableName())
						.fields(FieldFactory.getControlGroupFields())
						.addRecord(props);
				
				insertRecordBuilder.save();
				
				controlGroup.setId((Long) props.get("id"));
			}
		}
		return false;
	}

}
