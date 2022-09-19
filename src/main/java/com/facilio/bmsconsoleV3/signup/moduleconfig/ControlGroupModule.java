package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.control.util.ControlScheduleUtil;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;

import java.util.*;

public class ControlGroupModule extends BaseModuleConfig{
    public ControlGroupModule(){
        setModuleName(ControlScheduleUtil.CONTROL_GROUP_MODULE_NAME);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> controlGroup = new ArrayList<FacilioView>();
        controlGroup.add(getAllControlGroupView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", ControlScheduleUtil.CONTROL_GROUP_MODULE_NAME);
        groupDetails.put("views", controlGroup);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllControlGroupView() {
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Groups");
        allView.setModuleName(ControlScheduleUtil.CONTROL_GROUP_MODULE_NAME);
        allView.setSortFields(sortFields);

        List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
        appDomains.add(AppDomain.AppDomainType.FACILIO);

        return allView;
    }
}
