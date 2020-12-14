package com.facilio.bmsconsole.jobs;

import com.facilio.accounts.dto.Organization;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.iam.accounts.util.IAMOrgUtil;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.messageQueue.MessageQueueTopic;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DataPendingAlertJob extends FacilioJob {
    private static final Logger LOGGER = LogManager.getLogger(DataPendingAlertJob.class.getName());
    public static final FacilioModule AGENT_MESSAGE_MODULE = ModuleFactory.getAgentMessageModule();
    public static final  Map<String, FacilioField> FIELD_MAP = FieldFactory.getAsMap(FieldFactory.getAgentMessageFields());
    @Override
    public void execute(JobContext jc) throws Exception {
        try{
            long jobStart = System.currentTimeMillis();
            List<Map<String,Object>> topicList = MessageQueueTopic.getTopics(null);
            List<Long> orgIds = topicList.stream().map(p -> (long) p.get("orgId")).collect(Collectors.toList());
            List<Organization> organizationList = IAMOrgUtil.getOrgs(orgIds);
            for(Organization org : organizationList){
                calculatePendingMsg(org);
            }
            LOGGER.info("Time taken to complete DataPending job : "+(System.currentTimeMillis() - jobStart) + "ms");
        }catch (Exception e){
            LOGGER.error("Exception occurred in DataPendingAlertJob ", e);
            CommonCommandUtil.emailException(DataPendingAlertJob.class.getName(), "Error occurred in DataPendingAlertJob calculation for job id : "+jc.getJobId(), e);
        }
    }

    private void calculatePendingMsg(Organization org) throws Exception {
        try{
            long orgId = org.getOrgId();
            String orgDomain = org.getDomain();
            ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
            long count = bean.getPendingDataCount();
            StringBuilder msg = new StringBuilder()
                    .append("There are ")
                    .append(count)
                    .append("messages pending for last 2hrs. org - ")
                    .append(orgDomain)
                    .append("orgId- ")
                    .append(orgId);
            JSONObject json = new JSONObject();
            json.put("to", "agent@facilio.com");
            json.put("sender", "noreply@facilio.com");
            json.put("subject", "Pending Messages");
            json.put("message", msg);
            FacilioFactory.getEmailClient().sendEmail(json);
        }catch (Exception e){
            throw e;
        }
    }
}
