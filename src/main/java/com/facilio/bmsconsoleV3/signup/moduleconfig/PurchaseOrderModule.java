package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class PurchaseOrderModule extends BaseModuleConfig{
    public PurchaseOrderModule(){
        setModuleName(FacilioConstants.ContextNames.PURCHASE_ORDER);
    }

    @Override
    protected void addForms() throws Exception {

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> purchaseOrder = new ArrayList<FacilioView>();
        purchaseOrder.add(getAllPurchaseOrderView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.PURCHASE_ORDER);
        groupDetails.put("views", purchaseOrder);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllPurchaseOrderView() {
        FacilioField localId = new FacilioField();
        localId.setName("id");
        localId.setColumnName("ID");
        localId.setDataType(FieldType.NUMBER);
        localId.setModule(ModuleFactory.getPurchaseOrderModule());

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All");
        allView.setSortFields(Arrays.asList(new SortField(localId, false)));

        return allView;
    }

}
