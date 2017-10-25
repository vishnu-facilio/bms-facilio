package com.facilio.bmsconsole.modules;

import java.util.ArrayList;
import java.util.List;

import com.facilio.constants.FacilioConstants;

public class FieldFactory {
	
	public static FacilioField getOrgIdField() {
		return getOrgIdField(null);
	}
	public static FacilioField getOrgIdField(FacilioModule module) {
		FacilioField field = new FacilioField();
		field.setName("orgId");
		field.setDataType(FieldType.NUMBER);
		field.setColumnName("ORGID");
		if(module != null) {
			field.setModule(module);
		}
		return field;
	}
	
	public static FacilioField getModuleIdField() {
		return getModuleIdField(null);
	}
	public static FacilioField getModuleIdField(FacilioModule module) {
		FacilioField field = new FacilioField();
		field.setName("moduleId");
		field.setDataType(FieldType.NUMBER);
		field.setColumnName("MODULEID");
		if(module != null) {
			field.setModule(module);
		}
		return field;
	}
	
	public static FacilioField getIdField() {
		return getModuleIdField(null);
	}
	
	public static FacilioField getIdField(FacilioModule module) {
		FacilioField field = new FacilioField();
		field.setName("id");
		field.setDataType(FieldType.NUMBER);
		field.setColumnName("ID");
		if(module != null) {
			field.setModule(module);
		}
		return field;
	}
	
	public static FacilioField getNameField(FacilioModule module) {
		FacilioField name = new FacilioField();
		name.setName("name");
		name.setDataType(FieldType.STRING);
		name.setColumnName("NAME");
		if(module != null) {
			name.setModule(module);
		}
		
		return name;
	}
	
	public static List<FacilioField> getEmailSettingFields() {
		List<FacilioField> fields = new ArrayList<>();
		FacilioModule module = ModuleFactory.getEmailSettingModule();
		
		fields.add(getIdField(module));
		fields.add(getOrgIdField(module));
		
		FacilioField bcc = new FacilioField();
		bcc.setName("bccEmail");
		bcc.setDataType(FieldType.STRING);
		bcc.setColumnName("BCC_EMAIL");
		bcc.setModule(module);
		fields.add(bcc);
		
		FacilioField flags = new FacilioField();
		flags.setName("flags");
		flags.setDataType(FieldType.NUMBER);
		flags.setColumnName("FLAGS");
		flags.setModule(module);
		fields.add(flags);
		
		return fields;
	}
	
	public static List<FacilioField> getWorkflowEventFields() 
	{
		List<FacilioField> fields = new ArrayList<>();
		FacilioModule module = ModuleFactory.getWorkflowEventModule();
		
		fields.add(getIdField(module));
		fields.add(getOrgIdField(module));
		fields.add(getModuleIdField(module));
		
		FacilioField field3 = new FacilioField();
		field3.setName("eventType");
		field3.setDataType(FieldType.NUMBER);
		field3.setColumnName("EVENT_TYPE");
		field3.setModule(module);
		fields.add(field3);

		return fields;
	}
	
	public static List<FacilioField> getWorkflowRuleFields() 
	{
		List<FacilioField> fields = new ArrayList<>();
		FacilioModule module = ModuleFactory.getWorkflowRuleModule();
		
		fields.add(getIdField(module));
		fields.add(getOrgIdField(module));
		fields.add(getNameField(module));
		
		FacilioField description = new FacilioField();
		description.setName("description");
		description.setDataType(FieldType.STRING);
		description.setColumnName("DESCRIPTION");
		description.setModule(module);
		fields.add(description);

		FacilioField eventId = new FacilioField();
		eventId.setName("eventId");
		eventId.setDataType(FieldType.NUMBER);
		eventId.setColumnName("EVENT_ID");
		eventId.setModule(module);
		fields.add(eventId);
		
		FacilioField criteriaId = new FacilioField();
		criteriaId.setName("criteriaId");
		criteriaId.setDataType(FieldType.NUMBER);
		criteriaId.setColumnName("CRITERIAID");
		criteriaId.setModule(module);
		fields.add(criteriaId);
		
		FacilioField executionOrder = new FacilioField();
		executionOrder.setName("executionOrder");
		executionOrder.setDataType(FieldType.NUMBER);
		executionOrder.setColumnName("EXECUTION_ORDER");
		executionOrder.setModule(module);
		fields.add(executionOrder);
		
		FacilioField status = new FacilioField();
		status.setName("status");
		status.setDataType(FieldType.BOOLEAN);
		status.setColumnName("STATUS");
		status.setModule(module);
		fields.add(status);
		
		FacilioField ruleType = new FacilioField();
		ruleType.setName("ruleType");
		ruleType.setDataType(FieldType.NUMBER);
		ruleType.setColumnName("RULE_TYPE");
		ruleType.setModule(module);
		fields.add(ruleType);
		
		return fields;
	}
	
	public static List<FacilioField> getActionFields() 
	{
		List<FacilioField> fields = new ArrayList<>();
		FacilioModule module = ModuleFactory.getActionModule();
		
		fields.add(getOrgIdField(module));
		fields.add(getIdField(module));
		
		FacilioField actionType = new FacilioField();
		actionType.setName("actionTypeVal");
		actionType.setDataType(FieldType.NUMBER);
		actionType.setColumnName("ACTION_TYPE");
		actionType.setModule(module);
		fields.add(actionType);
		
		FacilioField templateType = new FacilioField();
		templateType.setName("defaultTemplateId");
		templateType.setDataType(FieldType.NUMBER);
		templateType.setColumnName("DEFAULT_TEMPLATE_ID");
		templateType.setModule(module);
		fields.add(templateType);
		
		FacilioField templateId = new FacilioField();
		templateId.setName("templateId");
		templateId.setDataType(FieldType.NUMBER);
		templateId.setColumnName("TEMPLATE_ID");
		templateId.setModule(module);
		fields.add(templateId);
		
		return fields;
	}

	public static List<FacilioField> getSupportEmailFields() {
		List<FacilioField> fields = new ArrayList<>();
		FacilioModule module = ModuleFactory.getSupportEmailsModule();
		
		fields.add(getIdField(module));
		fields.add(getOrgIdField(module));
		
		FacilioField replyName = new FacilioField();
		replyName.setName("replyName");
		replyName.setDataType(FieldType.STRING);
		replyName.setColumnName("REPLY_NAME");
		replyName.setModule(module);
		fields.add(replyName);
		
		FacilioField actualEmail = new FacilioField();
		actualEmail.setName("actualEmail");
		actualEmail.setDataType(FieldType.STRING);
		actualEmail.setColumnName("ACTUAL_EMAIL");
		actualEmail.setModule(module);
		fields.add(actualEmail);
		
		FacilioField fwdEmail = new FacilioField();
		fwdEmail.setName("fwdEmail");
		fwdEmail.setDataType(FieldType.STRING);
		fwdEmail.setColumnName("FWD_EMAIL");
		fwdEmail.setModule(module);
		fields.add(fwdEmail);
		
		LookupField autoAssignGroup = new LookupField();
		autoAssignGroup.setName("autoAssignGroup");
		autoAssignGroup.setDataType(FieldType.LOOKUP);
		autoAssignGroup.setColumnName("AUTO_ASSIGN_GROUP_ID");
		autoAssignGroup.setModule(module);
		autoAssignGroup.setSpecialType(FacilioConstants.ContextNames.GROUP);
		fields.add(autoAssignGroup);
		
		return fields;
	}

	public static List<FacilioField> getRequesterFields() {
		List<FacilioField> fields = new ArrayList<>();
		FacilioModule module = ModuleFactory.getRequesterModule();
		
		fields.add(getOrgIdField(module));
		
		FacilioField field1 = new FacilioField();
		field1.setName("requesterId");
		field1.setDataType(FieldType.NUMBER);
		field1.setColumnName("REQUESTER_ID");
		field1.setModule(module);
		fields.add(field1);
		
		fields.add(getNameField(module));
		
		FacilioField field3 = new FacilioField();
		field3.setName("cognitoId");
		field3.setDataType(FieldType.STRING);
		field3.setColumnName("COGNITO_ID");
		field3.setModule(module);
		fields.add(field3);
		
		LookupField field4 = new LookupField();
		field4.setName("userVerified");
		field4.setDataType(FieldType.BOOLEAN);
		field4.setColumnName("USER_VERIFIED");
		field4.setModule(module);
		fields.add(field4);
		
		LookupField field5 = new LookupField();
		field5.setName("email");
		field5.setDataType(FieldType.STRING);
		field5.setColumnName("EMAIL");
		field5.setModule(module);
		fields.add(field5);
		
		LookupField field6 = new LookupField();
		field6.setName("portalAccess");
		field6.setDataType(FieldType.BOOLEAN);
		field6.setColumnName("PORTAL_ACCESS");
		field6.setModule(module);
		fields.add(field6);
		
		return fields;
	}
	
	public static List<FacilioField> getNoteFields() {
		List<FacilioField> fields = new ArrayList<>();
		FacilioModule module = ModuleFactory.getNotesModule();
		
		fields.add(getOrgIdField(module));
		
		FacilioField field1 = new FacilioField();
		field1.setName("noteId");
		field1.setDataType(FieldType.NUMBER);
		field1.setColumnName("NOTEID");
		field1.setModule(module);
		fields.add(field1);
		
		LookupField field2 = new LookupField();
		field2.setName("ownerId");
		field2.setDataType(FieldType.LOOKUP);
		field2.setColumnName("OWNERID");
		field2.setModule(module);
		field2.setSpecialType(FacilioConstants.ContextNames.USER);
		fields.add(field2);
		
		FacilioField field3 = new FacilioField();
		field3.setName("creationTime");
		field3.setDataType(FieldType.NUMBER);
		field3.setColumnName("CREATION_TIME");
		field3.setModule(module);
		fields.add(field3);
		
		LookupField field4 = new LookupField();
		field4.setName("title");
		field4.setDataType(FieldType.STRING);
		field4.setColumnName("TITLE");
		field4.setModule(module);
		fields.add(field4);
		
		LookupField field5 = new LookupField();
		field5.setName("body");
		field5.setDataType(FieldType.STRING);
		field5.setColumnName("BODY");
		field5.setModule(module);
		fields.add(field5);

		return fields;
	}
	
	public static List<FacilioField> getWorkorderEmailFields() {
		List<FacilioField> fields = new ArrayList<>();
		FacilioModule module = ModuleFactory.getWorkOrderRequestEMailModule();
		
		fields.add(getIdField(module));
		
		FacilioField s3MessageIdField = new FacilioField();
		s3MessageIdField.setName("s3MessageId");
		s3MessageIdField.setDataType(FieldType.STRING);
		s3MessageIdField.setColumnName("S3_MESSAGE_ID");
		s3MessageIdField.setModule(module);
		fields.add(s3MessageIdField);
		
		FacilioField isProcessField = new FacilioField();
		isProcessField.setName("isProcessed");
		isProcessField.setDataType(FieldType.BOOLEAN);
		isProcessField.setColumnName("IS_PROCESSED");
		isProcessField.setModule(module);
		fields.add(isProcessField);
		
		return fields;
	}
	
	public static List<FacilioField> getTicketActivityFields() {

		FacilioModule module = ModuleFactory.getTicketActivityModule();
		
		FacilioField tId = new FacilioField();
		tId.setName("ticketId");
		tId.setDataType(FieldType.NUMBER);
		tId.setColumnName("TICKET_ID");
		tId.setModule(module);
		
		FacilioField modifiedTime = new FacilioField();
		modifiedTime.setName("modifiedTime");
		modifiedTime.setDataType(FieldType.NUMBER);
		modifiedTime.setColumnName("MODIFIED_TIME");
		modifiedTime.setModule(module);
		
		LookupField modifiedBy = new LookupField();
		modifiedBy.setName("modifiedBy");
		modifiedBy.setDataType(FieldType.LOOKUP);
		modifiedBy.setColumnName("MODIFIED_BY");
		modifiedBy.setModule(module);
		modifiedBy.setSpecialType(FacilioConstants.ContextNames.USER);
		
		FacilioField activityType = new FacilioField();
		activityType.setName("activityType");
		activityType.setDataType(FieldType.NUMBER);
		activityType.setColumnName("ACTIVITY_TYPE");
		activityType.setModule(module);
		
		FacilioField info = new FacilioField();
		info.setName("info");
		info.setDataType(FieldType.STRING);
		info.setColumnName("INFO");
		info.setModule(module);
		
		List<FacilioField> fields = new ArrayList<>();
		fields.add(tId);
		fields.add(modifiedTime);
		fields.add(modifiedBy);
		fields.add(activityType);
		fields.add(info);
		
		return fields;
	}
	
	public static List<FacilioField> getAlarmFollowersFeilds() {
		FacilioModule module = ModuleFactory.getAlarmFollowersModule();
		
		FacilioField alarmId = new FacilioField();
		alarmId.setName("alarmId");
		alarmId.setDataType(FieldType.NUMBER);
		alarmId.setColumnName("ALARM_ID");
		alarmId.setModule(module);
		
		FacilioField followerType = new FacilioField();
		followerType.setName("type");
		followerType.setDataType(FieldType.STRING);
		followerType.setColumnName("FOLLOWER_TYPE");
		followerType.setModule(module);
		
		FacilioField follower = new FacilioField();
		follower.setName("follower");
		follower.setDataType(FieldType.STRING);
		follower.setColumnName("FOLLOWER");
		follower.setModule(module);
		
		List<FacilioField> fields = new ArrayList<>();
		fields.add(alarmId);
		fields.add(followerType);
		fields.add(follower);
		
		return fields;
	}
	
	public static List<FacilioField> getUserTemplateFields() {
		FacilioModule module = ModuleFactory.getTemplatesModule();
		
		List<FacilioField> fields = new ArrayList<>();
		fields.add(getIdField(module));
		fields.add(getOrgIdField(module));
		fields.add(getNameField(module));
		
		FacilioField typeField = new FacilioField();
		typeField.setName("type");
		typeField.setDataType(FieldType.NUMBER);
		typeField.setColumnName("TEMPLATE_TYPE");
		typeField.setModule(module);
		fields.add(typeField);
		
		return fields;
	}
	
	public static List<FacilioField> getEMailTemplateFields() {
		return getEMailTemplateFields(true);
	}
	
	private static List<FacilioField> getEMailTemplateFields(boolean isIdNeeded) {
		FacilioModule module = ModuleFactory.getEMailTemplatesModule();
		
		List<FacilioField> fields = new ArrayList<>();
		
		if(isIdNeeded) {
			fields.add(getIdField(module));
		}
		
		FacilioField emailFrom = new FacilioField();
		emailFrom.setName("from");
		emailFrom.setDataType(FieldType.STRING);
		emailFrom.setColumnName("FROM_ADDR");
		emailFrom.setModule(module);
		fields.add(emailFrom);
		
		FacilioField emailTo = new FacilioField();
		emailTo.setName("to");
		emailTo.setDataType(FieldType.STRING);
		emailTo.setColumnName("TO_ADDR");
		emailTo.setModule(module);
		fields.add(emailTo);
		
		FacilioField emailSubject = new FacilioField();
		emailSubject.setName("subject");
		emailSubject.setDataType(FieldType.STRING);
		emailSubject.setColumnName("SUBJECT");
		emailSubject.setModule(module);
		fields.add(emailSubject);
		
		FacilioField emailBody = new FacilioField();
		emailBody.setName("bodyId");
		emailBody.setDataType(FieldType.STRING);
		emailBody.setColumnName("BODY_ID");
		emailBody.setModule(module);
		fields.add(emailBody);
		
		return fields;
	}
	
	public static List<FacilioField> getSMSTemplateFields() {
		return getSMSTemplateFields(true);
	}
	
	private static List<FacilioField> getSMSTemplateFields(boolean isIdNeeded) {
		FacilioModule module = ModuleFactory.getSMSTemplatesModule();
		
		List<FacilioField> fields = new ArrayList<>();
		
		if(isIdNeeded) {
			fields.add(getIdField(module));
		}
		
		FacilioField smsFrom = new FacilioField();
		smsFrom.setName("from");
		smsFrom.setDataType(FieldType.STRING);
		smsFrom.setColumnName("FROM_NUM");
		smsFrom.setModule(module);
		fields.add(smsFrom);
		
		FacilioField smsTo = new FacilioField();
		smsTo.setName("to");
		smsTo.setDataType(FieldType.STRING);
		smsTo.setColumnName("TO_NUM");
		smsTo.setModule(module);
		fields.add(smsTo);
		
		FacilioField smsMsg = new FacilioField();
		smsMsg.setName("msg");
		smsMsg.setDataType(FieldType.STRING);
		smsMsg.setColumnName("MSG");
		smsMsg.setModule(module);
		fields.add(smsMsg);
		
		return fields;
	}
	
	public static List<FacilioField> getWorkorderTemplateFields() {
		FacilioModule module = ModuleFactory.getWorkorderTemplateModule();
		
		List<FacilioField> fields = new ArrayList<>();
		
		fields.add(getIdField(module));
		
		FacilioField content = new FacilioField();
		content.setName("contentId");
		content.setDataType(FieldType.STRING);
		content.setColumnName("CONTENT_ID");
		content.setModule(module);
		fields.add(content);
		
		return fields;
	}
	
	public static List<FacilioField> getAlarmTemplateFields() {
		FacilioModule module = ModuleFactory.getAlarmTemplateModule();
		
		List<FacilioField> fields = new ArrayList<>();
		
		fields.add(getIdField(module));
		
		FacilioField content = new FacilioField();
		content.setName("contentId");
		content.setDataType(FieldType.STRING);
		content.setColumnName("CONTENT_ID");
		content.setModule(module);
		fields.add(content);
		
		return fields;
	}
	
	public static List<FacilioField> getPreventiveMaintenanceFields() {
		FacilioModule module = ModuleFactory.getPreventiveMaintenancetModule();
		
		List<FacilioField> fields = new ArrayList<>();
		
		FacilioField id = new FacilioField();
		id.setName("preventiveMaintenanceId");
		id.setDataType(FieldType.NUMBER);
		id.setColumnName("PREVENTIVE_MAINTENANCE_ID");
		id.setModule(module);
		fields.add(id);
		
		fields.add(getOrgIdField(module));
		
		FacilioField title = new FacilioField();
		title.setName("title");
		title.setDataType(FieldType.STRING);
		title.setColumnName("TITLE");
		title.setModule(module);
		fields.add(title);
		
		FacilioField pmType = new FacilioField();
		pmType.setName("pmType");
		pmType.setDataType(FieldType.NUMBER);
		pmType.setColumnName("PM_TYPE");
		pmType.setModule(module);
		fields.add(pmType);
		
		FacilioField status = new FacilioField();
		status.setName("status");
		status.setDataType(FieldType.NUMBER);
		status.setColumnName("STATUS");
		status.setModule(module);
		fields.add(status);
		
		FacilioField createdBy = new FacilioField();
		createdBy.setName("createdBy");
		createdBy.setDataType(FieldType.NUMBER);
		createdBy.setColumnName("CREATED_BY");
		createdBy.setModule(module);
		fields.add(createdBy);
		
		FacilioField modifiedBy = new FacilioField();
		modifiedBy.setName("modifiedBy");
		modifiedBy.setDataType(FieldType.NUMBER);
		modifiedBy.setColumnName("MODIFIED_BY");
		modifiedBy.setModule(module);
		fields.add(modifiedBy);
		
		FacilioField creationTime = new FacilioField();
		creationTime.setName("creationTime");
		creationTime.setDataType(FieldType.NUMBER);
		creationTime.setColumnName("CREATION_TIME");
		creationTime.setModule(module);
		fields.add(creationTime);
		
		FacilioField lastModifiedTime = new FacilioField();
		lastModifiedTime.setName("lastModifiedTime");
		lastModifiedTime.setDataType(FieldType.NUMBER);
		lastModifiedTime.setColumnName("LAST_MODIFIED_TIME");
		lastModifiedTime.setModule(module);
		fields.add(lastModifiedTime);
		
		FacilioField templateId = new FacilioField();
		templateId.setName("templateId");
		templateId.setDataType(FieldType.NUMBER);
		templateId.setColumnName("TEMPLATE_ID");
		templateId.setModule(module);
		fields.add(templateId);
		
		return fields;
	}
	
	public static List<FacilioField> getCriteriaFields() {
		FacilioModule module = ModuleFactory.getCriteriaModule();
		List<FacilioField> fields = new ArrayList<>();
		
		FacilioField criteriaId = new FacilioField();
		criteriaId.setName("criteriaId");
		criteriaId.setDataType(FieldType.NUMBER);
		criteriaId.setColumnName("CRITERIAID");
		criteriaId.setModule(module);
		fields.add(criteriaId);
		
		fields.add(getOrgIdField(module));
		
		FacilioField pattern = new FacilioField();
		pattern.setName("pattern");
		pattern.setDataType(FieldType.STRING);
		pattern.setColumnName("PATTERN");
		pattern.setModule(module);
		fields.add(pattern);
		
		return fields;
	}
	
	public static List<FacilioField> getConditionFields() {
		FacilioModule module = ModuleFactory.getConditionsModule();
		List<FacilioField> fields = new ArrayList<>();
		
		FacilioField conditionId = new FacilioField();
		conditionId.setName("conditionId");
		conditionId.setDataType(FieldType.NUMBER);
		conditionId.setColumnName("CONDITIONID");
		conditionId.setModule(module);
		fields.add(conditionId);
		
		FacilioField parentCriteriaId = new FacilioField();
		parentCriteriaId.setName("parentCriteriaId");
		parentCriteriaId.setDataType(FieldType.NUMBER);
		parentCriteriaId.setColumnName("PARENT_CRITERIA_ID");
		parentCriteriaId.setModule(module);
		fields.add(parentCriteriaId);
		
		FacilioField sequence = new FacilioField();
		sequence.setName("sequence");
		sequence.setDataType(FieldType.NUMBER);
		sequence.setColumnName("SEQUENCE");
		sequence.setModule(module);
		fields.add(sequence);
		
		FacilioField fieldId = new FacilioField();
		fieldId.setName("fieldId");
		fieldId.setDataType(FieldType.NUMBER);
		fieldId.setColumnName("FIELDID");
		fieldId.setModule(module);
		fields.add(fieldId);
		
		FacilioField operatorStr = new FacilioField();
		operatorStr.setName("operatorStr");
		operatorStr.setDataType(FieldType.STRING);
		operatorStr.setColumnName("OPERATOR");
		operatorStr.setModule(module);
		fields.add(operatorStr);
		
		FacilioField value = new FacilioField();
		value.setName("value");
		value.setDataType(FieldType.STRING);
		value.setColumnName("VAL");
		value.setModule(module);
		fields.add(value);
		
		FacilioField criteriaValueId = new FacilioField();
		criteriaValueId.setName("criteriaValueId");
		criteriaValueId.setDataType(FieldType.NUMBER);
		criteriaValueId.setColumnName("CRITERIA_VAL_ID");
		criteriaValueId.setModule(module);
		fields.add(criteriaValueId);
		
		FacilioField computedWhereClause = new FacilioField();
		computedWhereClause.setName("computedWhereClause");
		computedWhereClause.setDataType(FieldType.STRING);
		computedWhereClause.setColumnName("COMPUTED_WHERE_CLAUSE");
		computedWhereClause.setModule(module);
		fields.add(computedWhereClause);
		
		return fields;
		
	}
	
	public static List<FacilioField> getViewFields() {
		FacilioModule module = ModuleFactory.getViewsModule();
		
		List<FacilioField> fields = new ArrayList<>();
		fields.add(getIdField(module));
		fields.add(getOrgIdField(module));
		fields.add(getNameField(module));
		
		FacilioField displayName = new FacilioField();
		displayName.setName("displayName");
		displayName.setDataType(FieldType.STRING);
		displayName.setColumnName("DISPLAY_NAME");
		displayName.setModule(module);
		fields.add(displayName);
		
		FacilioField type = new FacilioField();
		type.setName("type");
		type.setDataType(FieldType.NUMBER);
		type.setColumnName("VIEW_TYPE");
		type.setModule(module);
		fields.add(type);
		
		fields.add(getModuleIdField(module));
		
		FacilioField criteria = new FacilioField();
		criteria.setName("criteriaId");
		criteria.setDataType(FieldType.NUMBER);
		criteria.setColumnName("CRITERIAID");
		criteria.setModule(module);
		fields.add(criteria);
		
		return fields;
		
	}
	
	public static List<FacilioField> getDefaultReadingFields(FacilioModule module) {
		
		List<FacilioField> fields = new ArrayList<>();
		
		FacilioField ttime = new FacilioField();
		ttime.setName("ttime");
		ttime.setDisplayName("Timestamp");
		ttime.setDataType(FieldType.DATE_TIME);
		ttime.setColumnName("TTIME");
		ttime.setModule(module);
		fields.add(ttime);
		
		FacilioField parent = new FacilioField();
		parent.setName("parentId");
		parent.setDisplayName("Parent");
		parent.setDataType(FieldType.NUMBER);
		parent.setColumnName("PARENT_ID");
		parent.setModule(module);
		
		fields.add(parent);
		
		return fields;
	}
}	
