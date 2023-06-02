package com.facilio.bmsconsoleV3.commands.jobplan;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanTaskSectionContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanTasksContext;
import com.facilio.bmsconsoleV3.util.JobPlanAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JobPlanContextCloningCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Map<String,Object> recordMap = (Map<String, Object>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        List<JobPlanContext> jobPlanContextList = new ArrayList<>();
        if(recordMap.containsKey(FacilioConstants.ContextNames.JOB_PLAN)){
            jobPlanContextList.add((JobPlanContext) recordMap.get(FacilioConstants.ContextNames.JOB_PLAN));
        }
        else if(recordMap.containsKey(FacilioConstants.ContextNames.JOB_PLAN_LIST)) {
            jobPlanContextList.addAll((List<JobPlanContext>) recordMap.get(FacilioConstants.ContextNames.JOB_PLAN_LIST));
        }
        if(jobPlanContextList == null || jobPlanContextList.isEmpty()){
            throw new IllegalArgumentException("No Job Plan Available");
        }
        Boolean isCloning = (Boolean) context.getOrDefault(FacilioConstants.JOB_PLAN.IS_CLONING,false);
        for(JobPlanContext jobPlanContext : jobPlanContextList){
                Long groupId = jobPlanContext.getGroup().getId();
                List<JobPlanContext> jobPlanList = JobPlanAPI.getJobPlanFromGroupId(groupId);
                if(jobPlanList == null || jobPlanList.isEmpty()){
                    throw new IllegalArgumentException("This Group has No Job Plans (Group Id - #"+groupId);
                }
                Double version = getJobPlanVersionNumber(jobPlanList);
                jobPlanContext.setJobPlanVersion(version);
                if(jobPlanContext.getJobplansection() == null || jobPlanContext.getJobplansection().isEmpty()){
                    throw new IllegalArgumentException("No Job plan Task Section Available (JobPlan Id - #"+jobPlanContext.getId());
                }
                List<Map<String,Object>> jobPlanSectionContextMapList = new ArrayList<>();
                List<JobPlanTaskSectionContext> jobPlanTaskSectionContextList = jobPlanContext.getJobplansection();
                for(JobPlanTaskSectionContext jobPlanTaskSectionContext : jobPlanTaskSectionContextList){
                    Map<String,Object> jobPlanSectionContextMap = FieldUtil.getAsProperties(jobPlanTaskSectionContext);
                    jobPlanSectionContextMap.put("id",null);
                    jobPlanSectionContextMapList.add(jobPlanSectionContextMap);
                }
                Map<String,List<Map<String,Object>>> subFormMap = new HashMap<>();
                subFormMap.put(FacilioConstants.ContextNames.JOB_PLAN_SECTION,jobPlanSectionContextMapList);
                jobPlanContext.setSubForm(subFormMap);
                if(isCloning){
                    jobPlanContext.setGroup(null);
                    jobPlanContext.setJobPlanVersion(0.0);
                    jobPlanContext.setName("Copy Of "+jobPlanContext.getName());
                }
        }
        if(recordMap.containsKey(FacilioConstants.ContextNames.JOB_PLAN)){
            recordMap.put(FacilioConstants.ContextNames.JOB_PLAN,jobPlanContextList);
        }
        else if(recordMap.containsKey(FacilioConstants.ContextNames.JOB_PLAN_LIST)){
            recordMap.put(FacilioConstants.ContextNames.JOB_PLAN_LIST,jobPlanContextList);
        }
        context.put(FacilioConstants.ContextNames.RECORD_MAP,recordMap);
        return false;
    }
    public Double getJobPlanVersionNumber(List<JobPlanContext> jobPlanContextList) {
        Double version = 1.0;
        if(jobPlanContextList == null || jobPlanContextList.isEmpty()){
            return version;
        }
        for(JobPlanContext jobPlanContext : jobPlanContextList){
            if(jobPlanContext.getJobPlanVersion() > version){
                version = jobPlanContext.getJobPlanVersion();
            }
        }
        return version + 1;
    }
}
