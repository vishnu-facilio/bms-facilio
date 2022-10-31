package com.facilio.bmsconsoleV3.commands.jobplan;

import com.facilio.bmsconsoleV3.context.jobplan.JobPlanTaskSectionContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanTasksContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

/**
 * FillUpJobPlanTaskAdditionInfoObject fills the required properties into additionInfo JSON object, and clears the unwanted properties.
 */
public class FillUpJobPlanTaskAdditionInfoObject extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<JobPlanTaskSectionContext> taskSections = recordMap.get(FacilioConstants.ContextNames.JOB_PLAN_SECTION);

        if (CollectionUtils.isNotEmpty(taskSections)) {
            for (JobPlanTaskSectionContext section : taskSections) {

                List<JobPlanTasksContext> taskList = section.getTasks();
                if (CollectionUtils.isEmpty(taskList)) {
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Task list is mandatory for a task section");
                }

                for (JobPlanTasksContext task : taskList) {
                    // handle task additionInfo
                    handleTaskAdditionInfo(task);

                }
                section.setTasks(taskList);
            }

            recordMap.put(FacilioConstants.ContextNames.JOB_PLAN_SECTION, taskSections);
        }
        return false;
    }

    // Helper function to add/remove the additionInfo properties
    private void handleTaskAdditionInfo(JobPlanTasksContext task) {
        // handle enableInput
        if (task.getEnableInput() != null) {
            task.addAdditionInfo("enableInput", task.getEnableInput());
        }

        // handle remarkOption
        if (task.getRemarkOption() != null) {
            task.addAdditionInfo("remarkOption", task.getRemarkOption());
        }

        // handle attachmentOption
        if (task.getAttachmentOption() != null) {
            task.addAdditionInfo("attachmentOption", task.getAttachmentOption());
        }

        // handle createWoOnFailure
        if (task.getCreateWoOnFailure() != null) {
            task.addAdditionInfo("createWoOnFailure", task.getCreateWoOnFailure());
        }

        // handle woCreateFormId, if it isn't available remove it.
        if (task.getWoCreateFormId() != null && task.getWoCreateFormId() > 0 && task.getCreateWoOnFailure()) {
            task.addAdditionInfo("woCreateFormId", task.getWoCreateFormId());
        } else {
            task.getAdditionInfo().remove("woCreateFormId");
        }

        // handle options
        if (task.getOptions() != null) {
            task.addAdditionInfo("options", task.getOptions());
        }

        // handle attachmentOption, if it isn't available remove it.
        if (task.getAttachmentOption() != null && task.getAttachmentRequired()) {
            task.addAdditionInfo("attachmentOption", task.getAttachmentOption());
        } else {
            task.getAdditionInfo().remove("attachmentOption");
        }

        // handle remarkOption, if it isn't available remove it.
        if (task.getRemarkOption() != null && task.getRemarksRequired()) {
            task.addAdditionInfo("remarkOption", task.getRemarkOption());
        } else {
            task.getAdditionInfo().remove("remarkOption");
        }

        // handle validation, if it isn't available remove it.
        if (task.getValidation() != null) {
            task.addAdditionInfo("validation", task.getValidation());
        } else {
            task.getAdditionInfo().remove("validation");
        }

        // handle minSafeLimit, if it isn't available remove it.
        if (task.getMinSafeLimit() != null && task.getValidation() != null) {
            task.addAdditionInfo("minSafeLimit", task.getMinSafeLimit());
        } else {
            task.getAdditionInfo().remove("minSafeLimit");
        }

        // handle maxSafeLimit, if it isn't available remove it.
        if (task.getMaxSafeLimit() != null && task.getValidation() != null) {
            task.addAdditionInfo("maxSafeLimit", task.getMaxSafeLimit());
        } else {
            task.getAdditionInfo().remove("maxSafeLimit");
        }
    }
}
