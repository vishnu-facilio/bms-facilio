package com.facilio.agentv2.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agentv2.AgentApiV2;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.aws.util.AwsUtil;
import com.facilio.tasker.ScheduleInfo;
import com.facilio.tasker.job.JobContext;
import com.facilio.tasker.job.JobStore;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;

import java.time.LocalTime;

public class CreateAgentCommand extends AgentV2Command {

    private static final org.apache.log4j.Logger LOGGER = LogManager.getLogger(CreateAgentCommand.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {
        if (containsCheck(AgentConstants.AGENT, context)) {
            String orgDomainname = AccountUtil.getCurrentOrg().getDomain();
            String agentDownloadUrl = "agentDownloadUrl";
            FacilioAgent agent = (FacilioAgent) context.get(AgentConstants.AGENT);
            String certFileDownloadUrl = "certDownloadUrl";
            long agentId = AgentApiV2.addAgent(agent);
            agent.setId(agentId);
            String type = agent.getType();
            if (type.equalsIgnoreCase("facilio")
                    || type.equalsIgnoreCase("niagara")) {
                return createPolicy(context, orgDomainname, agent, certFileDownloadUrl);
            } else if (type.equalsIgnoreCase("rest")) {
                return createJob(agent);
            } else {
                return false;
            }
        } else {
            throw new Exception(" agent missing from context " + context);
        }
    }

    private boolean createJob(FacilioAgent agent) throws Exception {

        JobContext cloudAgentJob = getJobContext(agent);
        JobStore.addJob(cloudAgentJob);
        return false;
    }

    private boolean createPolicy(Context context, String orgDomainname, FacilioAgent agent, String certFileDownloadUrl) throws Exception {
        String agentDownloadUrl;
        AwsUtil.addClientToPolicy(agent.getName(), orgDomainname, "facilio");
        if (certFileDownloadUrl != null) {
            context.put(AgentConstants.CERT_FILE_DOWNLOAD_URL, certFileDownloadUrl);
            // pack agent and give a download link
            agentDownloadUrl = packAgent(agent);
            if ((agentDownloadUrl != null) && (agent.getType().equals("facilio")) && (agent.getType().equals("niagara"))) {
                switch (agent.getType()) {
                    case "facilio":
                    case "niagara":
                        context.put(AgentConstants.DOWNLOAD_AGENT, agentDownloadUrl);
                        break;
                    default:
                }
            } else {
                LOGGER.info(" agentDownload link cant be null ");
            }

            return false;
        } else {
            throw new Exception(" certFile download url can't be null ");
        }
    }

    private String packAgent(FacilioAgent agent) {
        return "no implementation yet"; //TODO implement agent packing.
    }

    private JobContext getJobContext(FacilioAgent agent) {
        long interval = agent.getInterval();
        JobContext cloudAgentJobContext = new JobContext();
        ScheduleInfo scheduleInfo = new ScheduleInfo();
        scheduleInfo.setFrequencyType(ScheduleInfo.FrequencyType.DAILY);
        long totalMinutesInADay = 60 * 24;
        LocalTime time = LocalTime.of(0, 0);
        for (long frequency = totalMinutesInADay / interval; frequency > 0; frequency--) {
            time = time.plusMinutes(interval);
            scheduleInfo.addTime(time);
        }
        cloudAgentJobContext.setSchedule(scheduleInfo);
        cloudAgentJobContext.setJobId(agent.getId());
        cloudAgentJobContext.setIsPeriodic(true);
        cloudAgentJobContext.setJobName("CloudAgent");
        cloudAgentJobContext.setActive(true);
        cloudAgentJobContext.setExecutorName("facilio");
        cloudAgentJobContext.setTransactionTimeout(300000);
        cloudAgentJobContext.setExecutionTime(System.currentTimeMillis());
        cloudAgentJobContext.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
        LOGGER.info("Adding Cloud Agent : " + agent.getName() + " : " + cloudAgentJobContext.toString());
        return cloudAgentJobContext;
    }
}
