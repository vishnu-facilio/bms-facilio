package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.control.util.ControlScheduleUtil;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;

import java.util.*;

public class ControlGroupTenantSharingModule extends BaseModuleConfig{
    public ControlGroupTenantSharingModule(){
        setModuleName(ControlScheduleUtil.CONTROL_GROUP_TENANT_SHARING_MODULE_NAME);
    }

    @Override
    protected void addForms() throws Exception {

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> controlGroupTenantSharing = new ArrayList<FacilioView>();
        controlGroupTenantSharing.add(getTenantControlGroupView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "tenantportal-systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", ControlScheduleUtil.CONTROL_GROUP_TENANT_SHARING_MODULE_NAME);
        groupDetails.put("views", controlGroupTenantSharing);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getTenantControlGroupView() {
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "Control_Group_V2_Tenant_Sharing.ID", FieldType.NUMBER), true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Groups");
        allView.setModuleName(ControlScheduleUtil.CONTROL_GROUP_TENANT_SHARING_MODULE_NAME);
        allView.setSortFields(sortFields);
        allView.setAppLinkNames(Collections.singletonList(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP));

        return allView;
    }
}
