package com.facilio.bmsconsoleV3.actions;

import com.amazonaws.services.dynamodbv2.xspec.S;
import com.facilio.bmsconsole.context.PMResourcePlanner;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext;
import com.facilio.bmsconsoleV3.util.JobPlanAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.plannedmaintenance.PlannedMaintenanceAPI;
import com.facilio.v3.V3Action;
import org.apache.kafka.common.protocol.types.Field;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JobPlanAction extends V3Action {
    private long jobPlanId;
    public void setJobPlanId(long jobplanId){
        this.jobPlanId = jobplanId;
    }
    public long getJobPlanId(){
        return jobPlanId;
    }
    private List<Long> jobPlanIds;
    public void setJobPlanIds(List<Long> jobPlanIds){
        this.jobPlanIds = jobPlanIds;
    }
    public List<Long> getJobPlanIds(){
        return jobPlanIds;
    }
    private long groupId;

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }
    private long jobPlanVersion;

    public long getJobPlanVersion() {
        return jobPlanVersion;
    }

    public void setJobPlanVersion(long jobPlanVersion) {
        this.jobPlanVersion = jobPlanVersion;
    }

    public String reviseJobPlan() throws Exception{
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.JOB_PLAN.JOB_PLAN_ID,jobPlanId);
        context.put(FacilioConstants.JOB_PLAN.JOB_PLAN_STATUS,JobPlanContext.JPStatus.PENDING_REVISION);
        FacilioChain chain = TransactionChainFactoryV3.reviseJobPlan();
        chain.setContext(context);
        chain.execute();
        Map<String ,Object> result = new HashMap<>();
        Map<String,Object> recordMap = (Map<String, Object>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        if(recordMap != null && recordMap.containsKey(FacilioConstants.ContextNames.JOB_PLAN_LIST)){
            List<JobPlanContext> jobPlanContextList = (List<JobPlanContext>) recordMap.get(FacilioConstants.ContextNames.JOB_PLAN_LIST);
            if(jobPlanContextList != null && !jobPlanContextList.isEmpty()){
                result.put(FacilioConstants.JOB_PLAN.JOB_PLAN_ID,jobPlanContextList.get(0).getId());
                result.put("groupId",jobPlanContextList.get(0).getGroup().getId());
                result.put("jobPlanVersion",jobPlanContextList.get(0).getJobPlanVersion());
            }
        }
        setData("result",result);
        return SUCCESS;
    }
    public String publishJobPlan() throws Exception{
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.JOB_PLAN.JOB_PLAN_ID,jobPlanId);
        FacilioChain chain = TransactionChainFactoryV3.getPublishJobPlanChain();
        chain.setContext(context);
        chain.execute();
        Map<String ,Object> result = new HashMap<>();
        Map<String,Object> recordMap = (Map<String, Object>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        if(recordMap != null && recordMap.containsKey(FacilioConstants.ContextNames.JOB_PLAN_LIST)){
            List<JobPlanContext> jobPlanContextList = (List<JobPlanContext>) recordMap.get(FacilioConstants.ContextNames.JOB_PLAN_LIST);
            if(jobPlanContextList != null && !jobPlanContextList.isEmpty()){
                result.put("groupId",jobPlanContextList.get(0).getGroup().getId());
                result.put("jobPlanVersion",jobPlanContextList.get(0).getJobPlanVersion());
            }
        }
        setData("result",result);
        return SUCCESS;
    }
    public String bulkPublishJobPlan() throws Exception{
        FacilioContext context = new FacilioContext();
        context.put("jobPlanIds",jobPlanIds);
        FacilioChain chain = TransactionChainFactoryV3.getPublishJobPlanChain();
        chain.setContext(context);
        chain.execute();
        return SUCCESS;
    }
    public String fetchVersionHistory() throws Exception{
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.JOB_PLAN.JOB_PLAN_ID,jobPlanId);
        FacilioChain chain = TransactionChainFactoryV3.fetchJobPlanVersionHistoryChain();
        chain.setContext(context);
        chain.execute();
        Map<String,Object> recordMap = (Map<String, Object>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        List<JobPlanContext> jobPlanContextList = (List<JobPlanContext>) recordMap.get(FacilioConstants.ContextNames.JOB_PLAN_LIST);
        setData("result",jobPlanContextList);
        return SUCCESS;
    }
    public String cloneJobPlan() throws Exception{
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.JOB_PLAN.JOB_PLAN_ID,jobPlanId);
        context.put(FacilioConstants.JOB_PLAN.JOB_PLAN_STATUS,JobPlanContext.JPStatus.IN_ACTIVE);
        context.put(FacilioConstants.JOB_PLAN.IS_CLONING,true);
        FacilioChain chain = TransactionChainFactoryV3.getJobPlanCloneChain();
        chain.setContext(context);
        chain.execute();
        Map<String ,Object> result = new HashMap<>();
        Map<String,Object> recordMap = (Map<String, Object>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        if(recordMap != null && recordMap.containsKey(FacilioConstants.ContextNames.JOB_PLAN_LIST)){
            List<JobPlanContext> jobPlanContextList = (List<JobPlanContext>) recordMap.get(FacilioConstants.ContextNames.JOB_PLAN_LIST);
            if(jobPlanContextList != null && !jobPlanContextList.isEmpty()){
                result.put(FacilioConstants.JOB_PLAN.JOB_PLAN_ID,jobPlanContextList.get(0).getId());
            }
        }
        setData("result",result);
        return SUCCESS;
    }
    public String fetchAssociatedRecords() throws Exception{
        long id = jobPlanId;
        List<Map<String,Object>> pmResourcePlannerList = PlannedMaintenanceAPI.getPPmDetailsAssociatedWithJobPlan(id);
        setData("result",pmResourcePlannerList);
        return SUCCESS;
    }
    public String getJobPlanGroupAndVersion() throws Exception{
        long id = jobPlanId;
        Map<String,Object> recordMap = JobPlanAPI.getJobPlanGroupAndVersion(jobPlanId);
        setData("result",recordMap);
        return SUCCESS;
    }
    public String getJobPlanIdFromGroupAndVersion() throws Exception{
        long group = groupId;
        long vesrion = jobPlanVersion;
        long jobPlanId = JobPlanAPI.getJobPlanIdFromGroupAndVersion(group,vesrion);
        setData("result",jobPlanId);
        return SUCCESS;
    }
}
