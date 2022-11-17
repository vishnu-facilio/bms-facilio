package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class InventoryReservationModule extends BaseModuleConfig{
    public InventoryReservationModule() throws Exception {
        setModuleName(FacilioConstants.ContextNames.INVENTORY_RESERVATION);
    }

    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> inventoryReservationViews = new ArrayList<FacilioView>();
        inventoryReservationViews.add(getAllInventoryReservationViews().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.INVENTORY_RESERVATION);
        groupDetails.put("views", inventoryReservationViews);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private  FacilioView getAllInventoryReservationViews() {
        FacilioField createdTime = new FacilioField();
        createdTime.setName("sysCreatedTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("SYS_CREATED_TIME");
        createdTime.setModule(ModuleFactory.getInventoryReservationModule());
        SortField sortField = new SortField(createdTime, false);

        FacilioView viewOne = new FacilioView();
        viewOne.setName("all");
        viewOne.setDisplayName("All Inventory Reservation");
        viewOne.setSortFields(Collections.singletonList(sortField));
        viewOne.setFields(getViewOneColumns());


        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        viewOne.setAppLinkNames(appLinkNames);

        return viewOne;
    }

    private static List<ViewField> getViewOneColumns() {
        List<ViewField> columns = new ArrayList<ViewField>();
        columns.add(new ViewField("itemType","Item"));
        columns.add(new ViewField("storeRoom","Storeroom"));
//        columns.add(new ViewField("reservationType","Reservation Type"));
        columns.add(new ViewField("reservedQuantity","Reserved Quantity"));
        columns.add(new ViewField("balanceReservedQuantity","Balance Reserved Quantity"));
        columns.add(new ViewField("inventoryRequest","Inventory Request"));
        columns.add(new ViewField("workOrder", "Work Order"));
        columns.add(new ViewField("issuedQuantity","Issued Quantity"));
        columns.add(new ViewField("reservationSource","Reservation Source"));
        columns.add(new ViewField("reservationStatus","Reservation Status"));
        return columns;
    }
}
