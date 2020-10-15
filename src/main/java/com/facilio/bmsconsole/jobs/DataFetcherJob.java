package com.facilio.bmsconsole.jobs;

import com.facilio.agent.integration.wattsense.WattsenseClient;
import com.facilio.agentv2.AgentApiV2;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.dataFetchers.DataFetcher;
import com.facilio.dataFetchers.Wateruos;
import com.facilio.dataFetchers.WattsenseDataFetcher;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class DataFetcherJob extends FacilioJob {
    private static final Logger LOGGER = LogManager.getLogger(DataFetcherJob.class.getName());

    @Override
    public void execute(JobContext jc) throws Exception {

        try {
            LOGGER.info("Calling Wattsense data fetcher");
        /*DataFetcher wateruos = new Wateruos();
        wateruos.process();*/
            FacilioAgent agent = AgentApiV2.getAgent(33L);
            WattsenseClient client = new WattsenseClient(agent);
            client.setApiKey("XdwqbEv0aOgrVXBQZPD7Lxp5A89GP68wR064gW3eq4lz6JkKMwdYRmbyo2N1JR4Y");
            client.setSecretKey("YPreE3g20A8wX9dBVNYpL1QzvMW5QzM18QKZtoWleDbR6OKJa4yoxm7qr5PZkmRM");
            DataFetcher wattsenseDataFetcher = new WattsenseDataFetcher(client);
            LOGGER.info(agent);
            LOGGER.info(agent.getName());
            wattsenseDataFetcher.setAgent(agent);
            wattsenseDataFetcher.process();
        } catch (Exception e) {
            LOGGER.info("Error while fetching data", e);
        }

    }
}
