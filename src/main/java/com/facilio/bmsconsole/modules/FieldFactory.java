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
		
		FacilioField field = new FacilioField();
		field.setName("workflowRuleId");
		field.setDataType(FieldType.NUMBER);
		field.setColumnName("WORKFLOW_RULE_ID");
		field.setModuleTableName(tableName);
		fields.add(field);
		
		fields.add(getOrgIdField(tableName));
		
		FacilioField field2 = new FacilioField();
		field2.setName("name");
		field2.setDataType(FieldType.STRING);
		field2.setColumnName("NAME");
		field2.setModuleTableName(tableName);
		fields.add(field2);
		
		FacilioField field3 = new FacilioField();
		field3.setName("description");
		field3.setDataType(FieldType.STRING);
		field3.setColumnName("DESCRIPTION");
		field3.setModuleTableName(tableName);
		fields.add(field3);

		FacilioField field4 = new FacilioField();
		field4.setName("eventId");
		field4.setDataType(FieldType.NUMBER);
		field4.setColumnName("EVENT_ID");
		field4.setModuleTableName(tableName);
		fields.add(field4);
		
		FacilioField field5 = new FacilioField();
		field5.setName("criteriaId");
		field5.setDataType(FieldType.NUMBER);
		field5.setColumnName("CRITERIAID");
		field5.setModuleTableName(tableName);
		fields.add(field5);
		
		FacilioField field6 = new FacilioField();
		field6.setName("executionOrder");
		field6.setDataType(FieldType.NUMBER);
		field6.setColumnName("EXECUTION_ORDER");
		field6.setModuleTableName(tableName);
		fields.add(field6);
		
		FacilioField field7 = new FacilioField();
		field7.setName("status");
		field7.setDataType(FieldType.NUMBER);
		field7.setColumnName("STATUS");
		field7.setModuleTableName(tableName);
		fields.add(field7);
		
		FacilioField field8 = new FacilioField();
		field8.setName("ruleType");
		field8.setDataType(FieldType.NUMBER);
		field8.setColumnName("RULE_TYPE");
		field8.setModuleTableName(tableName);
		fields.add(field8);
		
		return fields;
	}
	
	public static List<FacilioField> getWorkflowRuleActionFields() 
	{
		List<FacilioField> fields = new ArrayList<>();
		String tableName = "Workflow_Rule_Action";
		
		FacilioField field = new FacilioField();
		field.setName("worflowRuleActionId");
		field.setDataType(FieldType.NUMBER);
		field.setColumnName("WORKFLOW_RULE_ACTION_ID");
		field.setModuleTableName(tableName);
		fields.add(field);
		
		FacilioField field2 = new FacilioField();
		field2.setName("workflowRuleId");
		field2.setDataType(FieldType.NUMBER);
		field2.setColumnName("WORKFLOW_RULE_ID");
		field2.setModuleTableName(tableName);
		fields.add(field2);
		
		FacilioField field3 = new FacilioField();
		field3.setName("actionId");
		field3.setDataType(FieldType.NUMBER);
		field3.setColumnName("ACTION_ID");
		field3.setModuleTableName(tableName);
		fields.add(field3);

		return fields;
	}
	
	public static List<FacilioField> getActionFields() 
	{
		List<FacilioField> fields = new ArrayList<>();
		String tableName = "Action";
		
		FacilioField field = new FacilioField();
		field.setName("actionId");
		field.setDataType(FieldType.NUMBER);
		field.setColumnName("ACTION_ID");
		field.setModuleTableName(tableName);
		fields.add(field);
		
		fields.add(getOrgIdField(tableName));
		
		FacilioField field2 = new FacilioField();
		field2.setName("actionType");
		field2.setDataType(FieldType.NUMBER);
		field2.setColumnName("ACTION_TYPE");
		field2.setModuleTableName(tableName);
		fields.add(field2);
		
		FacilioField field3 = new FacilioField();
		field3.setName("templateType");
		field3.setDataType(FieldType.NUMBER);
		field3.setColumnName("TEMPLATE_TYPE");
		field3.setModuleTableName(tableName);
		fields.add(field3);
		
		FacilioField field4 = new FacilioField();
		field4.setName("templateId");
		field4.setDataType(FieldType.NUMBER);
		field4.setColumnName("TEMPLATE_ID");
		field4.setModuleTableName(tableName);
		fields.add(field4);
		
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
}	
