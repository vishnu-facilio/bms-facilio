package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.controlaction.context.ControlGroupContext;
import com.facilio.controlaction.util.ControlActionUtil;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class DeleteControlGroupCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		
		List<ControlGroupContext> controlGroups = (List<ControlGroupContext>) context.get(ControlActionUtil.CONTROL_ACTION_GROUP_CONTEXTS);
		
		if(controlGroups != null) {
			for(ControlGroupContext controlGroup :controlGroups) {
				
				if(controlGroup.getId() > 0) {
					
					controlGroup.setIsDeleted(true);
					
					Map<String, Object> props = FieldUtil.getAsProperties(controlGroup);
					
					GenericUpdateRecordBuilder update = new GenericUpdateRecordBuilder()
							.table(ModuleFactory.getControlGroupModule().getTableName())
							.fields(FieldFactory.getControlGroupFields())
							.andCondition(CriteriaAPI.getIdCondition(controlGroup.getId(), ModuleFactory.getControlGroupModule()))
							;
					
					update.update(props);
					
				}
				
				//ControlActionUtil.deleteDependenciesForControlGroup(controlGroup);
			}
		}
		return false;
		
	}
}
