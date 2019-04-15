package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.SkillContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;

public class AddSkillCommand implements Command {
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
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
