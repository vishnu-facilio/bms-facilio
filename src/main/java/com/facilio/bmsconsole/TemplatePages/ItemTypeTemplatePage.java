package com.facilio.bmsconsole.TemplatePages;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.RelatedListWidgetUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.relation.util.RelationshipWidgetUtil;
import org.json.simple.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class ItemTypeTemplatePage implements TemplatePageFactory {
    @Override
    public String getModuleName() {
        return FacilioConstants.ContextNames.ITEM_TYPES;
    }

    @Override
    public PagesContext getTemplatePage(ApplicationContext app, FacilioModule module) throws Exception {

        return new PagesContext(null, null, "", null, true, false, false)
                .addLayout(PagesContext.PageLayoutType.WEB)
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("itemdetails", null, null)
                .addWidget("itemdetails", "Item details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_3", 0, 0, null, getSummaryWidgetDetails(module.getName(), app))
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
                .addWidget("itemstoreroom", "Storeroom", PageWidget.WidgetType.STORE_ROOM, "flexiblewebstoreroom_6", 0, 0, null, null)
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
                .addSection("itemtypebulkrelationship", "Relationships", "List of relationships and types between records across modules")
                .addWidget("itemtypebulkrelationship", "Related", PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET, "flexiblewebbulkrelationshipwidget_6", 0, 0, null, RelationshipWidgetUtil.fetchRelationshipsOfModule(module))
                .widgetDone()
                .sectionDone()
                .addSection("itemtyperelatedlist", "Related List", "List of all related records across modules")
                .addWidget("itemtyperelatedlist", "Related List", PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblewebbulkrelatedlist_6", 0, 0, null, RelatedListWidgetUtil.fetchAllRelatedListForModule(module))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone();
    }

    private static JSONObject getWidgetGroup() throws Exception {
        JSONObject commentWidgetParam = new JSONObject();
        commentWidgetParam.put("notesModuleName", FacilioConstants.ContextNames.ITEM_TYPES_NOTES);
        JSONObject attachmentWidgetParam = new JSONObject();
        attachmentWidgetParam.put("attachmentsModuleName", FacilioConstants.ContextNames.ITEM_TYPES_ATTACHMENTS);
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

    private static JSONObject getItemQuantity() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Map<String, Map<String, String>> itemQuantityDetails = new HashMap<>();

        itemQuantityDetails.put("leftField", constructFieldMap("quantity", "Available Quantity", "false"));
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

    private static JSONObject getItemDate() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Map<String, Map<String, String>> itemDateDetails = new HashMap<>();

        itemDateDetails.put("leftField", constructFieldMap("lastIssuedDate", "Last issued date", "true"));
        itemDateDetails.put("rightField", constructFieldMap("lastPurchasedDate", "Last purchased date", "true"));

        return getSummaryCardDetails(itemDateDetails);
    }

    private static JSONObject getPriceAndTrackingCard() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Map<String, Map<String, String>> itemDateDetails = new HashMap<>();

        itemDateDetails.put("leftField", constructFieldMap("lastPurchasedPrice", "Last purchased price", "false"));
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
        widgetGroup.setName("generalInformation");
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
