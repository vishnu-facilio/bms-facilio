package com.facilio.bmsconsoleV3.commands.jobplan;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanTaskSectionContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanTasksContext;
import com.facilio.bmsconsoleV3.util.JobPlanAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;

import lombok.extern.log4j.Log4j;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log4j
public class AddJobPlanTasksCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<JobPlanTaskSectionContext> taskSections = recordMap.get(FacilioConstants.ContextNames.JOB_PLAN_SECTION);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule jobPlanTaskModule = modBean.getModule(FacilioConstants.ContextNames.JOB_PLAN_TASK);
        List<FacilioField> fields = modBean.getAllFields(jobPlanTaskModule.getName());

        if(CollectionUtils.isNotEmpty(taskSections)) {
            List<JobPlanTasksContext> allTasks = new ArrayList<>();
            for(JobPlanTaskSectionContext section : taskSections) {
                List<JobPlanTasksContext> taskList = section.getTasks();
                if(CollectionUtils.isEmpty(taskList)) {
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Task list is mandatory for a task section");
                }

                JobPlanAPI.deleteJobPlanTasks( section.getId());
                int sequence = 1;
                for(JobPlanTasksContext task : taskList) {
                    // deletion of  JobPlan Task Input option will be done automatically as Cascade delete is given
                    task.setTaskSection(new JobPlanTaskSectionContext(section.getId()));
                    task.setJobPlan(section.getJobPlan());
                    task.setCreatedBy(AccountUtil.getCurrentUser());
                    task.setCreatedTime(System.currentTimeMillis());
                    task.setSequence(sequence++);
                }
                LOGGER.error("task map  ---- "+FieldUtil.getAsJSONArray(taskList, JobPlanTasksContext.class));
                V3RecordAPI.addRecord(false, taskList, jobPlanTaskModule, fields);
                allTasks.addAll(taskList); // Collecting all task to update the InputOptions.
            }
            context.put(FacilioConstants.ContextNames.TASK_LIST, allTasks);
        }
        return false;
    }
}
