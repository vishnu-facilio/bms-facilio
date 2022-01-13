package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.context.MLResponseContext;
import com.facilio.bmsconsole.context.MLServiceContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;

public class UpdateJobDetailsForMLCommand extends FacilioCommand {

	private static final Logger LOGGER = Logger.getLogger(UpdateJobDetailsForMLCommand.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {

		MLServiceContext mlServiceContext = (MLServiceContext) context.get(FacilioConstants.ContextNames.ML_MODEL_INFO);
		try {

			LOGGER.info("Start of UpdateJobDetailsForMLCommand for usecase id "+mlServiceContext.getUseCaseId());
			//TODO : update ml api result in ml_models table
			List<MLResponseContext> mlResponseList = mlServiceContext.getMlResponseList();
			//MLResponseContext mlResponse = mlServiceContext.getMlResponse();
			boolean activateMLJob = true; 
			if (mlResponseList==null || mlResponseList.size() != mlServiceContext.getModels().size()) {
				activateMLJob = false;
			}
			for(MLResponseContext mlResponse : mlResponseList) {
				if(!mlResponse.getStatus()) {
					activateMLJob = false;
					break;
				} 
			}
			if (activateMLJob) {
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

}
