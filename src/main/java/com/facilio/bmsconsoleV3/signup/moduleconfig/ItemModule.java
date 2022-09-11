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
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.NumberField;
import com.facilio.time.DateTimeUtil;

import java.util.*;

public class ItemModule extends BaseModuleConfig{
    public ItemModule(){
        setModuleName(FacilioConstants.ContextNames.ITEM);
    }

    @Override
    protected void addForms() throws Exception {

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> item = new ArrayList<FacilioView>();
        item.add(getAllInventry().setOrder(order++));
        item.add(getStalePartsView().setOrder(order++));
        item.add(getUnderStockedItemView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.ITEM);
        groupDetails.put("views", item);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllInventry() {

        FacilioModule itemsModule = ModuleFactory.getInventryModule();

        FacilioField createdTime = new LookupField();
        createdTime.setName("itemType");
        createdTime.setDataType(FieldType.LOOKUP);
        createdTime.setColumnName("ITEM_TYPES_ID");
        createdTime.setModule(ModuleFactory.getInventryModule());

        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Items");
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

    private static FacilioView getUnderStockedItemView() {

        Criteria criteria = getUnderstockedItemCriteria(ModuleFactory.getInventryModule());

        FacilioModule itemsModule = ModuleFactory.getInventryModule();

        FacilioField createdTime = new LookupField();
        createdTime.setName("itemType");
        createdTime.setDataType(FieldType.LOOKUP);
        createdTime.setColumnName("ITEM_TYPES_ID");
        createdTime.setModule(ModuleFactory.getInventryModule());

        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, true));

        FacilioView allView = new FacilioView();
        allView.setName("understocked");
        allView.setDisplayName("Understocked Items");
        allView.setSortFields(sortFields);

        allView.setCriteria(criteria);

        return allView;
    }

    private static Criteria getUnderstockedItemCriteria(FacilioModule module) {
        FacilioField quantity = new NumberField();
        quantity.setName("quantity");
        quantity.setColumnName("QUANTITY");
        quantity.setDataType(FieldType.DECIMAL);
        quantity.setModule(module);

        FacilioField minimumQuantity = new FacilioField();
        minimumQuantity.setName("minimumQuantity");
        minimumQuantity.setColumnName("MINIMUM_QUANTITY");
        minimumQuantity.setDataType(FieldType.DECIMAL);
        minimumQuantity.setModule(module);

        Condition ticketClose = new Condition();
        ticketClose.setField(quantity);
        ticketClose.setOperator(NumberOperators.LESS_THAN);
        ticketClose.setValue("MINIMUM_QUANTITY");

        Criteria criteria = new Criteria();
        criteria.addAndCondition(ticketClose);
        return criteria;
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
}
