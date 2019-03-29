package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.SkillContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericDeleteRecordBuilder;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class DeleteSkillCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		SkillContext skill = (SkillContext) context.get(FacilioConstants.ContextNames.SKILL);
		long skillId = skill.getId();
		if(skillId != -1) {
			GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
					.table("Skills")
					.andCustomWhere("ID = ?", skillId);
			builder.delete();
			context.put(FacilioConstants.ContextNames.RESULT, "success");
		}
		return false;
		}

}
