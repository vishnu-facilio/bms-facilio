package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsoleV3.signup.util.AddModuleViewsAndGroups;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class AgentAlarmsModule extends BaseModuleConfig{
    public AgentAlarmsModule() throws Exception{
        setModuleName(FacilioConstants.ContextNames.AGENT_ALARM);
    }
    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> agentAlarms = new ArrayList<FacilioView>();
        agentAlarms.add(getAgentAlarmOccurrenceViews().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "agentAlarmViews");
        groupDetails.put("displayName", "Agent Alarms");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.AGENT_ALARM);
        groupDetails.put("views", agentAlarms);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAgentAlarmOccurrenceViews() {
        FacilioField createdTime = new FacilioField();
        createdTime.setName("lastOccurredTime");
        createdTime.setDataType(FieldType.DATE_TIME);
        createdTime.setColumnName("LAST_OCCURRED_TIME");
        createdTime.setModule(ModuleFactory.getBaseAlarmModule());

        FacilioView allView = new FacilioView();
        allView.setName("agentAll");
        allView.setDisplayName("All Alarms");
        allView.setModuleName("agentAlarm");
        allView.setSortFields(Arrays.asList(new SortField(createdTime, false)));
        allView.setDefault(true);

        return allView;
    }
}
