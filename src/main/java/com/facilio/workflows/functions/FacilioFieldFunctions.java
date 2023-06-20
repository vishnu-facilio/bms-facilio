package com.facilio.workflows.functions;

import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.scriptengine.annotation.ScriptNameSpace;
import com.facilio.scriptengine.context.ScriptContext;
import com.facilio.scriptengine.exceptions.FunctionParamException;
import com.facilio.scriptengine.systemfunctions.FacilioNameSpaceConstants;

import java.util.Map;

@ScriptNameSpace(nameSpace = FacilioNameSpaceConstants.FIELD_FUNCTION)
public class FacilioFieldFunctions {
	public Object id(ScriptContext scriptContext,  Map<String, Object> globalParam, Object... objects) throws Exception {

		checkParam(objects);

		if(objects[0] == null) {
			return null;
		}
		FacilioField field = (FacilioField)objects[0];

		return field.getId();
	}

	public Object asMap(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {

		checkParam(objects);

		if(objects[0] == null) {
			return null;
		}
		FacilioField field = (FacilioField)objects[0];

		return FieldUtil.getAsProperties(field);
	}

	public void checkParam(Object... objects) throws Exception {
		if(objects.length <= 0) {
			throw new FunctionParamException("Required Object is null");
		}
	}
}