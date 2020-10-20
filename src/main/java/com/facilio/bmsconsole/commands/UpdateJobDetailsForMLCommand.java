package com.facilio.bmsconsole.commands;

import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.context.MLResponseContext;
import com.facilio.bmsconsole.context.MLServiceContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflowv2.util.UserFunctionAPI;

public class UpdateJobDetailsForMLCommand extends FacilioCommand {

	private static final Logger LOGGER = Logger.getLogger(UpdateJobDetailsForMLCommand.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {

		MLServiceContext mlServiceContext = (MLServiceContext) context.get(FacilioConstants.ContextNames.ML_MODEL_INFO);
		try {

			LOGGER.info("Start of UpdateJobDetailsForMLCommand for usecase id "+mlServiceContext.getUseCaseId());
			//TODO : update ml api result in ml_models table
			MLResponseContext mlResponse = mlServiceContext.getMlResponse();
			if (mlResponse!=null && mlResponse.getStatus()) {
				// initiate the ml jobs
				FacilioChain chain = FacilioChainFactory.activateMLServiceChain();
				FacilioContext chainContext = chain.getContext();
				chainContext.put(FacilioConstants.ContextNames.ML_MODEL_INFO, mlServiceContext);
				chain.execute();
			}

		} catch (Exception e) {
			LOGGER.error("Error while updating ml response in table");
			throw e;
		}
		LOGGER.info("End of UpdateJobDetailsForMLCommand");
		return false;

	}

	private Object getWorkFlowId(Map<String, Object> workflowInfo) {
		String namespace = (String) workflowInfo.get("namespace");
		String function = (String) workflowInfo.get("function");
		try {
			WorkflowContext workflowContext = UserFunctionAPI.getWorkflowFunction(namespace, function);
			return workflowContext.getId();
		} catch (Exception e) {
			LOGGER.error("Error while getting flow id for given namespace <" + namespace + "> and function <" + function
					+ ">");
			e.printStackTrace();
		}
		return null;
	}

}
