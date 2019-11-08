package com.facilio.bmsconsole.jobs;

import com.facilio.dataFetchers.DataFetcher;
import com.facilio.dataFetchers.Wateruos;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class DataFetcherJob extends FacilioJob {
    private static final Logger LOGGER = LogManager.getLogger(DataFetcherJob.class.getName());

    @Override
    public void execute(JobContext jc) throws Exception {

            LOGGER.info("Calling wateruos");
            DataFetcher wateruos = new Wateruos();
            wateruos.process();

    }
}
