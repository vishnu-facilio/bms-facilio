package com.facilio.bmsconsole.modules;

import java.util.ArrayList;
import java.util.List;

import com.facilio.constants.FacilioConstants;

public class FieldFactory {
	
	public static FacilioField getOrgIdField() {
		return getOrgIdField(null);
	}
	public static FacilioField getOrgIdField(String tableName) {
		FacilioField field = new FacilioField();
		field.setName("orgId");
		field.setDataType(FieldType.NUMBER);
		field.setColumnName("ORGID");
		if(tableName != null && !tableName.isEmpty()) {
			field.setModuleTableName(tableName);
		}
		return field;
	}
	
	public static FacilioField getModuleIdField() {
		return getModuleIdField(null);
	}
	public static FacilioField getModuleIdField(String tableName) {
		FacilioField field = new FacilioField();
		field.setName("moduleId");
		field.setDataType(FieldType.NUMBER);
		field.setColumnName("MODULEID");
		if(tableName != null && !tableName.isEmpty()) {
			field.setModuleTableName(tableName);
		}
		return field;
	}
	
	public static FacilioField getIdField() {
		return getModuleIdField(null);
	}
	
	public static FacilioField getIdField(String tableName) {
		FacilioField field = new FacilioField();
		field.setName("id");
		field.setDataType(FieldType.NUMBER);
		field.setColumnName("ID");
		if(tableName != null && !tableName.isEmpty()) {
			field.setModuleTableName(tableName);
		}
		return field;
	}
	
	public static List<FacilioField> getEmailSettingFields() {
		List<FacilioField> fields = new ArrayList<>();
		String tableName = "EmailSettings";
		
		fields.add(getIdField(tableName));
		fields.add(getOrgIdField(tableName));
		
		FacilioField bcc = new FacilioField();
		bcc.setName("bccEmail");
		bcc.setDataType(FieldType.STRING);
		bcc.setColumnName("BCC_EMAIL");
		bcc.setModuleTableName(tableName);
		fields.add(bcc);
		
		FacilioField flags = new FacilioField();
		flags.setName("flags");
		flags.setDataType(FieldType.NUMBER);
		flags.setColumnName("FLAGS");
		flags.setModuleTableName(tableName);
		fields.add(flags);
		
		return fields;
	}
	
	public static List<FacilioField> getEventFields() 
	{
		List<FacilioField> fields = new ArrayList<>();
		String tableName = "Event";
		
		FacilioField field = new FacilioField();
		field.setName("eventId");
		field.setDataType(FieldType.NUMBER);
		field.setColumnName("EVENT_ID");
		field.setModuleTableName(tableName);
		fields.add(field);
		
		fields.add(getOrgIdField(tableName));
		
		FacilioField field2 = new FacilioField();
		field2.setName("moduleId");
		field2.setDataType(FieldType.NUMBER);
		field2.setColumnName("MODULEID");
		field2.setModuleTableName(tableName);
		fields.add(field2);
		
		FacilioField field3 = new FacilioField();
		field3.setName("eventType");
		field3.setDataType(FieldType.NUMBER);
		field3.setColumnName("EVENT_TYPE");
		field3.setModuleTableName(tableName);
		fields.add(field3);

		return fields;
	}
	
	public static List<FacilioField> getWorkflowRuleFields() 
	{
		List<FacilioField> fields = new ArrayList<>();
		String tableName = "Workflow_Rule";
		
		fields.add(getIdField(tableName));
		fields.add(getOrgIdField(tableName));
		
		FacilioField name = new FacilioField();
		name.setName("name");
		name.setDataType(FieldType.STRING);
		name.setColumnName("NAME");
		name.setModuleTableName(tableName);
		fields.add(name);
		
		FacilioField description = new FacilioField();
		description.setName("description");
		description.setDataType(FieldType.STRING);
		description.setColumnName("DESCRIPTION");
		description.setModuleTableName(tableName);
		fields.add(description);

		FacilioField eventId = new FacilioField();
		eventId.setName("eventId");
		eventId.setDataType(FieldType.NUMBER);
		eventId.setColumnName("EVENT_ID");
		eventId.setModuleTableName(tableName);
		fields.add(eventId);
		
		FacilioField criteriaId = new FacilioField();
		criteriaId.setName("criteriaId");
		criteriaId.setDataType(FieldType.NUMBER);
		criteriaId.setColumnName("CRITERIAID");
		criteriaId.setModuleTableName(tableName);
		fields.add(criteriaId);
		
		FacilioField executionOrder = new FacilioField();
		executionOrder.setName("executionOrder");
		executionOrder.setDataType(FieldType.NUMBER);
		executionOrder.setColumnName("EXECUTION_ORDER");
		executionOrder.setModuleTableName(tableName);
		fields.add(executionOrder);
		
		FacilioField status = new FacilioField();
		status.setName("status");
		status.setDataType(FieldType.BOOLEAN);
		status.setColumnName("STATUS");
		status.setModuleTableName(tableName);
		fields.add(status);
		
		FacilioField ruleType = new FacilioField();
		ruleType.setName("ruleType");
		ruleType.setDataType(FieldType.NUMBER);
		ruleType.setColumnName("RULE_TYPE");
		ruleType.setModuleTableName(tableName);
		fields.add(ruleType);
		
		return fields;
	}
	
	public static List<FacilioField> getActionFields() 
	{
		List<FacilioField> fields = new ArrayList<>();
		String tableName = "Action";
		
		fields.add(getOrgIdField(tableName));
		fields.add(getIdField(tableName));
		
		FacilioField actionType = new FacilioField();
		actionType.setName("actionTypeVal");
		actionType.setDataType(FieldType.NUMBER);
		actionType.setColumnName("ACTION_TYPE");
		actionType.setModuleTableName(tableName);
		fields.add(actionType);
		
		FacilioField templateType = new FacilioField();
		templateType.setName("defaultTemplateId");
		templateType.setDataType(FieldType.NUMBER);
		templateType.setColumnName("DEFAULT_TEMPLATE_ID");
		templateType.setModuleTableName(tableName);
		fields.add(templateType);
		
		FacilioField templateId = new FacilioField();
		templateId.setName("templateId");
		templateId.setDataType(FieldType.NUMBER);
		templateId.setColumnName("TEMPLATE_ID");
		templateId.setModuleTableName(tableName);
		fields.add(templateId);
		
		return fields;
	}

	public static List<FacilioField> getSupportEmailFields() {
		List<FacilioField> fields = new ArrayList<>();
		String tableName = "SupportEmails";
		
		fields.add(getIdField(tableName));
		fields.add(getOrgIdField(tableName));
		
		FacilioField replyName = new FacilioField();
		replyName.setName("replyName");
		replyName.setDataType(FieldType.STRING);
		replyName.setColumnName("REPLY_NAME");
		replyName.setModuleTableName(tableName);
		fields.add(replyName);
		
		FacilioField actualEmail = new FacilioField();
		actualEmail.setName("actualEmail");
		actualEmail.setDataType(FieldType.STRING);
		actualEmail.setColumnName("ACTUAL_EMAIL");
		actualEmail.setModuleTableName(tableName);
		fields.add(actualEmail);
		
		FacilioField fwdEmail = new FacilioField();
		fwdEmail.setName("fwdEmail");
		fwdEmail.setDataType(FieldType.STRING);
		fwdEmail.setColumnName("FWD_EMAIL");
		fwdEmail.setModuleTableName(tableName);
		fields.add(fwdEmail);
		
		LookupField autoAssignGroup = new LookupField();
		autoAssignGroup.setName("autoAssignGroup");
		autoAssignGroup.setDataType(FieldType.LOOKUP);
		autoAssignGroup.setColumnName("AUTO_ASSIGN_GROUP_ID");
		autoAssignGroup.setModuleTableName(tableName);
		autoAssignGroup.setSpecialType(FacilioConstants.ContextNames.GROUP);
		fields.add(autoAssignGroup);
		
		return fields;
	}

	public static List<FacilioField> getRequesterFields() {
		List<FacilioField> fields = new ArrayList<>();
		String tableName = "Requester";
		
		fields.add(getOrgIdField(tableName));
		
		FacilioField field1 = new FacilioField();
		field1.setName("requesterId");
		field1.setDataType(FieldType.NUMBER);
		field1.setColumnName("REQUESTER_ID");
		field1.setModuleTableName(tableName);
		fields.add(field1);
		
		FacilioField field2 = new FacilioField();
		field2.setName("name");
		field2.setDataType(FieldType.STRING);
		field2.setColumnName("NAME");
		field2.setModuleTableName(tableName);
		fields.add(field2);
		
		FacilioField field3 = new FacilioField();
		field3.setName("cognitoId");
		field3.setDataType(FieldType.STRING);
		field3.setColumnName("COGNITO_ID");
		field3.setModuleTableName(tableName);
		fields.add(field3);
		
		LookupField field4 = new LookupField();
		field4.setName("userVerified");
		field4.setDataType(FieldType.BOOLEAN);
		field4.setColumnName("USER_VERIFIED");
		field4.setModuleTableName(tableName);
		fields.add(field4);
		
		LookupField field5 = new LookupField();
		field5.setName("email");
		field5.setDataType(FieldType.STRING);
		field5.setColumnName("EMAIL");
		field5.setModuleTableName(tableName);
		fields.add(field5);
		
		LookupField field6 = new LookupField();
		field6.setName("portalAccess");
		field6.setDataType(FieldType.BOOLEAN);
		field6.setColumnName("PORTAL_ACCESS");
		field6.setModuleTableName(tableName);
		fields.add(field6);
		
		return fields;
	}
	
	public static List<FacilioField> getNoteFields() {
		List<FacilioField> fields = new ArrayList<>();
		String tableName = "Notes";
		
		fields.add(getOrgIdField(tableName));
		
		FacilioField field1 = new FacilioField();
		field1.setName("noteId");
		field1.setDataType(FieldType.NUMBER);
		field1.setColumnName("NOTEID");
		field1.setModuleTableName(tableName);
		fields.add(field1);
		
		LookupField field2 = new LookupField();
		field2.setName("ownerId");
		field2.setDataType(FieldType.LOOKUP);
		field2.setColumnName("OWNERID");
		field2.setModuleTableName(tableName);
		field2.setSpecialType(FacilioConstants.ContextNames.USER);
		fields.add(field2);
		
		FacilioField field3 = new FacilioField();
		field3.setName("creationTime");
		field3.setDataType(FieldType.NUMBER);
		field3.setColumnName("CREATION_TIME");
		field3.setModuleTableName(tableName);
		fields.add(field3);
		
		LookupField field4 = new LookupField();
		field4.setName("title");
		field4.setDataType(FieldType.STRING);
		field4.setColumnName("TITLE");
		field4.setModuleTableName(tableName);
		fields.add(field4);
		
		LookupField field5 = new LookupField();
		field5.setName("body");
		field5.setDataType(FieldType.STRING);
		field5.setColumnName("BODY");
		field5.setModuleTableName(tableName);
		fields.add(field5);

		return fields;
	}
	
	public static List<FacilioField> getWorkorderEmailFields() {
		List<FacilioField> fields = new ArrayList<>();
		String tableName = "WorkOrderRequest_EMail";
		
		fields.add(getIdField(tableName));
		
		FacilioField s3MessageIdField = new FacilioField();
		s3MessageIdField.setName("s3MessageId");
		s3MessageIdField.setDataType(FieldType.STRING);
		s3MessageIdField.setColumnName("S3_MESSAGE_ID");
		s3MessageIdField.setModuleTableName(tableName);
		fields.add(s3MessageIdField);
		
		FacilioField isProcessField = new FacilioField();
		isProcessField.setName("isProcessed");
		isProcessField.setDataType(FieldType.BOOLEAN);
		isProcessField.setColumnName("IS_PROCESSED");
		isProcessField.setModuleTableName(tableName);
		fields.add(isProcessField);
		
		return fields;
	}
	
	public static List<FacilioField> getTicketActivityFields() {

		String tableName = "Ticket_Activity";
		
		FacilioField tId = new FacilioField();
		tId.setName("ticketId");
		tId.setDataType(FieldType.NUMBER);
		tId.setColumnName("TICKET_ID");
		tId.setModuleTableName(tableName);
		
		FacilioField modifiedTime = new FacilioField();
		modifiedTime.setName("modifiedTime");
		modifiedTime.setDataType(FieldType.NUMBER);
		modifiedTime.setColumnName("MODIFIED_TIME");
		modifiedTime.setModuleTableName(tableName);
		
		LookupField modifiedBy = new LookupField();
		modifiedBy.setName("modifiedBy");
		modifiedBy.setDataType(FieldType.LOOKUP);
		modifiedBy.setColumnName("MODIFIED_BY");
		modifiedBy.setModuleTableName(tableName);
		modifiedBy.setSpecialType(FacilioConstants.ContextNames.USER);
		
		FacilioField activityType = new FacilioField();
		activityType.setName("activityType");
		activityType.setDataType(FieldType.NUMBER);
		activityType.setColumnName("ACTIVITY_TYPE");
		activityType.setModuleTableName(tableName);
		
		List<FacilioField> fields = new ArrayList<>();
		fields.add(tId);
		fields.add(modifiedTime);
		fields.add(modifiedBy);
		fields.add(activityType);
		
		return fields;
	}
	
	public static List<FacilioField> getAlarmFollowersFeilds() {
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
		
		return fields;
	}
}	
