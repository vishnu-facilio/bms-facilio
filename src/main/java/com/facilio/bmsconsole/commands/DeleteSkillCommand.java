package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.SkillContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.DBUtil;
import com.facilio.sql.GenericDeleteRecordBuilder;
import com.facilio.transaction.FacilioConnectionPool;

public class DeleteSkillCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		//SkillContext skill = (SkillContext) context.get(FacilioConstants.ContextNames.SKILL);
		long skillId = (long) context.get(FacilioConstants.ContextNames.ID);
		if(skillId != -1) {
			GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
					.connection(((FacilioContext)context).getConnectionWithTransaction())
					.table("Skills")
					.where("ID = ?", skillId);
			builder.delete();
			context.put(FacilioConstants.ContextNames.RESULT, "success");
		}
		return false;
		}

}
