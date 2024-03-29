/**
 *
 */
package com.facilio.bmsconsole.jobs;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.transaction.FacilioTransactionManager;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * @author facilio
 *
 */
public class DemoRollUpJob extends FacilioJob {

    private static final Logger LOGGER = LogManager.getLogger(DemoRollUpJob.class.getName());

    public void execute ( JobContext jc ) throws Exception {
        // TODO Auto-generated method stub

        long orgId = AccountUtil.getCurrentOrg().getId();
        long timeDuration = 1;
        try {
            FacilioChain demoRollupChain = TransactionChainFactory.demoRollUpChain();
            FacilioContext context = demoRollupChain.getContext();
            context.put(ContextNames.DEMO_ROLLUP_EXECUTION_TIME,timeDuration);
            context.put(ContextNames.DEMO_ROLLUP_JOB_ORG,orgId);
            demoRollupChain.execute();
        } catch (Exception e) {
            CommonCommandUtil.emailException("DemoRollUp","DemoRollUp Failed - orgid -- " + AccountUtil.getCurrentOrg().getId(),e);
            FacilioTransactionManager.INSTANCE.getTransactionManager().setRollbackOnly();
        }

    }


}
