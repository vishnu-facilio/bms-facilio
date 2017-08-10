package com.facilio.bmsconsole.modules;

import java.util.ArrayList;
import java.util.List;

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
}	
