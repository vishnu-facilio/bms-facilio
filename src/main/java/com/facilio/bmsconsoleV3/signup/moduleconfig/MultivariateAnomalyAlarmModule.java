package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class MultivariateAnomalyAlarmModule extends BaseModuleConfig{
    public MultivariateAnomalyAlarmModule(){
        setModuleName(FacilioConstants.ContextNames.MULTIVARIATE_ANOMALY_ALARM);
    }


    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> multivariateAnamolyAlarm = new ArrayList<FacilioView>();
        multivariateAnamolyAlarm.add(getMultivariateAnomalyAlarmOccurrenceViews().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.MULTIVARIATE_ANOMALY_ALARM);
        groupDetails.put("views", multivariateAnamolyAlarm);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getMultivariateAnomalyAlarmOccurrenceViews() {
        FacilioField createdTime = new FacilioField();
        createdTime.setName("lastOccurredTime");
        createdTime.setDataType(FieldType.DATE_TIME);
        createdTime.setColumnName("LAST_OCCURRED_TIME");
        createdTime.setModule(ModuleFactory.getBaseAlarmModule());

        FacilioView allView = new FacilioView();
        allView.setName("mlmvaAlarms");
        allView.setDisplayName("All Alarms");
        allView.setModuleName("multivariateanomalyalarm");
        allView.setSortFields(Arrays.asList(new SortField(createdTime, false)));
        allView.setDefault(true);

        return allView;
    }
}

