package com.facilio.bmsconsoleV3.commands.tasks;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3TaskContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanTaskSectionContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanTasksContext;
import com.facilio.bmsconsoleV3.context.tasks.SectionInputOptionsContext;
import com.facilio.bmsconsoleV3.context.tasks.TaskInputOptionsContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
                    if (section.getInputTypeEnum().equals(JobPlanTaskSectionContext.InputType.RADIO)) {
                        List<Map<String, Object>> options = fetchSectionInputOptions(modBean, section);
                        if (options != null) {
                            section.setInputOptions(options);
                        }
                    }

                    List<JobPlanTasksContext> taskList = FieldUtil.getAsBeanListFromMapList(section.getTasks(), JobPlanTasksContext.class);
                    if (taskList != null && !taskList.isEmpty()) {

                        // jobPlan task level
                        for (JobPlanTasksContext task : taskList) {
                            if (task.getInputTypeEnum().equals(V3TaskContext.InputType.RADIO)) {
                                List<Map<String, Object>> options = fetchTaskInputOptions(modBean, task);
                                if (options != null) {
                                    task.setInputOptions(options);
                                }
                            }
                        }
                        // Set the tasks - so that it reflects in back in recordMap object
                        section.setTasks(FieldUtil.getAsMapList(taskList, JobPlanTasksContext.class));
                    }
                }
            }

        }
        return false;
    }

    /**
     * Helper function to fetch the JobPlan TaskInputOptions
     *
     * @param modBean
     * @param task
     * @return
     * @throws Exception
     */
    private List<Map<String, Object>> fetchTaskInputOptions(ModuleBean modBean, JobPlanTasksContext task) throws Exception {
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.JOB_PLAN_TASK_INPUT_OPTIONS);
        SelectRecordsBuilder<TaskInputOptionsContext> selectRecordsBuilder = new SelectRecordsBuilder<>();
        List<FacilioField> fields = new ArrayList<>(FieldFactory.getJobPlanTaskInputOptionsFields().values());
        selectRecordsBuilder
                .module(module)
                .table(module.getTableName())
                .beanClass(TaskInputOptionsContext.class)
                .select(fields)
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getJobPlanTaskInputOptionsFields().get("jobPlanTask"),
                        String.valueOf(task.getId()), NumberOperators.EQUALS));

        return selectRecordsBuilder.getAsProps();
    }

    /**
     * Helper function to fetch the JobPlan SectionInputOptions
     *
     * @param modBean
     * @param section
     * @return
     * @throws Exception
     */
    private List<Map<String, Object>> fetchSectionInputOptions(ModuleBean modBean, JobPlanTaskSectionContext section) throws Exception {
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.JOB_PLAN_SECTION_INPUT_OPTIONS);
        SelectRecordsBuilder<SectionInputOptionsContext> selectRecordsBuilder = new SelectRecordsBuilder<>();
        List<FacilioField> fields = new ArrayList<>(FieldFactory.getJobPlanSectionInputOptionsFields().values());
        selectRecordsBuilder
                .module(module)
                .table(module.getTableName())
                .beanClass(SectionInputOptionsContext.class)
                .select(fields)
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getJobPlanSectionInputOptionsFields().get("jobPlanSection"),
                        String.valueOf(section.getId()), NumberOperators.EQUALS));

        return selectRecordsBuilder.getAsProps();
    }
}
