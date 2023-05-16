package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsoleV3.context.ScopeVariableModulesFields;
import com.facilio.bmsconsoleV3.util.ScopingUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
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

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Items");

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        allView.setAppLinkNames(appLinkNames);

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

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        staleParts.setAppLinkNames(appLinkNames);

        return staleParts;
    }

    private static FacilioView getUnderStockedItemView() {

        Criteria criteria = getUnderstockedItemCriteria(ModuleFactory.getInventryModule());

        FacilioModule itemsModule = ModuleFactory.getInventryModule();

        FacilioView allView = new FacilioView();
        allView.setName("understocked");
        allView.setDisplayName("Understocked Items");

        allView.setCriteria(criteria);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        allView.setAppLinkNames(appLinkNames);

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

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule itemModule = modBean.getModule(FacilioConstants.ContextNames.ITEM);

        FacilioForm itemForm = new FacilioForm();
        itemForm.setDisplayName("UPDATE ITEM ATTRIBUTES");
        itemForm.setName("web_default");
        itemForm.setModule(itemModule);
        itemForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        itemForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));

        List<FormField> itemFormFields = new ArrayList<>();
        itemFormFields.add(new FormField("itemType", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Item Type", FormField.Required.REQUIRED, "itemTypes", 1, 2));
        itemFormFields.add(new FormField("storeRoom", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Storeroom", FormField.Required.REQUIRED, "storeRoom", 2, 3));
        itemFormFields.add(new FormField("minimumQuantity", FacilioField.FieldDisplayType.NUMBER, "Minimum Quantity", FormField.Required.OPTIONAL, 3, 2));
        itemFormFields.add(new FormField("costType", FacilioField.FieldDisplayType.SELECTBOX, "Cost Type", FormField.Required.OPTIONAL, 4, 3));
        itemFormFields.add(new FormField("issuanceCost", FacilioField.FieldDisplayType.DECIMAL, "Issuance Cost", FormField.Required.OPTIONAL, 5, 2));
        //        itemForm.setFields(itemFormFields);

        FormSection section = new FormSection("Default", 1, itemFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        itemForm.setSections(Collections.singletonList(section));
        itemForm.setIsSystemForm(true);
        itemForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(itemForm);
    }
}
