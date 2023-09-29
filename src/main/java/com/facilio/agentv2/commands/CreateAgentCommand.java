package com.facilio.agentv2.commands;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.AgentType;
import com.facilio.agent.integration.DownloadCertFile;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.AgentUtilV2;
import com.facilio.agentv2.CloudAgentUtil;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.cacheimpl.AgentBean;
import com.facilio.aws.util.AwsUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.queue.source.MessageSource;
import com.facilio.queue.source.MessageSourceUtil;
import com.facilio.service.FacilioService;
import com.facilio.services.messageQueue.MessageQueueFactory;
import com.facilio.services.messageQueue.MessageQueueTopic;
import com.facilio.taskengine.job.JobContext;
import com.facilio.tasker.FacilioTimer;
import com.facilio.workflows.util.WorkflowUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CreateAgentCommand extends AgentV2Command {

    private static final org.apache.log4j.Logger LOGGER = LogManager.getLogger(CreateAgentCommand.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {
        if(!containsCheck(AgentConstants.AGENT, context)) throw new Exception("ADD AGENT :: Agent missing from context " + context);

        FacilioAgent agent = (FacilioAgent) context.get(AgentConstants.AGENT);
        checkDuplicateAgentName(agent.getName());
        Organization currentOrg = AccountUtil.getCurrentOrg();
        assert currentOrg != null;
        AgentBean agentBean = AgentConstants.getAgentBean();
        AgentType agentType = AgentType.valueOf(agent.getAgentType());
        addAgentAndSetAgentId(agentBean, agent);
        createMessageTopic(currentOrg);
        if (agentType.isMqttConnectionRequired() && !FacilioProperties.isDevelopment()) {
            createPolicy(agent, currentOrg, currentOrg.getDomain());
        }
        if (agentType.isAgentService()) {
            CloudAgentUtil.addCloudServiceAgent(agent);
        }
        addJobs(agentType, agentBean, agent, currentOrg);
        return false;
    }

    private static void addJobs(AgentType agentType, AgentBean agentBean, FacilioAgent agent, Organization currentOrg) throws Exception {
        if (agentType == AgentType.CLOUD) {
            agentBean.scheduleRestJob(agent);
        }
        JobContext mlJob =  FacilioTimer.getJob(currentOrg.getOrgId(), FacilioConstants.Job.ML_BMS_POINTS_TAGGING_JOB);
        if (mlJob == null){
            AgentUtilV2.scheduleMlBmsJob(currentOrg.getOrgId());
        }
        JobContext dataLogsJob =  FacilioTimer.getJob(currentOrg.getOrgId(),FacilioConstants.Job.DATA_LOG_DELETE_RECORDS_JOB);
        if(dataLogsJob == null){
            AgentUtilV2.scheduleDataLogDeleteJob(currentOrg.getOrgId(), FacilioConstants.Job.DATA_LOG_DELETE_RECORDS_JOB);
        }
        if (agentType != AgentType.NIAGARA && agentType != AgentType.FACILIO) {
            agentBean.schedulePointsDataMissingJob(agent);
        }
        JobContext unModeledJob = FacilioTimer.getJob(currentOrg.getOrgId(),FacilioConstants.Job.DATA_UN_MODELED_RECORDS_JOB);
        if(unModeledJob == null){
            AgentUtilV2.scheduleDataLogDeleteJob(currentOrg.getOrgId(), FacilioConstants.Job.DATA_UN_MODELED_RECORDS_JOB);
            LOGGER.info("Added DeleteUnModeledRecordsJob");
        }
    }

    private static void addAgentAndSetAgentId(AgentBean agentBean, FacilioAgent agent) throws Exception {
        long agentId = agentBean.addAgent(agent);
        agent.setId(agentId);
    }

    /**
     * Steps:
     * <ol>
     *   <li>Add Certificates</li>
     *   <li>Create Thing Group</li>
     *   <li>Attach Policy to Thing Group</li>
     *   <li>Add "${iot:Connection.Thing.ThingName}" to Policy as client
     *       <ul>
     *           <li>[1, 2, 3, 4] -- One time actions</li>
     *       </ul>
     *   </li>
     *   <li>Create Thing</li>
     *   <li>Attach Certificate to Thing</li>
     * </ol>
     */
    private void createPolicy(FacilioAgent agent, Organization currentOrg, String domainName) throws Exception {
    	try {
            //One Time Actions
            boolean isCertificateAdded = addCertificate(currentOrg, domainName);
            if(isCertificateAdded) {
                boolean isThingGroupCreated = AwsUtil.createThingGroupAndAttachToPolicy(domainName);

                if (!isThingGroupCreated) {
                    throw new RuntimeException("ADD AGENT:: Exception while creating Thing Group");
                }
            }

            //Create thing and Attaching Certificate to thing
            AwsUtil.createThingAttachCertificateAndAddToThingGroup(agent.getName(), domainName);
        } catch (Exception e){
            LOGGER.error("ADD AGENT:: Exception occurred while adding Agent cert/policy ",e);
            throw e;
        }
    }

    private static boolean addCertificate(Organization currentOrg, String domainName) throws Exception {
        String certFileId = com.facilio.agent.FacilioAgent.getCertFileId("facilio");
        long orgId = currentOrg.getOrgId();
        Map<String, Object> orgInfo = CommonCommandUtil.getOrgInfo(orgId, certFileId);
        boolean isCertificateAdded = orgInfo == null || orgInfo.isEmpty();
        if (isCertificateAdded) {
            DownloadCertFile.addCertificate(domainName, "facilio");
        }
        return isCertificateAdded;
    }

    private void createMessageTopic (Organization currentOrg) throws Exception {
        long orgId = currentOrg.getOrgId();
        List<Map<String, Object>> topics = MessageQueueTopic.getTopics(Collections.singletonList(orgId), MessageSourceUtil.getDefaultSource());
    	if(CollectionUtils.isEmpty(topics)) {
    		String domain = currentOrg.getDomain();
    		MessageSource source = AgentUtilV2.getMessageSource(null);
    		FacilioService.runAsService(FacilioConstants.Services.AGENT_SERVICE,()->  MessageQueueTopic.addMsgTopic(domain, orgId, source));
    		// Adding kafka topic
     		MessageQueueFactory.getMessageQueue(source).createQueue(domain);
    	}
    }

    private void checkDuplicateAgentName(String name) throws Exception {
        FacilioModule newAgentModule = ModuleFactory.getNewAgentModule();
        Map<String, FacilioField> fieldsmap = FieldFactory.getAsMap(FieldFactory.getNewAgentFields());

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(newAgentModule.getTableName())
                .select(Collections.singletonList(fieldsmap.get(AgentConstants.NAME)))
                .andCondition(CriteriaAPI.getCondition(fieldsmap.get(AgentConstants.NAME), name, StringOperators.IS));
        List<Map<String, Object>> list = builder.get();
        if (CollectionUtils.isNotEmpty(list)) {
            throw new IllegalArgumentException("Agent name already taken");
        }
    }
}
