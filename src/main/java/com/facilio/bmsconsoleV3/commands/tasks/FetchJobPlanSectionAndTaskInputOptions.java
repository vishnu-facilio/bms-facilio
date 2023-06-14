package com.facilio.bmsconsoleV3.commands.tasks;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsoleV3.context.V3TaskContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanTaskSectionContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanTasksContext;
import com.facilio.bmsconsoleV3.context.tasks.SectionInputOptionsContext;
import com.facilio.bmsconsoleV3.context.tasks.TaskInputOptionsContext;
import com.facilio.bmsconsoleV3.util.JobPlanAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * FetchJobPlanSectionAndTaskInputOptions fetches the JobPlan's
 * - TaskInputOptions and puts into the respective Task Object
 * - SectionInputOptions and puts into the respective Section Object
 */
public class FetchJobPlanSectionAndTaskInputOptions extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        // Get the JobPlan Object
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<JobPlanContext> jobPlanContextList = (List<JobPlanContext>) recordMap.get(FacilioConstants.ContextNames.JOB_PLAN);

        if (jobPlanContextList == null) {
            return false;
        }

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        // JobPlan level
        for (JobPlanContext jobPlan : jobPlanContextList) {
            List<JobPlanTaskSectionContext> jobPlanTaskSectionList = jobPlan.getJobplansection();
            if (jobPlanTaskSectionList != null && !jobPlanTaskSectionList.isEmpty()) {

                // JobPlan section level
                for (JobPlanTaskSectionContext section : jobPlanTaskSectionList) {

                    // set input options for section
                    if (section.getInputTypeEnum().equals(TaskContext.InputType.RADIO)) {
                        List<Map<String, Object>> options = JobPlanAPI.fetchSectionInputOptions(modBean, section);
                        if (options != null) {
                            section.setInputOptions(options);
                        }
                    }

                    List<JobPlanTasksContext> taskList = JobPlanAPI.orderTaskBySequenceNumber(section.getTasks());
                    if (taskList != null && !taskList.isEmpty()) {

                        // jobPlan task level
                        for (JobPlanTasksContext task : taskList) {
                            if (task.getInputTypeEnum().equals(V3TaskContext.InputType.RADIO)) {
                                List<Map<String, Object>> options = JobPlanAPI.fetchTaskInputOptions(modBean, task);
                                if (options != null) {
                                    task.setInputOptions(options);
                                }
                            }
                        }
                        // Set the tasks - so that it reflects in back in recordMap object
                        section.setTasks(taskList);
                    }
                }
            }

        }
        return false;
    }
}
