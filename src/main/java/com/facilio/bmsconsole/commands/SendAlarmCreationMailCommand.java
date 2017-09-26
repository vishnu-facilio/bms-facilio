package com.facilio.bmsconsole.commands;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.workflow.DefaultTemplates;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.OrgInfo;
import com.facilio.fw.UserInfo;
import com.facilio.sql.GenericInsertRecordBuilder;

public class SendAlarmCreationMailCommand implements Command {

	private static final String EMAIL1 = "prabhu@facilio.com";
	private static final String EMAIL2 = "raj@facilio.com";
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		AlarmContext alarm = (AlarmContext) context.get(FacilioConstants.ContextNames.ALARM);
		if(alarm != null) {
			Connection conn = ((FacilioContext) context).getConnectionWithTransaction();
			addFollowers(alarm, conn);
			sendEmail(alarm);
		}
		return false;
	}
	
	private void sendEmail(AlarmContext alarm) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, Exception {
		Map<String, Object> placeHolders = new HashMap<>();
		CommonCommandUtil.appendModuleNameInKey(FacilioConstants.ContextNames.ALARM, FacilioConstants.ContextNames.ALARM, FieldUtil.getAsProperties(alarm), placeHolders);
		CommonCommandUtil.appendModuleNameInKey(null, "org", FieldUtil.getAsProperties(OrgInfo.getCurrentOrgInfo()), placeHolders);
		CommonCommandUtil.appendModuleNameInKey(null, "user", FieldUtil.getAsProperties(UserInfo.getCurrentUser()), placeHolders);
		
		placeHolders.put("follower.email", EMAIL1);
		JSONObject mailJson = DefaultTemplates.ALARM_CREATION_EMAIL.getTemplate(placeHolders);
		AwsUtil.sendEmail(mailJson);
		
		mailJson.put("to", EMAIL2);
		AwsUtil.sendEmail(mailJson);
		
	}
	
	private void addFollowers(AlarmContext alarm, Connection conn) throws SQLException, RuntimeException {
		String tableName = "AlarmFollowers";
		
		FacilioField alarmId = new FacilioField();
		alarmId.setName("alarmId");
		alarmId.setDataType(FieldType.NUMBER);
		alarmId.setColumnName("ALARM_ID");
		alarmId.setModuleTableName(tableName);
		
		FacilioField followerType = new FacilioField();
		followerType.setName("type");
		followerType.setDataType(FieldType.STRING);
		followerType.setColumnName("FOLLOWER_TYPE");
		followerType.setModuleTableName(tableName);
		
		FacilioField follower = new FacilioField();
		follower.setName("follower");
		follower.setDataType(FieldType.STRING);
		follower.setColumnName("FOLLOWER");
		follower.setModuleTableName(tableName);
		
		List<FacilioField> fields = new ArrayList<>();
		fields.add(alarmId);
		fields.add(followerType);
		fields.add(follower);
		
		Map<String, Object> followerProps1 = new HashMap<>();
		followerProps1.put("alarmId", alarm.getId());
		followerProps1.put("type", "email");
		followerProps1.put("follower", EMAIL1);
		
		Map<String, Object> followerProps2 = new HashMap<>();
		followerProps2.put("alarmId", alarm.getId());
		followerProps2.put("type", "email");
		followerProps2.put("follower", EMAIL2);
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.connection(conn)
				.table("AlarmFollowers")
				.fields(fields)
				.addRecord(followerProps1)
				.addRecord(followerProps2);
		
		insertBuilder.save();
		
	}

}
