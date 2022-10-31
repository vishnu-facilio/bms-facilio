package com.facilio.bmsconsoleV3.commands.tasks;

import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanTaskSectionContext;
import com.facilio.bmsconsoleV3.context.tasks.SectionInputOptionsContext;
import com.facilio.bmsconsoleV3.context.tasks.TaskInputOptionsContext;
import com.facilio.bmsconsoleV3.util.JobPlanAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

/**
 * AddJobPlanSectionInputOptions gets the List of Sections and iterates over it to add the Input Options into
 * JobPlan_Section_Input_Options table if INPUT TYPE IS RADIO.
 *
 * Optimization:
 *      Deleting the record and inserting it keep on changing the ID of input option. So try updating the record
 *      values when count of the records are equal, and delete-insert only if the count changes.
 */
public class AddJobPlanSectionInputOptions extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<JobPlanTaskSectionContext> taskSections = recordMap.get(FacilioConstants.ContextNames.JOB_PLAN_SECTION);

        if (taskSections == null || taskSections.isEmpty()) {
            return false;
        }

        FacilioModule module = ModuleFactory.getJobPlanSectionInputOptionsModule();

        // iterate sections
        for (JobPlanTaskSectionContext section : taskSections) {

            if (section.getInputTypeEnum() == TaskContext.InputType.RADIO) {

                if (section.getInputOptions() != null && !section.getInputOptions().isEmpty()) {

                    List<SectionInputOptionsContext> sectionInputOptions = FieldUtil.getAsBeanListFromMapList(section.getInputOptions(),
                            SectionInputOptionsContext.class);
                    for (SectionInputOptionsContext inputOption : sectionInputOptions) {
                        inputOption.setJobPlanSection(section);
                    }

                    //V3Util.deleteRecords(module.getName(), deleteRecordMap, null, null, false); - delete not works
                    //V3RecordAPI.deleteRecordsById(module.getName(), deleteRecordIds); - works, but doesn't work when we remove few options and update.

                    // delete the JobPlan Section Input Options.
                    JobPlanAPI.deleteJobPlanSectionInputOptions(section.getId());

                    List<Map<String, Object>> sectionInputOptionMapList = FieldUtil.getAsMapList(sectionInputOptions, TaskInputOptionsContext.class);
                    // insert Section Input Options
                    V3Util.createRecordList(module, sectionInputOptionMapList, null, null);
                }
            }
        }


        return false;
    }
}
