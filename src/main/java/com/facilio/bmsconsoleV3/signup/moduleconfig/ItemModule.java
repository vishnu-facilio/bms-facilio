package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.TemplatePages.ItemTemplatePage;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.PagesContext;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.SystemButtonApi;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsole.workflow.rule.CustomButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.SystemButtonRuleContext;
import com.facilio.bmsconsoleV3.context.ScopeVariableModulesFields;
import com.facilio.bmsconsoleV3.util.ScopingUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.NumberField;
import com.facilio.modules.fields.SystemEnumField;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.context.Constants;

import java.util.*;

public class ItemModule extends BaseModuleConfig{
    public ItemModule(){
        setModuleName(FacilioConstants.ContextNames.ITEM);
    }

    @Override
    public void addData() throws Exception {
        addFields();
        addSystemButton();
    }
    private void addFields() throws Exception{
        ModuleBean modBean = Constants.getModBean();
        FacilioModule itemModule = modBean.getModule(FacilioConstants.ContextNames.ITEM);
        if(itemModule!=null && itemModule.getModuleId()>0){
           FacilioField weightedAverageCost = FieldFactory.getDefaultField("weightedAverageCost","Weighted Average Cost","WEIGHTED_AVERAGE_COST",FieldType.DECIMAL, FacilioField.FieldDisplayType.DECIMAL);
           weightedAverageCost.setModule(itemModule);
           modBean.addField(weightedAverageCost);
        }
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
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);
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
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);
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
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);
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
        itemForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP));

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

    @Override
    public Map<String, List<PagesContext>> fetchSystemPageConfigs() throws Exception {
        FacilioModule module= Constants.getModBean().getModule("item");
        List<String> appNames = new ArrayList<>();
        appNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);
        Map<String,List<PagesContext>> appNameVsPage = new HashMap<>();
        for (String appName : appNames) {
            ApplicationContext app = ApplicationApi.getApplicationForLinkName(appName);
            PagesContext nonRotatingItemsPage =  ItemTemplatePage.getNonRotatingItemsPage(app,module,false,true);
            PagesContext rotatingItemsPage = ItemTemplatePage.getRotatingItemsPage(app, module, false, false);
            List<PagesContext> pagesList = new ArrayList<>();
            pagesList.add(nonRotatingItemsPage);
            pagesList.add(rotatingItemsPage);
            appNameVsPage.put(appName,pagesList);
        }
        return appNameVsPage;
    }


    private static void addSystemButton() throws Exception{
        SystemButtonRuleContext editButton = new SystemButtonRuleContext();
        editButton.setName("Edit");
        editButton.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        editButton.setIdentifier("edit");
        editButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        editButton.setPermission(AccountConstants.ModulePermission.UPDATE.name());
        editButton.setPermissionRequired(true);
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.ITEM,editButton);

        SystemButtonRuleContext stockButton = new SystemButtonRuleContext();
        stockButton.setName("Stock");
        stockButton.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        stockButton.setIdentifier(FacilioConstants.ContextNames.ITEM_STOCK_BUTTON);
        stockButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        stockButton.setPermission(AccountConstants.ModulePermission.CREATE.name());
        stockButton.setPermissionRequired(true);
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.ITEM,stockButton);

        SystemButtonRuleContext goToItemType = new SystemButtonRuleContext();
        goToItemType.setName("Go To Item Type");
        goToItemType.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        goToItemType.setIdentifier(FacilioConstants.ContextNames.GO_TO_ITEM_TYPE_BUTTON);
        goToItemType.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.ITEM,goToItemType);

        SystemButtonRuleContext issueItems = new SystemButtonRuleContext();
        issueItems.setName("Issue Items");
        issueItems.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        issueItems.setIdentifier(FacilioConstants.ContextNames.ISSUE_ITEMS_BUTTON);
        issueItems.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        issueItems.setPermission(AccountConstants.ModulePermission.UPDATE.name());
        issueItems.setPermissionRequired(true);
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.ITEM,issueItems);

        SystemButtonRuleContext returnItems = new SystemButtonRuleContext();
        returnItems.setName("Return Items");
        returnItems.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        returnItems.setIdentifier(FacilioConstants.ContextNames.RETURN_ITEMS_BUTTON);
        returnItems.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        returnItems.setPermission(AccountConstants.ModulePermission.UPDATE.name());
        returnItems.setPermissionRequired(true);
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.ITEM,returnItems);

        SystemButtonRuleContext adjustItemBalanceButton = new SystemButtonRuleContext();
        adjustItemBalanceButton.setName("Adjust Balance");
        adjustItemBalanceButton.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        adjustItemBalanceButton.setIdentifier(FacilioConstants.ContextNames.ADJUST_ITEM_BALANCE_BUTTON);
        adjustItemBalanceButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        adjustItemBalanceButton.setPermission(AccountConstants.ModulePermission.UPDATE.name());
        adjustItemBalanceButton.setPermissionRequired(true);
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.ITEM,adjustItemBalanceButton);

        SystemButtonRuleContext listEditButton = new SystemButtonRuleContext();
        listEditButton.setName("Edit");
        listEditButton.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        listEditButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
        listEditButton.setIdentifier("edit_list");
        listEditButton.setPermissionRequired(true);
        listEditButton.setPermission("UPDATE");
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.ITEM,listEditButton);

        SystemButtonRuleContext exportAsCSVButton = new SystemButtonRuleContext();
        exportAsCSVButton.setName("Export As CSV");
        exportAsCSVButton.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        exportAsCSVButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_TOP.getIndex());
        exportAsCSVButton.setIdentifier("export_as_csv");
        exportAsCSVButton.setPermissionRequired(true);
        exportAsCSVButton.setPermission("EXPORT");
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.ITEM,exportAsCSVButton);

        SystemButtonRuleContext exportAsExcelButton = new SystemButtonRuleContext();
        exportAsExcelButton.setName("Export As Excel");
        exportAsExcelButton.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        exportAsExcelButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_TOP.getIndex());
        exportAsExcelButton.setIdentifier("export_as_excel");
        exportAsExcelButton.setPermissionRequired(true);
        exportAsExcelButton.setPermission("EXPORT");
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.ITEM,exportAsExcelButton);
    }
}
