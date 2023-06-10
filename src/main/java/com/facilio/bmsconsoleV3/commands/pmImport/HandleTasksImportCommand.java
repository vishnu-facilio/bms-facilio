package com.facilio.bmsconsoleV3.commands.pmImport;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PlannedMaintenance;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsoleV3.context.V3SpaceCategoryContext;
import com.facilio.bmsconsoleV3.context.V3TaskContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetCategoryContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanTaskSectionContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanTasksContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext.JPStatus;
import com.facilio.bmsconsoleV3.context.tasks.TaskInputOptionsContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.logging.Logger;

public class HandleTasksImportCommand extends FacilioCommand {

    private static final Logger LOGGER = Logger.getLogger(HandleTasksImportCommand.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {

        Object recordsObj = context.get("insertRecords");
        if (recordsObj == null) {
            LOGGER.info("insertRecords is null; import will fail");
            return false;
        }
        List<Map<String, Object>> records = (List<Map<String, Object>>) recordsObj;

        Map<String, List<Map<String, Object>>> recordsClassifiedByPM = classifyByJobPlanName(records);

        processRecords(recordsClassifiedByPM);

        return false;
    }

    private void processRecords(Map<String, List<Map<String, Object>>> recordsClassifiedByPM) throws Exception {
        // for every batch of JP.
        for (Map.Entry<String, List<Map<String, Object>>> entry : recordsClassifiedByPM.entrySet()) {
            String jpName = entry.getKey();
            List<Map<String, Object>> tasks = entry.getValue();

            createJobPlan(jpName, tasks);
        }
    }

    private void createJobPlan(String jpName, List<Map<String, Object>> records) throws Exception {
        if (records.size() == 0) {
            LOGGER.info("no tasks for JP " + jpName + "; skipping");
            return;
        }

        Map<String, Object> firstRecord = records.get(0);

        JobPlanContext jobPlan = new JobPlanContext();
        jobPlan.setJpStatusEnum(JPStatus.IN_ACTIVE);
        jobPlan.setName(jpName);

        // setting section scope
        Object categoryObj = firstRecord.get("category");
        if (categoryObj == null) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Category is mandatory for Job Plan");
        }
        jobPlan.setJobPlanCategory(FacilioUtil.parseInt(categoryObj));

        // setting asset category
        Object assetCategoryObj = firstRecord.get("assetCategory");
        if (jobPlan.getJobPlanCategory().equals(PlannedMaintenance.PMScopeAssigmentType.ASSETCATEGORY.getVal())
                && assetCategoryObj == null) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Asset Category is mandatory when Scope is Asset Category");
        }
        if (assetCategoryObj != null) {
            Map<String, Object> catObj = (Map<String, Object>) assetCategoryObj;
            V3AssetCategoryContext cat = new V3AssetCategoryContext();
            cat.setId(FacilioUtil.parseLong(catObj.get("id")));
            jobPlan.setAssetCategory(cat);
        }


        // setting space category
        Object spaceCategoryObj = firstRecord.get("assetCategory");
        if (jobPlan.getJobPlanCategory().equals(PlannedMaintenance.PMScopeAssigmentType.SPACECATEGORY.getVal())
                && spaceCategoryObj == null) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Space Category is mandatory when Scope is Space Category");
        }
        if (spaceCategoryObj != null) {
            Map<String, Object> catObj = (Map<String, Object>) spaceCategoryObj;
            V3SpaceCategoryContext cat = new V3SpaceCategoryContext();
            cat.setId(FacilioUtil.parseLong(catObj.get("id")));
            jobPlan.setSpaceCategory(cat);
        }

        final String jobPlanModuleName = "jobplan";
        jobPlan = (JobPlanContext) Util.persistModuleRecord(jobPlanModuleName, jobPlan);

        Map<String, List<Map<String, Object>>> recordsClassifiedBySection = classifyBySectionName(records);
        createSectionAndTasks(jobPlan, recordsClassifiedBySection);
    }

    private void createSectionAndTasks(JobPlanContext jobPlan, Map<String, List<Map<String, Object>>> recordsClassifiedBySection) throws Exception {
        for (Map.Entry<String, List<Map<String, Object>>> entry : recordsClassifiedBySection.entrySet()) {
            String sectionName = entry.getKey();
            List<Map<String, Object>> recList = entry.getValue();

            Map<String, Object> firstRecord = recList.get(0);
            Double sequence = (Double) firstRecord.get("sectionSequence");
            if(sequence == null){
                throw new IllegalArgumentException("Section Sequence Number is mandatory");
            }
            JobPlanTaskSectionContext section = createSection(jobPlan, sectionName, sequence.intValue(), firstRecord);
            createTasks(jobPlan, section, recList);
        }
    }

    private JobPlanTaskSectionContext createSection(JobPlanContext jobPlan, String sectionName, int seqNumber, Map<String, Object> firstRecord) throws Exception {
        JobPlanTaskSectionContext jpSection = new JobPlanTaskSectionContext();
        jpSection.setName(sectionName);
        jpSection.setJobPlan(jobPlan);
        jpSection.setInputType(TaskContext.InputType.NONE);
        jpSection.setSequenceNumber(seqNumber);

        Integer sectionScope = (Integer) firstRecord.get("sectionScope");
        jpSection.setJobPlanSectionCategory(sectionScope);
        if (sectionScope != null &&
                sectionScope.equals(PreventiveMaintenance.PMAssignmentType.ASSET_CATEGORY.getVal())) {

            // Section's scope is Asset Category
            Map<String, Object> sectionAssetCategory =
                    (Map<String, Object>) firstRecord.get("sectionAssetCategory");
            if (sectionAssetCategory != null) {
                V3AssetCategoryContext cat = new V3AssetCategoryContext();
                cat.setId(FacilioUtil.parseLong(sectionAssetCategory.get("id")));
                jpSection.setAssetCategory(cat);
            }
        }

        if (sectionScope != null &&
                sectionScope.equals(PreventiveMaintenance.PMAssignmentType.SPACE_CATEGORY.getVal())) {
            // Section's scope is Space Category
            Map<String, Object> sectionSpaceCategory =
                    (Map<String, Object>) firstRecord.get("sectionSpaceCategory");
            if (sectionSpaceCategory != null) {
                V3SpaceCategoryContext cat = new V3SpaceCategoryContext();
                cat.setId(FacilioUtil.parseLong(sectionSpaceCategory.get("id")));
                jpSection.setSpaceCategory(cat);
            }
        }

        final String jpSectionModuleName = "jobplansection";
        return (JobPlanTaskSectionContext) Util.persistModuleRecord(jpSectionModuleName, jpSection);
    }

    private void createTasks(JobPlanContext jobPlan, JobPlanTaskSectionContext section, List<Map<String, Object>> recList) throws Exception {

        List<JobPlanTasksContext> tasks = new ArrayList<>();
        List<TaskInputOptionsContext> allTaskInputOptions = new ArrayList<>();

        for (Map<String, Object> rec : recList) {
            Double sequence = (Double) rec.get("taskSequence");
            if(sequence == null){
                throw new IllegalArgumentException("Task Sequence Number is mandatory");
            }

            JobPlanTasksContext jpTask = Util.createJobPlanTask(jobPlan, section, rec, sequence.intValue());


            // setting task scope
            Integer taskScope = (Integer) rec.get("taskScope");
            jpTask.setJobPlanTaskCategory(taskScope);

            if (taskScope != null &&
                    taskScope.equals(PreventiveMaintenance.PMAssignmentType.ASSET_CATEGORY.getVal())) {
                // task scope is Asset Category
                Map<String, Object> taskAssetCategory =
                        (Map<String, Object>) rec.get("taskAssetCategory");
                if (taskAssetCategory != null) {
                    V3AssetCategoryContext cat = new V3AssetCategoryContext();
                    cat.setId(FacilioUtil.parseLong(taskAssetCategory.get("id")));
                    jpTask.setAssetCategory(cat);
                }
            }

            if (taskScope != null &&
                    taskScope.equals(PreventiveMaintenance.PMAssignmentType.SPACE_CATEGORY.getVal())) {
                // Section's scope is Space Category
                Map<String, Object> taskSpaceCategory =
                        (Map<String, Object>) rec.get("taskSpaceCategory");
                if (taskSpaceCategory != null) {
                    V3SpaceCategoryContext cat = new V3SpaceCategoryContext();
                    cat.setId(FacilioUtil.parseLong(taskSpaceCategory.get("id")));
                    jpTask.setSpaceCategory(cat);
                }
            }

            if (jpTask.getInputTypeEnum().equals(V3TaskContext.InputType.RADIO)) {
                List<TaskInputOptionsContext> taskInputOptions = createJpTaskInputOptions(rec);
                for (TaskInputOptionsContext option : taskInputOptions) {
                    option.setJobPlanTask(jpTask);
                }
                allTaskInputOptions.addAll(taskInputOptions);
            }

            tasks.add(jpTask);
        }

        // saving jobPlan tasks
        final String jpTaskModuleName = "jobplantask";
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule jobPlanTaskModule = modBean.getModule(jpTaskModuleName);
        List<FacilioField> fields = modBean.getAllFields(jobPlanTaskModule.getName());
        V3RecordAPI.addRecord(false, tasks, jobPlanTaskModule, fields);

        // saving jobPlan task options
        FacilioModule taskInputMod = ModuleFactory.getJobPlanTaskInputOptionsModule();
        List<Map<String, Object>> taskInputOptionMapList = FieldUtil.getAsMapList(allTaskInputOptions, TaskInputOptionsContext.class);
        V3Util.createRecordList(taskInputMod, taskInputOptionMapList, null, null);
    }

    private List<TaskInputOptionsContext> createJpTaskInputOptions(Map<String, Object> rec) throws Exception {
        List<TaskInputOptionsContext> options = new ArrayList<>();

        Object optionsObj = rec.get("options");
        if (FacilioUtil.isEmptyOrNull(optionsObj)) {
            return options;
        }

        String[] optionValues = explodeAndStrip((String) optionsObj);
        int seq = 0;
        for (String op : optionValues) {
            TaskInputOptionsContext taskOption = new TaskInputOptionsContext();
            taskOption.setLabel(op);
            taskOption.setValue(op);
            taskOption.setSequence(++seq);
            options.add(taskOption);
        }

        return options;
    }

    private String[] explodeAndStrip(String value) {
        String[] explodedValues = StringUtils.split(value, ",");
        return StringUtils.stripAll(explodedValues);
    }

    private Map<String, List<Map<String, Object>>> classifyByJobPlanName(List<Map<String, Object>> records) {
        return classifyByKey("name", records);
    }

    private Map<String, List<Map<String, Object>>> classifyBySectionName(List<Map<String, Object>> records) {
        return classifyByKey("sectionName", records);
    }

    private Map<String, List<Map<String, Object>>> classifyByKey(String key, List<Map<String, Object>> records) {
        Map<String, List<Map<String, Object>>> classification = new HashMap<>();
        for (Map<String, Object> rec : records) {
            String keyValue = (String) rec.get(key);
            if (classification.containsKey(keyValue)) {
                classification.get(keyValue).add(rec);
            } else {
                List<Map<String, Object>> newBin = new ArrayList<>();
                newBin.add(rec);
                classification.put(keyValue, newBin);
            }
        }
        return classification;
    }

}
