package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.SkillContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

public class GetSkillCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		long skillId = (long) context.get(FacilioConstants.ContextNames.ID);
		
		if(skillId > 0) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			
			
			SelectRecordsBuilder<SkillContext> builder = new SelectRecordsBuilder<SkillContext>()
					.table(dataTableName)
					.moduleName(moduleName)
					.beanClass(SkillContext.class)
					.select(fields)
					.andCustomWhere("ID = ?", skillId)
					.orderBy("ID");

			List<SkillContext> skills = builder.get();	
			if(skills.size() > 0) {
				SkillContext skill = skills.get(0);
				context.put(FacilioConstants.ContextNames.SKILL, skill);
			}
		}
		else {
			throw new IllegalArgumentException("Invalid Skill ID : "+skillId);
		}
		
		return false;
	}

}
