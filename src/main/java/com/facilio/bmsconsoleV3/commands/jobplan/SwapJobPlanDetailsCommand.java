package com.facilio.bmsconsoleV3.commands.jobplan;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanTaskSectionContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanTasksContext;
import com.facilio.bmsconsoleV3.util.JobPlanAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.V3Builder.UpdateBuilder;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SwapJobPlanDetailsCommand extends FacilioCommand {
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
            throw new IllegalArgumentException("No Job plans Available");
        }
        List<JobPlanContext> jobPlanListToBePublished = new ArrayList<>();
        for(JobPlanContext jobPlanContext : jobPlanContextList){
            if(jobPlanContext.getJpStatusEnum() == JobPlanContext.JPStatus.REVISED){
                throw new IllegalArgumentException("Revised Job Plan with id #"+jobPlanContext.getId()+" cannot be published");
            }
            Long jobPlanId = jobPlanContext.getId();
            Long groupId = jobPlanContext.getGroup().getId();
            if(jobPlanId != groupId){
                JobPlanContext oldJobplan = JobPlanAPI.getJobPlan(groupId);
                oldJobplan.setJobplansection(JobPlanAPI.setJobPlanDetails(groupId));
                oldJobplan.setId(jobPlanId);
                oldJobplan.setJpStatusEnum(JobPlanContext.JPStatus.REVISED);
                oldJobplan.setJpStatus(JobPlanContext.JPStatus.REVISED.getVal());
                updateJobplan(oldJobplan);
                jobPlanContext.setId(groupId);
                updateJobplan(jobPlanContext);
                fillJobPlanTaskSectionDetails(jobPlanContext);
                fillJobPlanTaskSectionDetails(oldJobplan);
                jobPlanListToBePublished.add(jobPlanContext);
            }
            else{
                jobPlanListToBePublished.add(jobPlanContext);
            }
        }
        if(recordMap.containsKey(FacilioConstants.ContextNames.JOB_PLAN)){
            recordMap.put(FacilioConstants.ContextNames.JOB_PLAN,jobPlanListToBePublished);
        }
        else if(recordMap.containsKey(FacilioConstants.ContextNames.JOB_PLAN_LIST)){
            recordMap.put(FacilioConstants.ContextNames.JOB_PLAN_LIST,jobPlanListToBePublished);
        }
        context.put(FacilioConstants.ContextNames.RECORD_MAP,recordMap);

        return false;
    }
    public static int updateJobplan(JobPlanContext jobPlanContext) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.JOB_PLAN);

        UpdateRecordBuilder<JobPlanContext> builder = new UpdateRecordBuilder<JobPlanContext>()
                .module(module)
                .fields(modBean.getAllFields(module.getName()))
                .andCondition(CriteriaAPI.getIdCondition(jobPlanContext.getId(),module));
       return builder.update(jobPlanContext);
    }
    public static void fillJobPlanTaskSectionDetails(JobPlanContext newJobPlan) throws Exception {
        List<JobPlanTaskSectionContext> newJobplansectionList = newJobPlan.getJobplansection();
        for(JobPlanTaskSectionContext jobPlanTaskSectionContext : newJobplansectionList){
            JobPlanContext jp = new JobPlanContext();
            jp.setId(newJobPlan.getId());
            jobPlanTaskSectionContext.setJobPlan(jp);
            updateJobPlanSection(jobPlanTaskSectionContext);
            fillJobPlanTaskDetails(jobPlanTaskSectionContext,newJobPlan);
        }

    }
    public static int updateJobPlanSection(JobPlanTaskSectionContext jobPlanTaskSectionContext) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.JOB_PLAN_SECTION);

        UpdateRecordBuilder<JobPlanTaskSectionContext> builder = new UpdateRecordBuilder<JobPlanTaskSectionContext>()
                .module(module)
                .fields(modBean.getAllFields(module.getName()))
                .andCondition(CriteriaAPI.getIdCondition(jobPlanTaskSectionContext.getId(),module));
        return builder.update(jobPlanTaskSectionContext);
    }
    public static void fillJobPlanTaskDetails(JobPlanTaskSectionContext taskSectionContext,JobPlanContext jobPlanContext) throws Exception{
        List<JobPlanTasksContext> jobPlanTasksContextList = taskSectionContext.getTasks();
        for(JobPlanTasksContext jobPlanTasksContext : jobPlanTasksContextList){
            JobPlanContext jp = new JobPlanContext();
            jp.setId(jobPlanContext.getId());
            jobPlanTasksContext.setJobPlan(jp);
            updateJobPlanTasks(jobPlanTasksContext);
        }
    }
    public static int updateJobPlanTasks(JobPlanTasksContext jobPlanTasksContext)throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.JOB_PLAN_TASK);

        UpdateRecordBuilder<JobPlanTasksContext> builder = new UpdateRecordBuilder<JobPlanTasksContext>()
                .module(module)
                .fields(modBean.getAllFields(module.getName()))
                .andCondition(CriteriaAPI.getIdCondition(jobPlanTasksContext.getId(),module));
        return builder.update(jobPlanTasksContext);
    }
}
