package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.controlaction.context.ControlGroupContext;
import com.facilio.controlaction.context.ControlGroupInclExclContext;
import com.facilio.controlaction.util.ControlActionUtil;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class AddControlGroupInclExclCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		
		List<ControlGroupContext> controlGroups = (List<ControlGroupContext>) context.get(ControlActionUtil.CONTROL_ACTION_GROUP_CONTEXTS);
		
		if(controlGroups != null) {
			
			GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
					.table(ModuleFactory.getControlGroupInclExclModule().getTableName())
					.fields(FieldFactory.getControlGroupInclExclFields());
			
			List<Map<String, Object>> propList = new ArrayList<Map<String,Object>>();
			
			for(ControlGroupContext controlGroup :controlGroups) {
				
				if(controlGroup.getControlGroupInclExclContexts() != null) {
					
					for( ControlGroupInclExclContext resources : controlGroup.getControlGroupInclExclContexts()) {
						
						resources.setOrgId(AccountUtil.getCurrentOrg().getId());
						resources.setControlGroupId(controlGroup.getId());
						
						Map<String, Object> props = FieldUtil.getAsProperties(resources);
						
						propList.add(props);
						
					}
				}
				
			}
			if(!propList.isEmpty()) {
				insertRecordBuilder.addRecords(propList);
				insertRecordBuilder.save();
			}
		}
		return false;
	}

}
