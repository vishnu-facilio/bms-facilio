package com.facilio.bmsconsole.actions;

import org.apache.log4j.Logger;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.MLServiceContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class MLServiceAction extends FacilioAction {

	private static final Logger LOGGER = Logger.getLogger(MLServiceAction.class.getName());

	private static final long serialVersionUID = 1L;

	private MLServiceContext modelInfo;

	public String addMLModel() throws Exception {
		LOGGER.info("MLServiceAction has been initiated..");
		
		boolean is_default = this.modelInfo.getServiceType().equals("default");

		FacilioChain chain = FacilioChainFactory.addMLServiceChain(is_default);
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.ML_SERVICE_DATA, this.modelInfo);
		chain.execute();

		setResult(FacilioConstants.ContextNames.MESSAGE, modelInfo.getStatus());
		setResult("usecaseId", modelInfo.getUseCaseId());
		LOGGER.info("MLServiceAction has been executed for usecaseID = "+modelInfo.getUseCaseId());
		LOGGER.info("MLServiceAction has been executed with status :: "+modelInfo.getStatus());

		return SUCCESS;
		
	}

	public static void  initMLServiceFromAdmin(MLServiceContext mlservicecontext) throws Exception {
		MLServiceAction actionObj = new MLServiceAction();
		actionObj.modelInfo =  mlservicecontext;
		actionObj.addMLModel();
	}

	public MLServiceContext getModelInfo() {
		return modelInfo;
	}

	public void setModelInfo(MLServiceContext modelInfo) {
		this.modelInfo = modelInfo;
	}
}
