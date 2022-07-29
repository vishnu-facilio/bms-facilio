package com.facilio.bmsconsoleV3.commands.pmImport;


import com.facilio.accounts.dto.User;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PMPlanner;
import com.facilio.bmsconsole.context.PMResourcePlanner;
import com.facilio.bmsconsole.context.PMTriggerV2;
import com.facilio.bmsconsole.context.PMTriggerV2.PMTriggerFrequency;
import com.facilio.bmsconsoleV3.context.V3ResourceContext;
import com.facilio.bmsconsoleV3.util.V3ResourceAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.taskengine.ScheduleInfo;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.*;
import java.util.logging.Logger;


public class HandleResourcePlannerImportCommand extends FacilioCommand {
    private static final Logger LOGGER = Logger.getLogger(HandleResourcePlannerImportCommand.class.getName());


    private Map<PMTriggerFrequency, List<Map<String, Object>>> classifyByTrigger(List<Map<String, Object>> records) throws Exception {
        Map<PMTriggerFrequency, List<Map<String, Object>>> classification = new HashMap<>();
        for (Map<String, Object> rec : records) {
            Object triggerFrequencyObj = rec.get("trFrequency");
            if (triggerFrequencyObj == null) {
                throw new Exception("trigger frequency cannot be null");
            }
            PMTriggerFrequency val = PMTriggerFrequency.valueOf((String) triggerFrequencyObj);
            if (classification.containsKey(val)) {
                classification.get(val).add(rec);
            } else {
                List<Map<String, Object>> newBin = new ArrayList<>();
                newBin.add(rec);
                classification.put(val, newBin);
            }
        }
        return classification;
    }


    private PMTriggerV2 createTrigger(Long pmID, PMTriggerFrequency triggerFreq, Map<String, Object> pm) throws Exception {
        PMTriggerV2 trigger = Util.createTrigger(pmID, triggerFreq, pm);
        final String triggerModuleName = "pmTriggerV2";
        return (PMTriggerV2) Util.persistModuleRecord(triggerModuleName, trigger);
    }


    private void processRecordsForPM(Long pmID, Map<PMTriggerFrequency, List<Map<String, Object>>> classifiedRecords) throws Exception {
        for (Map.Entry<PMTriggerFrequency, List<Map<String, Object>>> entry : classifiedRecords.entrySet()) {
            PMTriggerFrequency triggerFreq = entry.getKey();
            List<Map<String, Object>> pmList = entry.getValue();

            if (pmList.size() == 0) {
                LOGGER.info(triggerFreq + " has no pms, continuing.");
                continue;
            }

            // Considering only the first pm for trigger
            // rest of the pms are expected to have the same config.
            Map<String, Object> pm = pmList.get(0);

            PMTriggerV2 trigger = createTrigger(pmID, triggerFreq, pm);
            PMPlanner planner = createPlanner(pmID, trigger, pm);
            createResourcePlanner(pmID, planner, trigger, pmList);

        }

    }

    private PMPlanner createPlanner(Long pmID, PMTriggerV2 trigger, Map<String, Object> pm) throws Exception {
        JSONParser parser = new JSONParser();
        ScheduleInfo schedule = FieldUtil.getAsBeanFromJson((JSONObject) parser.parse(trigger.getSchedule()), ScheduleInfo.class);

        PMPlanner planner = new PMPlanner();
        planner.setPmId(pmID);
        planner.setName(schedule.getFrequencyTypeEnum().getDescription());
        planner.setTrigger(trigger);

        final String plannerModuleName = "pmPlanner";
        return (PMPlanner) Util.persistModuleRecord(plannerModuleName, planner);
    }

    private void createResourcePlanner(Long pmID, PMPlanner planner, PMTriggerV2 trigger, List<Map<String, Object>> rpList) throws Exception {

        List<Map<String, Object>> mutatedRpList = new ArrayList<>();

        for (Map<String, Object> record : rpList) {
            PMResourcePlanner resourcePlanner = new PMResourcePlanner();

            //        private PMJobPlan jobPlan;

            Map<String, Object> rpResource = (Map<String, Object>) record.get("rpResource");
            if (rpResource == null) {
                throw new Exception("rpResource cannot be null");
            }
            long resourceID = FacilioUtil.parseLong(rpResource.get("id"));
            V3ResourceContext resource = V3ResourceAPI.getResource(resourceID);
            resourcePlanner.setResource(resource);

            Object assignedToObj = record.get("rpAssignedTo");
            if (assignedToObj != null) {
                long assignedToID = FacilioUtil.parseLong(assignedToObj);
                User assignedTo = new User();
                assignedTo.setId(assignedToID);
                resourcePlanner.setAssignedTo(assignedTo);
            }

            resourcePlanner.setPmId(pmID);
            resourcePlanner.setPlanner(planner);
            resourcePlanner.setTrigger(trigger);

            mutatedRpList.add(FieldUtil.getAsProperties(resourcePlanner));
        }


        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule resourcePlannerModule = modBean.getModule("pmResourcePlanner");

        V3Util.createRecordList(resourcePlannerModule, mutatedRpList, null, null);

    }


    private void processRecords(Map<Long, List<Map<String, Object>>> recordsClassifiedByPM) throws Exception {
        // for every batch of PMs.
        for (Map.Entry<Long, List<Map<String, Object>>> entry : recordsClassifiedByPM.entrySet()) {
            Long pmID = entry.getKey();
            List<Map<String, Object>> pmList = entry.getValue();

            Map<PMTriggerFrequency, List<Map<String, Object>>> recordsClassifiedByTriggers = classifyByTrigger(pmList);
            processRecordsForPM(pmID, recordsClassifiedByTriggers);
        }
    }

    @Override
    public boolean executeCommand(Context context) throws Exception {

        Object recordsObj = context.get("insertRecords");
        if (recordsObj == null) {
            LOGGER.info("insertRecords is null; import will fail");
            return false;
        }
        List<Map<String, Object>> records = (List<Map<String, Object>>) recordsObj;

        Map<Long, List<Map<String, Object>>> recordsClassifiedByPM = Util.classifyByPM((records));

        processRecords(recordsClassifiedByPM);

        // truncate table

        return false;
    }
}
