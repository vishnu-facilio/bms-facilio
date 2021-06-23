package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.SkillContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.fields.FacilioField;

public class AddSkillCommand extends FacilioCommand {
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		SkillContext skill = (SkillContext) context.get(FacilioConstants.ContextNames.SKILL);
		System.out.println("############### skill :"+skill);
		if(skill != null) 
		{
				
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			
			InsertRecordBuilder<SkillContext> builder = new InsertRecordBuilder<SkillContext>()
															.moduleName(moduleName)
															.table(dataTableName)
															.fields(fields);
															
			long id = builder.insert(skill);
			skill.setId(id);
			
			context.put(FacilioConstants.ContextNames.RECORD_ID, id);
		}
		else 
		{
			throw new IllegalArgumentException("Skill Object cannot be null");
		}
		return false;
	}
}
