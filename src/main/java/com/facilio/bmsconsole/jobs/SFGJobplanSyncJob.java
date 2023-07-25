package com.facilio.bmsconsole.jobs;

import com.facilio.bmsconsoleV3.commands.ReadOnlyChainFactoryV3;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.SFG20JobPlan.SFG20SyncHistoryContext;
import com.facilio.bmsconsoleV3.util.V3VisitorManagementAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;

import static com.facilio.bmsconsoleV3.util.SFG20JobPlanAPI.getSFGSyncHistoryForId;

public class SFGJobplanSyncJob extends FacilioJob {

    @Override
    public void execute(JobContext jc) throws Exception {
        Long syncHistoryId = jc.getJobId();
        SFG20SyncHistoryContext syncHistoryContext = getSFGSyncHistoryForId(syncHistoryId);
        FacilioChain chain = TransactionChainFactoryV3.getSFG20ConnectCommand();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.SFG20.SYNC_HISTORY,syncHistoryContext);
        chain.execute();

    }

}
