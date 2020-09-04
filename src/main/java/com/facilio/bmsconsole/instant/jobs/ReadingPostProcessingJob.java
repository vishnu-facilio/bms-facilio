package com.facilio.bmsconsole.instant.jobs;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.tasker.job.InstantJob;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class ReadingPostProcessingJob extends InstantJob {
    private static final Logger LOGGER = LogManager.getLogger(ReadingPostProcessingJob.class.getName());
    @Override
    public void execute(FacilioContext context) throws Exception {
        try {
            LOGGER.info("Post processing of reading via instant job for org : "+ AccountUtil.getCurrentOrg().getOrgId());
            FacilioChain postProcessingChain = ReadOnlyChainFactory.readingPostProcessingChain();
            postProcessingChain.setContext(context);
            postProcessingChain.execute();
        }
        catch (Exception e) {
            LOGGER.error("Error occurred in Reading post processing instant job", e);
            throw e;
        }
    }
}
