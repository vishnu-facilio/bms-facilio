package com.facilio.bmsconsole.actions;

import org.apache.log4j.Logger;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.MLServiceContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.time.DateTimeUtil;

public class MLServiceAction extends FacilioAction {
	
	private static final Logger LOGGER = Logger.getLogger(MLServiceAction.class.getName());
	
    private static final long serialVersionUID = 1L;

    private MLServiceContext modelInfo;
    
    // for development purpose
    private static long startTime = 1590980400000L;
    private static long endTime = 1590987600000L;
//    private static final long usecase_id = 21876312873L;
    
    private static final long DAYS_IN_MILLISECONDS = 24*60*60*1000l;

    public String addMLModel() throws Exception {
    	LOGGER.info("MLServiceAction has been initiated..");
    	
    	endTime = DateTimeUtil.getCurrenTime();
    	startTime = endTime - (90 * DAYS_IN_MILLISECONDS);
    	
    	LOGGER.info("MLServiceAction api hit modelInfo --> " + this.modelInfo );
    	LOGGER.info("MLServiceAction api hit startTime = " + startTime+", endTime = "+endTime);
    	
    	FacilioChain chain = FacilioChainFactory.addMLServiceChain();
        FacilioContext context = chain.getContext();
        
        context.put(FacilioConstants.ContextNames.START_TTIME, startTime);
        context.put(FacilioConstants.ContextNames.END_TTIME, endTime);
        context.put(FacilioConstants.ContextNames.ML_MODEL_INFO, this.modelInfo);
        chain.execute();

        setResult(FacilioConstants.ContextNames.RESULT, modelInfo.getStatus());
        LOGGER.info("MLServiceAction has been executed for usecaseID = "+modelInfo.getUseCaseId());
        
        return SUCCESS;
    }

	public MLServiceContext getModelInfo() {
		return modelInfo;
	}

	public void setModelInfo(MLServiceContext modelInfo) {
		this.modelInfo = modelInfo;
	}
}
