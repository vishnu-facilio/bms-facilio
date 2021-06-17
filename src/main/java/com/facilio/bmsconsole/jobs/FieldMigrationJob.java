package com.facilio.bmsconsole.jobs;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.transaction.FacilioTransactionManager;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.List;

public class FieldMigrationJob extends FacilioJob {
    private static final Logger LOGGER = LogManager.getLogger(FieldMigrationJob.class.getName());

    @Override
    public void execute(JobContext jc) throws Exception {
        try {
            JSONObject props= BmsJobUtil.getJobProps(jc.getJobId(), jc.getJobName());
            FacilioContext context = new FacilioContext();

            List<Long> resourceId = (List<Long>) props.get(FacilioConstants.ContextNames.RESOURCE_LIST);
            FacilioChain chain = TransactionChainFactory.migrateFieldDataChain();
            context.put(FacilioConstants.ContextNames.SOURCE_ID, (long) props.get(FacilioConstants.ContextNames.SOURCE_ID));
            context.put(FacilioConstants.ContextNames.TARGET_ID, (long) props.get(FacilioConstants.ContextNames.TARGET_ID));
            context.put(FacilioConstants.ContextNames.RESOURCE_LIST, resourceId);
            context.put(FacilioConstants.ContextNames.FIELD_MIGRATION_JOB_ID, jc.getJobId());
            chain.execute(context);
        }
        catch(Exception e) {
            try {
                FacilioTransactionManager.INSTANCE.getTransactionManager().setRollbackOnly();
                LOGGER.error("Error Occured in Field migration Job -- " + e.toString());
            }
            catch(Exception transactionException) {
                LOGGER.error(transactionException.toString());
            }
        }
    }
    @Override
    public void handleTimeOut() {
        // TODO Auto-generated method stub
        LOGGER.info("Time out called during HistoricalRuleCalculation Job");
        super.handleTimeOut();
    }
}
