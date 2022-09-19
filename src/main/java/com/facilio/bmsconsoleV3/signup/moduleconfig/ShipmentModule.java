package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class ShipmentModule extends BaseModuleConfig{
    public ShipmentModule(){
        setModuleName(FacilioConstants.ContextNames.SHIPMENT);
    }


    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> shipment = new ArrayList<FacilioView>();
        shipment.add(getAllShipmentView().setOrder(order++).setOrder(order++));
        shipment.add(getShipmentForStatus("staged", "Staged", 2).setOrder(order++));
        shipment.add(getShipmentForStatus("received", "Received", 4).setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.SHIPMENT);
        groupDetails.put("views", shipment);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllShipmentView() {
        FacilioField localId = new FacilioField();
        localId.setName("localId");
        localId.setColumnName("LOCAL_ID");
        localId.setDataType(FieldType.NUMBER);
        localId.setModule(ModuleFactory.getShipmentModule());

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All");
        allView.setSortFields(Arrays.asList(new SortField(localId, false)));

        return allView;
    }

    private static FacilioView getShipmentForStatus(String viewName, String viewDisplayName, int status) {
        FacilioModule shipmentModule = ModuleFactory.getShipmentModule();

        FacilioField localId = new FacilioField();
        localId.setName("localId");
        localId.setColumnName("LOCAL_ID");
        localId.setDataType(FieldType.NUMBER);
        localId.setModule(ModuleFactory.getShipmentModule());

        List<SortField> sortFields = Arrays.asList(new SortField(localId, false));

        Criteria criteria = getShipmentStatusCriteria(shipmentModule, status);

        FacilioView statusView = new FacilioView();
        statusView.setName(viewName);
        statusView.setDisplayName(viewDisplayName);
        statusView.setSortFields(sortFields);
        statusView.setCriteria(criteria);

        return statusView;
    }

    private static Criteria getShipmentStatusCriteria(FacilioModule module, int status) {

        FacilioField shipmentStatusField = new FacilioField();
        shipmentStatusField.setName("status");
        shipmentStatusField.setColumnName("STATUS");
        shipmentStatusField.setDataType(FieldType.NUMBER);
        shipmentStatusField.setModule(ModuleFactory.getShipmentModule());

        Condition statusCond = new Condition();
        statusCond.setField(shipmentStatusField);
        statusCond.setOperator(NumberOperators.EQUALS);
        statusCond.setValue(String.valueOf(status));

        Criteria shipmentStatusCriteria = new Criteria();
        shipmentStatusCriteria.addAndCondition(statusCond);
        return shipmentStatusCriteria;
    }
}
