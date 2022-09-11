package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class PurchasedItemModule extends BaseModuleConfig{
    public PurchasedItemModule(){
        setModuleName(FacilioConstants.ContextNames.PURCHASED_ITEM);
    }

    @Override
    protected void addForms() throws Exception {

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> purchasedItem = new ArrayList<FacilioView>();
        purchasedItem.add(getAllPurchasedItem().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.PURCHASED_ITEM);
        groupDetails.put("views", purchasedItem);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllPurchasedItem() {
        FacilioModule purchasedItemModule = ModuleFactory.getPurchasedItemModule();

        FacilioField createdTime = new FacilioField();
        createdTime.setName("sysCreatedTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("CREATED_TIME");
        createdTime.setModule(purchasedItemModule);

        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, true));

        FacilioView allView = new FacilioView();
        allView.setName("allpuritem");
        allView.setDisplayName("All Purchased Items");
        allView.setSortFields(sortFields);

        return allView;
    }
}
