package com.facilio.bundle.context;

import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bundle.interfaces.BundleComponentInterface;
import com.facilio.bundle.utils.BundleConstants;
import com.facilio.chain.FacilioContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.FacilioModule.ModuleType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.workflows.context.WorkflowUserFunctionContext;
import com.facilio.workflowv2.util.WorkflowV2API;

public class FunctionBundleComponent implements BundleComponentInterface {

	@Override
	public JSONObject getFormatedObject(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONArray getAllFormatedObject(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		List<WorkflowUserFunctionContext> functions = WorkflowV2API.getAllFunctions();
		
		return FieldUtil.getAsJSONArray(functions, WorkflowUserFunctionContext.class);
	}

	@Override
	public void install(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
