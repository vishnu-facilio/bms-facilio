package com.facilio.bmsconsoleV3.commands.jobplan;

import com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext;
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
 * FillUpJobPlanSectionAdditionInfoObject fills the required properties into additionInfo JSON object,
 * and clears the unwanted properties.
 */
public class FillUpJobPlanSectionAdditionInfoObject extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<JobPlanContext> jobPlans = recordMap.get(FacilioConstants.ContextNames.JOB_PLAN);

        if (CollectionUtils.isNotEmpty(jobPlans)) {

            for (JobPlanContext jobPlan : jobPlans) {
                List<JobPlanTaskSectionContext> taskSections = jobPlan.getJobplansection();

                if (CollectionUtils.isNotEmpty(taskSections)) {

                    List<Map<String, Object>> sectionMapList = new ArrayList<>();
                    for (JobPlanTaskSectionContext section : taskSections) {
                        List<JobPlanTasksContext> taskList = FieldUtil.getAsBeanListFromMapList(section.getTasks(), JobPlanTasksContext.class);

                        if (CollectionUtils.isEmpty(taskList)) {
                            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Task list is mandatory for a task section");
                        }

                        // handle section additionInfo
                        handleSectionAdditionInfo(section);

                        // convert section as Map and add to sectionMapList
                        HashMap<String, Object> sectionMap = (HashMap<String, Object>)
                                FieldUtil.getAsMapList(
                                                Collections.singletonList(section),
                                                JobPlanTaskSectionContext.class)
                                        .get(0);// get first item, as we send only one section at a time.
                        sectionMapList.add(sectionMap);
                    }

                    // add sectionMapList to JobPlan's SubForm. SubForm is used to update the JobPlanSection table.
                    Map<String, List<Map<String, Object>>> subFormMap = new HashMap<>();
                    subFormMap.put("jobplansection", sectionMapList);
                    jobPlan.setSubForm(subFormMap);
                }
            }

            recordMap.put(FacilioConstants.ContextNames.JOB_PLAN, jobPlans);
        }
        return false;
    }

    // Helper function to add/remove the additionInfo properties
    private void handleSectionAdditionInfo(JobPlanTaskSectionContext section) throws Exception {
        // handle enableInput
        if (section.getEnableInput() != null) {
            section.addAdditionInfo("enableInput", section.getEnableInput());
        }

        // handle remarkOption
        if (section.getRemarkOption() != null) {
            section.addAdditionInfo("remarkOption", section.getRemarkOption());
        }

        // handle attachmentOption
        if (section.getAttachmentOption() != null) {
            section.addAdditionInfo("attachmentOption", section.getAttachmentOption());
        }

        // handle createWoOnFailure
        if (section.getCreateWoOnFailure() != null) {
            section.addAdditionInfo("createWoOnFailure", section.getCreateWoOnFailure());
        }

        // handle woCreateFormId, if it isn't available remove it.
        if (section.getWoCreateFormId() != null && section.getWoCreateFormId() > 0 && section.getCreateWoOnFailure()) {
            section.addAdditionInfo("woCreateFormId", section.getWoCreateFormId());
        } else {
            section.getAdditionInfo().remove("woCreateFormId");
        }

        // handle options
        if (section.getOptions() != null) {
            section.addAdditionInfo("options", section.getOptions());
        }

        // handle attachmentOption, if it isn't available remove it.
        if (section.getAttachmentOption() != null && section.getAttachmentRequired()) {
            section.addAdditionInfo("attachmentOption", section.getAttachmentOption());
        } else {
            section.getAdditionInfo().remove("attachmentOption");
        }

        // handle attachmentOptionValues, if it isn't available remove it.
        if (section.getAttachmentOptionValues() != null && section.getAttachmentRequired()) {
            section.addAdditionInfo("attachmentOptionValues", section.getAttachmentOptionValues());
        } else {
            section.getAdditionInfo().remove("attachmentOptionValues");
        }

        // handle remarkOption, if it isn't available remove it.
        if (section.getRemarkOption() != null && section.getRemarksRequired()) {
            section.addAdditionInfo("remarkOption", section.getRemarkOption());
        } else {
            section.getAdditionInfo().remove("remarkOption");
        }

        // handle remarkOptionValues, if it isn't available remove it.
        if (section.getRemarkOptionValues() != null && section.getRemarksRequired()) {
            section.addAdditionInfo("remarkOptionValues", section.getRemarkOptionValues());
        } else {
            section.getAdditionInfo().remove("remarkOptionValues");
        }

        // handle deviationOperatorId, if it isn't available remove it.
        if (section.getDeviationOperatorId() != null && section.getDeviationOperatorId() > 0 && section.getDeviationOperator() != null) {
            section.addAdditionInfo("deviationOperatorId", section.getDeviationOperatorId());
        } else {
            section.getAdditionInfo().remove("deviationOperatorId");
        }

        // handle validation, if it isn't available remove it.
        if (section.getValidation() != null) {
            section.addAdditionInfo("validation", section.getValidation());
        } else {
            section.getAdditionInfo().remove("validation");
        }

        // handle defaultValue, if it isn't available remove it.
        if (section.getDefaultValue() != null) {
            section.addAdditionInfo("defaultValue", section.getDefaultValue());
        } else {
            section.getAdditionInfo().remove("defaultValue");
        }

        // handle failureValue, if it isn't available remove it.
        if (section.getFailureValue() != null) {
            section.addAdditionInfo("failureValue", section.getFailureValue());
        } else {
            section.getAdditionInfo().remove("failureValue");
        }
    }
}
