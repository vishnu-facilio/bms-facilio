package com.facilio.bmsconsoleV3.commands.jobplan;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanTaskSectionContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioContext;
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
import java.util.Map;

public class CreateJobPlanAfterCloning extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule jobPlanModule = modBean.getModule(FacilioConstants.ContextNames.JOB_PLAN);
        List<JobPlanContext> jobplans = (List<JobPlanContext>) (((Map<String,Object>)context.get(FacilioConstants.ContextNames.RECORD_MAP)).get(FacilioConstants.ContextNames.JOB_PLAN_LIST));
        List<ModuleBaseWithCustomFields> jobPlanContextList = new ArrayList<>();
        List<ModuleBaseWithCustomFields> jobPlanTaskSectionContextList= new ArrayList<>();
        if(CollectionUtils.isNotEmpty(jobplans)){
            for(JobPlanContext jobPlan : jobplans){
                if(jobPlan == null){
                    continue;
                }
                jobPlanContextList.add(jobPlan);
                jobPlanTaskSectionContextList.addAll(jobPlan.getJobplansection());

            }
            FacilioContext facilioContext = V3Util.createRecord(jobPlanModule,jobPlanContextList);
            List<JobPlanContext> newJobPlans = (List<JobPlanContext>) (((Map<String,Object>)facilioContext.get(FacilioConstants.ContextNames.RECORD_MAP)).get(FacilioConstants.ContextNames.JOB_PLAN));
            context.put("newJobPlans",newJobPlans);
        }
        return false;
    }
}
