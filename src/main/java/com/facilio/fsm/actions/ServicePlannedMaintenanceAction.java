package com.facilio.fsm.actions;

import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.taskengine.ScheduleInfo;
import com.facilio.v3.V3Action;
import com.facilio.v3.util.V3Util;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter @Setter
public class ServicePlannedMaintenanceAction  extends V3Action {
    private static final long serialVersionUID = 1L;

    private Long startTime;
    private String scheduleInfo;
    private Long recordId;

    public String getNextExecutionDates() throws Exception {
        JSONParser parser = new JSONParser();
        List<Long> nextExecutionTimes = new ArrayList<>();
        ScheduleInfo schedule = FieldUtil.getAsBeanFromJson((JSONObject)parser.parse(this.scheduleInfo), ScheduleInfo.class);
        Pair<Long, Integer> result = schedule.nextExecutionTime(Pair.of(startTime - 300, 0));
        long currentTime = System.currentTimeMillis()/1000;
        while (result.getLeft() < currentTime) {
            result = schedule.nextExecutionTime(result);
        }
        long endDate = schedule.getEndDate();
        nextExecutionTimes.add(result.getLeft());
        for (int i = 0; i < 9; i++) {
            result = schedule.nextExecutionTime(result);
            long nextExecutionTime = result.getLeft();
            if(endDate > 0 && nextExecutionTime * 1000 > endDate){
                break;
            }
            nextExecutionTimes.add(nextExecutionTime);
        }
        setData(FacilioConstants.ServicePlannedMaintenance.NEXT_EXECUTION_DATES,nextExecutionTimes);
        return V3Action.SUCCESS;
    }
    public String publishServicePM() throws Exception {
        Map<String, Object> patchObj = new HashMap<>();
        JSONObject bodyParam = new JSONObject();
        bodyParam.put("publishServicePM",true);
        V3Util.processAndUpdateSingleRecord(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE, recordId, patchObj, bodyParam , null, null, null, null,null, null, null,null);
        HashMap<String, String> successMsg = new HashMap<>();
        successMsg.put("message","Successfully published Service PM");
        setData(FacilioConstants.ServicePlannedMaintenance.SERVICE_PM_STATUS_ACTIONS,successMsg);
        return V3Action.SUCCESS;
    }

}
