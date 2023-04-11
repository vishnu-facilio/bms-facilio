package com.facilio.bmsconsoleV3.commands.jobplan;

import com.facilio.accounts.bean.UserBean;
import com.facilio.accounts.dto.IAMUser;
import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanTasksContext;
import com.facilio.bmsconsoleV3.util.JobPlanAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.plannedmaintenance.PlannedMaintenanceAPI;
import org.apache.commons.chain.Context;
import org.apache.kafka.common.protocol.types.Field;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FetchVersionHistoryCommand extends FacilioCommand {
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
        List<JobPlanContext> jobPlanGroupList = new ArrayList<>();
        for(JobPlanContext jobPlanContext : jobPlanContextList){
            if(jobPlanContext.getGroup() == null){
                throw new IllegalArgumentException("No Groups Available");
            }
            jobPlanGroupList.addAll(JobPlanAPI.getJobPlanFromGroupId(jobPlanContext.getGroup().getId()));
            for(JobPlanContext jobPlan : jobPlanGroupList){
                IAMUser iAmUser = jobPlan.getSysCreatedBy();
                UserBean userBean = (UserBean) BeanFactory.lookup("UserBean");
                User user = userBean.getUser(iAmUser.getUid(),false);
                iAmUser.setName(user.getName());
                iAmUser.setEmail(user.getEmail());
            }
        }
        recordMap.put(FacilioConstants.ContextNames.JOB_PLAN_LIST,jobPlanGroupList);
        context.put(FacilioConstants.ContextNames.RECORD_MAP,recordMap);
        return false;
    }
}
