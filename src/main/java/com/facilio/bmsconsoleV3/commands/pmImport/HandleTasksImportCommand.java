package com.facilio.bmsconsoleV3.commands.pmImport;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PMJobPlan;
import com.facilio.bmsconsoleV3.context.V3TaskContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanTaskSectionContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanTasksContext;
import com.facilio.command.FacilioCommand;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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

        Map<Long, List<Map<String, Object>>> recordsClassifiedByPM = Util.classifyByPM(records);

        processRecords(recordsClassifiedByPM);

        return false;
    }

    private void processRecords(Map<Long, List<Map<String, Object>>> recordsClassifiedByPM) throws Exception {
        // for every batch of PMs.
        for (Map.Entry<Long, List<Map<String, Object>>> entry : recordsClassifiedByPM.entrySet()) {
            Long pmID = entry.getKey();
            List<Map<String, Object>> pmList = entry.getValue();

            List<Map<String, Object>> prerequisiteTasks = getPrerequisiteTasks(pmList);
            createPreRequisiteJobPlan(pmID, prerequisiteTasks);

            List<Map<String, Object>> tasks = getTasks(pmList);
            createJobPlan(pmID, tasks);
        }
    }

    private List<Map<String, Object>> getPrerequisiteTasks(List<Map<String, Object>> recList) {
        return recList.stream().
                filter(rec -> FacilioUtil.parseBoolean(rec.get("isPrerequisite"))).
                collect(Collectors.toList());
    }


    private List<Map<String, Object>> getTasks(List<Map<String, Object>> recList) {
        return recList.stream().
                filter(rec -> !FacilioUtil.parseBoolean(rec.get("isPrerequisite"))).
                collect(Collectors.toList());
    }

    private void createJobPlan(Long pmID, List<Map<String, Object>> records) throws Exception {
        if (records.size() == 0) {
            LOGGER.info("no tasks for PM " + pmID + "; skipping");
            return;
        }

        PMJobPlan jobPlan = new PMJobPlan();
        jobPlan.setPmId(pmID);
        jobPlan.setPreRequisite(false);
        jobPlan.setName("pm-" + pmID + "-import");

        final String jobPlanModuleName = "pmJobPlan";
        jobPlan = (PMJobPlan) Util.persistModuleRecord(jobPlanModuleName, jobPlan);

        Map<String, List<Map<String, Object>>> recordsClassifiedBySection = classifyBySection(records);
        createSectionAndTasks(jobPlan, recordsClassifiedBySection);
    }

    private void createPreRequisiteJobPlan(Long pmID, List<Map<String, Object>> records) throws Exception {
        if (records.size() == 0) {
            LOGGER.info("no prerequisites for PM " + pmID + "; skipping");
            return;
        }
        PMJobPlan jobPlan = new PMJobPlan();
        jobPlan.setPmId(pmID);
        jobPlan.setPreRequisite(true);
        jobPlan.setName("pm-" + pmID + "-import");

        final String jobPlanModuleName = "pmJobPlan";
        jobPlan = (PMJobPlan) Util.persistModuleRecord(jobPlanModuleName, jobPlan);

        Map<String, List<Map<String, Object>>> recordsClassifiedBySection = classifyBySection(records);
        createSectionAndTasks(jobPlan, recordsClassifiedBySection);
    }

    private void createSectionAndTasks(PMJobPlan jobPlan, Map<String, List<Map<String, Object>>> recordsClassifiedBySection) throws Exception {
        for (Map.Entry<String, List<Map<String, Object>>> entry : recordsClassifiedBySection.entrySet()) {
            String sectionName = entry.getKey();
            List<Map<String, Object>> recList = entry.getValue();

            JobPlanTaskSectionContext section = createSection(jobPlan, sectionName);
            createTasks(jobPlan, section, recList);
        }
    }

    private JobPlanTaskSectionContext createSection(PMJobPlan jobPlan, String sectionName) throws Exception {
        JobPlanTaskSectionContext jpSection = new JobPlanTaskSectionContext();
        jpSection.setName(sectionName);
        jpSection.setJobPlan(jobPlan);

        final String jpSectionModuleName = "jobplansection";
        return (JobPlanTaskSectionContext) Util.persistModuleRecord(jpSectionModuleName, jobPlan);
    }

    private void createTasks(PMJobPlan jobPlan, JobPlanTaskSectionContext section, List<Map<String, Object>> recList) throws Exception {

        List<Map<String, Object>> mutatedTaskList = new ArrayList<>();

        for (Map<String, Object> rec : recList) {
            JobPlanTasksContext jpTask = new JobPlanTasksContext();
            jpTask.setTaskSection(section);
            jpTask.setJobPlan(jobPlan);
            jpTask.setInputType(1);
            jpTask.setStatusNew(V3TaskContext.TaskStatus.OPEN.getValue());
            jpTask.setCreatedBy(AccountUtil.getCurrentUser());
            jpTask.setCreatedTime(System.currentTimeMillis());

            Object subjectObj = rec.get("jpTaskSubject");
            if (subjectObj != null) {
                jpTask.setSubject((String) subjectObj);
            }

            Object descriptionObj = rec.get("jpTaskDescription");
            if (descriptionObj != null) {
                jpTask.setDescription((String) descriptionObj);
            }

            mutatedTaskList.add(FieldUtil.getAsProperties(jpTask));
        }

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule jpTaskModule = modBean.getModule("jobplantask");

        V3Util.createRecordList(jpTaskModule, mutatedTaskList, null, null);
    }



    private Map<String, List<Map<String, Object>>> classifyBySection(List<Map<String, Object>> pmList) {
        Map<String, List<Map<String, Object>>> classification = new HashMap<>();
        for (Map<String, Object> rec : pmList) {
            Object sectionNameObj = rec.get("sectionName");
            String sectionName = (String) sectionNameObj;
            if (classification.containsKey(sectionName)) {
                classification.get(sectionName).add(rec);
            } else {
                List<Map<String, Object>> newBin = new ArrayList<>();
                newBin.add(rec);
                classification.put(sectionName, newBin);
            }
        }
        return classification;
    }
}
