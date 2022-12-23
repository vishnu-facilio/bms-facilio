package com.facilio.workflows.functions;

import com.facilio.beans.ModuleBean;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.EnumField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.MultiEnumField;
import com.facilio.scriptengine.annotation.ScriptNameSpace;
import com.facilio.scriptengine.exceptions.FunctionParamException;
import com.facilio.scriptengine.systemfunctions.FacilioNameSpaceConstants;
import com.facilio.util.FacilioUtil;
import com.facilio.workflowv2.util.WorkflowV2Util;

import java.util.List;
import java.util.Map;

@ScriptNameSpace(nameSpace = FacilioNameSpaceConstants.MODULE_FUNCTION)
public class FacilioModuleFunctions {
	public Object getModule(Map<String, Object> globalParam, Object... objects) throws Exception {

		checkParam(objects);

		if(objects[0] == null) {
			return null;
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

		String moduleRefObject = objects[0].toString();

		if(FacilioUtil.isNumeric(moduleRefObject)) {
			long moduleId = Long.parseLong(objects[0].toString());
			return modBean.getModule(moduleId);
		}
		else {
			return modBean.getModule(moduleRefObject);
		}
	}

	public Object getField(Map<String, Object> globalParam, Object... objects) throws Exception {

		checkParam(objects);

		if(objects[0] == null) {
			return null;
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

		String fieldRefObject = objects[0].toString();
		String moduleRefObject = null;

		if(objects.length > 1 && objects[1] != null) {
			moduleRefObject = objects[1].toString();
		}

		if(FacilioUtil.isNumeric(fieldRefObject)) {
			long fieldId =(long) Double.parseDouble(fieldRefObject);
			return modBean.getField(fieldId);
		}
		else if(moduleRefObject != null) {
			if(FacilioUtil.isNumeric(moduleRefObject)) {
				Long moduleId = Long.parseLong(moduleRefObject);
				return null;
			}
			else {
				return modBean.getField(fieldRefObject, moduleRefObject);
			}
		}


		return null;
	}

	public Object getEnumFieldValue(Map<String, Object> globalParam, Object... objects) throws Exception {

		checkParam(objects);

		if(objects[0] == null) {
			return null;
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

		String fieldName = objects[0].toString();
		String moduleName = objects[1].toString();

		FacilioField field = modBean.getField(fieldName, moduleName);

		if (field instanceof EnumField) {
			EnumField enumField = (EnumField) field;
			int index = Integer.parseInt(objects[2].toString());
			return enumField.getValue(index);
		}
		else if (field instanceof MultiEnumField) {
			MultiEnumField enumField = (MultiEnumField) field;
			List<Integer> intList = WorkflowV2Util.anyListToIntList.apply((List<?>) objects[2]);
			return enumField.getValue(intList);
		}

		return null;
	};

	public void checkParam(Object... objects) throws Exception {
		if(objects.length <= 0) {
			throw new FunctionParamException("Required Object is null");
		}
	}

	// checkParam of getEnumFieldValue
	/*public void checkParam(Object... objects) throws Exception {
		if(objects.length <= 2) {
			throw new FunctionParamException("Required Object is null -- "+objects);
		}
	}*/
}
