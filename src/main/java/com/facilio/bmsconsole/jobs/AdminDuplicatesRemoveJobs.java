/**
 * 
 */
package com.facilio.bmsconsole.jobs;

import java.util.Map;

import com.facilio.services.email.EmailFactory;
import com.facilio.services.factory.FacilioFactory;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.util.ReadingToolsAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

/**
 * @author facilio
 *
 */
public class AdminDuplicatesRemoveJobs extends FacilioJob{

	private static final Logger LOGGER = LogManager.getLogger(AdminDuplicatesRemoveJobs	.class.getName());

	@Override
	public void execute(JobContext jc) throws Exception {
		// TODO Auto-generated method stub

		try {
			long jobId = jc.getJobId();
			long jobStartTime = System.currentTimeMillis();
			Map<String, Object> prop = ReadingToolsAPI.getDeltaCalculationResourceJob(jc.getJobId());
			long orgId = (long)prop.get("orgId");
			long fieldId = (long) prop.get("fieldId");
			long assetId = (long) prop.get("assetId");
			long startTtime = (long)prop.get("startTtime");
			long endTtime = (long)prop.get("endTtime");
			String email = (String) prop.get("email");
			long fieldOptionType = (long)prop.get("fieldOptionType");
			
				FacilioContext context=new FacilioContext();
				context.put(FacilioConstants.ContextNames.ADMIN_DELTA_JOBID,jobId );
				context.put(ContextNames.ADMIN_DELTA_ORG, orgId);
				context.put(ContextNames.FIELD_ID,fieldId);
				context.put(ContextNames.ASSET_ID,assetId);
				context.put(ContextNames.START_TTIME,startTtime);
				context.put(ContextNames.END_TTIME,endTtime);
				context.put(ContextNames.ADMIN_USER_EMAIL, email);
				
				FacilioChain deltaCalculationChain = TransactionChainFactory.removeDuplicatesChain();
				deltaCalculationChain.execute(context);
				String msg = "Time taken for Duplicate Data Remove of JobId : "+jobId+" for asset : "+assetId+" between "+startTtime+" and "+endTtime+" is "+(System.currentTimeMillis() - jobStartTime);
				LOGGER.info(msg);
					JSONObject json = new JSONObject();
					json.put("to", email);
					json.put("sender", EmailFactory.getEmailClient().getNoReplyFromEmail());
					json.put("subject", "Duplicate Data Remove : "+jobId);
					json.put("message", msg);
					
					FacilioFactory.getEmailClient().sendEmail(json);
			}		
			catch(Exception e) {
				LOGGER.info("Error occurred during Duplicate Data Remove for job id : "+jc.getJobId(), e);
				CommonCommandUtil.emailException(AdminDeltaCalculationJobs.class.getName(), "Error occurred during Duplicate Data Remove for job id : "+jc.getJobId(), e);
			}

	}
}


