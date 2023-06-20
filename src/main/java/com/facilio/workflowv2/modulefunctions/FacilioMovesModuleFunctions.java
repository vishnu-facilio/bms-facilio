package com.facilio.workflowv2.modulefunctions;

import java.util.List;
import java.util.Map;

import com.facilio.scriptengine.context.ScriptContext;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.scriptengine.annotation.ScriptModule;
import com.facilio.v3.util.V3Util;

@ScriptModule(moduleName = FacilioConstants.ContextNames.MOVES)
public class FacilioMovesModuleFunctions extends FacilioModuleFunctionImpl {
	
	@Override
	public void add(Map<String, Object> globalParams, List<Object> objects, ScriptContext scriptContext) throws Exception {
		Map<String, Object> moveObj = (Map<String, Object>) objects.get(1);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.MOVES);
		V3Util.createRecord(module, (JSONObject) moveObj);
		scriptContext.incrementTotalInsertCount();
	}

}
