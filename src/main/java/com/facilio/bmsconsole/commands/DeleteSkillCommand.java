package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.SkillContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;

public class DeleteSkillCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
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
