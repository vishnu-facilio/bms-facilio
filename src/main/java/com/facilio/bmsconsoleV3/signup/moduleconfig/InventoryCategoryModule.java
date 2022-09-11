package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class InventoryCategoryModule extends BaseModuleConfig{
    public InventoryCategoryModule(){
        setModuleName(FacilioConstants.ContextNames.INVENTORY_CATEGORY);
    }

    @Override
    protected void addForms() throws Exception {

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> inventoryCategory = new ArrayList<FacilioView>();
        inventoryCategory.add(getAllInventoryCategory().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.INVENTORY_CATEGORY);
        groupDetails.put("views", inventoryCategory);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllInventoryCategory() {

        FacilioModule inventoryCategoryModule = ModuleFactory.getInventoryCategoryModule();

        FacilioField createdTime = new FacilioField();
        createdTime.setName("name");
        createdTime.setDataType(FieldType.STRING);
        createdTime.setColumnName("NAME");
        createdTime.setModule(inventoryCategoryModule);

        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Inventory Category");
        allView.setSortFields(sortFields);

        return allView;
    }
}
