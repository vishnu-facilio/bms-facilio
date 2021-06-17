package com.facilio.bmsconsole.jobs;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.actions.DemoRollUpActionAPI;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.transaction.FacilioTransactionManager;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;

public class DemoRollUpOneTimeJob extends FacilioJob{

	private static final Logger LOGGER = LogManager.getLogger(DemoRollUpOneTimeJob.class.getName());
	@Override
	public void execute(JobContext jc) throws Exception {
		// TODO Auto-generated method stub
		
		try {
			long id = jc.getJobId();
			List<Map<String,Object>> props = DemoRollUpActionAPI.getData(id);
			if(CollectionUtils.isNotEmpty(props)) {
				for(Map<String,Object> itr:props) {
					long orgId = (long) itr.get("orgId");
					String currentTime = (String) itr.get("startTime");
					ZonedDateTime currentTimeZdt = ZonedDateTime.parse(currentTime);
					LOGGER.info("DemorollupYearlyOneTimeJob started in facilioJob");
					FacilioChain context = TransactionChainFactory.demoRollUpYearlyChain();
					context.getContext().put(ContextNames.START_TIME, currentTimeZdt);
					context.getContext().put(ContextNames.DEMO_ROLLUP_JOB_ORG, orgId);
					context.execute();
				}
			}else {
				throw new IllegalArgumentException("Exception occurred in DemoAdmin Job input is null");
			}
		}catch(Exception e) {
			CommonCommandUtil.emailException("DemoRollUpYearlyOneTime", "DemoRollUpYearlyOneTime Failed - orgid -- " + jc.getOrgId(), e);
			FacilioTransactionManager.INSTANCE.getTransactionManager().setRollbackOnly();
		}
	}

}
