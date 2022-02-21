package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.MLServiceUtil;
import com.facilio.bmsconsoleV3.context.V3MLServiceContext;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class MLServiceBeforeCreateValidationCommand extends FacilioCommand implements Serializable {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		if(context.get(MLServiceUtil.ML_CONTEXT) != null) {
			return false;
		}

		String moduleName = Constants.getModuleName(context);
		Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
		List<V3MLServiceContext> mlServiceContexts = recordMap.get(moduleName);
		if(CollectionUtils.isEmpty(mlServiceContexts)) {
			return true;
		}

		V3MLServiceContext mlServiceContext = mlServiceContexts.get(0);
		if (mlServiceContext.getModelName() == null) {
			throw new RESTException(ErrorCode.VALIDATION_ERROR, "Model name can't be empty");
		}
		if (mlServiceContext.getProjectName() == null) {
			throw new RESTException(ErrorCode.VALIDATION_ERROR, "Project name can't be empty");
		}
		if (mlServiceContext.getParentAssetId() == null) {
			throw new RESTException(ErrorCode.VALIDATION_ERROR, "Parent Asset ID can't be empty");
		}
		if (mlServiceContext.getWorkflowInfo() != null) {
			JSONObject workflowInfo = mlServiceContext.getWorkflowInfo();
			if (!workflowInfo.containsKey("namespace")) {
				throw new RESTException(ErrorCode.VALIDATION_ERROR, "workflow should have namespace");
			}
			if (!workflowInfo.containsKey("function")) {
				throw new RESTException(ErrorCode.VALIDATION_ERROR, "workflow should have function");
			}
			MLServiceUtil.extractAndUpdateWorkflowID(mlServiceContext);
		}
		if (mlServiceContext.getStartTime() == null) {
			throw new RESTException(ErrorCode.VALIDATION_ERROR, "StartTime can't be empty");
		}
		if (mlServiceContext.getEndTime() == null) {
			throw new RESTException(ErrorCode.VALIDATION_ERROR, "EndTime can't be empty");
		}
		MLServiceUtil.validateDateRange(mlServiceContext);
		MLServiceUtil.updateMLModelMeta(mlServiceContext);
		mlServiceContext.setStatus("MLService initiated..");
		context.put(MLServiceUtil.MLSERVICE_CONTEXT, mlServiceContext);
		return false;
	}

}