package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;

import java.util.*;

public class InventoryRequestLineItemsModule extends BaseModuleConfig{
    public InventoryRequestLineItemsModule(){
        setModuleName(FacilioConstants.ContextNames.INVENTORY_REQUEST_LINE_ITEMS);
    }

    @Override
    protected void addForms() throws Exception {

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> inventoryRequestLineItems = new ArrayList<FacilioView>();
        inventoryRequestLineItems.add(getAllInventoryRequestLineItemsView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.INVENTORY_REQUEST_LINE_ITEMS);
        groupDetails.put("views", inventoryRequestLineItems);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllInventoryRequestLineItemsView() {
        FacilioModule invReqLineItems = ModuleFactory.getInventoryRequestLineItemsModule();

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Inventory Request Line Items");
        allView.setModuleName(invReqLineItems.getName());
        allView.setSortFields(sortFields);

        return allView;
    }
}
