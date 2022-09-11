package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleFactory;

import java.util.*;

public class WorkOrderToolsModule extends BaseModuleConfig{
    public WorkOrderToolsModule() throws Exception{
        setModuleName(FacilioConstants.ContextNames.WORKORDER_TOOLS);
    }

    @Override
    protected void addForms() throws Exception {

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> workOrderTools = new ArrayList<FacilioView>();
        workOrderTools.add(getAllWorkOrderTools().setOrder(order++));
        workOrderTools.add(getAllWorkOrderToolsDetailsView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", getModuleName());
        groupDetails.put("views", workOrderTools);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllWorkOrderTools() {

        FacilioModule workOrderToolsModule = ModuleFactory.getWorkOrderToolsModule();

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Work Order Tools");
        allView.setModuleName(workOrderToolsModule.getName());

        return allView;
    }

    private static FacilioView getAllWorkOrderToolsDetailsView() {
        FacilioModule workOrderToolsModule = ModuleFactory.getWorkOrderToolsModule();

        FacilioView detailsView = new FacilioView();
        detailsView.setName("details");
        detailsView.setDisplayName("Work Order Tools Details");
        detailsView.setModuleName(workOrderToolsModule.getName());

        return detailsView;
    }
}
