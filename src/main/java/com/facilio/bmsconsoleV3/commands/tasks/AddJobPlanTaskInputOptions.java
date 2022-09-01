package com.facilio.bmsconsoleV3.commands.tasks;

import com.facilio.bmsconsoleV3.context.V3TaskContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanTasksContext;
import com.facilio.bmsconsoleV3.context.tasks.TaskInputOptionsContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * AddJobPlanTaskInputOptions gets the List of Tasks and iterates over it to add the Input Options into
 * JobPlan_Task_Input_Options table if INPUT TYPE IS RADIO.
 *
 * Optimization:
 *      Deleting the record and inserting it keep on changing the ID of input option. So try updating the record
 *      values when count of the records are equal, and delete-insert only if the count changes.
 */
public class AddJobPlanTaskInputOptions extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        List<JobPlanTasksContext> tasks = (List<JobPlanTasksContext>) context.get(FacilioConstants.ContextNames.TASK_LIST);
        if (tasks == null) {
            JobPlanTasksContext task = (JobPlanTasksContext) context.get(FacilioConstants.ContextNames.TASK);
            if (task != null) {
                tasks = Collections.singletonList(task);
            }
        }

        if (tasks == null || tasks.isEmpty()) {
            return false;
        }

        FacilioModule module = ModuleFactory.getJobPlanTaskInputOptionsModule();

        // iterate tasks
        for (JobPlanTasksContext task : tasks) {

            if (task.getInputTypeEnum() == V3TaskContext.InputType.RADIO) {

                if (task.getInputOptions() != null && !task.getInputOptions().isEmpty()) {

                    List<TaskInputOptionsContext> taskInputOptions = FieldUtil.getAsBeanListFromMapList(task.getInputOptions(), TaskInputOptionsContext.class);
                    for (TaskInputOptionsContext inputOption : taskInputOptions) {
                        inputOption.setJobPlanTask(task);
                    }

                    List<Map<String, Object>> taskInputOptionMapList = FieldUtil.getAsMapList(taskInputOptions, TaskInputOptionsContext.class);
                    // insert Task Input Options
                    V3Util.createRecordList(module, taskInputOptionMapList, null, null);
                }
            }
        }


        return false;
    }
}
