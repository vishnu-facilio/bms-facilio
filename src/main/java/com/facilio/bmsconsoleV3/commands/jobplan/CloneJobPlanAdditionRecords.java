package com.facilio.bmsconsoleV3.commands.jobplan;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.jobplan.*;
import com.facilio.bmsconsoleV3.util.JobPlanAPI;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import java.util.ArrayList;
import java.util.List;


public class CloneJobPlanAdditionRecords extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule jobPlanLabourModule = modBean.getModule(FacilioConstants.ContextNames.JOB_PLAN_LABOURS);
        FacilioModule jobPlanItemsModule = modBean.getModule(FacilioConstants.ContextNames.JOB_PLAN_ITEMS);
        FacilioModule jobPlanToolsModule = modBean.getModule(FacilioConstants.ContextNames.JOB_PLAN_TOOLS);
        FacilioModule jobPlanServicesModule = modBean.getModule(FacilioConstants.ContextNames.JOB_PLAN_SERVICES);

        List<Long> jobPlanIdList = new ArrayList<>();
        if(context.containsKey(FacilioConstants.JOB_PLAN.JOB_PLAN_ID)) {
            Long jobPlanId = (Long) context.get(FacilioConstants.JOB_PLAN.JOB_PLAN_ID);
            if (jobPlanId == null) {
                throw new IllegalArgumentException(jobPlanId+"Job Plan Can't Be Empty");
            }
            jobPlanIdList.add(jobPlanId);
        }
        else if(context.containsKey("jobPlanIds")){
            List<Long> jobPlanIds = (List<Long>) context.get("jobPlanIds");
            if(jobPlanIds == null || jobPlanIds.isEmpty()){
                throw new IllegalArgumentException("Must contain atLeast One JobPlan");
            }
            jobPlanIdList.addAll(jobPlanIds);
        }
        List<JobPlanContext> newJobPlanList = (List<JobPlanContext>) (context.get("newJobPlans"));
        if(CollectionUtils.isEmpty(newJobPlanList)){
            return false;
        }
        createJobPlanLabourRecord(newJobPlanList,jobPlanIdList,jobPlanLabourModule);
        createJobPlansItemsRecord(newJobPlanList,jobPlanIdList,jobPlanItemsModule);
        createJobPlanToolsRecord(newJobPlanList,jobPlanIdList,jobPlanToolsModule);
        createJobPlanServicesRecord(newJobPlanList,jobPlanIdList,jobPlanServicesModule);
        return false;
    }
    public void createJobPlanLabourRecord(List<JobPlanContext> newJobPlanList,List<Long> jobPlanIdList,FacilioModule jobPlanLabourModule) throws Exception{
        List<V3JobPlanLabourContext> jobPlanLabourContextList = JobPlanAPI.getJobPlanLabourFromJobPlanIds(jobPlanIdList);
        List<ModuleBaseWithCustomFields> jobPlanLabourContexts = new ArrayList<>();
        if(CollectionUtils.isEmpty(jobPlanLabourContextList)){
            return;
        }
        for(V3JobPlanLabourContext jobPlanLabourContext : jobPlanLabourContextList){
            Long jobPlanId = jobPlanLabourContext.getParent().getId();
            int index = jobPlanIdList.indexOf(jobPlanId);
            JobPlanContext jobPlanContext = newJobPlanList.get(index);
            jobPlanLabourContext.setParent(jobPlanContext);
        }
        jobPlanLabourContexts.addAll(jobPlanLabourContextList);
        V3Util.createRecord(jobPlanLabourModule,jobPlanLabourContexts);
    }
    public void createJobPlansItemsRecord(List<JobPlanContext> newJobPlanList,List<Long> jobPlanIdList,FacilioModule jobPlanItemsModule) throws Exception{
        List<JobPlanItemsContext> jobPlanItemContextList = JobPlanAPI.getJobPlanItemsFromJobPlanId(jobPlanIdList);
        List<ModuleBaseWithCustomFields> jobPlanItemsContexts = new ArrayList<>();
        if(CollectionUtils.isEmpty(jobPlanItemContextList)){
            return;
        }
        for(JobPlanItemsContext jobPlanItemsContext : jobPlanItemContextList){
            Long jobPlanId = jobPlanItemsContext.getJobPlan().getId();
            int index = jobPlanIdList.indexOf(jobPlanId);
            JobPlanContext jobPlanContext = newJobPlanList.get(index);
            jobPlanItemsContext.setJobPlan(jobPlanContext);
        }
        jobPlanItemsContexts.addAll(jobPlanItemContextList);
        V3Util.createRecord(jobPlanItemsModule,jobPlanItemsContexts);
    }
    public void createJobPlanToolsRecord(List<JobPlanContext> newJobplanList,List<Long> jobPlanIdList, FacilioModule jobPlanToolsModule) throws Exception{
        List<JobPlanToolsContext> jobPlanToolsContextList = JobPlanAPI.getJobPlanToolsFromJobPlanId(jobPlanIdList);
        List<ModuleBaseWithCustomFields> jobPlanToolsContexts = new ArrayList<>();
        if(CollectionUtils.isEmpty(jobPlanToolsContextList)){
            return;
        }
        for(JobPlanToolsContext jobPlanToolsContext : jobPlanToolsContextList){
            Long jobPlanId = jobPlanToolsContext.getJobPlan().getId();
            int index = jobPlanIdList.indexOf(jobPlanId);
            JobPlanContext jobPlanContext = newJobplanList.get(index);
            jobPlanToolsContext.setJobPlan(jobPlanContext);
        }
        jobPlanToolsContexts.addAll(jobPlanToolsContextList);
        V3Util.createRecord(jobPlanToolsModule,jobPlanToolsContexts);
    }
    public void createJobPlanServicesRecord(List<JobPlanContext> newJobplanList,List<Long> jobPlanIdList, FacilioModule jobPlanServicesModule) throws Exception{
        List<JobPlanServicesContext> jobPlanServicesContextList = JobPlanAPI.getJobPlanServicesFromJobPlanId(jobPlanIdList);
        List<ModuleBaseWithCustomFields> jobPlanServicesContexts = new ArrayList<>();
        if(CollectionUtils.isEmpty(jobPlanServicesContextList)){
            return;
        }
        for(JobPlanServicesContext jobPlanServicesContext : jobPlanServicesContextList){
            Long jobPlanId = jobPlanServicesContext.getJobPlan().getId();
            int index = jobPlanIdList.indexOf(jobPlanId);
            JobPlanContext jobPlanContext = newJobplanList.get(index);
            jobPlanServicesContext.setJobPlan(jobPlanContext);
        }
        jobPlanServicesContexts.addAll(jobPlanServicesContextList);
        V3Util.createRecord(jobPlanServicesModule,jobPlanServicesContexts);
    }

}
