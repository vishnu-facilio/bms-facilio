package com.facilio.bmsconsole.modules;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.facilio.constants.FacilioConstants;

public class FieldFactory {

	public static List<FacilioField> getLookupFieldFields() {
		List<FacilioField> fields = new ArrayList<>();
		FacilioModule module = ModuleFactory.getLookupFieldsModule();

		FacilioField fieldId = new FacilioField();
		fieldId.setName("fieldId");
		fieldId.setDataType(FieldType.NUMBER);
		fieldId.setColumnName("FIELDID");
		fieldId.setModule(module);
		fields.add(fieldId);
		
		FacilioField specialType = new FacilioField();
		specialType.setName("specialType");
		specialType.setDataType(FieldType.STRING);
		specialType.setColumnName("SPECIAL_TYPE");
		specialType.setModule(module);
		fields.add(specialType);

		FacilioField lookupModuleId = new FacilioField();
		lookupModuleId.setName("lookupModuleId");
		lookupModuleId.setDataType(FieldType.NUMBER);
		lookupModuleId.setColumnName("LOOKUP_MODULE_ID");
		lookupModuleId.setModule(module);
		fields.add(lookupModuleId);

		return fields;
	}
	
	public static List<FacilioField> getNumberFieldFields() {
		List<FacilioField> fields = new ArrayList<>();
		FacilioModule module = ModuleFactory.getNumberFieldModule();
		
		FacilioField fieldId = new FacilioField();
		fieldId.setName("fieldId");
		fieldId.setDataType(FieldType.NUMBER);
		fieldId.setColumnName("FIELDID");
		fieldId.setModule(module);
		fields.add(fieldId);
		
		FacilioField unit = new FacilioField();
		unit.setName("unit");
		unit.setDataType(FieldType.STRING);
		unit.setColumnName("UNIT");
		unit.setModule(module);
		fields.add(unit);
		
		return fields;
	}

	public static List<FacilioField> getAddFieldFields() {
		List<FacilioField> fields = new ArrayList<>();
		FacilioModule module = ModuleFactory.getFieldsModule();

		fields.add(getOrgIdField(module));
		fields.add(getModuleIdField(module));

		FacilioField extendedModuleId = new FacilioField();
		extendedModuleId.setName("extendedModuleId");
		extendedModuleId.setDataType(FieldType.NUMBER);
		extendedModuleId.setColumnName("EXTENDED_MODULEID");
		extendedModuleId.setModule(module);
		fields.add(extendedModuleId);

		fields.add(getNameField(module));

		FacilioField displayName = new FacilioField();
		displayName.setName("displayName");
		displayName.setDataType(FieldType.STRING);
		displayName.setColumnName("DISPLAY_NAME");
		displayName.setModule(module);
		fields.add(displayName);

		FacilioField columnName = new FacilioField();
		columnName.setName("columnName");
		columnName.setDataType(FieldType.STRING);
		columnName.setColumnName("COLUMN_NAME");
		columnName.setModule(module);
		fields.add(columnName);

		FacilioField sequenceNumber = new FacilioField();
		sequenceNumber.setName("sequenceNumber");
		sequenceNumber.setDataType(FieldType.NUMBER);
		sequenceNumber.setColumnName("SEQUENCE_NUMBER");
		sequenceNumber.setModule(module);
		fields.add(sequenceNumber);

		FacilioField dataType = new FacilioField();
		dataType.setName("dataType");
		dataType.setDataType(FieldType.NUMBER);
		dataType.setColumnName("DATA_TYPE");
		dataType.setModule(module);
		fields.add(dataType);

		FacilioField displayType = new FacilioField();
		displayType.setName("displayTypeInt");
		displayType.setDataType(FieldType.NUMBER);
		displayType.setColumnName("DISPLAY_TYPE");
		displayType.setModule(module);
		fields.add(displayType);

		FacilioField required = new FacilioField();
		required.setName("required");
		required.setDataType(FieldType.BOOLEAN);
		required.setColumnName("REQUIRED");
		required.setModule(module);
		fields.add(required);

		return fields;
	}

	public static List<FacilioField> getSelectFieldFields() {
		List<FacilioField> fields = getAddFieldFields();

		FacilioModule module = ModuleFactory.getFieldsModule();

		FacilioField fieldId = new FacilioField();
		fieldId.setName("fieldId");
		fieldId.setDataType(FieldType.NUMBER);
		fieldId.setColumnName("FIELDID");
		fieldId.setModule(module);
		fields.add(fieldId);

		FacilioField defaultField = new FacilioField();
		defaultField.setName("default");
		defaultField.setDataType(FieldType.BOOLEAN);
		defaultField.setColumnName("IS_DEFAULT");
		defaultField.setModule(module);
		fields.add(defaultField);

		FacilioField mainField = new FacilioField();
		mainField.setName("mainField");
		mainField.setDataType(FieldType.BOOLEAN);
		mainField.setColumnName("IS_MAIN_FIELD");
		mainField.setModule(module);
		fields.add(mainField);

		FacilioField disabled = new FacilioField();
		disabled.setName("disabled");
		disabled.setDataType(FieldType.BOOLEAN);
		disabled.setColumnName("DISABLED");
		disabled.setModule(module);
		fields.add(disabled);

		FacilioField styleClass = new FacilioField();
		styleClass.setName("styleClass");
		styleClass.setDataType(FieldType.STRING);
		styleClass.setColumnName("STYLE_CLASS");
		styleClass.setModule(module);
		fields.add(styleClass);

		FacilioField icon = new FacilioField();
		icon.setName("icon");
		icon.setDataType(FieldType.STRING);
		icon.setColumnName("ICON");
		icon.setModule(module);
		fields.add(icon);

		FacilioField placeHolder = new FacilioField();
		placeHolder.setName("placeHolder");
		placeHolder.setDataType(FieldType.STRING);
		placeHolder.setColumnName("PLACE_HOLDER");
		placeHolder.setModule(module);
		fields.add(placeHolder);

		return fields;
	}

	public static List<FacilioField> getUpdateFieldFields() { // Only these
																// fields can be
																// updated
		List<FacilioField> fields = new ArrayList<>();
		FacilioModule module = ModuleFactory.getFieldsModule();

		FacilioField displayName = new FacilioField();
		displayName.setName("displayName");
		displayName.setDataType(FieldType.STRING);
		displayName.setColumnName("DISPLAY_NAME");
		displayName.setModule(module);
		fields.add(displayName);

		FacilioField displayType = new FacilioField();
		displayType.setName("displayTypeInt");
		displayType.setDataType(FieldType.NUMBER);
		displayType.setColumnName("DISPLAY_TYPE");
		displayType.setModule(module);
		fields.add(displayType);

		FacilioField sequenceNumber = new FacilioField();
		sequenceNumber.setName("sequenceNumber");
		sequenceNumber.setDataType(FieldType.NUMBER);
		sequenceNumber.setColumnName("SEQUENCE_NUMBER");
		sequenceNumber.setModule(module);
		fields.add(sequenceNumber);

		FacilioField required = new FacilioField();
		required.setName("required");
		required.setDataType(FieldType.BOOLEAN);
		required.setColumnName("REQUIRED");
		required.setModule(module);
		fields.add(required);

		FacilioField disabled = new FacilioField();
		disabled.setName("disabled");
		disabled.setDataType(FieldType.BOOLEAN);
		disabled.setColumnName("DISABLED");
		disabled.setModule(module);
		fields.add(disabled);

		FacilioField styleClass = new FacilioField();
		styleClass.setName("styleClass");
		styleClass.setDataType(FieldType.STRING);
		styleClass.setColumnName("STYLE_CLASS");
		styleClass.setModule(module);
		fields.add(styleClass);

		FacilioField icon = new FacilioField();
		icon.setName("icon");
		icon.setDataType(FieldType.STRING);
		icon.setColumnName("ICON");
		icon.setModule(module);
		fields.add(icon);

		FacilioField placeHolder = new FacilioField();
		placeHolder.setName("placeHolder");
		placeHolder.setDataType(FieldType.STRING);
		placeHolder.setColumnName("PLACE_HOLDER");
		placeHolder.setModule(module);
		fields.add(placeHolder);

		return fields;
	}

	public static FacilioField getOrgIdField() {
		return getOrgIdField(null);
	}

	public static FacilioField getOrgIdField(FacilioModule module) {
		FacilioField field = new FacilioField();
		field.setName("orgId");
		field.setDataType(FieldType.NUMBER);
		field.setColumnName("ORGID");
		if (module != null) {
			field.setModule(module);
		}
		return field;
	}

	public static FacilioField getKinesisField() {
		return getKinesisField(null);
	}

	public static FacilioField getKinesisField(FacilioModule module) {
		FacilioField field = new FacilioField();
		field.setName("kinesisTopic");
		field.setDataType(FieldType.STRING);
		field.setColumnName("KINESIS_TOPIC");
		if (module != null) {
			field.setModule(module);
		}
		return field;
	}

	public static FacilioField getUserIdField(FacilioModule module) {
		FacilioField field = new FacilioField();
		field.setName("userId");
		field.setDataType(FieldType.NUMBER);
		field.setColumnName("USERID");
		if (module != null) {
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
		if (module != null) {
			field.setModule(module);
		}
		return field;
	}

	public static FacilioField getIdField() {
		return getIdField(null);
	}

	public static FacilioField getIdField(FacilioModule module) {
		FacilioField field = new FacilioField();
		field.setName("id");
		field.setDataType(FieldType.NUMBER);
		field.setColumnName("ID");
		if (module != null) {
			field.setModule(module);
		}
		return field;
	}

	public static FacilioField getNameField(FacilioModule module) {
		FacilioField name = new FacilioField();
		name.setName("name");
		name.setDataType(FieldType.STRING);
		name.setColumnName("NAME");
		if (module != null) {
			name.setModule(module);
		}

		return name;
	}

	public static List<FacilioField> getCategoryReadingsFields(FacilioModule module) {
		List<FacilioField> fields = new ArrayList<>();

		FacilioField parentCategoryId = new FacilioField();
		parentCategoryId.setName("parentCategoryId");
		parentCategoryId.setDataType(FieldType.NUMBER);
		parentCategoryId.setColumnName("PARENT_CATEGORY_ID");
		parentCategoryId.setModule(module);
		fields.add(parentCategoryId);

		FacilioField readingModuleId = new FacilioField();
		readingModuleId.setName("readingModuleId");
		readingModuleId.setDataType(FieldType.NUMBER);
		readingModuleId.setColumnName("READING_MODULE_ID");
		readingModuleId.setModule(module);
		fields.add(readingModuleId);

		return fields;
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

	public static List<FacilioField> getWorkflowEventFields() {
		List<FacilioField> fields = new ArrayList<>();
		FacilioModule module = ModuleFactory.getWorkflowEventModule();

		fields.add(getIdField(module));
		fields.add(getOrgIdField(module));
		fields.add(getModuleIdField(module));

		FacilioField field3 = new FacilioField();
		field3.setName("activityType");
		field3.setDataType(FieldType.NUMBER);
		field3.setColumnName("ACTIVITY_TYPE");
		field3.setModule(module);
		fields.add(field3);

		return fields;
	}

	public static List<FacilioField> getWorkflowRuleActionFields() {
		List<FacilioField> fields = new ArrayList<>();
		FacilioModule module = ModuleFactory.getWorkflowRuleActionModule();

		FacilioField workflowRuleActionId = new FacilioField();
		workflowRuleActionId.setName("workflowRuleActionId");
		workflowRuleActionId.setDataType(FieldType.NUMBER);
		workflowRuleActionId.setColumnName("WORKFLOW_RULE_ACTION_ID");
		workflowRuleActionId.setModule(module);
		fields.add(workflowRuleActionId);

		FacilioField workflowRuleId = new FacilioField();
		workflowRuleId.setName("workflowRuleId");
		workflowRuleId.setDataType(FieldType.NUMBER);
		workflowRuleId.setColumnName("WORKFLOW_RULE_ID");
		workflowRuleId.setModule(module);
		fields.add(workflowRuleId);

		FacilioField actionId = new FacilioField();
		actionId.setName("actionId");
		actionId.setDataType(FieldType.NUMBER);
		actionId.setColumnName("ACTION_ID");
		actionId.setModule(module);
		fields.add(actionId);

		return fields;
	}

	public static List<FacilioField> getWorkflowRuleFields() {
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

		FacilioField criteriaId = new NumberField();
		criteriaId.setName("criteriaId");
		criteriaId.setDataType(FieldType.NUMBER);
		criteriaId.setColumnName("CRITERIAID");
		criteriaId.setModule(module);
		fields.add(criteriaId);
		
		fields.add(getField("expressionId", "EXPRESSION_ID", module, FieldType.NUMBER));

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

	public static List<FacilioField> getReadingRuleFields() {
		List<FacilioField> fields = new ArrayList<>();
		FacilioModule module = ModuleFactory.getReadingRuleModule();

		fields.add(getIdField(module));

		FacilioField startValue = new FacilioField();
		startValue.setName("startValue");
		startValue.setDataType(FieldType.NUMBER);
		startValue.setColumnName("START_VALUE");
		startValue.setModule(module);
		fields.add(startValue);

		FacilioField interval = new FacilioField();
		interval.setName("interval");
		interval.setDataType(FieldType.NUMBER);
		interval.setColumnName("INTERVAL_VALUE");
		interval.setModule(module);
		fields.add(interval);

		FacilioField lastValue = new FacilioField();
		lastValue.setName("lastValue");
		lastValue.setDataType(FieldType.NUMBER);
		lastValue.setColumnName("LAST_VALUE");
		lastValue.setModule(module);
		fields.add(lastValue);
		
		FacilioField resourceId = new FacilioField();
		resourceId.setName("resourceId");
		resourceId.setDataType(FieldType.NUMBER);
		resourceId.setColumnName("RESOURCE_ID");
		resourceId.setModule(module);
		fields.add(resourceId);
		
		fields.add(getField("readingFieldId", "READING_FIELD_ID", module, FieldType.NUMBER));

		return fields;
	}

	public static List<FacilioField> getActionFields() {
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

		FacilioField status = new FacilioField();
		status.setName("status");
		status.setDataType(FieldType.BOOLEAN);
		status.setColumnName("STATUS");
		status.setModule(module);
		fields.add(status);

		return fields;
	}

	public static List<FacilioField> getUserFields() {
		List<FacilioField> fields = new ArrayList<>();
		FacilioModule module = ModuleFactory.getUserModule();

		FacilioField userid = new FacilioField();
		userid.setName("userid");
		userid.setDataType(FieldType.NUMBER);
		userid.setColumnName("USERID");
		userid.setModule(module);
		fields.add(userid);

		FacilioField name = new FacilioField();
		name.setName("name");
		name.setDataType(FieldType.STRING);
		name.setColumnName("NAME");
		name.setModule(module);
		fields.add(name);

		FacilioField cognito_id = new FacilioField();
		cognito_id.setName("cognito_id");
		cognito_id.setDataType(FieldType.STRING);
		cognito_id.setColumnName("COGNITO_ID");
		cognito_id.setModule(module);
		fields.add(cognito_id);

		FacilioField user_verified = new FacilioField();
		user_verified.setName("user_verified");
		user_verified.setDataType(FieldType.BOOLEAN);
		user_verified.setColumnName("USER_VERIFIED");
		user_verified.setModule(module);
		fields.add(user_verified);

		FacilioField email = new FacilioField();
		email.setName("email");
		email.setDataType(FieldType.STRING);
		email.setColumnName("EMAIL");
		email.setModule(module);
		fields.add(email);

		FacilioField photo_id = new FacilioField();
		photo_id.setName("photo_id");
		photo_id.setDataType(FieldType.NUMBER);
		photo_id.setColumnName("PHOTO_ID");
		photo_id.setModule(module);
		fields.add(photo_id);

		FacilioField timezone = new FacilioField();
		timezone.setName("timezone");
		timezone.setDataType(FieldType.STRING);
		timezone.setColumnName("TIMEZONE");
		timezone.setModule(module);
		fields.add(timezone);

		FacilioField language = new FacilioField();
		language.setName("language");
		language.setDataType(FieldType.STRING);
		language.setColumnName("LANGUAGE");
		language.setModule(module);
		fields.add(language);

		FacilioField phone = new FacilioField();
		phone.setName("phone");
		phone.setDataType(FieldType.STRING);
		phone.setColumnName("PHONE");
		phone.setModule(module);
		fields.add(phone);

		FacilioField mobile = new FacilioField();
		mobile.setName("mobile");
		mobile.setDataType(FieldType.STRING);
		mobile.setColumnName("MOBILE");
		mobile.setModule(module);
		fields.add(mobile);

		FacilioField street = new FacilioField();
		street.setName("street");
		street.setDataType(FieldType.STRING);
		street.setColumnName("STREET");
		street.setModule(module);
		fields.add(street);

		FacilioField city = new FacilioField();
		city.setName("city");
		city.setDataType(FieldType.STRING);
		city.setColumnName("CITY");
		city.setModule(module);
		fields.add(city);

		FacilioField state = new FacilioField();
		state.setName("state");
		state.setDataType(FieldType.STRING);
		state.setColumnName("STATE");
		state.setModule(module);
		fields.add(state);

		FacilioField zip = new FacilioField();
		zip.setName("zip");
		zip.setDataType(FieldType.STRING);
		zip.setColumnName("ZIP");
		zip.setModule(module);
		fields.add(zip);

		FacilioField country = new FacilioField();
		country.setName("country");
		country.setDataType(FieldType.STRING);
		country.setColumnName("COUNTRY");
		country.setModule(module);
		fields.add(country);
		return fields;

	}

	public static List<FacilioField> getOrgUserFields() {
		List<FacilioField> fields = new ArrayList<>();
		FacilioModule module = ModuleFactory.getOrgUserModule();

		FacilioField orguserid = new FacilioField();
		orguserid.setName("orgUserId");
		orguserid.setDataType(FieldType.NUMBER);
		orguserid.setColumnName("ORG_USERID");
		orguserid.setModule(module);
		fields.add(orguserid);

		FacilioField userid = new FacilioField();
		userid.setName("userId");
		userid.setDataType(FieldType.NUMBER);
		userid.setColumnName("USERID");
		userid.setModule(module);
		fields.add(userid);

		FacilioField orgid = new FacilioField();
		orgid.setName("orgId");
		orgid.setDataType(FieldType.NUMBER);
		orgid.setColumnName("ORGID");
		orgid.setModule(module);
		fields.add(orgid);

		FacilioField userstatus = new FacilioField();
		userstatus.setName("userStatus");
		userstatus.setDataType(FieldType.BOOLEAN);
		userstatus.setColumnName("USER_STATUS");
		userstatus.setModule(module);
		fields.add(userstatus);

		FacilioField roleid = new FacilioField();
		roleid.setName("roleId");
		roleid.setDataType(FieldType.NUMBER);
		roleid.setColumnName("ROLE_ID");
		roleid.setModule(module);
		fields.add(roleid);

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
		List<FacilioField> fields = new ArrayList<>();

		fields.add(getIdField(module));
		fields.add(getOrgIdField(module));

		FacilioField ticketId = new FacilioField();
		ticketId.setName("ticketId");
		ticketId.setDataType(FieldType.NUMBER);
		ticketId.setColumnName("TICKET_ID");
		ticketId.setModule(module);
		fields.add(ticketId);

		FacilioField modifiedTime = new FacilioField();
		modifiedTime.setName("modifiedTime");
		modifiedTime.setDataType(FieldType.NUMBER);
		modifiedTime.setColumnName("MODIFIED_TIME");
		modifiedTime.setModule(module);
		fields.add(modifiedTime);

		FacilioField modifiedBy = new FacilioField();
		modifiedBy.setName("modifiedBy");
		modifiedBy.setDataType(FieldType.NUMBER);
		modifiedBy.setColumnName("MODIFIED_BY");
		modifiedBy.setModule(module);
		fields.add(modifiedBy);

		FacilioField activityType = new FacilioField();
		activityType.setName("activityType");
		activityType.setDataType(FieldType.NUMBER);
		activityType.setColumnName("ACTIVITY_TYPE");
		activityType.setModule(module);
		fields.add(activityType);

		FacilioField info = new FacilioField();
		info.setName("infoJsonStr");
		info.setDataType(FieldType.STRING);
		info.setColumnName("INFO");
		info.setModule(module);
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
		
		FacilioField placeholder = new FacilioField();
		placeholder.setName("placeholderStr");
		placeholder.setDataType(FieldType.STRING);
		placeholder.setColumnName("PLACEHOLDER");
		placeholder.setModule(module);
		fields.add(placeholder);

		return fields;
	}

	public static List<FacilioField> getEMailTemplateFields() {
		return getEMailTemplateFields(true);
	}

	private static List<FacilioField> getEMailTemplateFields(boolean isIdNeeded) {
		FacilioModule module = ModuleFactory.getEMailTemplatesModule();

		List<FacilioField> fields = new ArrayList<>();

		if (isIdNeeded) {
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

		if (isIdNeeded) {
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
		smsMsg.setName("message");
		smsMsg.setDataType(FieldType.STRING);
		smsMsg.setColumnName("MSG");
		smsMsg.setModule(module);
		fields.add(smsMsg);

		return fields;
	}
	
	public static List<FacilioField> getPushNotificationTemplateFields() {
		return getPushNotificationTemplateFields(true);
	}

	private static List<FacilioField> getPushNotificationTemplateFields(boolean isIdNeeded) {
		FacilioModule module = ModuleFactory.getPushNotificationTemplateModule();

		List<FacilioField> fields = new ArrayList<>();

		if (isIdNeeded) {
			fields.add(getIdField(module));
		}

		FacilioField smsTo = new FacilioField();
		smsTo.setName("to");
		smsTo.setDataType(FieldType.STRING);
		smsTo.setColumnName("TO_UID");
		smsTo.setModule(module);
		fields.add(smsTo);

		FacilioField smsMsg = new FacilioField();
		smsMsg.setName("body");
		smsMsg.setDataType(FieldType.STRING);
		smsMsg.setColumnName("MSG");
		smsMsg.setModule(module);
		fields.add(smsMsg);
		
		FacilioField title = new FacilioField();
		title.setName("title");
		title.setDataType(FieldType.STRING);
		title.setColumnName("TITLE");
		title.setModule(module);
		fields.add(title);
		
		FacilioField url = new FacilioField();
		url.setName("url");
		url.setDataType(FieldType.STRING);
		url.setColumnName("URL");
		url.setModule(module);
		fields.add(url);
		
		return fields;
	}

	public static List<FacilioField> getWebNotificationTemplateFields() {
		return getWebNotificationTemplateFields(true);
	}

	private static List<FacilioField> getWebNotificationTemplateFields(boolean isIdNeeded) {
		FacilioModule module = ModuleFactory.getWebNotificationTemplateModule();

		List<FacilioField> fields = new ArrayList<>();

		if (isIdNeeded) {
			fields.add(getIdField(module));
		}

		FacilioField smsTo = new FacilioField();
		smsTo.setName("to");
		smsTo.setDataType(FieldType.STRING);
		smsTo.setColumnName("TO_UID");
		smsTo.setModule(module);
		fields.add(smsTo);

		FacilioField smsMsg = new FacilioField();
		smsMsg.setName("message");
		smsMsg.setDataType(FieldType.STRING);
		smsMsg.setColumnName("MSG");
		smsMsg.setModule(module);
		fields.add(smsMsg);
		
		FacilioField title = new FacilioField();
		title.setName("title");
		title.setDataType(FieldType.STRING);
		title.setColumnName("TITLE");
		title.setModule(module);
		fields.add(title);
		
		FacilioField url = new FacilioField();
		url.setName("url");
		url.setDataType(FieldType.STRING);
		url.setColumnName("URL");
		url.setModule(module);
		fields.add(url);

		return fields;
	}
	
	public static List<FacilioField> getExcelTemplateFields() {
		List<FacilioField> fields = new ArrayList<>();
		FacilioModule module = ModuleFactory.getExcelTemplatesModule();

		fields.add(getIdField(module));

		FacilioField excelFileId = new FacilioField();
		excelFileId.setName("excelFileId");
		excelFileId.setDataType(FieldType.NUMBER);
		excelFileId.setColumnName("EXCEL_FILE_ID");
		excelFileId.setModule(module);
		fields.add(excelFileId);

		return fields;
	}
	
	public static List<FacilioField> getTemplatePlaceholder() {
		List<FacilioField> fields = new ArrayList<>();
		FacilioModule module = ModuleFactory.getTemplatePlaceholder();
		
		FacilioField templateId = new FacilioField();
		templateId.setName("templateId");
		templateId.setDataType(FieldType.NUMBER);
		templateId.setColumnName("TEMPLATE_ID");
		templateId.setModule(module);
		fields.add(templateId);
		
		FacilioField placeholder = new FacilioField();
		placeholder.setName("placeholder");
		placeholder.setDataType(FieldType.STRING);
		placeholder.setDisplayName("Placeholder");
		placeholder.setColumnName("PLACEHOLDER");
		placeholder.setModule(module);
		fields.add(placeholder);
		
		return fields;
	}
	
	public static List<FacilioField> getJSONTemplateFields() {
		FacilioModule module = ModuleFactory.getJSONTemplateModule();

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

	public static List<FacilioField> getTenantFields() {
		List<FacilioField> fields = new ArrayList<>();
		FacilioModule module = ModuleFactory.getTenantModule();

		fields.add(getIdField(module));

		FacilioField nameField = new FacilioField();
		nameField.setName("name");
		nameField.setDataType(FieldType.STRING);
		nameField.setColumnName("NAME");
		nameField.setModule(module);
		fields.add(nameField);

		fields.add(getOrgIdField());

		FacilioField addressField = new FacilioField();
		addressField.setName("address");
		addressField.setDataType(FieldType.STRING);
		addressField.setColumnName("ADDRESS");
		addressField.setModule(module);
		fields.add(addressField);

		FacilioField contactemailField = new FacilioField();
		contactemailField.setName("contactEmail");
		contactemailField.setDataType(FieldType.STRING);
		contactemailField.setColumnName("CONTACTEMAIL");
		contactemailField.setModule(module);
		fields.add(contactemailField);

		FacilioField contactNumberField = new FacilioField();
		contactNumberField.setName("contactNumber");
		contactNumberField.setDataType(FieldType.STRING);
		contactNumberField.setColumnName("CONTACTNUMBER");
		contactNumberField.setModule(module);
		fields.add(contactNumberField);

		FacilioField templateIdField = new FacilioField();
		templateIdField.setName("templateId");
		templateIdField.setDataType(FieldType.STRING);
		templateIdField.setColumnName("TEMPLATEID");
		templateIdField.setModule(module);
		fields.add(templateIdField);

		return fields;
	}

	public static List<FacilioField> getPreventiveMaintenanceFields() {
		FacilioModule module = ModuleFactory.getPreventiveMaintenancetModule();

		List<FacilioField> fields = new ArrayList<>();

		fields.add(getIdField(module));
		fields.add(getOrgIdField(module));

		FacilioField title = new FacilioField();
		title.setName("title");
		title.setDataType(FieldType.STRING);
		title.setColumnName("TITLE");
		title.setModule(module);
		fields.add(title);

		FacilioField status = new FacilioField();
		status.setName("status");
		status.setDataType(FieldType.BOOLEAN);
		status.setColumnName("STATUS");
		status.setModule(module);
		fields.add(status);

		FacilioField createdBy = new FacilioField();
		createdBy.setName("createdById");
		createdBy.setDataType(FieldType.NUMBER);
		createdBy.setColumnName("CREATED_BY");
		createdBy.setModule(module);
		fields.add(createdBy);

		FacilioField modifiedBy = new FacilioField();
		modifiedBy.setName("modifiedById");
		modifiedBy.setDataType(FieldType.NUMBER);
		modifiedBy.setColumnName("MODIFIED_BY");
		modifiedBy.setModule(module);
		fields.add(modifiedBy);

		FacilioField creationTime = new FacilioField();
		creationTime.setName("createdTime");
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

		FacilioField spaceId = new FacilioField();
		spaceId.setName("resourceId");
		spaceId.setDataType(FieldType.NUMBER);
		spaceId.setColumnName("RESOURCE_ID");
		spaceId.setModule(module);
		fields.add(spaceId);

		FacilioField assignedToId = new FacilioField();
		assignedToId.setName("assignedToid");
		assignedToId.setDataType(FieldType.NUMBER);
		assignedToId.setColumnName("ASSIGNED_TO_ID");
		assignedToId.setModule(module);
		fields.add(assignedToId);
		
		FacilioField assignmentGroupId = new FacilioField();
		assignmentGroupId.setName("assignmentGroupId");
		assignmentGroupId.setDataType(FieldType.NUMBER);
		assignmentGroupId.setColumnName("ASSIGNMENT_GROUP_ID");
		assignmentGroupId.setModule(module);
		fields.add(assignmentGroupId);
		
		FacilioField typeId = new FacilioField();
		typeId.setName("typeId");
		typeId.setDataType(FieldType.NUMBER);
		typeId.setColumnName("TYPE_ID");
		typeId.setModule(module);
		fields.add(typeId);

		fields.add(getField("triggerType", "TRIGGER_TYPE", module, FieldType.NUMBER));
		fields.add(getField("endTime", "END_TIME", module, FieldType.DATE_TIME));
		fields.add(getField("currentExecutionCount", "CURRENT_EXECUTION_COUNT", module, FieldType.NUMBER));
		fields.add(getField("maxCount", "MAX_COUNT", module, FieldType.NUMBER));
		
		return fields;
	}

	public static List<FacilioField> getJobFields() {
		FacilioModule module = ModuleFactory.getJobsModule();
		List<FacilioField> fields = new ArrayList<>();

		FacilioField jobId = new FacilioField();
		jobId.setName("jobId");
		jobId.setDataType(FieldType.NUMBER);
		jobId.setColumnName("JOBID");
		jobId.setModule(module);
		fields.add(jobId);

		fields.add(getOrgIdField(module));

		FacilioField jobName = new FacilioField();
		jobName.setName("jobName");
		jobName.setDataType(FieldType.STRING);
		jobName.setColumnName("JOBNAME");
		jobName.setModule(module);
		fields.add(jobName);

		FacilioField isActive = new FacilioField();
		isActive.setName("active");
		isActive.setDataType(FieldType.BOOLEAN);
		isActive.setColumnName("IS_ACTIVE");
		isActive.setModule(module);
		fields.add(isActive);

		FacilioField transactionTimeout = new FacilioField();
		transactionTimeout.setName("transactionTimeout");
		transactionTimeout.setDataType(FieldType.NUMBER);
		transactionTimeout.setColumnName("TRANSACTION_TIMEOUT");
		transactionTimeout.setModule(module);
		fields.add(transactionTimeout);

		FacilioField isPeriodic = new FacilioField();
		isPeriodic.setName("isPeriodic");
		isPeriodic.setDataType(FieldType.BOOLEAN);
		isPeriodic.setColumnName("IS_PERIODIC");
		isPeriodic.setModule(module);
		fields.add(isPeriodic);

		FacilioField period = new FacilioField();
		period.setName("period");
		period.setDataType(FieldType.NUMBER);
		period.setColumnName("PERIOD");
		period.setModule(module);
		fields.add(period);
		
		fields.add(getField("scheduleJson", "SCHEDULE_INFO", module, FieldType.STRING));

		FacilioField executionTime = new FacilioField();
		executionTime.setName("executionTime");
		executionTime.setDataType(FieldType.NUMBER);
		executionTime.setColumnName("NEXT_EXECUTION_TIME");
		executionTime.setModule(module);
		fields.add(executionTime);

		FacilioField executorName = new FacilioField();
		executorName.setName("executorName");
		executorName.setDataType(FieldType.STRING);
		executorName.setColumnName("EXECUTOR_NAME");
		executorName.setModule(module);
		fields.add(executorName);
		
		fields.add(getField("endExecutionTime", "END_EXECUTION_TIME", module, FieldType.NUMBER));
		fields.add(getField("maxExecution", "MAX_EXECUTION", module, FieldType.NUMBER));

		FacilioField currentExecutionCount = new FacilioField();
		currentExecutionCount.setName("currentExecutionCount");
		currentExecutionCount.setDataType(FieldType.NUMBER);
		currentExecutionCount.setColumnName("CURRENT_EXECUTION_COUNT");
		currentExecutionCount.setModule(module);
		fields.add(currentExecutionCount);

		

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
		
		FacilioField formulaId = new FacilioField();
		formulaId.setName("formulaId");
		formulaId.setDataType(FieldType.NUMBER);
		formulaId.setColumnName("FORMULA_ID");
		formulaId.setModule(module);
		fields.add(formulaId);

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

		FacilioField fieldName = new FacilioField();
		fieldName.setName("fieldName");
		fieldName.setDataType(FieldType.STRING);
		fieldName.setColumnName("FIELD_NAME");
		fieldName.setModule(module);
		fields.add(fieldName);

		FacilioField columnName = new FacilioField();
		columnName.setName("columnName");
		columnName.setDataType(FieldType.STRING);
		columnName.setColumnName("COLUMN_NAME");
		columnName.setModule(module);
		fields.add(columnName);

		FacilioField operatorId = new FacilioField();
		operatorId.setName("operatorId");
		operatorId.setDataType(FieldType.NUMBER);
		operatorId.setColumnName("OPERATOR");
		operatorId.setModule(module);
		fields.add(operatorId);

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

		FacilioField isExpressionValue = new FacilioField();
		isExpressionValue.setName("isExpressionValue");
		isExpressionValue.setDataType(FieldType.BOOLEAN);
		isExpressionValue.setColumnName("IS_EXPRESSION_VALUE");
		isExpressionValue.setModule(module);
		fields.add(isExpressionValue);

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
		
		FacilioField isDefault = new FacilioField();
		isDefault.setName("isDefault");
		isDefault.setDataType(FieldType.BOOLEAN);
		isDefault.setColumnName("ISDEFAULT");
		isDefault.setModule(module);
		fields.add(isDefault);
		
		FacilioField viewOrder = new FacilioField();
		viewOrder.setName("sequenceNumber");
		viewOrder.setDataType(FieldType.NUMBER);
		viewOrder.setColumnName("SEQUENCE_NUMBER");
		viewOrder.setModule(module);
		fields.add(viewOrder);
		
		FacilioField isHidden = new FacilioField();
		isHidden.setName("isHidden");
		isHidden.setDataType(FieldType.BOOLEAN);
		isHidden.setColumnName("ISHIDDEN");
		isHidden.setModule(module);
		fields.add(isHidden);
		
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

		FacilioField ttimeDate = new FacilioField();
		ttimeDate.setName("date");
		ttimeDate.setDisplayName("Date");
		ttimeDate.setDataType(FieldType.STRING);
		ttimeDate.setColumnName("TTIME_DATE");
		ttimeDate.setModule(module);
		fields.add(ttimeDate);

		FacilioField ttimeMonth = new FacilioField();
		ttimeMonth.setName("month");
		ttimeMonth.setDisplayName("Month");
		ttimeMonth.setDataType(FieldType.NUMBER);
		ttimeMonth.setColumnName("TTIME_MONTH");
		ttimeMonth.setModule(module);
		fields.add(ttimeMonth);

		FacilioField ttimeWeek = new FacilioField();
		ttimeWeek.setName("week");
		ttimeWeek.setDisplayName("Week");
		ttimeWeek.setDataType(FieldType.NUMBER);
		ttimeWeek.setColumnName("TTIME_WEEK");
		ttimeWeek.setModule(module);
		fields.add(ttimeWeek);

		FacilioField ttimeDay = new FacilioField();
		ttimeDay.setName("day");
		ttimeDay.setDisplayName("Day");
		ttimeDay.setDataType(FieldType.NUMBER);
		ttimeDay.setColumnName("TTIME_DAY");
		ttimeDay.setModule(module);
		fields.add(ttimeDay);

		FacilioField ttimeHour = new FacilioField();
		ttimeHour.setName("hour");
		ttimeHour.setDisplayName("Hour");
		ttimeHour.setDataType(FieldType.NUMBER);
		ttimeHour.setColumnName("TTIME_HOUR");
		ttimeHour.setModule(module);
		fields.add(ttimeHour);

		FacilioField parent = new FacilioField();
		parent.setName("parentId");
		parent.setDisplayName("Parent");
		parent.setDataType(FieldType.NUMBER);
		parent.setColumnName("PARENT_ID");
		parent.setModule(module);

		fields.add(parent);

		return fields;
	}

	public static List<FacilioField> getConnectedAppFields() {
		FacilioModule module = ModuleFactory.getConnectedAppModule();

		List<FacilioField> fields = new ArrayList<>();
		fields.add(getOrgIdField(module));

		FacilioField connectedAppId = new FacilioField();
		connectedAppId.setName("connectedAppId");
		connectedAppId.setDataType(FieldType.NUMBER);
		connectedAppId.setColumnName("CONNECTED_APP_ID");
		connectedAppId.setModule(module);
		fields.add(connectedAppId);

		FacilioField name = new FacilioField();
		name.setName("name");
		name.setDataType(FieldType.STRING);
		name.setColumnName("NAME");
		name.setModule(module);
		fields.add(name);

		FacilioField linkName = new FacilioField();
		linkName.setName("linkName");
		linkName.setDataType(FieldType.STRING);
		linkName.setColumnName("LINK_NAME");
		linkName.setModule(module);
		fields.add(linkName);

		FacilioField description = new FacilioField();
		description.setName("description");
		description.setDataType(FieldType.STRING);
		description.setColumnName("DESCRIPTION");
		description.setModule(module);
		fields.add(description);

		FacilioField baseurl = new FacilioField();
		baseurl.setName("baseurl");
		baseurl.setDataType(FieldType.STRING);
		baseurl.setColumnName("BASEURL");
		baseurl.setModule(module);
		fields.add(baseurl);

		return fields;
	}

	public static List<FacilioField> getTabWidgetFields() {
		FacilioModule module = ModuleFactory.getTabWidgetModule();

		List<FacilioField> fields = new ArrayList<>();
		fields.add(getOrgIdField(module));

		FacilioField tabWidgetId = new FacilioField();
		tabWidgetId.setName("tabWidgetId");
		tabWidgetId.setDataType(FieldType.NUMBER);
		tabWidgetId.setColumnName("TAB_WIDGET_ID");
		tabWidgetId.setModule(module);
		fields.add(tabWidgetId);

		FacilioField moduleLinkName = new FacilioField();
		moduleLinkName.setName("moduleLinkName");
		moduleLinkName.setDataType(FieldType.STRING);
		moduleLinkName.setColumnName("MODULE_LINK_NAME");
		moduleLinkName.setModule(module);
		fields.add(moduleLinkName);

		FacilioField tabName = new FacilioField();
		tabName.setName("tabName");
		tabName.setDataType(FieldType.STRING);
		tabName.setColumnName("TAB_NAME");
		tabName.setModule(module);
		fields.add(tabName);

		FacilioField tabLinkName = new FacilioField();
		tabLinkName.setName("tabLinkName");
		tabLinkName.setDataType(FieldType.STRING);
		tabLinkName.setColumnName("TAB_LINK_NAME");
		tabLinkName.setModule(module);
		fields.add(tabLinkName);

		FacilioField widgetName = new FacilioField();
		widgetName.setName("widgetName");
		widgetName.setDataType(FieldType.STRING);
		widgetName.setColumnName("WIDGET_NAME");
		widgetName.setModule(module);
		fields.add(widgetName);

		FacilioField connectedAppId = new FacilioField();
		connectedAppId.setName("connectedAppId");
		connectedAppId.setDataType(FieldType.NUMBER);
		connectedAppId.setColumnName("CONNECTED_APP_ID");
		connectedAppId.setModule(module);
		fields.add(connectedAppId);

		FacilioField resourcePath = new FacilioField();
		resourcePath.setName("resourcePath");
		resourcePath.setDataType(FieldType.STRING);
		resourcePath.setColumnName("RESOURCE_PATH");
		resourcePath.setModule(module);
		fields.add(resourcePath);

		return fields;
	}

	public static List<FacilioField> getBusinessHoursFields() {
		FacilioModule module = ModuleFactory.getBusinessHoursModule();
		List<FacilioField> fields = new ArrayList<>();
		fields.add(getIdField(module));
		fields.add(getOrgIdField(module));

		return fields;
	}

	public static List<FacilioField> getSingleDayBusinessHoursFields() {
		FacilioModule module = ModuleFactory.getSingleDayBusinessHourModule();
		List<FacilioField> fields = new ArrayList<>();

		FacilioField childId = new FacilioField();
		childId.setName("childId");
		childId.setDataType(FieldType.NUMBER);
		childId.setColumnName("ID");
		childId.setModule(module);
		fields.add(childId);

		FacilioField parentId = new FacilioField();
		parentId.setName("parentId");
		parentId.setDataType(FieldType.NUMBER);
		parentId.setColumnName("PARENT_ID");
		parentId.setModule(module);
		fields.add(parentId);

		FacilioField dayOfWeek = new FacilioField();
		dayOfWeek.setName("dayOfWeek");
		dayOfWeek.setDataType(FieldType.NUMBER);
		dayOfWeek.setColumnName("DAY_OF_WEEK");
		dayOfWeek.setModule(module);
		fields.add(dayOfWeek);

		FacilioField startTime = new FacilioField();
		startTime.setName("startTime");
		startTime.setDataType(FieldType.MISC);
		startTime.setColumnName("START_TIME");
		startTime.setModule(module);
		fields.add(startTime);

		FacilioField endTime = new FacilioField();
		endTime.setName("endTime");
		endTime.setDataType(FieldType.MISC);
		endTime.setColumnName("END_TIME");
		endTime.setModule(module);
		fields.add(endTime);

		return fields;
	}

	public static List<FacilioField> getFileFields() {
		FacilioModule module = ModuleFactory.getFilesModule();
		List<FacilioField> fields = new ArrayList<>();

		FacilioField fileName = new FacilioField();
		fileName.setName("fileName");
		fileName.setDataType(FieldType.STRING);
		fileName.setColumnName("FILE_NAME");
		fileName.setModule(module);
		fields.add(fileName);

		FacilioField fileSize = new FacilioField();
		fileSize.setName("fileSize");
		fileSize.setDataType(FieldType.NUMBER);
		fileSize.setColumnName("FILE_SIZE");
		fileSize.setModule(module);
		fields.add(fileSize);

		FacilioField uploadedBy = new FacilioField();
		uploadedBy.setName("uploadedBy");
		uploadedBy.setDataType(FieldType.NUMBER);
		uploadedBy.setColumnName("UPLOADED_BY");
		uploadedBy.setModule(module);
		fields.add(uploadedBy);

		FacilioField uploadedTime = new FacilioField();
		uploadedTime.setName("uploadedTime");
		uploadedTime.setDataType(FieldType.NUMBER);
		uploadedTime.setColumnName("UPLOADED_TIME");
		uploadedTime.setModule(module);
		fields.add(uploadedTime);

		FacilioField contentType = new FacilioField();
		contentType.setName("contentType");
		contentType.setDataType(FieldType.NUMBER);
		contentType.setColumnName("CONTENT_TYPE");
		contentType.setModule(module);
		fields.add(contentType);

		return fields;
	}

	public static List<FacilioField> getZoneRelFields() {
		FacilioModule module = ModuleFactory.getZoneRelModule();
		List<FacilioField> fields = new ArrayList<>();

		FacilioField zoneId = new FacilioField();
		zoneId.setName("zoneId");
		zoneId.setDataType(FieldType.NUMBER);
		zoneId.setColumnName("ZONE_ID");
		zoneId.setModule(module);
		fields.add(zoneId);

		FacilioField basespaceId = new FacilioField();
		basespaceId.setName("basespaceId");
		basespaceId.setDataType(FieldType.NUMBER);
		basespaceId.setColumnName("BASE_SPACE_ID");
		basespaceId.setModule(module);
		fields.add(basespaceId);

		return fields;
	}

	public static List<FacilioField> getControllerFields() {
		FacilioModule module = ModuleFactory.getControllerModule();
		List<FacilioField> fields = new ArrayList<>();

		fields.add(getIdField(module));
		fields.add(getOrgIdField(module));

		FacilioField name = new FacilioField();
		name.setName("name");
		name.setDataType(FieldType.STRING);
		name.setColumnName("NAME");
		name.setModule(module);
		fields.add(name);

		FacilioField spaceId = new FacilioField();
		spaceId.setName("spaceId");
		spaceId.setDataType(FieldType.NUMBER);
		spaceId.setColumnName("SPACE_ID");
		spaceId.setModule(module);
		fields.add(spaceId);

		FacilioField macAddr = new FacilioField();
		macAddr.setName("macAddr");
		macAddr.setDataType(FieldType.STRING);
		macAddr.setColumnName("MAC_ADDR");
		macAddr.setModule(module);
		fields.add(macAddr);

		return fields;
	}

	public static List<FacilioField> getNotificationFields() {
		FacilioModule module = ModuleFactory.getNotificationModule();
		List<FacilioField> fields = new ArrayList<>();

		fields.add(getIdField(module));
		fields.add(getOrgIdField(module));
		fields.add(getUserIdField(module));

		FacilioField notificationType = new FacilioField();
		notificationType.setName("notificationTypeVal");
		notificationType.setDataType(FieldType.NUMBER);
		notificationType.setColumnName("NOTIFICATION_TYPE");
		notificationType.setModule(module);
		fields.add(notificationType);

		FacilioField actorId = new FacilioField();
		actorId.setName("actorId");
		actorId.setDataType(FieldType.NUMBER);
		actorId.setColumnName("ACTOR_ID");
		actorId.setModule(module);
		fields.add(actorId);

		FacilioField info = new FacilioField();
		info.setName("info");
		info.setDataType(FieldType.STRING);
		info.setColumnName("INFO");
		info.setModule(module);
		fields.add(info);

		FacilioField isRead = new FacilioField();
		isRead.setName("isRead");
		isRead.setDataType(FieldType.BOOLEAN);
		isRead.setColumnName("IS_READ");
		isRead.setModule(module);
		fields.add(isRead);

		FacilioField readAt = new FacilioField();
		readAt.setName("readAt");
		readAt.setDataType(FieldType.NUMBER);
		readAt.setColumnName("READ_AT");
		readAt.setModule(module);
		fields.add(readAt);

		FacilioField isSeen = new FacilioField();
		isSeen.setName("isSeen");
		isSeen.setDataType(FieldType.BOOLEAN);
		isSeen.setColumnName("IS_SEEN");
		isSeen.setModule(module);
		fields.add(isSeen);

		FacilioField seenAt = new FacilioField();
		seenAt.setName("seenAt");
		seenAt.setDataType(FieldType.NUMBER);
		seenAt.setColumnName("SEEN_AT");
		seenAt.setModule(module);
		fields.add(seenAt);

		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(module);
		fields.add(createdTime);

		return fields;
	}

	public static List<FacilioField> getVirtualMeterRelFields() {
		List<FacilioField> fields = new ArrayList<>();
		FacilioModule module = ModuleFactory.getVirtualMeterRelModule();

		FacilioField virtualMeterId = new FacilioField();
		virtualMeterId.setName("virtualMeterId");
		virtualMeterId.setDataType(FieldType.NUMBER);
		virtualMeterId.setColumnName("VIRTUAL_METER_ID");
		virtualMeterId.setModule(module);
		fields.add(virtualMeterId);

		FacilioField childMeterId = new FacilioField();
		childMeterId.setName("childMeterId");
		childMeterId.setDataType(FieldType.NUMBER);
		childMeterId.setColumnName("CHILD_METER_ID");
		childMeterId.setModule(module);
		fields.add(childMeterId);

		return fields;
	}

	public static List<FacilioField> getPmToWoRelFields() {
		List<FacilioField> fields = new ArrayList<>();
		FacilioModule module = ModuleFactory.getPmToWoRelModule();

		FacilioField pmId = new FacilioField();
		pmId.setName("pmId");
		pmId.setDataType(FieldType.NUMBER);
		pmId.setColumnName("PM_ID");
		pmId.setModule(module);
		fields.add(pmId);

		FacilioField woId = new FacilioField();
		woId.setName("woId");
		woId.setDataType(FieldType.NUMBER);
		woId.setColumnName("WO_ID");
		woId.setModule(module);
		fields.add(woId);
		return fields;
	}

	public static List<FacilioField> getDashboardFields() {
		List<FacilioField> fields = new ArrayList<>();
		FacilioModule module = ModuleFactory.getDashboardModule();

		fields.add(getIdField(module));
		fields.add(getOrgIdField(module));
		fields.add(getModuleIdField(module));

		FacilioField dashboardName = new FacilioField();
		dashboardName.setName("dashboardName");
		dashboardName.setDataType(FieldType.STRING);
		dashboardName.setColumnName("DASHBOARD_NAME");
		dashboardName.setModule(module);
		fields.add(dashboardName);
		
		FacilioField dashboardLinkName = new FacilioField();
		dashboardLinkName.setName("linkName");
		dashboardLinkName.setDataType(FieldType.STRING);
		dashboardLinkName.setColumnName("LINK_NAME");
		dashboardLinkName.setModule(module);
		fields.add(dashboardLinkName);

		FacilioField createdByUser = new FacilioField();
		createdByUser.setName("createdByUserId");
		createdByUser.setDataType(FieldType.NUMBER);
		createdByUser.setColumnName("CREATED_BY_USER_ID");
		createdByUser.setModule(module);
		fields.add(createdByUser);

		FacilioField publishStatus = new FacilioField();
		publishStatus.setName("publishStatus");
		publishStatus.setDataType(FieldType.NUMBER);
		publishStatus.setColumnName("PUBLISH_STATUS");
		publishStatus.setModule(module);
		fields.add(publishStatus);

		FacilioField dashboardUrl = new FacilioField();
		dashboardUrl.setName("dashboardUrl");
		dashboardUrl.setDataType(FieldType.STRING);
		dashboardUrl.setColumnName("DASHBOARD_URL");
		dashboardUrl.setModule(module);
		fields.add(dashboardUrl);

		return fields;
	}

	public static List<FacilioField> getDashbaordVsWidgetFields() {
		List<FacilioField> fields = new ArrayList<>();
		FacilioModule module = ModuleFactory.getDashboardVsWidgetModule();

		FacilioField dashboardWidgetDashboardId = new FacilioField();
		dashboardWidgetDashboardId.setName("dashboardId");
		dashboardWidgetDashboardId.setDataType(FieldType.NUMBER);
		dashboardWidgetDashboardId.setColumnName("DASHBOARD_ID");
		dashboardWidgetDashboardId.setModule(module);
		fields.add(dashboardWidgetDashboardId);

		FacilioField widgetId = new FacilioField();
		widgetId.setName("widgetId");
		widgetId.setDataType(FieldType.NUMBER);
		widgetId.setColumnName("WIDGET_ID");
		widgetId.setModule(module);
		fields.add(widgetId);

		FacilioField dashboardWidgetLayoutWidth = new FacilioField();
		dashboardWidgetLayoutWidth.setName("layoutWidth");
		dashboardWidgetLayoutWidth.setDataType(FieldType.NUMBER);
		dashboardWidgetLayoutWidth.setColumnName("LAYOUT_WIDTH");
		dashboardWidgetLayoutWidth.setModule(module);
		fields.add(dashboardWidgetLayoutWidth);

		FacilioField dashboardWidgetLayoutHeight = new FacilioField();
		dashboardWidgetLayoutHeight.setName("layoutHeight");
		dashboardWidgetLayoutHeight.setDataType(FieldType.NUMBER);
		dashboardWidgetLayoutHeight.setColumnName("LAYOUT_HEIGHT");
		dashboardWidgetLayoutHeight.setModule(module);
		fields.add(dashboardWidgetLayoutHeight);
		
		FacilioField xPosition = new FacilioField();
		xPosition.setName("xPosition");
		xPosition.setDataType(FieldType.NUMBER);
		xPosition.setColumnName("X_POSITION");
		xPosition.setModule(module);
		fields.add(xPosition);

		FacilioField yPosition = new FacilioField();
		yPosition.setName("yPosition");
		yPosition.setDataType(FieldType.NUMBER);
		yPosition.setColumnName("Y_POSITION");
		yPosition.setModule(module);
		fields.add(yPosition);

		FacilioField dashboardWidgetLayoutPosition = new FacilioField();
		dashboardWidgetLayoutPosition.setName("layoutPosition");
		dashboardWidgetLayoutPosition.setDataType(FieldType.NUMBER);
		dashboardWidgetLayoutPosition.setColumnName("LAYOUT_POSITION");
		dashboardWidgetLayoutPosition.setModule(module);
		fields.add(dashboardWidgetLayoutPosition);

		return fields;
	}

	public static List<FacilioField> getWidgetFields() {
		List<FacilioField> fields = new ArrayList<>();
		FacilioModule module = ModuleFactory.getWidgetModule();

		fields.add(getIdField(module));
		fields.add(getOrgIdField(module));
		fields.add(getModuleIdField(module));

		FacilioField dashboardWidgetName = new FacilioField();
		dashboardWidgetName.setName("widgetName");
		dashboardWidgetName.setDataType(FieldType.STRING);
		dashboardWidgetName.setColumnName("WIDGET_NAME");
		dashboardWidgetName.setModule(module);
		fields.add(dashboardWidgetName);

		FacilioField dashboardWidgetType = new FacilioField();
		dashboardWidgetType.setName("type");
		dashboardWidgetType.setDataType(FieldType.NUMBER);
		dashboardWidgetType.setColumnName("TYPE");
		dashboardWidgetType.setModule(module);
		fields.add(dashboardWidgetType);

		FacilioField dashboardWidgetUrl = new FacilioField();
		dashboardWidgetUrl.setName("dataOptionDataUrl");
		dashboardWidgetUrl.setDataType(FieldType.STRING);
		dashboardWidgetUrl.setColumnName("WIDGET_URL");
		dashboardWidgetUrl.setModule(module);
		fields.add(dashboardWidgetUrl);

		FacilioField dashboardWidgetDataOptionRefreshIntervel = new FacilioField();
		dashboardWidgetDataOptionRefreshIntervel.setName("dataRefreshIntervel");
		dashboardWidgetDataOptionRefreshIntervel.setDataType(FieldType.NUMBER);
		dashboardWidgetDataOptionRefreshIntervel.setColumnName("DATA_REFRESH_INTERTVEL");
		dashboardWidgetDataOptionRefreshIntervel.setModule(module);
		fields.add(dashboardWidgetDataOptionRefreshIntervel);
		
		FacilioField dashboardWidgetHeaderTitle = new FacilioField();
		dashboardWidgetHeaderTitle.setName("headerText");
		dashboardWidgetHeaderTitle.setDataType(FieldType.STRING);
		dashboardWidgetHeaderTitle.setColumnName("HEADER_TEXT");
		dashboardWidgetHeaderTitle.setModule(module);
		fields.add(dashboardWidgetHeaderTitle);

		FacilioField dashboardWidgetHeaderSubTitle = new FacilioField();
		dashboardWidgetHeaderSubTitle.setName("headerSubText");
		dashboardWidgetHeaderSubTitle.setDataType(FieldType.STRING);
		dashboardWidgetHeaderSubTitle.setColumnName("HEADER_SUB_TEXT");
		dashboardWidgetHeaderSubTitle.setModule(module);
		fields.add(dashboardWidgetHeaderSubTitle);

		FacilioField dashboardWidgetHeaderIsExport = new FacilioField();
		dashboardWidgetHeaderIsExport.setName("headerIsExport");
		dashboardWidgetHeaderIsExport.setDataType(FieldType.BOOLEAN);
		dashboardWidgetHeaderIsExport.setColumnName("HEADER_IS_EXPORT");
		dashboardWidgetHeaderIsExport.setModule(module);
		fields.add(dashboardWidgetHeaderIsExport);

		return fields;
	}

	public static List<FacilioField> getWidgetChartFields() {
		List<FacilioField> fields = new ArrayList<>();
		FacilioModule module = ModuleFactory.getWidgetChartModule();

		fields.add(getIdField(module));
		
		FacilioField reportId = new FacilioField();
		reportId.setName("reportId");
		reportId.setDataType(FieldType.NUMBER);
		reportId.setColumnName("REPORT_ID");
		reportId.setModule(module);
		fields.add(reportId);

		return fields;
	}
	public static List<FacilioField> getWidgetListViewFields() {
		List<FacilioField> fields = new ArrayList<>();
		FacilioModule module = ModuleFactory.getWidgetListViewModule();

		fields.add(getIdField(module));
		
		FacilioField moduleName = new FacilioField();
		moduleName.setName("moduleName");
		moduleName.setDataType(FieldType.STRING);
		moduleName.setColumnName("MODULE_NAME");
		moduleName.setModule(module);
		fields.add(moduleName);
		
		FacilioField viewName = new FacilioField();
		viewName.setName("viewName");
		viewName.setDataType(FieldType.STRING);
		viewName.setColumnName("VIEW_NAME");
		viewName.setModule(module);
		fields.add(viewName);

		return fields;
	}
	public static List<FacilioField> getWidgetStaticFields() {
		List<FacilioField> fields = new ArrayList<>();
		FacilioModule module = ModuleFactory.getWidgetStaticModule();

		fields.add(getIdField(module));
		
		FacilioField staticKey = new FacilioField();
		staticKey.setName("staticKey");
		staticKey.setDataType(FieldType.STRING);
		staticKey.setColumnName("STATIC_KEY");
		staticKey.setModule(module);
		fields.add(staticKey);

		return fields;
	}
	public static List<FacilioField> getWidgetWebFields() {
		List<FacilioField> fields = new ArrayList<>();
		FacilioModule module = ModuleFactory.getWidgetWebModule();

		fields.add(getIdField(module));
		
		FacilioField webUrl = new FacilioField();
		webUrl.setName("webUrl");
		webUrl.setDataType(FieldType.STRING);
		webUrl.setColumnName("WEB_URL");
		webUrl.setModule(module);
		fields.add(webUrl);

		return fields;
	}
	
	public static List<FacilioField> getReportFolderFields() {
		
		List<FacilioField> fields = new ArrayList<>();
		FacilioModule module = ModuleFactory.getReportFolder();

		fields.add(getIdField(module));
		fields.add(getModuleIdField(module));
		fields.add(getOrgIdField(module));

		FacilioField parentFolderId = new FacilioField();
		parentFolderId.setName("parentFolderId");
		parentFolderId.setDataType(FieldType.NUMBER);
		parentFolderId.setColumnName("PARENT_FOLDER_ID");
		parentFolderId.setModule(module);
		fields.add(parentFolderId);
		
		FacilioField buildingId = new FacilioField();
		buildingId.setName("buildingId");
		buildingId.setDataType(FieldType.NUMBER);
		buildingId.setColumnName("BUILDING_ID");
		buildingId.setModule(module);
		fields.add(buildingId);

		FacilioField name = new FacilioField();
		name.setName("name");
		name.setDataType(FieldType.STRING);
		name.setColumnName("NAME");
		name.setModule(module);
		fields.add(name);

		return fields;
	}
	
	public static List<FacilioField> getReportFields() {
		
		List<FacilioField> fields = new ArrayList<>();
		FacilioModule module = ModuleFactory.getReport();

		fields.add(getIdField(module));
		
		FacilioField parentFolderId = new FacilioField();
		parentFolderId.setName("parentFolderId");
		parentFolderId.setDataType(FieldType.NUMBER);
		parentFolderId.setColumnName("REPORT_FOLDER_ID");
		parentFolderId.setModule(module);
		fields.add(parentFolderId);
		
		FacilioField name = new FacilioField();
		name.setName("name");
		name.setDataType(FieldType.STRING);
		name.setColumnName("NAME");
		name.setModule(module);
		fields.add(name);
		
		FacilioField description = new FacilioField();
		description.setName("description");
		description.setDataType(FieldType.STRING);
		description.setColumnName("DESCRIPTION");
		description.setModule(module);
		fields.add(description);
		
		FacilioField chartType = new FacilioField();
		chartType.setName("chartType");
		chartType.setDataType(FieldType.NUMBER);
		chartType.setColumnName("CHART_TYPE");
		chartType.setModule(module);
		fields.add(chartType);

		FacilioField secChartType = new FacilioField();
		secChartType.setName("secChartType");
		secChartType.setDataType(FieldType.NUMBER);
		secChartType.setColumnName("SEC_CHART_TYPE");
		secChartType.setModule(module);
		fields.add(secChartType);

		FacilioField xAxis = new FacilioField();
		xAxis.setName("xAxis");
		xAxis.setDataType(FieldType.NUMBER);
		xAxis.setColumnName("X_AXIS");
		xAxis.setModule(module);
		fields.add(xAxis);
		
		FacilioField xAxisaggregateFunction = new FacilioField();
		xAxisaggregateFunction.setName("xAxisaggregateFunction");
		xAxisaggregateFunction.setDataType(FieldType.NUMBER);
		xAxisaggregateFunction.setColumnName("X_AGGREGATE_FUNCTION");
		xAxisaggregateFunction.setModule(module);
		fields.add(xAxisaggregateFunction);

		FacilioField xAxisLabel = new FacilioField();
		xAxisLabel.setName("xAxisLabel");
		xAxisLabel.setDataType(FieldType.STRING);
		xAxisLabel.setColumnName("X_AXIS_LABEL");
		xAxisLabel.setModule(module);
		fields.add(xAxisLabel);
		
		FacilioField xAxisUnit = new FacilioField();
		xAxisUnit.setName("xAxisUnit");
		xAxisUnit.setDataType(FieldType.STRING);
		xAxisUnit.setColumnName("X_AXIS_UNIT");
		xAxisUnit.setModule(module);
		fields.add(xAxisUnit);
		
		FacilioField y1Axis = new FacilioField();
		y1Axis.setName("y1Axis");
		y1Axis.setDataType(FieldType.NUMBER);
		y1Axis.setColumnName("Y1_AXIS");
		y1Axis.setModule(module);
		fields.add(y1Axis);
		
		FacilioField y1AxisaggregateFunction = new FacilioField();
		y1AxisaggregateFunction.setName("y1AxisaggregateFunction");
		y1AxisaggregateFunction.setDataType(FieldType.NUMBER);
		y1AxisaggregateFunction.setColumnName("Y1_AGGREGATE_FUNCTION");
		y1AxisaggregateFunction.setModule(module);
		fields.add(y1AxisaggregateFunction);
		
		FacilioField y1AxisLabel = new FacilioField();
		y1AxisLabel.setName("y1AxisLabel");
		y1AxisLabel.setDataType(FieldType.STRING);
		y1AxisLabel.setColumnName("Y1_AXIS_LABEL");
		y1AxisLabel.setModule(module);
		fields.add(y1AxisLabel);
		
		FacilioField y1AxisUnit = new FacilioField();
		y1AxisUnit.setName("y1AxisUnit");
		y1AxisUnit.setDataType(FieldType.STRING);
		y1AxisUnit.setColumnName("Y1_AXIS_UNIT");
		y1AxisUnit.setModule(module);
		fields.add(y1AxisUnit);
		
		FacilioField y2Axis = new FacilioField();
		y2Axis.setName("y2Axis");
		y2Axis.setDataType(FieldType.NUMBER);
		y2Axis.setColumnName("Y2_AXIS");
		y2Axis.setModule(module);
		fields.add(y2Axis);
		
		FacilioField y2AxisaggregateFunction = new FacilioField();
		y2AxisaggregateFunction.setName("y2AxisaggregateFunction");
		y2AxisaggregateFunction.setDataType(FieldType.NUMBER);
		y2AxisaggregateFunction.setColumnName("Y2_AGGREGATE_FUNCTION");
		y2AxisaggregateFunction.setModule(module);
		fields.add(y2AxisaggregateFunction);
		
		FacilioField y3Axis = new FacilioField();
		y3Axis.setName("y3Axis");
		y3Axis.setDataType(FieldType.NUMBER);
		y3Axis.setColumnName("Y3_AXIS");
		y3Axis.setModule(module);
		fields.add(y3Axis);
		
		FacilioField y3AxisaggregateFunction = new FacilioField();
		y3AxisaggregateFunction.setName("y3AxisaggregateFunction");
		y3AxisaggregateFunction.setDataType(FieldType.NUMBER);
		y3AxisaggregateFunction.setColumnName("Y3_AGGREGATE_FUNCTION");
		y3AxisaggregateFunction.setModule(module);
		fields.add(y3AxisaggregateFunction);
		
		FacilioField isComparisionReport = new FacilioField();
		isComparisionReport.setName("isComparisionReport");
		isComparisionReport.setDataType(FieldType.NUMBER);
		isComparisionReport.setColumnName("IS_COMPARISION_REPORT");
		isComparisionReport.setModule(module);
		fields.add(isComparisionReport);
		
		FacilioField isHighResolutionReport = new FacilioField();
		isHighResolutionReport.setName("isHighResolutionReport");
		isHighResolutionReport.setDataType(FieldType.BOOLEAN);
		isHighResolutionReport.setColumnName("IS_HIGHRESOLUTION_REPORT");
		isHighResolutionReport.setModule(module);
		fields.add(isHighResolutionReport);
		
		FacilioField xAxisLegend = new FacilioField();
		xAxisLegend.setName("xAxisLegend");
		xAxisLegend.setDataType(FieldType.STRING);
		xAxisLegend.setColumnName("X_AXIS_LEGEND");
		xAxisLegend.setModule(module);
		fields.add(xAxisLegend);

		FacilioField groupBy = new FacilioField();
		groupBy.setName("groupBy");
		groupBy.setDataType(FieldType.NUMBER);
		groupBy.setColumnName("GROUP_BY");
		groupBy.setModule(module);
		fields.add(groupBy);
		
		FacilioField groupByLabel = new FacilioField();
		groupByLabel.setName("groupByLabel");
		groupByLabel.setDataType(FieldType.STRING);
		groupByLabel.setColumnName("GROUP_BY_LABEL");
		groupByLabel.setModule(module);
		fields.add(groupByLabel);
		
		FacilioField groupByUnit = new FacilioField();
		groupByUnit.setName("groupByUnit");
		groupByUnit.setDataType(FieldType.STRING);
		groupByUnit.setColumnName("GROUP_BY_UNIT");
		groupByUnit.setModule(module);
		fields.add(groupByUnit);
		
		FacilioField groupByFieldAggregateFunction = new FacilioField();
		groupByFieldAggregateFunction.setName("groupByFieldAggregateFunction");
		groupByFieldAggregateFunction.setDataType(FieldType.NUMBER);
		groupByFieldAggregateFunction.setColumnName("GROUP_BY_AGGREGATE_FUNCTION");
		groupByFieldAggregateFunction.setModule(module);
		fields.add(groupByFieldAggregateFunction);
		
		FacilioField limit = new FacilioField();
		limit.setName("limit");
		limit.setDataType(FieldType.NUMBER);
		limit.setColumnName("LLIMIT");
		limit.setModule(module);
		fields.add(limit);
		
		FacilioField orderBy = new FacilioField();
		orderBy.setName("orderBy");
		orderBy.setDataType(FieldType.STRING);
		orderBy.setColumnName("ORDER_BY");
		orderBy.setModule(module);
		fields.add(orderBy);
		
		FacilioField orderByFunction = new FacilioField();
		orderByFunction.setName("orderByFunction");
		orderByFunction.setDataType(FieldType.NUMBER);
		orderByFunction.setColumnName("ORDER_BY_FUNCTION");
		orderByFunction.setModule(module);
		fields.add(orderByFunction);
		
		return fields;
	}
	public static List<FacilioField> getReportFieldFields() {
		List<FacilioField> fields = new ArrayList<>();
		FacilioModule module = ModuleFactory.getReportField();

		fields.add(getIdField(module));

		FacilioField fieldLabel = new FacilioField();
		fieldLabel.setName("fieldLabel");
		fieldLabel.setDataType(FieldType.STRING);
		fieldLabel.setColumnName("FIELD_LABEL");
		fieldLabel.setModule(module);
		fields.add(fieldLabel);
		
		FacilioField unit = new FacilioField();
		unit.setName("unit");
		unit.setDataType(FieldType.STRING);
		unit.setColumnName("UNIT");
		unit.setModule(module);
		fields.add(unit);
		
		FacilioField moduleFieldId = new FacilioField();
		moduleFieldId.setName("moduleFieldId");
		moduleFieldId.setDataType(FieldType.NUMBER);
		moduleFieldId.setColumnName("MODULE_FIELD_ID");
		moduleFieldId.setModule(module);
		fields.add(moduleFieldId);

		FacilioField formulaFieldId = new FacilioField();
		formulaFieldId.setName("formulaFieldId");
		formulaFieldId.setDataType(FieldType.NUMBER);
		formulaFieldId.setColumnName("FORMULA_FIELD_ID");
		formulaFieldId.setModule(module);
		fields.add(formulaFieldId);
		
		return fields;
	}
	public static List<FacilioField> getReportDateFilterFields() {
		List<FacilioField> fields = new ArrayList<>();
		FacilioModule module = ModuleFactory.getReportDateFilter();

		fields.add(getIdField(module));
		
		FacilioField reportId = new FacilioField();
		reportId.setName("reportId");
		reportId.setDataType(FieldType.NUMBER);
		reportId.setColumnName("REPORT_ID");
		reportId.setModule(module);
		fields.add(reportId);
		
		FacilioField fieldId = new FacilioField();
		fieldId.setName("fieldId");
		fieldId.setDataType(FieldType.NUMBER);
		fieldId.setColumnName("FIELD_ID");
		fieldId.setModule(module);
		fields.add(fieldId);

		FacilioField operatorId = new FacilioField();
		operatorId.setName("operatorId");
		operatorId.setDataType(FieldType.NUMBER);
		operatorId.setColumnName("OPERATOR");
		operatorId.setModule(module);
		fields.add(operatorId);

		FacilioField value = new FacilioField();
		value.setName("value");
		value.setDataType(FieldType.STRING);
		value.setColumnName("VAL");
		value.setModule(module);
		fields.add(value);
		
		return fields;
	}
	public static List<FacilioField> getReportEnergyMeterFields() {
		List<FacilioField> fields = new ArrayList<>();
		FacilioModule module = ModuleFactory.getReportEnergyMeter();

		fields.add(getIdField(module));
		
		FacilioField reportId = new FacilioField();
		reportId.setName("reportId");
		reportId.setDataType(FieldType.NUMBER);
		reportId.setColumnName("REPORT_ID");
		reportId.setModule(module);
		fields.add(reportId);
		
		FacilioField buildingId = new FacilioField();
		buildingId.setName("buildingId");
		buildingId.setDataType(FieldType.NUMBER);
		buildingId.setColumnName("BUILDING_ID");
		buildingId.setModule(module);
		fields.add(buildingId);
		
		FacilioField serviceId = new FacilioField();
		serviceId.setName("serviceId");
		serviceId.setDataType(FieldType.NUMBER);
		serviceId.setColumnName("SERVICE_ID");
		serviceId.setModule(module);
		fields.add(serviceId);
		
		FacilioField subMeterId = new FacilioField();
		subMeterId.setName("subMeterId");
		subMeterId.setDataType(FieldType.NUMBER);
		subMeterId.setColumnName("SUB_METER_ID");
		subMeterId.setModule(module);
		fields.add(subMeterId);

		FacilioField groupBy = new FacilioField();
		groupBy.setName("groupBy");
		groupBy.setDataType(FieldType.STRING);
		groupBy.setColumnName("GROUP_BY");
		groupBy.setModule(module);
		fields.add(groupBy);
		
		return fields;
	}
	public static List<FacilioField> getReportFormulaFieldFields() {
		List<FacilioField> fields = new ArrayList<>();
		FacilioModule module = ModuleFactory.getReportFormulaField();

		fields.add(getIdField(module));

		FacilioField name = new FacilioField();
		name.setName("name");
		name.setDataType(FieldType.STRING);
		name.setColumnName("NAME");
		name.setModule(module);
		fields.add(name);

		FacilioField dataType = new FacilioField();
		dataType.setName("dataType");
		dataType.setDataType(FieldType.NUMBER);
		dataType.setColumnName("DATA_TYPE");
		dataType.setModule(module);
		fields.add(dataType);
		
		FacilioField formula = new FacilioField();
		formula.setName("formula");
		formula.setDataType(FieldType.STRING);
		formula.setColumnName("FORMULA");
		formula.setModule(module);
		fields.add(formula);

		return fields;
	}
	public static List<FacilioField> getReportCriteriaFields() {
		List<FacilioField> fields = new ArrayList<>();
		FacilioModule module = ModuleFactory.getReportCriteria();

		fields.add(getIdField(module));

		FacilioField reportId = new FacilioField();
		reportId.setName("reportId");
		reportId.setDataType(FieldType.NUMBER);
		reportId.setColumnName("REPORT_ID");
		reportId.setModule(module);
		fields.add(reportId);

		FacilioField criteriaId = new FacilioField();
		criteriaId.setName("criteriaId");
		criteriaId.setDataType(FieldType.NUMBER);
		criteriaId.setColumnName("CRITERIA_ID");
		criteriaId.setModule(module);
		fields.add(criteriaId);

		return fields;
	}
	
	public static List<FacilioField> getReportThresholdFields() {
		
		List<FacilioField> fields = new ArrayList<>();
		FacilioModule module = ModuleFactory.getReportThreshold();

		fields.add(getIdField(module));

		FacilioField reportId = new FacilioField();
		reportId.setName("reportId");
		reportId.setDataType(FieldType.NUMBER);
		reportId.setColumnName("REPORT_ID");
		reportId.setModule(module);
		fields.add(reportId);

		FacilioField name = new FacilioField();
		name.setName("name");
		name.setDataType(FieldType.STRING);
		name.setColumnName("NAME");
		name.setModule(module);
		fields.add(name);
		
		FacilioField value = new FacilioField();
		value.setName("value");
		value.setDataType(FieldType.NUMBER);
		value.setColumnName("VALUE");
		value.setModule(module);
		fields.add(value);
		
		FacilioField color = new FacilioField();
		color.setName("color");
		color.setDataType(FieldType.STRING);
		color.setColumnName("COLOR");
		color.setModule(module);
		fields.add(color);
		
		FacilioField lineStyle = new FacilioField();
		lineStyle.setName("lineStyle");
		lineStyle.setDataType(FieldType.NUMBER);
		lineStyle.setColumnName("LINE_STYLE");
		lineStyle.setModule(module);
		fields.add(lineStyle);

		return fields;
	}
	public static List<FacilioField> getReportUserFilterFields() {
		List<FacilioField> fields = new ArrayList<>();
		FacilioModule module = ModuleFactory.getReportUserFilter();

		fields.add(getIdField(module));

		FacilioField reportId = new FacilioField();
		reportId.setName("reportId");
		reportId.setDataType(FieldType.NUMBER);
		reportId.setColumnName("REPORT_ID");
		reportId.setModule(module);
		fields.add(reportId);

		FacilioField reportFieldId = new FacilioField();
		reportFieldId.setName("reportFieldId");
		reportFieldId.setDataType(FieldType.NUMBER);
		reportFieldId.setColumnName("REPORT_FIELD_ID");
		reportFieldId.setModule(module);
		fields.add(reportFieldId);

		FacilioField whereClause = new FacilioField();
		whereClause.setName("whereClause");
		whereClause.setDataType(FieldType.STRING);
		whereClause.setColumnName("WHERE_CLAUSE");
		whereClause.setModule(module);
		fields.add(whereClause);

		return fields;
	}
	public static List<FacilioField> getFormulaFields() {
		List<FacilioField> fields = new ArrayList<>();
		FacilioModule module = ModuleFactory.getFormulaModule();

		fields.add(getIdField(module));
		fields.add(getOrgIdField(module));
		fields.add(getModuleIdField(module));

		FacilioField selectFieldId = new FacilioField();
		selectFieldId.setName("selectFieldId");
		selectFieldId.setDataType(FieldType.NUMBER);
		selectFieldId.setColumnName("SELECT_FIELD_ID");
		selectFieldId.setModule(module);
		fields.add(selectFieldId);

		FacilioField aggregateOperation = new FacilioField();
		aggregateOperation.setName("aggregateOperationValue");
		aggregateOperation.setDataType(FieldType.NUMBER);
		aggregateOperation.setColumnName("AGGREGATE_OPERATION");
		aggregateOperation.setModule(module);
		fields.add(aggregateOperation);

		FacilioField criteriaId = new FacilioField();
		criteriaId.setName("criteriaId");
		criteriaId.setDataType(FieldType.NUMBER);
		criteriaId.setColumnName("CRITERIA_ID");
		criteriaId.setModule(module);
		fields.add(criteriaId);

		return fields;

	}
	
	public static List<FacilioField> getExpressionFields() {
		List<FacilioField> fields = new ArrayList<>();
		FacilioModule module = ModuleFactory.getExpressionModule();

		fields.add(getIdField(module));
		fields.add(getOrgIdField(module));

		FacilioField selectFieldId = new FacilioField();
		selectFieldId.setName("expressionString");
		selectFieldId.setDataType(FieldType.STRING);
		selectFieldId.setColumnName("EXPRESSION_STRING");
		selectFieldId.setModule(module);
		fields.add(selectFieldId);

		return fields;
	}

	public static List<FacilioField> getPMReminderFields() {
		List<FacilioField> fields = new ArrayList<>();
		FacilioModule module = ModuleFactory.getPMReminderModule();

		fields.add(getIdField(module));
		fields.add(getOrgIdField(module));

		FacilioField pmId = new FacilioField();
		pmId.setName("pmId");
		pmId.setDataType(FieldType.NUMBER);
		pmId.setColumnName("PM_ID");
		pmId.setModule(module);
		fields.add(pmId);

		FacilioField type = new FacilioField();
		type.setName("type");
		type.setDataType(FieldType.NUMBER);
		type.setColumnName("REMINDER_TYPE");
		type.setModule(module);
		fields.add(type);

		FacilioField duration = new FacilioField();
		duration.setName("duration");
		duration.setDataType(FieldType.NUMBER);
		duration.setColumnName("DURATION");
		duration.setModule(module);
		fields.add(duration);

		FacilioField actionId = new FacilioField();
		actionId.setName("actionId");
		actionId.setDataType(FieldType.NUMBER);
		actionId.setColumnName("ACTION_ID");
		actionId.setModule(module);
		fields.add(actionId);

		return fields;
	}

	public static List<FacilioField> getViewColumnFields() {
		FacilioModule module = ModuleFactory.getViewColumnsModule();
		List<FacilioField> fields = new ArrayList<>();
		
		FacilioField id = new FacilioField();
		id.setName("id");
		id.setDataType(FieldType.NUMBER);
		id.setColumnName("ID");
		id.setModule(module);
		fields.add(id);
		
		FacilioField view = new FacilioField();
		view.setName("viewId");
		view.setDataType(FieldType.NUMBER);
		view.setColumnName("VIEWID");
		view.setModule(module);
		fields.add(view);

		FacilioField field = new FacilioField();
		field.setName("fieldId");
		field.setDataType(FieldType.NUMBER);
		field.setColumnName("FIELDID");
		field.setModule(module);
		fields.add(field);
		
		FacilioField displayName = new FacilioField();
		displayName.setName("columnDisplayName");
		displayName.setDataType(FieldType.STRING);
		displayName.setColumnName("DISPLAY_NAME");
		displayName.setModule(module);
		fields.add(displayName);
		
		FacilioField parentFieldId = new FacilioField();
		parentFieldId.setName("parentFieldId");
		parentFieldId.setDataType(FieldType.NUMBER);
		parentFieldId.setColumnName("PARENT_FIELDID");
		parentFieldId.setModule(module);
		fields.add(parentFieldId);

		return fields;
	}
	
	public static List<FacilioField> getAfterPMReminderWORelFields() {
		FacilioModule module = ModuleFactory.getAfterPMRemindersWORelModule();
		List<FacilioField> fields = new ArrayList<>();
		
		fields.add(getIdField(module));
		
		FacilioField pmJobId = new FacilioField();
		pmJobId.setName("pmReminderId");
		pmJobId.setDataType(FieldType.NUMBER);
		pmJobId.setColumnName("PM_REMINDER_ID");
		pmJobId.setModule(module);
		fields.add(pmJobId);
		
		FacilioField woId = new FacilioField();
		woId.setName("woId");
		woId.setDataType(FieldType.NUMBER);
		woId.setColumnName("WO_ID");
		woId.setModule(module);
		fields.add(woId);
		
		return fields;
	}
	
	public static List<FacilioField> getPMTriggerFields() {
		FacilioModule module = ModuleFactory.getPMTriggersModule();
		List<FacilioField> fields = new ArrayList<>();
		
		fields.add(getIdField(module));
		fields.add(getOrgIdField(module));
		fields.add(getField("pmId", "PM_ID", module, FieldType.NUMBER));
		fields.add(getField("scheduleJson", "SCHEDULE_INFO", module, FieldType.STRING));
		fields.add(getField("startTime", "START_TIME", module, FieldType.DATE_TIME));
		fields.add(getField("readingRuleId", "READING_RULE_ID", module, FieldType.NUMBER));
		
		return fields;
	}
	
	public static List<FacilioField> getPMJobFields() {
		FacilioModule module = ModuleFactory.getPMJobsModule();
		List<FacilioField> fields = new ArrayList<>();
		
		fields.add(getIdField(module));
		fields.add(getField("pmTriggerId", "PM_TRIGGER_ID", module, FieldType.NUMBER));
		fields.add(getField("nextExecutionTime", "NEXT_EXECUTION_TIME", module, FieldType.NUMBER));
		fields.add(getField("templateId", "TEMPLATE_ID",  module, FieldType.NUMBER));
		
		return fields;
	}
	
	public static List<FacilioField> getBeforePMRemindersTriggerRelFields() {
		FacilioModule module = ModuleFactory.getBeforePMRemindersTriggerRelModule();
		List<FacilioField> fields = new ArrayList<>();
		
		fields.add(getIdField(module));
		fields.add(getField("pmReminderId", "PM_REMINDER_ID", module, FieldType.NUMBER));
		fields.add(getField("pmTriggerId", "PM_TRIGGER_ID", module, FieldType.NUMBER));
		
		return fields;
	}
	
	public static List<FacilioField> getTaskInputOptionsFields() {
		FacilioModule module = ModuleFactory.getTaskInputOptionModule();
		List<FacilioField> fields = new ArrayList<>();
		
		fields.add(getIdField(module));
		fields.add(getField("taskId", "TASK_ID", module, FieldType.STRING));
		fields.add(getField("option", "OPTION_VALUE", module, FieldType.STRING));
		
		return fields;
	}
	
	public static List<FacilioField> getTaskSectionFields() {
		FacilioModule module = ModuleFactory.getTaskSectionModule();
		List<FacilioField> fields = new ArrayList<>();
		
		fields.add(getIdField(module));
		fields.add(getNameField(module));
		fields.add(getField("parentTicketId", "PARENT_TICKET_ID", module, FieldType.NUMBER));
		fields.add(getField("isEditable", "IS_EDITABLE", module, FieldType.NUMBER));
		fields.add(getField("sequenceNumber", "SEQUENCE_NUMBER", module, FieldType.NUMBER));
		
		return fields;
	}
	
	public static List<FacilioField> getAlarmEntityFields() {
		FacilioModule module = ModuleFactory.getAlarmEntityModule();
		List<FacilioField> fields = new ArrayList<>();
		
		fields.add(getOrgIdField(module));
		fields.add(getField("entityId", "ENTITY_ID", module, FieldType.NUMBER));
		
		return fields;
	}
	
	public static  List<FacilioField> getAssignmentTemplateFields() {
		FacilioModule module = ModuleFactory.getAssignmentTemplatesModule();
		List<FacilioField> fields = new ArrayList<>();
		fields.add(getIdField(module));
		fields.add(getField("assignedUserId", "USERID", module, FieldType.NUMBER));
		fields.add(getField("assignedGroupId", "GROUPID", module, FieldType.NUMBER));
		return fields;
	}
	
	public static  List<FacilioField> getSlaTemplateFields() {
		FacilioModule module = ModuleFactory.getSlaTemplatesModule();
		List<FacilioField> fields = new ArrayList<>();
		fields.add(getIdField(module));
		fields.add(getField("slaPolicyJsonStr", "SLAPOLICYJSON", module, FieldType.STRING));
		return fields;
    }
	
	public static List<FacilioField> getReportScheduleInfoFields() {
		FacilioModule module = ModuleFactory.getReportScheduleInfoModule();
		List<FacilioField> fields = new ArrayList<>();
		
		fields.add(getIdField(module));
		fields.add(getField("reportId", "REPORTID", module, FieldType.NUMBER));
		fields.add(getField("fileFormat", "FILE_FORMAT", module, FieldType.NUMBER));
		fields.add(getField("dateFilter", "DATE_FILTER", module, FieldType.STRING));
		fields.add(getField("templateId", "TEMPLATEID", module, FieldType.NUMBER));
		
		return fields;
	}
	
	public static List<FacilioField> getBaseLineFields() {
		FacilioModule module = ModuleFactory.getBaseLineModule();
		List<FacilioField> fields = new ArrayList<>();
		
		fields.add(getIdField(module));
		fields.add(getOrgIdField(module));
		fields.add(getNameField(module));
		fields.add(getField("spaceId", "SPACE_ID", module, FieldType.NUMBER));
		fields.add(getField("rangeType", "RANGE_TYPE", module, FieldType.NUMBER));
		fields.add(getField("startTime", "START_TIME", module, FieldType.DATE));
		fields.add(getField("endTime", "END_TIME", module, FieldType.DATE));
		
		return fields;
	}
	
	public static List<FacilioField> getBaseLineReportsRelFields() {
		FacilioModule module = ModuleFactory.getBaseLineReportRelModule();
		List<FacilioField> fields = new ArrayList<>();
		
		fields.add(getField("id", "BASE_LINE_ID", module, FieldType.NUMBER));
		fields.add(getField("reportId", "REPORT_ID", module, FieldType.NUMBER));
		fields.add(getField("isAdjust", "IS_ADJUST", module, FieldType.BOOLEAN));
		
		return fields;
	}

	public static FacilioField getField(String name, String colName, FieldType type) {
		return getField(name, colName, null, type);
	}
	
	public static FacilioField getField(String name, String colName, FacilioModule module, FieldType type) {
		FacilioField columnFld = null;
		switch (type) {
			case NUMBER:
			case DECIMAL:
				columnFld = new NumberField();
			case LOOKUP:
				columnFld = new LookupField();
			default:
				columnFld = new FacilioField();
		}
		columnFld.setName(name);
		columnFld.setColumnName(colName);
		columnFld.setDataType(type);
		if(module != null) {
			columnFld.setModule(module);
		}
		return columnFld;
	}
	
	public static Map<String, FacilioField> getAsMap(List<FacilioField> fields) {
		return fields.stream()
				.collect(
						Collectors.toMap(FacilioField::getName, 
										Function.identity(), 
										(prevValue, curValue) -> {
											return prevValue;
										}
									)
						);
	}
	
	public static List<FacilioField> getCalendarColorFields() {
		FacilioModule module = ModuleFactory.getCalendarColorModule();
		List<FacilioField> fields = new ArrayList<>();
		
		fields.add(getIdField(module));
		fields.add(getOrgIdField(module));
		fields.add(getField("basedOn", "BASED_ON", module, FieldType.STRING));
		
		return fields;
	}
	
	public static List<FacilioField> getLastReadingFields() {
		FacilioModule module = ModuleFactory.getLastReadingModule();
		List<FacilioField> fields = new ArrayList<>();
		fields.add(getOrgIdField(module));
		fields.add(getField("resourceId", "RESOURCE_ID", module, FieldType.NUMBER));
		fields.add(getField("fieldId", "FIELD_ID", module, FieldType.NUMBER));
		fields.add(getField("ttime", "TTIME", module, FieldType.NUMBER));
		fields.add(getField("value", "VALUE", module, FieldType.DECIMAL));
		return fields;
	}
}
