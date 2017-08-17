package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.SkillContext;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.bmsconsole.util.SkillAPI;
import com.facilio.constants.FacilioConstants;

public class UpdateSkillCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		SkillContext skill = (SkillContext) context.get(FacilioConstants.ContextNames.SKILL);
		
		if(skill != null) 
		{
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
			Connection conn = ((FacilioContext) context).getConnectionWithTransaction();
			
			UpdateRecordBuilder<SkillContext> builder = new UpdateRecordBuilder<SkillContext>()
														.moduleName(moduleName)
														.dataTableName(dataTableName)
														.connection(conn);
			builder.update(skill);
		}
		else 
		{
			throw new IllegalArgumentException("Skill Object cannot be null");
		}
		return false;
	}

}
