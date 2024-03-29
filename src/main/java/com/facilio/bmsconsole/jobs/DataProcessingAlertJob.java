package com.facilio.bmsconsole.jobs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.facilio.services.email.EmailClient;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.accounts.dto.Organization;
import com.facilio.agentv2.AgentConstants;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.iam.accounts.util.IAMOrgUtil;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.messageQueue.MessageQueueTopic;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import com.facilio.time.DateTimeUtil;

public class DataProcessingAlertJob extends FacilioJob {
    private static final Logger LOGGER = LogManager.getLogger(DataProcessingAlertJob.class.getName());
    public static final FacilioModule AGENT_MODULE = ModuleFactory.getNewAgentModule();
    public static final  Map<String, FacilioField> FIELD_MAP = FieldFactory.getAsMap(FieldFactory.getNewAgentFields());
    @Override
    public void execute(JobContext jc) throws Exception {
        try{
            long jobStart = System.currentTimeMillis();
            List<Map<String,Object>> topicList = MessageQueueTopic.getTopics(null, null);
            List<Long> orgIds = topicList.stream().map(p -> (long) p.get("orgId")).collect(Collectors.toList());
            List<Organization> organizationList = IAMOrgUtil.getOrgs(orgIds);
            for(Organization org : organizationList){
                dataMissing(org);
            }
            LOGGER.info("Time taken to complete DataProcessingAlertJob job : "+(System.currentTimeMillis() - jobStart) + "ms");
        }catch (Exception e){
            LOGGER.error("Exception occurred in DataProcessingAlertJob ", e);
            CommonCommandUtil.emailException(DataPendingAlertJob.class.getName(), "Error occurred in DataProcessingAlertJob  for job id : "+jc.getJobId(), e);
        }
    }
    private void dataMissing(Organization org) throws Exception {
        try{
            long orgId = org.getOrgId();
            if(orgId == 152){
                return;
            }
            String orgDomain = org.getDomain();
            ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
            List<Map<String,Object>> props = bean.getMissingData();
            if(CollectionUtils.isEmpty(props)) {
                return;
            }
            for(Map<String,Object> prop : props){
                long agentId = (long) prop.get(AgentConstants.ID);
                String agentName = (String) prop.get(AgentConstants.NAME);
                long lastProcessedTime = (long) prop.get(AgentConstants.LAST_DATA_RECEIVED_TIME);
                sendMail(orgDomain, orgId,agentId,agentName,lastProcessedTime);
            }
        }catch (Exception e){
            throw e;
        }
    }

    private static void sendMail(String orgDomain, long orgId , long agentId, String agentName,long lastProcessedTime) throws Exception {
        StringBuilder msg = new StringBuilder()
                .append(" Data is not processing for last 2hrs.")
                .append("\n")
                .append(" orgId : ")
                .append(orgId)
                .append("\n")
                .append(" Agent Name : ")
                .append(agentName)
                .append("\n")
                .append("last message processed time: ")
                .append(lastProcessedTime);
        JSONObject json = new JSONObject();
        json.put("to", "agent@facilio.com");
        json.put("sender", EmailClient.getNoReplyFromEmail());
        json.put("subject", "Data Missing for Org : " + orgDomain);
        json.put("message", msg.toString());
        FacilioFactory.getEmailClient().sendEmail(json);
    }
}

