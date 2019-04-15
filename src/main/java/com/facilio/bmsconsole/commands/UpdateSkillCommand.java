package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.SkillContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;

public class UpdateSkillCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
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
