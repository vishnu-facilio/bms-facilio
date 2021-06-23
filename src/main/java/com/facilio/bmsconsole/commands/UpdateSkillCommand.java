package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.SkillContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;

public class UpdateSkillCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
				SkillContext skill = (SkillContext) context.get(FacilioConstants.ContextNames.SKILL);
		
		if(skill != null) 
		{
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			
			UpdateRecordBuilder<SkillContext> builder = new UpdateRecordBuilder<SkillContext>()
														.moduleName(moduleName)
														.table(dataTableName)
														.fields(fields)
														.andCustomWhere("ID = ?", skill.getId());
			builder.update(skill);
		}
		else 
		{
			throw new IllegalArgumentException("Skill Object cannot be null");
		}
		return false;
	}

}
