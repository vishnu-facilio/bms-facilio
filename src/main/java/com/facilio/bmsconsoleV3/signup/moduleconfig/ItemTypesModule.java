package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.RelatedListWidgetUtil;
import com.facilio.bmsconsole.util.SystemButtonApi;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsole.workflow.rule.CustomButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.SystemButtonRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.relation.util.RelationshipWidgetUtil;
import org.json.simple.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class ItemTypesModule extends BaseModuleConfig{
    public ItemTypesModule(){
        setModuleName(FacilioConstants.ContextNames.ITEM_TYPES);
    }

    @Override
    public void addData() throws Exception {
        addSystemButtons();
    }

    @Override
    public Map<String, List<PagesContext>> fetchSystemPageConfigs() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ITEM_TYPES);
        List<String> appNames = new ArrayList<>();
        appNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);
        Map<String,List<PagesContext>> appNameVsPage = new HashMap<>();
        for (String appName : appNames) {
            ApplicationContext app = ApplicationApi.getApplicationForLinkName(appName);
            appNameVsPage.put(appName,createItemTypesPage(app, module, false,true));
        }
        return appNameVsPage;
    }
    public static List<PagesContext> createItemTypesPage(ApplicationContext app, FacilioModule module, boolean isTemplate, boolean isDefault) throws Exception {

        return new ModulePages()
                .addPage("itemtypesdefaultpage","Default Item Type Page","",null,isTemplate,isDefault,true)
                .addLayout(PagesContext.PageLayoutType.WEB)
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("itemdetails", null, null)
                .addWidget("itemdetails", "Item details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_3", 0, 0, null,  getSummaryWidgetDetails(module.getName(),app))
                .widgetDone()
                .sectionDone()
                .addSection("itemtypedetailscard", null, null)
                .addWidget("itemquantitycard", null, PageWidget.WidgetType.ITEM_TYPE_DETAILS_CARD, "webitemtypedetailscard_3_4", 0, 0, getItemQuantity(),null)
                .widgetDone()
                .addWidget("itemdatecard", null, PageWidget.WidgetType.ITEM_TYPE_DETAILS_CARD, "webitemtypedetailscard_3_4", 4, 0, getItemDate(),null)
                .widgetDone()
                .addWidget("itempriceandtrackingcard", null, PageWidget.WidgetType.ITEM_TYPE_DETAILS_CARD, "webitemtypedetailscard_3_4", 8, 0, getPriceAndTrackingCard(),null)
                .widgetDone()
                .sectionDone()
                .addSection("itemstoreroom", null, null)
                .addWidget("itemstoreroom", "Store rooms", PageWidget.WidgetType.STORE_ROOM, "flexiblewebstoreroom_6", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("itemtypelineitem", null, null)
                .addWidget("itemtypelineitem", "Where used", PageWidget.WidgetType.WHERE_USED, "flexiblewebitemtypelineitem_6", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getWidgetGroup())
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("transaction", "Transaction", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("itemtypetransaction", null, null)
                .addWidget("itemtypetransaction", "Transactions", PageWidget.WidgetType.ITEM_TRANSACTIONS, "flexiblewebitemtransactions_7", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("related", "Related", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("itemtyperelatedlist", "Related List", "List of all related records across modules")
                .addWidget("itemtyperelatedlist", "Related List", PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblewebbulkrelatedlist_6", 0, 0, null, RelatedListWidgetUtil.fetchAllRelatedListForModule(module))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone().pageDone().getCustomPages();
    }
    private static JSONObject getWidgetGroup() throws Exception {
        JSONObject commentWidgetParam = new JSONObject();
        commentWidgetParam.put("notesModuleName", FacilioConstants.ContextNames.ITEM_TYPES_NOTES);
        JSONObject attachmentWidgetParam = new JSONObject();
        attachmentWidgetParam.put("attachmentsModuleName", FacilioConstants.ContextNames.ITEM_TYPES_ATTACHMENTS);
        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("notes", "Notes", "")
                .addWidget("commentwidget", "Comment", PageWidget.WidgetType.COMMENT,  "flexiblewebcomment_5", 0, 0, commentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Documents", "")
                .addWidget("attachmentwidget", "Documents", PageWidget.WidgetType.ATTACHMENT,  "flexiblewebattachment_5", 0, 0, attachmentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();
        return FieldUtil.getAsJSON(widgetGroup);
    }
    private static  JSONObject getSummaryCardDetails(Map<String,Map<String,String>> itemMap) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        return FieldUtil.getAsJSON(itemMap,true);
    }
    private static JSONObject getItemQuantity() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Map<String,Map<String ,String>> itemQuantityDetails=new HashMap<>();

        itemQuantityDetails.put("leftField",constructFieldMap("quantity","Available Quantity","false"));
        itemQuantityDetails.put("rightField",constructFieldMap("minimumQuantity","Minimum Quantity","false"));

        return getSummaryCardDetails(itemQuantityDetails);
    }

    private static Map<String,String> constructFieldMap(String fieldName,String DisplayName,String isDateField){

        Map<String,String> field=new HashMap<>();
        field.put("FieldName", fieldName);
        field.put("DisplayName", DisplayName);
        field.put("isDateField",isDateField);
        return field;
    }
    private static JSONObject getItemDate() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Map<String,Map<String ,String>> itemDateDetails=new HashMap<>();

        itemDateDetails.put("leftField",constructFieldMap("lastIssuedDate","Last issued date","true"));
        itemDateDetails.put("rightField",constructFieldMap("lastPurchasedDate","Last purchased date","true"));

        return getSummaryCardDetails(itemDateDetails);
    }

    private static JSONObject getPriceAndTrackingCard() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Map<String,Map<String ,String>> itemDateDetails=new HashMap<>();

        itemDateDetails.put("leftField",constructFieldMap("lastPurchasedPrice","Last purchased price","false"));
        itemDateDetails.put("rightField",constructFieldMap("isRotating","Individually tracked","false"));

        return getSummaryCardDetails(itemDateDetails);
    }
    private static JSONObject getSummaryWidgetDetails(String moduleName,ApplicationContext app) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        //general information

        FacilioField description = moduleBean.getField("description", moduleName);
        FacilioField category = moduleBean.getField("category", moduleName);
        FacilioField isApprovalNeeded = moduleBean.getField("isApprovalNeeded", moduleName);
        FacilioField isRotating = moduleBean.getField("isRotating", moduleName);
        FacilioField sysModifiedTime = moduleBean.getField("sysModifiedTime", moduleName);
        FacilioField sysCreatedTime = moduleBean.getField("sysCreatedTime", moduleName);

        SummaryWidgetGroup generalInformationwidgetGroup = new SummaryWidgetGroup();

        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, description, 1, 1, 4);
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, category, 2, 1, 1);
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, isApprovalNeeded, 2, 2, 1);
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, isRotating, 2, 3, 1);


        SummaryWidgetGroup systemInformationwidgetGroup = new SummaryWidgetGroup();

        addSummaryFieldInWidgetGroup(systemInformationwidgetGroup, sysCreatedTime, 1, 1, 1);
        addSummaryFieldInWidgetGroup(systemInformationwidgetGroup, sysModifiedTime, 1, 2, 1);


        generalInformationwidgetGroup.setName("moduleDetails");
        generalInformationwidgetGroup.setDisplayName("Primary Details");
        generalInformationwidgetGroup.setColumns(4);

        systemInformationwidgetGroup.setName("systemInfo");
        systemInformationwidgetGroup.setDisplayName("System Information");
        systemInformationwidgetGroup.setColumns(4);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(generalInformationwidgetGroup);
        widgetGroupList.add(systemInformationwidgetGroup);

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();
        widgetGroup.setName("generalInformation");
        widgetGroup.setDisplayName("Primary Details");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(app.getId());
        pageWidget.setGroups(widgetGroupList);

        return FieldUtil.getAsJSON(pageWidget);
    }
    private static void addSummaryFieldInWidgetGroup(SummaryWidgetGroup widgetGroup, FacilioField field, int rowIndex, int colIndex, int colSpan){
        addSummaryFieldInWidgetGroup(widgetGroup,field,rowIndex,colIndex,colSpan,null);
    }
    private static void addSummaryFieldInWidgetGroup(SummaryWidgetGroup widgetGroup, FacilioField field, int rowIndex, int colIndex, int colSpan,FacilioField lookupField) {
        if (field != null) {
            SummaryWidgetGroupFields summaryField = new SummaryWidgetGroupFields();
            summaryField.setName(field.getName());
            summaryField.setDisplayName(field.getDisplayName());
            summaryField.setFieldId(field.getFieldId());
            summaryField.setRowIndex(rowIndex);
            summaryField.setColIndex(colIndex);
            summaryField.setColSpan(colSpan);

            if(lookupField!=null){
                summaryField.setParentLookupFieldId(lookupField.getFieldId());
            }

            if (widgetGroup.getFields() == null) {
                widgetGroup.setFields(new ArrayList<>(Arrays.asList(summaryField)));
            } else {
                widgetGroup.getFields().add(summaryField);
            }
        }
    }
    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> itemTypes = new ArrayList<FacilioView>();
        itemTypes.add(getAllItemTypes().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.ITEM_TYPES);
        groupDetails.put("views", itemTypes);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllItemTypes() {

        FacilioModule itemsModule = ModuleFactory.getItemTypesModule();

        FacilioField createdTime = new FacilioField();
        createdTime.setName("name");
        createdTime.setDataType(FieldType.STRING);
        createdTime.setColumnName("NAME");
        createdTime.setModule(itemsModule);

        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Item Types");
        allView.setSortFields(sortFields);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule itemTypesModule = modBean.getModule(FacilioConstants.ContextNames.ITEM_TYPES);

        FacilioForm itemTypesForm = new FacilioForm();
        itemTypesForm.setDisplayName("NEW ITEM TYPE");
        itemTypesForm.setName("default_itemTypes_web");
        itemTypesForm.setModule(itemTypesModule);
        itemTypesForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        itemTypesForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP));

        List<FormField> itemTypesFormFields = new ArrayList<>();
        itemTypesFormFields.add(new FormField("photo", FacilioField.FieldDisplayType.IMAGE, "Photo", FormField.Required.OPTIONAL, 1, 1));
        itemTypesFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 2, 1));
        itemTypesFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 3, 1));
        FormField field = new FormField("category", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Category", FormField.Required.OPTIONAL, "inventoryCategory",4, 2);
        field.setAllowCreateOptions(true);
        field.addToConfig("canShowLookupWizard",true);
        itemTypesFormFields.add(field);
        itemTypesFormFields.add(new FormField("sellingPrice", FacilioField.FieldDisplayType.DECIMAL, "Selling Price", FormField.Required.OPTIONAL,  5, 2));
        itemTypesFormFields.add(new FormField("minimumQuantity", FacilioField.FieldDisplayType.NUMBER, "Minimum Quantity", FormField.Required.OPTIONAL, 5, 3));
        itemTypesFormFields.add(new FormField("isRotating", FacilioField.FieldDisplayType.DECISION_BOX, "Is Rotating", FormField.Required.OPTIONAL, 6, 2));
        itemTypesFormFields.add(new FormField("isApprovalNeeded", FacilioField.FieldDisplayType.DECISION_BOX, "Approval Needed", FormField.Required.OPTIONAL, 7, 3));
        itemTypesFormFields.add(new FormField("isConsumable", FacilioField.FieldDisplayType.DECISION_BOX, "To Be Issued", FormField.Required.OPTIONAL, 8, 2));
        FormField currentQuantity = new FormField("currentQuantity", FacilioField.FieldDisplayType.NUMBER, "Current Quantity", FormField.Required.OPTIONAL, 9, 2);
        currentQuantity.setHideField(true);
        itemTypesFormFields.add(currentQuantity);

//        itemTypesForm.setFields(itemTypesFormFields);

        FormSection section = new FormSection("Default", 1, itemTypesFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        itemTypesForm.setSections(Collections.singletonList(section));
        itemTypesForm.setIsSystemForm(true);
        itemTypesForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(itemTypesForm);
    }
    private static void addSystemButtons() throws Exception {
        SystemButtonRuleContext editItemTypes = new SystemButtonRuleContext();
        editItemTypes.setName("Edit");
        editItemTypes.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        editItemTypes.setIdentifier("edit");
        editItemTypes.setPermission(AccountConstants.ModulePermission.UPDATE.name());
        editItemTypes.setPermissionRequired(true);
        editItemTypes.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.ITEM_TYPES,editItemTypes);

        SystemButtonRuleContext stockButton = new SystemButtonRuleContext();
        stockButton.setName("Stock Button");
        stockButton.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        stockButton.setIdentifier(FacilioConstants.ContextNames.ITEM_STOCK_BUTTON);
        stockButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        stockButton.setPermission(AccountConstants.ModulePermission.CREATE.name());
        stockButton.setPermissionRequired(true);
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.ITEM_TYPES,stockButton);

        //List Page System Buttons

        SystemButtonRuleContext createButton = new SystemButtonRuleContext();
        createButton.setName("Create");
        createButton.setButtonType(SystemButtonRuleContext.ButtonType.CREATE.getIndex());
        createButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_TOP.getIndex());
        createButton.setIdentifier("create");
        createButton.setPermissionRequired(true);
        createButton.setPermission("CREATE");
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.ITEM_TYPES,createButton);


        SystemButtonRuleContext listEditButton = new SystemButtonRuleContext();
        listEditButton.setName("Edit");
        listEditButton.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        listEditButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
        listEditButton.setIdentifier("edit_list");
        listEditButton.setPermissionRequired(true);
        listEditButton.setPermission("UPDATE");
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.ITEM_TYPES,listEditButton);


        SystemButtonRuleContext exportAsCSVButton = new SystemButtonRuleContext();
        exportAsCSVButton.setName("Export As CSV");
        exportAsCSVButton.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        exportAsCSVButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_TOP.getIndex());
        exportAsCSVButton.setIdentifier("export_as_csv");
        exportAsCSVButton.setPermissionRequired(true);
        exportAsCSVButton.setPermission("EXPORT");
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.ITEM_TYPES,exportAsCSVButton);

        SystemButtonRuleContext exportAsExcelButton = new SystemButtonRuleContext();
        exportAsExcelButton.setName("Export As Excel");
        exportAsExcelButton.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        exportAsExcelButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_TOP.getIndex());
        exportAsExcelButton.setIdentifier("export_as_excel");
        exportAsExcelButton.setPermissionRequired(true);
        exportAsExcelButton.setPermission("EXPORT");
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.ITEM_TYPES,exportAsExcelButton);
    }
}