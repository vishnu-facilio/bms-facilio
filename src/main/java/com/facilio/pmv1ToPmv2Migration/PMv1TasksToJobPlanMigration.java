package com.facilio.pmv1ToPmv2Migration;

import com.facilio.accounts.dto.Role;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.templates.TaskSectionTemplate;
import com.facilio.bmsconsole.templates.TaskTemplate;
import com.facilio.bmsconsole.templates.WorkorderTemplate;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsoleV3.context.V3SpaceCategoryContext;
import com.facilio.bmsconsoleV3.context.V3TaskContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetCategoryContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanTaskSectionContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanTasksContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.EnumField;
import com.facilio.modules.fields.EnumFieldValue;
import com.facilio.v3.util.V3Util;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.json.simple.JSONObject;

import java.util.*;

/**
 * This command converts the PMv1 Tasks to a JobPlan/JobPlan Task Section/JobPlan Task
 *
 * Yet to handle:
 * - Task level resource mapping.
 * - Prerequisites
 */
@Log4j
public class PMv1TasksToJobPlanMigration extends FacilioCommand {

    List<Long> pmV1Ids;
    public static final int JOB_PLAN_NAME_LENGTH = 100;

    public PMv1TasksToJobPlanMigration(List<Long> pmV1Ids) {
        this.pmV1Ids = pmV1Ids;
        if (this.pmV1Ids == null) {
            this.pmV1Ids = new ArrayList<>();
        }
    }


    @Override
    public boolean executeCommand(Context context) throws Exception {

        // Have migration commands for each org
        // Transaction is only org level. If failed, have to continue from the last failed org and not from first
        // write code here
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
//        User user = AccountUtil.getCurrentUser();
//        Role role = user.getRole();
//        Boolean isPrevileged = role.isPrevileged();
//
//        if(!isPrevileged){
//            LOGGER.error("Process failed to create jobPlan as user isn't privileged user.");
//            return false;
//        }

        FacilioModule jobPlanModule = modBean.getModule("jobplan");
        boolean isFailed = false;

        if(CollectionUtils.isEmpty(pmV1Ids)){
            pmV1Ids = (ArrayList<Long>)context.getOrDefault("pmV1Ids", new ArrayList<>());
        }
        //long pmId = 14;
        String jobPlanFormName = "default_jobplan_web_maintenance";
        for (Long pmId : pmV1Ids) {
            PreventiveMaintenance pmv1 = PreventiveMaintenanceAPI.getPM(pmId, true);
            if (pmv1 != null) {
                LOGGER.info("Migrating from pmv1 #" + pmv1.getId() + ": " + pmv1.getName());
                WorkorderTemplate workorderTemplate = (WorkorderTemplate) TemplateAPI.getTemplate(pmv1.getTemplateId());
                if (workorderTemplate != null) {

                    // Create JobPlan
                    JobPlanContext jobPlanContext = new JobPlanContext();
                    jobPlanContext.setName("JP - " + pmv1.getName());
                    if(jobPlanContext.getName().length() > 100){
                        jobPlanContext.setName(jobPlanContext.getName().substring(0, JOB_PLAN_NAME_LENGTH));
                    }
                    jobPlanContext.setJpStatusEnum(JobPlanContext.JPStatus.IN_ACTIVE);
                    switch (pmv1.getAssignmentTypeEnum()) {
                        case ASSET_CATEGORY:
                            jobPlanContext.setJobPlanCategory(JobPlanContext.JPScopeAssignmentType.ASSETCATEGORY.getVal());
                            V3AssetCategoryContext assetCategoryContext = V3RecordAPI.getRecord("assetcategory", pmv1.getAssetCategoryId(), V3AssetCategoryContext.class);
                            jobPlanContext.setAssetCategory(assetCategoryContext);
                            break;
                        case SPACE_CATEGORY:
                            jobPlanContext.setJobPlanCategory(JobPlanContext.JPScopeAssignmentType.SPACECATEGORY.getVal());
                            V3SpaceCategoryContext spaceCategoryContext = V3RecordAPI.getRecord("spacecategory", pmv1.getSpaceCategoryId(), V3SpaceCategoryContext.class);
                            jobPlanContext.setSpaceCategory(spaceCategoryContext);
                            break;
                        case ALL_BUILDINGS:
                            jobPlanContext.setJobPlanCategory(JobPlanContext.JPScopeAssignmentType.BUILDINGS.getVal());
                            break;
                        case ALL_SITES:
                            jobPlanContext.setJobPlanCategory(JobPlanContext.JPScopeAssignmentType.SITES.getVal());
                            break;
                        case ALL_FLOORS:
                            jobPlanContext.setJobPlanCategory(JobPlanContext.JPScopeAssignmentType.FLOORS.getVal());
                            break;
                        case CURRENT_ASSET:
                        case SPECIFIC_ASSET:
                            // Not possible
                            break;
                    }

                    // Set the default formId
                    FacilioForm jobPlanDefaultForm = FormsAPI.getFormsFromDB("jobplan", jobPlanFormName);
                    if (jobPlanDefaultForm == null) {
                        isFailed = true;
                        LOGGER.error("Process failed as the jobPlan form is null");
                        break;
                    }
                    jobPlanContext.setFormId(jobPlanDefaultForm.getId());
                    LOGGER.info("Setting JobPlan Form ID: " + jobPlanDefaultForm.getId());

                    // Construct JobPlanSection/JobPlanTask
                    List<JobPlanTaskSectionContext> jobPlanTaskSectionContextList = new ArrayList<>();

                    // Iterate sectionTemplates to convert it to JobPlanTaskSection
                    List<TaskSectionTemplate> sectionTemplates = workorderTemplate.getSectionTemplates();
                    if (CollectionUtils.isEmpty(sectionTemplates)) {
                        LOGGER.error("sectionTemplates are empty for PMv1 " + pmv1.getId());
                        LOGGER.error("JobPlan not created as sectionTemplates is empty for PMv1" + pmv1.getId());
                        continue;
                    }

                    LOGGER.info("sectionTemplates: " + sectionTemplates.size());
                    int sectionSequenceNumber = 1;
                    for (TaskSectionTemplate sectionTemplate : sectionTemplates) {

                        /** Start of JobPlan Section Construction **/
                        JobPlanTaskSectionContext jobPlanTaskSectionContext = new JobPlanTaskSectionContext();
                        jobPlanTaskSectionContext.setSequenceNumber(sectionSequenceNumber++);
                        jobPlanTaskSectionContext.setName(sectionTemplate.getName());
                        jobPlanTaskSectionContext.setJobPlanSectionCategory(sectionTemplate.getAssignmentType());

                        // Sanitize jobPlanTaskSectionAdditionalInfo
                        JSONObject jobPlanTaskSectionAdditionalInfo = sectionTemplate.getAdditionInfo();
                        Map<String, Object> additionalInfoMap = new HashMap<>(jobPlanTaskSectionAdditionalInfo);
                        for (Map.Entry<String, Object> entry : additionalInfoMap.entrySet()) {
                            if (entry.getValue() == null) {
                                jobPlanTaskSectionAdditionalInfo.remove(entry.getKey());
                            }
                            if (entry.getValue() instanceof List && ((List<?>) entry.getValue()).size() == 0) {
                                jobPlanTaskSectionAdditionalInfo.remove(entry.getKey());
                            }
                        }

                        jobPlanTaskSectionContext.setAdditionInfo(jobPlanTaskSectionAdditionalInfo);
                        jobPlanTaskSectionContext.setAdditionalInfoJsonStr(jobPlanTaskSectionAdditionalInfo.toJSONString());

                        // Set inputType to additionalInfo
                        if (sectionTemplate.getInputTypeEnum() == null) {
                            jobPlanTaskSectionContext.setInputType(V3TaskContext.InputType.NONE.getVal());
                        } else {
                            jobPlanTaskSectionContext.setInputType(sectionTemplate.getInputType());
                            // enabling the enableInput field
                            switch (sectionTemplate.getInputTypeEnum()) {
                                case TEXT:
                                case NUMBER:
                                case RADIO:
                                    JSONObject json = jobPlanTaskSectionContext.getAdditionInfo();
                                    json.put("enableInput", true);
                                    jobPlanTaskSectionContext.setAdditionalInfoJsonStr(json.toJSONString());
                                    jobPlanTaskSectionContext.setEnableInput(true);
                                    break;
                            }

                            // Handle options input type for section - no reading in JobPlan-section
                            switch (sectionTemplate.getInputTypeEnum()) {
                                case RADIO:
                                    List<String> optionValues = new ArrayList<>();
                                    for (Map.Entry<String, Object> entry : additionalInfoMap.entrySet()) {
                                        if (Objects.equals(entry.getKey(), "options")) {
                                            optionValues = (List<String>) entry.getValue();
                                        }
                                    }
                                    if(CollectionUtils.isNotEmpty(optionValues)){
                                        List<Map<String, Object>> inputOptions = new ArrayList<>();
                                        int sequence = 1;
                                        for (String optionStr : optionValues) {
                                            LinkedHashMap<String, Object> option = new LinkedHashMap<>();
                                            option.put("sequence", sequence++);
                                            option.put("value", optionStr);
                                            inputOptions.add(option);
                                        }
                                        jobPlanTaskSectionContext.setInputOptions(inputOptions);
                                    }
                                    break;
                            }
                        }

                        PreventiveMaintenance.PMAssignmentType sectionAssignmentType = PreventiveMaintenance.PMAssignmentType.valueOf(sectionTemplate.getAssignmentType());
                        switch (jobPlanContext.getJobPlanCategoryEnum()) {
                            case SITES:
                                switch (sectionAssignmentType) {
                                    case CURRENT_ASSET:
                                        jobPlanTaskSectionContext.setJobPlanSectionCategory(PreventiveMaintenance.PMAssignmentType.CURRENT_ASSET.getVal());
                                        break;
                                    case ASSET_CATEGORY:
                                        jobPlanTaskSectionContext.setJobPlanSectionCategory(PreventiveMaintenance.PMAssignmentType.ASSET_CATEGORY.getVal());
                                        V3AssetCategoryContext assetCategoryContext = new V3AssetCategoryContext();
                                        assetCategoryContext.setId(sectionTemplate.getAssetCategoryId());
                                        jobPlanTaskSectionContext.setAssetCategory(assetCategoryContext);
                                        break;
                                    case SPACE_CATEGORY:
                                        jobPlanTaskSectionContext.setJobPlanSectionCategory(PreventiveMaintenance.PMAssignmentType.SPACE_CATEGORY.getVal());
                                        V3SpaceCategoryContext spaceCategoryContext = new V3SpaceCategoryContext();
                                        spaceCategoryContext.setId(sectionTemplate.getSpaceCategoryId());
                                        jobPlanTaskSectionContext.setSpaceCategory(spaceCategoryContext);
                                        break;
                                    case ALL_BUILDINGS:
                                        jobPlanTaskSectionContext.setJobPlanSectionCategory(PreventiveMaintenance.PMAssignmentType.ALL_BUILDINGS.getVal());
                                        break;
                                    case ALL_FLOORS:
                                        jobPlanTaskSectionContext.setJobPlanSectionCategory(PreventiveMaintenance.PMAssignmentType.ALL_FLOORS.getVal());
                                        break;
                                }
                                break;
                            case ASSETCATEGORY:
                                switch (sectionAssignmentType) {
                                    case CURRENT_ASSET:
                                    default:
                                        jobPlanTaskSectionContext.setJobPlanSectionCategory(PreventiveMaintenance.PMAssignmentType.CURRENT_ASSET.getVal());
                                        break;
                                }
                                break;
                            case SPACECATEGORY:
                                switch (sectionAssignmentType) {
                                    case ASSET_CATEGORY:
                                        jobPlanTaskSectionContext.setJobPlanSectionCategory(PreventiveMaintenance.PMAssignmentType.ASSET_CATEGORY.getVal());
                                        V3AssetCategoryContext assetCategoryContext = new V3AssetCategoryContext();
                                        assetCategoryContext.setId(sectionTemplate.getAssetCategoryId());
                                        jobPlanTaskSectionContext.setAssetCategory(assetCategoryContext);
                                        break;
                                    case CURRENT_ASSET:
                                    default:
                                        jobPlanTaskSectionContext.setJobPlanSectionCategory(PreventiveMaintenance.PMAssignmentType.CURRENT_ASSET.getVal());
                                        break;
                                }
                                break;
                            case BUILDINGS:
                            case FLOORS:
                                switch (sectionAssignmentType) {
                                    case CURRENT_ASSET:
                                        jobPlanTaskSectionContext.setJobPlanSectionCategory(PreventiveMaintenance.PMAssignmentType.CURRENT_ASSET.getVal());
                                        break;
                                    case ASSET_CATEGORY:
                                        jobPlanTaskSectionContext.setJobPlanSectionCategory(PreventiveMaintenance.PMAssignmentType.ASSET_CATEGORY.getVal());
                                        V3AssetCategoryContext assetCategoryContext = new V3AssetCategoryContext();
                                        assetCategoryContext.setId(sectionTemplate.getAssetCategoryId());
                                        jobPlanTaskSectionContext.setAssetCategory(assetCategoryContext);
                                        break;
                                    case SPACE_CATEGORY:
                                        jobPlanTaskSectionContext.setJobPlanSectionCategory(PreventiveMaintenance.PMAssignmentType.SPACE_CATEGORY.getVal());
                                        V3SpaceCategoryContext spaceCategoryContext = new V3SpaceCategoryContext();
                                        spaceCategoryContext.setId(sectionTemplate.getSpaceCategoryId());
                                        jobPlanTaskSectionContext.setSpaceCategory(spaceCategoryContext);
                                        break;
                                }
                                break;
                            case ASSETS:
                            case SPACES:
                                // Not possible
                                break;
                        }

                        //Photo mandatory
                        jobPlanTaskSectionContext.setAttachmentRequired(sectionTemplate.isAttachmentRequired());

                        LOGGER.info("JobPlanTaskSection additionalInfo: " + jobPlanTaskSectionContext.getAdditionalInfoJsonStr());
                        /** End of JobPlan Section Construction **/

                        // Construct JobPlan tasks
                        //TODO: Task level resourse association is not done yet as it isn't supported in JobPlan
                        int taskSequenceNumber = 1;
                        List<JobPlanTasksContext> jobPlanTasksContextList = new ArrayList<>();
                        if (CollectionUtils.isEmpty(sectionTemplate.getTaskTemplates())) {
                            isFailed = true;
                            break;
                        }
                        LOGGER.info("taskTemplates: " + sectionTemplate.getTaskTemplates().size());
                        for (TaskTemplate taskTemplate : sectionTemplate.getTaskTemplates()) {
                            /** Start of JobPlan Task Construction **/

                            JobPlanTasksContext jobPlanTasksContext = taskTemplate.getAsJobPlanTask();
                            jobPlanTasksContext.setSequence(taskSequenceNumber);
                            jobPlanTasksContext.setUniqueId(taskSequenceNumber);
                            jobPlanTasksContext.setStatusNew(V3TaskContext.TaskStatus.OPEN.getValue());
                            // Reset Section ID
                            jobPlanTasksContext.setSectionId(null);


                            // sanitize jobPlanTaskSectionAdditionalInfo
                            JSONObject jobPlanTaskAdditionalInfo = jobPlanTasksContext.getAdditionInfo();
                            Map<String, Object> additionalInfoMap_ = new HashMap<>(jobPlanTaskAdditionalInfo);
                            for (Map.Entry<String, Object> entry : additionalInfoMap_.entrySet()) {
                                if (entry.getValue() == null) {
                                    jobPlanTaskAdditionalInfo.remove(entry.getKey());
                                }
                            }
                            jobPlanTasksContext.setAdditionInfo(jobPlanTaskAdditionalInfo);
                            jobPlanTasksContext.setAdditionalInfoJsonStr(jobPlanTaskAdditionalInfo.toJSONString());

                            if (taskTemplate.getInputTypeEnum() == null) {
                                jobPlanTasksContext.setInputType(V3TaskContext.InputType.NONE.getVal());
                            } else {
                                jobPlanTasksContext.setInputType(taskTemplate.getInputType());

                                // null the readingField ID
                                switch (taskTemplate.getInputTypeEnum()) {
                                    case NONE:
                                    case TEXT:
                                    case NUMBER:
                                    case RADIO:
                                        jobPlanTasksContext.setReadingFieldId(null);
                                        jobPlanTasksContext.setReadingField(null);
                                        break;
                                }

                                // enabling the enableInput field
                                switch (taskTemplate.getInputTypeEnum()) {
                                    case TEXT:
                                    case NUMBER:
                                    case RADIO:
                                    case READING:
                                        JSONObject json = jobPlanTasksContext.getAdditionInfo();
                                        json.put("enableInput", true);
                                        jobPlanTasksContext.setAdditionalInfoJsonStr(json.toJSONString());
                                        jobPlanTasksContext.setEnableInput(true);
                                        break;
                                }

                                // Handle options and reading input types
                                switch (taskTemplate.getInputTypeEnum()) {
                                    case RADIO:
                                        EnumField optionField = (EnumField) modBean.getField(taskTemplate.getReadingFieldId());
                                        List<EnumFieldValue<Integer>> optionValues = optionField.getValues();
                                        List<Map<String, Object>> inputOptions = new ArrayList<>();
                                        for (EnumFieldValue<Integer> enumOption : optionValues) {
                                            LinkedHashMap<String, Object> option = new LinkedHashMap<>();
                                            option.put("sequence", enumOption.getSequence());
                                            option.put("value", enumOption.getValue());
                                            inputOptions.add(option);
                                        }
                                        jobPlanTasksContext.setInputOptions(inputOptions);
                                        break;
                                    case READING:
                                        //TODO: Special Handling for ReadingField can be done here
                                        break;
                                }
                            }

                            if (jobPlanTasksContext.getRemarksRequired() != null && jobPlanTasksContext.getRemarksRequired() && jobPlanTasksContext.getRemarkOptionValuesString() != null) {
                                JSONObject json = jobPlanTaskSectionContext.getAdditionInfo();
                                json.put("remarkOption", "specific");
                                jobPlanTasksContext.setAdditionalInfoJsonStr(json.toJSONString());
                            } else if (jobPlanTasksContext.getRemarksRequired() != null && jobPlanTasksContext.getRemarksRequired()) {
                                JSONObject json = jobPlanTaskSectionContext.getAdditionInfo();
                                json.put("remarkOption", "all");
                                jobPlanTasksContext.setAdditionalInfoJsonStr(json.toJSONString());
                            }

                            LOGGER.info(pmv1.getId() + ": JobPlanTask additionalInfo: " + jobPlanTasksContext.getAdditionalInfoJsonStr());

                            // ALl the JobPlan Task goes as CURRENT_ASSET
                            jobPlanTasksContext.setJobPlanTaskCategory(PreventiveMaintenance.PMAssignmentType.CURRENT_ASSET.getVal());


                            taskSequenceNumber++;
                            jobPlanTasksContextList.add(jobPlanTasksContext);

                            /** End of JobPlan Section Construction **/
                        }

                        // Add JobPlan Task list to Task Section and Add JobPlan Task Section to list
                        jobPlanTaskSectionContext.setTasks(jobPlanTasksContextList);
                        jobPlanTaskSectionContextList.add(jobPlanTaskSectionContext);
                    }

                    // Set JobPlanTaskSection to JobPlan
                    jobPlanContext.setJobplansection(jobPlanTaskSectionContextList);
                    if (!isFailed) {
                        FacilioContext jobPlanCreationContext = V3Util.createRecord(jobPlanModule, FieldUtil.getAsProperties(jobPlanContext));

                        Map<String, Object> changeSetMap = (Map<String, Object>) jobPlanCreationContext.get("changeSetMap");
                        if (MapUtils.isNotEmpty(changeSetMap)) {
                            Map<Number, Object> jobPlanMap = (Map<Number, Object>) changeSetMap.get("jobplan");
                            if (MapUtils.isNotEmpty(jobPlanMap)) {
                                Long jobPlanId = (Long) new ArrayList<>(jobPlanMap.keySet()).get(0);
                                if(jobPlanId != null) {
                                    LOGGER.info("JobPlan created for Pmv1: " + pmv1 + " with ID #" + jobPlanId);
                                }else{
                                    LOGGER.error("2. JobPlan created for Pmv1: " + pmv1);
                                }
                            } else {
                                LOGGER.error("3. JobPlan created for Pmv1: " + pmv1);
                            }
                        } else {
                            LOGGER.info("JobPlan created for Pmv1: " + pmv1);
                        }
                    } else {
                        LOGGER.error("JobPlan not created as the process has failed.");
                    }
                } else {
                    LOGGER.error("WorkOrder Template is null");
                }
            }else {
                LOGGER.error("Not a valid PM: " + pmId);
            }
        }
        return false;
    }

}
