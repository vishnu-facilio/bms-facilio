package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleFactory;

import java.util.*;

public class WorkOrderItemsModule extends BaseModuleConfig{
    public WorkOrderItemsModule() throws Exception{
        setModuleName(FacilioConstants.ContextNames.WORKORDER_ITEMS);
    }

    @Override
    protected void addForms() throws Exception {

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> workOrderItems = new ArrayList<FacilioView>();
        workOrderItems.add(getAllWorkOrderItems().setOrder(order++));
        workOrderItems.add(getAllWorkOrderItemsDetailsView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", getModuleName());
        groupDetails.put("views", workOrderItems);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllWorkOrderItems() {

        FacilioModule workOrderItemsModule = ModuleFactory.getWorkOrderItemsModule();
        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Work Order Items");
        allView.setModuleName(workOrderItemsModule.getName());

        return allView;
    }
    private static FacilioView getAllWorkOrderItemsDetailsView() {
        FacilioModule workOrderItemsModule = ModuleFactory.getWorkOrderItemsModule();

        FacilioView detailsView = new FacilioView();
        detailsView.setName("details");
        detailsView.setDisplayName("Work Order Items Details");
        detailsView.setModuleName(workOrderItemsModule.getName());

        return detailsView;
    }
}
