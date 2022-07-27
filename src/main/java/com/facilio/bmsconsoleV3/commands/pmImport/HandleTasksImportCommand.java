package com.facilio.bmsconsoleV3.commands.pmImport;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanTaskSectionContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanTasksContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

        Map<Long, List<Map<String, Object>>> recordsClassifiedByPM = Util.classifyByPM(records);

        processRecords(recordsClassifiedByPM);

        return false;
    }

    private void processRecords(Map<Long, List<Map<String, Object>>> recordsClassifiedByPM) throws Exception {
        // for every batch of PMs.
        for (Map.Entry<Long, List<Map<String, Object>>> entry : recordsClassifiedByPM.entrySet()) {
            Long pmID = entry.getKey();
            List<Map<String, Object>> pmList = entry.getValue();

            Map<String, List<Map<String, Object>>> recordsClassifiedBySection = classifyBySection(pmList);
            createSectionAndTasks(pmID, recordsClassifiedBySection);
        }
    }

    private void createSectionAndTasks(Long pmID, Map<String, List<Map<String, Object>>> recordsClassifiedBySection) throws Exception {

        for (Map.Entry<String, List<Map<String, Object>>> entry : recordsClassifiedBySection.entrySet()) {
            String sectionName = entry.getKey();
            List<Map<String, Object>> recList = entry.getValue();

            JobPlanTaskSectionContext section = createSection(pmID, sectionName);
            createTasks(pmID, section, recList);
        }

    }

    private void createTasks(Long pmID, JobPlanTaskSectionContext section, List<Map<String, Object>> recList) throws Exception {

        List<Map<String, Object>> mutatedTaskList = new ArrayList<>();

        for (Map<String, Object> rec : recList) {
            JobPlanTasksContext jpTask = new JobPlanTasksContext();
            jpTask.setTaskSection(section);

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

    private JobPlanTaskSectionContext createSection(Long pmID, String sectionName) throws Exception {

        JobPlanTaskSectionContext jpSection = new JobPlanTaskSectionContext();
        jpSection.setName(sectionName);

        final String jpSectionModuleName = "jobplansection";

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule jpSectionModule = modBean.getModule(jpSectionModuleName);
        FacilioContext ctx = V3Util.createRecord(jpSectionModule, FieldUtil.getAsProperties(jpSection));

        Map<String, Object> recordMap = (Map<String, Object>) ctx.get("recordMap");
        List<JobPlanTaskSectionContext> jpSectionList = (List<JobPlanTaskSectionContext>) recordMap.get(jpSectionModuleName);

        return jpSectionList.get(0);
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
