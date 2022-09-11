package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.control.util.ControlScheduleUtil;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;

import java.util.*;

public class ControlScheduleModule extends BaseModuleConfig{
    public ControlScheduleModule(){
        setModuleName(ControlScheduleUtil.CONTROL_SCHEDULE_MODULE_NAME);
    }

    @Override
    protected void addForms() throws Exception {

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> controlSchedule = new ArrayList<FacilioView>();
        controlSchedule.add(getAllControlScheduleView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", ControlScheduleUtil.CONTROL_SCHEDULE_MODULE_NAME);
        groupDetails.put("views", controlSchedule);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllControlScheduleView() {
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Schedules");
        allView.setModuleName(ControlScheduleUtil.CONTROL_SCHEDULE_MODULE_NAME);
        allView.setSortFields(sortFields);

        List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
        appDomains.add(AppDomain.AppDomainType.FACILIO);

        return allView;
    }
}
