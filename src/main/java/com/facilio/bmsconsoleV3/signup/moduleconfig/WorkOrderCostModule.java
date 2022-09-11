package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;

import java.util.*;

public class WorkOrderCostModule extends BaseModuleConfig{
    public WorkOrderCostModule(){
        setModuleName(FacilioConstants.ContextNames.WORKORDER_COST);
    }

    @Override
    protected void addForms() throws Exception {

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> workorderCost = new ArrayList<FacilioView>();
        workorderCost.add(getAllWorkOrderCostView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.WORKORDER_COST);
        groupDetails.put("views", workorderCost);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllWorkOrderCostView() {

        FacilioModule module = ModuleFactory.getWorkorderCostModule();

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Workorder Cost");
        allView.setModuleName(module.getName());
        allView.setSortFields(sortFields);

        return allView;
    }
}
