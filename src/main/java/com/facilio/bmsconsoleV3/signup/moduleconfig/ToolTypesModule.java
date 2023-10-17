package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
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
import org.json.simple.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class ToolTypesModule extends BaseModuleConfig{
    public ToolTypesModule(){
        setModuleName(FacilioConstants.ContextNames.TOOL_TYPES);
    }

    @Override
    public void addData() throws Exception {
        addSystemButtons();
    }

    @Override
    public Map<String, List<PagesContext>> fetchSystemPageConfigs() throws Exception {
        FacilioModule module=ModuleFactory.getToolTypesModule();
        List<String> appNames = new ArrayList<>();
        appNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        Map<String,List<PagesContext>> appNameVsPage = new HashMap<>();
        for (String appName : appNames) {
            ApplicationContext app = ApplicationApi.getApplicationForLinkName(appName);
            appNameVsPage.put(appName,createToolTypesPage(app, module, false,true));
        }
        return appNameVsPage;
    }

    public static List<PagesContext> createToolTypesPage(ApplicationContext app, FacilioModule module, boolean isTemplate, boolean isDefault) throws Exception {

        return new ModulePages()
                .addPage("itemtypesdefaultpage", "Default Tool Type Page", "", null, isTemplate, isDefault, true)
                .addLayout(PagesContext.PageLayoutType.WEB)
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("tooldetails", null, null)
                .addWidget("tooldetails", "Tool details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_3", 0, 0, null, getSummaryWidgetDetails(module.getName(), app))
                .widgetDone()
                .sectionDone()
                .addSection("toolscard", null, null)
                .addWidget("toolsbalanceandquantitycard", null, PageWidget.WidgetType.ITEM_TYPE_DETAILS_CARD, "webitemtypedetailscard_3_4", 0, 0, getToolsBalanceAndQuantity(),null)
                .widgetDone()
                .addWidget("toolsdatecard", null, PageWidget.WidgetType.ITEM_TYPE_DETAILS_CARD, "webitemtypedetailscard_3_4", 4, 0, getToolsDate(),null)
                .widgetDone()
                .addWidget("toolspriceandtrackingcard", null, PageWidget.WidgetType.ITEM_TYPE_DETAILS_CARD, "webitemtypedetailscard_3_4", 8, 0, getPriceAndTrackingCard(),null)
                .widgetDone()
                .sectionDone()
                .addSection("toolstoreroom", null, null)
                .addWidget("toolstoreroom", "Store rooms", PageWidget.WidgetType.STORE_ROOM, "flexiblewebstoreroom_6", 0, 0, null, null)
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
                .addSection("tooltypetransaction", null, null)
                .addWidget("tooltypetransaction", "Transactions", PageWidget.WidgetType.ITEM_TRANSACTIONS, "flexiblewebitemtransactions_7", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone().pageDone().getCustomPages();
    }

    private static JSONObject getWidgetGroup() throws Exception {
        JSONObject commentWidgetParam = new JSONObject();
        commentWidgetParam.put("notesModuleName", FacilioConstants.ContextNames.TOOL_TYPES_NOTES);
        JSONObject attachmentWidgetParam = new JSONObject();
        attachmentWidgetParam.put("attachmentsModuleName", FacilioConstants.ContextNames.TOOL_TYPES_ATTACHMENTS);
        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("notes", "Notes", "")
                .addWidget("commentwidget", "Comment", PageWidget.WidgetType.COMMENT, "flexiblewebcomment_5", 0, 0, commentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Documents", "")
                .addWidget("attachmentwidget", "Documents", PageWidget.WidgetType.ATTACHMENT, "flexiblewebattachment_5", 0, 0, attachmentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();
        return FieldUtil.getAsJSON(widgetGroup);
    }
    private static  JSONObject getSummaryCardDetails(Map<String,Map<String,String>> itemMap) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        return FieldUtil.getAsJSON(itemMap,true);
    }
    private static JSONObject getToolsBalanceAndQuantity() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Map<String,Map<String ,String>> itemQuantityDetails=new HashMap<>();

        itemQuantityDetails.put("leftField",constructFieldMap("currentQuantity","Current balance","false"));
        itemQuantityDetails.put("midField",constructFieldMap("quantity","Available Quantity","false"));
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
    private static JSONObject getToolsDate() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Map<String,Map<String ,String>> itemDateDetails=new HashMap<>();

        itemDateDetails.put("leftField",constructFieldMap("lastIssuedDate","Last issued date","true"));
        itemDateDetails.put("rightField",constructFieldMap("lastPurchasedDate","Last purchased date","true"));

        return getSummaryCardDetails(itemDateDetails);
    }

    private static JSONObject getPriceAndTrackingCard() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Map<String,Map<String ,String>> itemDateDetails=new HashMap<>();

        itemDateDetails.put("leftField",constructFieldMap("sellingPrice","Selling price per hour","false"));
        itemDateDetails.put("rightField",constructFieldMap("isRotating","Individually tracked","false"));

        return getSummaryCardDetails(itemDateDetails);
    }

    private static JSONObject getSummaryWidgetDetails(String moduleName, ApplicationContext app) throws Exception {
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
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, sysModifiedTime, 2, 4, 1);
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, sysCreatedTime, 3, 1, 1);


        generalInformationwidgetGroup.setName("moduleDetails");
        generalInformationwidgetGroup.setDisplayName("Primary Details");
        generalInformationwidgetGroup.setColumns(4);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(generalInformationwidgetGroup);

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();
        widgetGroup.setName("primaryDetails");
        widgetGroup.setDisplayName("Primary Details");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(app.getId());
        pageWidget.setGroups(widgetGroupList);

        return FieldUtil.getAsJSON(pageWidget);
    }

    private static void addSummaryFieldInWidgetGroup(SummaryWidgetGroup widgetGroup, FacilioField field, int rowIndex, int colIndex, int colSpan) {
        addSummaryFieldInWidgetGroup(widgetGroup, field, rowIndex, colIndex, colSpan, null);
    }

    private static void addSummaryFieldInWidgetGroup(SummaryWidgetGroup widgetGroup, FacilioField field, int rowIndex, int colIndex, int colSpan, FacilioField lookupField) {
        if (field != null) {
            SummaryWidgetGroupFields summaryField = new SummaryWidgetGroupFields();
            summaryField.setName(field.getName());
            summaryField.setDisplayName(field.getDisplayName());
            summaryField.setFieldId(field.getFieldId());
            summaryField.setRowIndex(rowIndex);
            summaryField.setColIndex(colIndex);
            summaryField.setColSpan(colSpan);

            if (lookupField != null) {
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
        ArrayList<FacilioView> toolTypes = new ArrayList<FacilioView>();
        toolTypes.add(getAllToolTypes().setOrder(order++));


        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.TOOL_TYPES);
        groupDetails.put("views", toolTypes);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllToolTypes() {

        FacilioModule itemsModule = ModuleFactory.getToolTypesModule();

        FacilioField createdTime = new FacilioField();
        createdTime.setName("name");
        createdTime.setDataType(FieldType.STRING);
        createdTime.setColumnName("NAME");
        createdTime.setModule(itemsModule);

        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Tool Types");
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
        FacilioModule toolTypesModule = modBean.getModule(FacilioConstants.ContextNames.TOOL_TYPES);

        FacilioForm toolTypesForm = new FacilioForm();
        toolTypesForm.setDisplayName("NEW TOOL TYPE");
        toolTypesForm.setName("default_toolTypes_web");
        toolTypesForm.setModule(toolTypesModule);
        toolTypesForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        toolTypesForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP));

        List<FormField> toolTypesFormFields = new ArrayList<>();
        toolTypesFormFields.add(new FormField("photo", FacilioField.FieldDisplayType.IMAGE, "Photo", FormField.Required.OPTIONAL, 1, 1));
        toolTypesFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 2, 1));
        toolTypesFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 3, 1));
        FormField field = new FormField("category", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Category", FormField.Required.OPTIONAL, "inventoryCategory",4, 1);
        field.setAllowCreateOptions(true);
        field.addToConfig("canShowLookupWizard",true);
        toolTypesFormFields.add(field);
        toolTypesFormFields.add(new FormField("sellingPrice", FacilioField.FieldDisplayType.DECIMAL, "Selling Price Per Hour", FormField.Required.OPTIONAL,  5, 2));
        toolTypesFormFields.add(new FormField("minimumQuantity", FacilioField.FieldDisplayType.NUMBER, "Minimum Quantity", FormField.Required.OPTIONAL, 5, 3));
        toolTypesFormFields.add(new FormField("isRotating", FacilioField.FieldDisplayType.DECISION_BOX, "Is Rotating", FormField.Required.OPTIONAL, 6, 2));
        toolTypesFormFields.add(new FormField("isApprovalNeeded", FacilioField.FieldDisplayType.DECISION_BOX, "Approval Needed", FormField.Required.OPTIONAL, 6, 3));


        FormSection section = new FormSection("Default", 1, toolTypesFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        toolTypesForm.setSections(Collections.singletonList(section));
        toolTypesForm.setIsSystemForm(true);
        toolTypesForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(toolTypesForm);
    }
    private static void addSystemButtons() throws Exception {
        SystemButtonRuleContext editToolTypes = new SystemButtonRuleContext();
        editToolTypes.setName("Edit");
        editToolTypes.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        editToolTypes.setIdentifier("edit");
        editToolTypes.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.TOOL_TYPES,editToolTypes);
    }

}
