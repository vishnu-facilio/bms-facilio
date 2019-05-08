package com.facilio.bmsconsole.commands;

//import java.sql.Connection;

import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddAlarmFollowersCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		AlarmContext alarm = (AlarmContext) context.get(FacilioConstants.ContextNames.ALARM);
		if(alarm != null) {
			//Connection conn = ((FacilioContext) context).getConnectionWithTransaction();
			List<String> emails = (List<String>) context.get(FacilioConstants.Workflow.NOTIFIED_EMAILS);
			List<String> smsList = (List<String>) context.get(FacilioConstants.Workflow.NOTIFIED_SMS);
			
			if((emails != null && !emails.isEmpty()) || (smsList != null && !smsList.isEmpty())) {
				addFollowers(alarm, emails, smsList);
			}
		}
		return false;
	}
	
	private void addFollowers(AlarmContext alarm, List<String> emails, List<String> smsList) throws SQLException, RuntimeException {
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table("AlarmFollowers")
				.fields(FieldFactory.getAlarmFollowersFeilds());
		
		if(emails != null && !emails.isEmpty()) {
			for(String email : emails) {
				Map<String, Object> followerProps = new HashMap<>();
				followerProps.put("alarmId", alarm.getId());
				followerProps.put("type", "email");
				followerProps.put("follower", email);
				insertBuilder.addRecord(followerProps);
			}
		}
		
		if(smsList != null && !smsList.isEmpty()) {
			for(String sms : smsList) {
				Map<String, Object> followerProps = new HashMap<>();
				followerProps.put("alarmId", alarm.getId());
				followerProps.put("type", "mobile");
				followerProps.put("follower", sms);
				insertBuilder.addRecord(followerProps);
			}
		}
		
		insertBuilder.save();
		
	}

}
