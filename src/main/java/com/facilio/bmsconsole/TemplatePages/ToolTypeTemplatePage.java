package com.facilio.bmsconsole.TemplatePages;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import org.json.simple.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class ToolTypeTemplatePage implements TemplatePageFactory {
    @Override
    public String getModuleName() {
        return FacilioConstants.ContextNames.TOOL_TYPES;
    }

    public PagesContext getTemplatePage(ApplicationContext app, FacilioModule module) throws Exception {

        return new PagesContext(null, null, "", null, true, false, false)
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
                .layoutDone();
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

    private static JSONObject getSummaryCardDetails(Map<String, Map<String, String>> itemMap) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        return FieldUtil.getAsJSON(itemMap, true);
    }

    private static JSONObject getToolsBalanceAndQuantity() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Map<String, Map<String, String>> itemQuantityDetails = new HashMap<>();

        itemQuantityDetails.put("leftField", constructFieldMap("currentQuantity", "Current balance", "false"));
        itemQuantityDetails.put("midField", constructFieldMap("quantity", "Available Quantity", "false"));
        itemQuantityDetails.put("rightField", constructFieldMap("minimumQuantity", "Minimum Quantity", "false"));

        return getSummaryCardDetails(itemQuantityDetails);
    }

    private static Map<String, String> constructFieldMap(String fieldName, String DisplayName, String isDateField) {

        Map<String, String> field = new HashMap<>();
        field.put("FieldName", fieldName);
        field.put("DisplayName", DisplayName);
        field.put("isDateField", isDateField);
        return field;
    }

    private static JSONObject getToolsDate() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Map<String, Map<String, String>> itemDateDetails = new HashMap<>();

        itemDateDetails.put("leftField", constructFieldMap("lastIssuedDate", "Last issued date", "true"));
        itemDateDetails.put("rightField", constructFieldMap("lastPurchasedDate", "Last purchased date", "true"));

        return getSummaryCardDetails(itemDateDetails);
    }

    private static JSONObject getPriceAndTrackingCard() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Map<String, Map<String, String>> itemDateDetails = new HashMap<>();

        itemDateDetails.put("leftField", constructFieldMap("sellingPrice", "Selling price per hour", "false"));
        itemDateDetails.put("rightField", constructFieldMap("isRotating", "Individually tracked", "false"));

        return getSummaryCardDetails(itemDateDetails);
    }

    private static JSONObject getSummaryWidgetDetails(String moduleName, ApplicationContext app) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        //general information

        FacilioField description = moduleBean.getField("description", moduleName);
        FacilioField category = moduleBean.getField("category", moduleName);
        FacilioField isApprovalNeeded = moduleBean.getField("isApprovalNeeded", moduleName);
        isApprovalNeeded.setDisplayName("Approval Needed");
        FacilioField isRotating = moduleBean.getField("isRotating", moduleName);
        isRotating.setDisplayName("Rotating");
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
}