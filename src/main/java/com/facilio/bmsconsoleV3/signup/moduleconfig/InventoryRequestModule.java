package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class InventoryRequestModule extends BaseModuleConfig{
    public InventoryRequestModule(){
        setModuleName(FacilioConstants.ContextNames.INVENTORY_REQUEST);
    }

    @Override
    protected void addForms() throws Exception {

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> inventoryRequest = new ArrayList<FacilioView>();
        inventoryRequest.add(getAllInventoryRequestView().setOrder(order++));
        inventoryRequest.add(getInventoryRequestIssued("issued", "Issued" ,true).setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.INVENTORY_REQUEST);
        groupDetails.put("views", inventoryRequest);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllInventoryRequestView() {
        FacilioField localId = new FacilioField();
        localId.setName("localId");
        localId.setColumnName("LOCAL_ID");
        localId.setDataType(FieldType.NUMBER);
        localId.setModule(ModuleFactory.getInventoryRequestModule());

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All");
        allView.setSortFields(Arrays.asList(new SortField(localId, false)));

        return allView;
    }

    private static FacilioView getInventoryRequestIssued(String viewName, String viewDisplayName, boolean isIssued) {
        FacilioModule irModule = ModuleFactory.getInventoryRequestModule();

        FacilioField createdTime = new FacilioField();
        createdTime.setName("localId");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("LOCAL_ID");
        createdTime.setModule(irModule);

        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

        Criteria criteria = getIRIssuedCondition(true);

        FacilioView statusView = new FacilioView();
        statusView.setName(viewName);
        statusView.setDisplayName(viewDisplayName);
        statusView.setSortFields(sortFields);
        statusView.setCriteria(criteria);
        List<ViewField> fields = new ArrayList<ViewField>();
        fields.add(new ViewField("id", "ID"));
        fields.add(new ViewField("name", "Name"));
        fields.add(new ViewField("requestedBy", "Requested By"));
        fields.add(new ViewField("requestedTime", "Requested Time"));
        fields.add(new ViewField("status", "Valid Till"));
        fields.add(new ViewField("totalCost", "Total Cost"));
        return statusView;
    }

    private static Criteria getIRIssuedCondition(boolean isIssued) {

        FacilioField irStatusField = new FacilioField();
        irStatusField.setName("isIssued");
        irStatusField.setColumnName("IS_ISSUED");
        irStatusField.setDataType(FieldType.BOOLEAN);
        irStatusField.setModule(ModuleFactory.getInventoryRequestModule());

        Condition statusCond = new Condition();
        statusCond.setField(irStatusField);
        statusCond.setOperator(BooleanOperators.IS);
        statusCond.setValue(String.valueOf(isIssued));

        Criteria inventoryRequestStatusCriteria = new Criteria();
        inventoryRequestStatusCriteria.addAndCondition(statusCond);
        return inventoryRequestStatusCriteria;

    }
}
