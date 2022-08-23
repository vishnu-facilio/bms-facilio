package com.facilio.tasker;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.taskengine.JobScheduler;
import com.facilio.util.FacilioUtil;

import javax.xml.bind.JAXBException;
import java.io.IOException;

public class FacilioScheduler {

    public static void initScheduler() throws IOException, InterruptedException, JAXBException {
        JobScheduler.initScheduler(new InstantJobConfigImpl());
    }


    static class InstantJobConfigImpl extends FacilioAbstractJobConfig {

        @Override
        public String getJobFilePath() {
            String schJobFile = FacilioProperties.getConfig("schedulejobfile");
            schJobFile = schJobFile == null ? "schedulerJobs" : schJobFile;
            return FacilioUtil.normalizePath("conf/") + schJobFile + ".xml";
        }

        @Override
        public String getExecFilePath() {
            String schJobFile = FacilioProperties.getConfig("scheduleexecutorsfile");
            schJobFile = schJobFile == null ? "executors" : schJobFile;
            return FacilioUtil.normalizePath("conf/") + schJobFile + ".xml";
        }

        @Override
        public boolean isEnabledService() {
            return FacilioProperties.isScheduleServer();
        }

    }

    public static void stopSchedulers() {
        JobScheduler.stopSchedulers();
    }

}
