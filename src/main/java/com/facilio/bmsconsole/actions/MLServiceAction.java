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
    
    private static final long startTime = 1590980400000L;
    private static final long endTime = 1590987600000L;
    private static final long usecase_id = 21876312873L;

    public String addMLModel() throws  Exception {
    	
    	LOGGER.info("MLModelAction api hit --> " + this.modelInfo);
    	FacilioChain chain = FacilioChainFactory.addMLServiceChain();
        FacilioContext context = chain.getContext();
        
        context.put(FacilioConstants.ContextNames.START_TTIME, startTime);
        context.put(FacilioConstants.ContextNames.END_TTIME, endTime);
        
        modelInfo.setUseCaseId(usecase_id);
        
        context.put(FacilioConstants.ContextNames.ML_MODEL_INFO, this.modelInfo);
        
        chain.execute();
    	setResult(FacilioConstants.ContextNames.RESULT, modelInfo.getApiResponse().get("message"));
        return SUCCESS;
    }

	public MLServiceContext getModelInfo() {
		return modelInfo;
	}

	public void setModelInfo(MLServiceContext modelInfo) {
		this.modelInfo = modelInfo;
	}
}
