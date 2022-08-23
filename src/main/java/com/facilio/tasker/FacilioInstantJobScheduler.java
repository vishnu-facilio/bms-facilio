package com.facilio.tasker;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.chain.FacilioContext;
import com.facilio.taskengine.InstantJobScheduler;
import com.facilio.util.FacilioUtil;


public class FacilioInstantJobScheduler {
    private static final String INSTANT_JOB_FILE = FacilioUtil.normalizePath("conf/instantJobs.xml");
    private static final String INSTANT_JOB_EXECUTOR_FILE = FacilioUtil.normalizePath("conf/instantjobexecutors.yml");

    public static void init() throws Exception {
        InstantJobScheduler.initialize(new InstantJobConfigImpl());
    }

    static class InstantJobConfigImpl extends FacilioAbstractJobConfig {

        @Override
        public String getJobFilePath() {
            return INSTANT_JOB_FILE;
        }

        @Override
        public String getExecFilePath() {
            return INSTANT_JOB_EXECUTOR_FILE;
        }

        @Override
        public boolean isEnabledService() {
            return FacilioProperties.isInstantJobServer();
        }
    }

    public static void stopExecutors() {
        InstantJobScheduler.stopExecutors();
    }

    public static void deleteExecutorsInstantJobQueueTable() throws Exception {
        InstantJobScheduler.deleteExecutorsInstantJobQueueTable();
    }

    public static void addInstantJob(String executorName, String jobName, FacilioContext context) throws Exception {
        InstantJobScheduler.addInstantJob(executorName, jobName, context);
    }

}

