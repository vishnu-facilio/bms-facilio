package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleFactory;

import java.util.*;

public class WorkOrderServiceModule extends BaseModuleConfig{
    public WorkOrderServiceModule() throws Exception{
        setModuleName(FacilioConstants.ContextNames.WO_SERVICE);
    }

    @Override
    protected void addForms() throws Exception {

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> workOrderService = new ArrayList<FacilioView>();
        workOrderService.add(getAllWorkOrderService().setOrder(order++));
        workOrderService.add(getAllWorkOrderServiceDetailsView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", getModuleName());
        groupDetails.put("views", workOrderService);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllWorkOrderService() {
        FacilioModule workOrderServiceModule = ModuleFactory.getWorkOrderServiceModule();

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Work Order Service");
        allView.setModuleName(workOrderServiceModule.getName());

        return allView;
    }

    private static FacilioView getAllWorkOrderServiceDetailsView() {
        FacilioModule workOrderServiceModule = ModuleFactory.getWorkOrderServiceModule();

        FacilioView detailsView = new FacilioView();
        detailsView.setName("details");
        detailsView.setDisplayName("Work Order Service Details");
        detailsView.setModuleName(workOrderServiceModule.getName());

        return detailsView;
    }
}
