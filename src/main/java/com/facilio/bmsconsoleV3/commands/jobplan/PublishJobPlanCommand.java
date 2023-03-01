package com.facilio.bmsconsoleV3.commands.jobplan;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PublishJobPlanCommand extends FacilioCommand {
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
        for(JobPlanContext jobPlanContext : jobPlanContextList){
            publishJobPlan(jobPlanContext);
        }
        return false;
    }
    public int publishJobPlan(JobPlanContext jobPlan) throws Exception{
        if(jobPlan == null){
            throw new IllegalArgumentException("JobPlan can't be null");
        }
        jobPlan.setJpStatus(JobPlanContext.JPStatus.ACTIVE.getVal());
        jobPlan.setJpStatusEnum(JobPlanContext.JPStatus.ACTIVE);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule jobPlanModule = modBean.getModule(FacilioConstants.ContextNames.JOB_PLAN);
        List<FacilioField> jobPlanFields = modBean.getAllFields(FacilioConstants.ContextNames.JOB_PLAN);

        UpdateRecordBuilder<JobPlanContext> builder = new UpdateRecordBuilder<>();
        builder.module(jobPlanModule);
        builder.fields(jobPlanFields);
        builder.andCondition(CriteriaAPI.getIdCondition(jobPlan.getId(),jobPlanModule));
        int totalCount = builder.update(jobPlan);
        return totalCount;
    }
}
