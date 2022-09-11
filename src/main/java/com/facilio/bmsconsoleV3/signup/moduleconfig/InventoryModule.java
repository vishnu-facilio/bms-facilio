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
import com.facilio.modules.fields.NumberField;
import com.facilio.time.DateTimeUtil;

import java.util.*;

public class InventoryModule extends BaseModuleConfig{
    public InventoryModule(){
        setModuleName(FacilioConstants.ContextNames.INVENTORY);
    }

    @Override
    protected void addForms() throws Exception {

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> inventory = new ArrayList<FacilioView>();
        inventory.add(getAllInventory().setOrder(order++));
        inventory.add(getStalePartsView().setOrder(order++));
        inventory.add(getUnderStockedPartsView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.INVENTORY);
        groupDetails.put("views", inventory);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllInventory() {

        FacilioModule inventoryModule = ModuleFactory.getInventoryModule();

        FacilioField createdTime = new FacilioField();
        createdTime.setName("sysCreatedTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("CREATED_TIME");
        createdTime.setModule(inventoryModule);

        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Parts");
        allView.setSortFields(sortFields);

        return allView;
    }

    private static FacilioView getStalePartsView() {

        Criteria criteria = getStalePartsCriteria(ModuleFactory.getInventryModule());

        FacilioField createdTime = new FacilioField();
        createdTime.setName("sysCreatedTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("CREATED_TIME");
        createdTime.setModule(ModuleFactory.getInventryModule());

        FacilioView staleParts = new FacilioView();
        staleParts.setName("stale");
        staleParts.setDisplayName("Stale");
        staleParts.setCriteria(criteria);
        staleParts.setSortFields(Arrays.asList(new SortField(createdTime, false)));

        return staleParts;
    }

    private static FacilioView getUnderStockedPartsView() {

        Criteria criteria = getUnderstockedPartCriteria(ModuleFactory.getInventoryModule());

        FacilioField createdTime = new FacilioField();
        createdTime.setName("sysCreatedTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("CREATED_TIME");
        createdTime.setModule(ModuleFactory.getInventoryModule());

        FacilioView staleParts = new FacilioView();
        staleParts.setName("understocked");
        staleParts.setDisplayName("Under Stocked");
        staleParts.setCriteria(criteria);
        staleParts.setSortFields(Arrays.asList(new SortField(createdTime, false)));

        return staleParts;
    }

    private static Criteria getStalePartsCriteria(FacilioModule module) {
        NumberField modifiedTime = new NumberField();
        modifiedTime.setName("modifiedTime");
        modifiedTime.setColumnName("MODIFIED_TIME");
        modifiedTime.setDataType(FieldType.NUMBER);
        modifiedTime.setModule(module);

        Long currTime = DateTimeUtil.getCurrenTime();
        Long twoMonthInMillis = 5184000000l;

        Condition staleParts = new Condition();
        staleParts.setField(modifiedTime);
        staleParts.setOperator(NumberOperators.LESS_THAN);
        staleParts.setValue(currTime - twoMonthInMillis + "");

        Criteria criteria = new Criteria();
        criteria.addAndCondition(staleParts);
        return criteria;
    }

    private static Criteria getUnderstockedPartCriteria(FacilioModule module) {
        FacilioField quantity = new NumberField();
        quantity.setName("quantity");
        quantity.setColumnName("QUANTITY");
        quantity.setDataType(FieldType.NUMBER);
        quantity.setModule(module);

        FacilioField minimumQuantity = new FacilioField();
        minimumQuantity.setName("minimumQuantity");
        minimumQuantity.setColumnName("MINIMUM_QUANTITY");
        minimumQuantity.setDataType(FieldType.NUMBER);
        minimumQuantity.setModule(module);

        Condition ticketClose = new Condition();
        ticketClose.setField(quantity);
        ticketClose.setOperator(NumberOperators.LESS_THAN);
        ticketClose.setValue("MINIMUM_QUANTITY");

        Criteria criteria = new Criteria();
        criteria.addAndCondition(ticketClose);
        return criteria;
    }
}
