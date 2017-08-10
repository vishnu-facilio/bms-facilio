package com.facilio.bmsconsole.modules;

import java.util.ArrayList;
import java.util.List;

public class FieldFactory {
	public static FacilioField getOrgIdField() {
		FacilioField field = new FacilioField();
		field.setName("orgId");
		field.setDataType(FieldType.NUMBER);
		field.setColumnName("ORGID");
		
		return field;
	}
	
	public static FacilioField getModuleIdField() {
		FacilioField field = new FacilioField();
		field.setName("moduleId");
		field.setDataType(FieldType.NUMBER);
		field.setColumnName("MODULEID");
		
		return field;
	}
	
}	
