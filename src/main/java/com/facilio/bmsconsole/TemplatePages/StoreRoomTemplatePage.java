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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class StoreRoomTemplatePage implements TemplatePageFactory {
    @Override
    public String getModuleName() {
        return FacilioConstants.ContextNames.STORE_ROOM;
    }

    @Override
    public PagesContext getTemplatePage(ApplicationContext app, FacilioModule module) throws Exception {
        return getStoreRoomPage(app,module,true,false);

    }
    public static PagesContext getStoreRoomPage(ApplicationContext app, FacilioModule module,Boolean isTemplate, Boolean isDefault) throws Exception {
        return  new PagesContext("storeRoomPage", "Store Room Page",null, null,isTemplate ,isDefault , false)
                .addWebLayout()
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("summaryfields", null, null)
                .addWidget("summaryFieldsWidget", "Store Room Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_4", 0, 0, null, getSummaryWidgetDetails(module.getName(), app))
                .widgetDone()
                .sectionDone()
                .addSection("ServingSites", null, null)
                .addWidget("servingSites", "Serving Sites", PageWidget.WidgetType.SERVING_SITES_WIDGET, "flexiblewebservingSites_6", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("inventory", "Inventory", PageTabContext.TabType.SIMPLE,true, null)
                .addColumn( PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("items", null, null)
                .addWidget("items", "Items", PageWidget.WidgetType.STOREROOM_ITEMS, "flexiblewebitems_7", 0, 0,  null, null)
                .widgetDone()
                .sectionDone()
                .addSection("tools", null, null)
                .addWidget("tools", "Tools", PageWidget.WidgetType.STOREROOM_TOOLS, "flexiblewebtools_7", 0, 0,  null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("transactions", "Transactions", PageTabContext.TabType.SIMPLE,true, null)
                .addColumn( PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("itemTransactions", null, null)
                .addWidget("itemTransactions", "Item Transactions", PageWidget.WidgetType.STOREROOM_ITEM_TRANSACTIONS, "flexiblewebstoreroomitemtransactions_7", 0, 0,  null, null)
                .widgetDone()
                .sectionDone()
                .addSection("toolTransactions", null, null)
                .addWidget("toolTransactions", "Tool Transactions", PageWidget.WidgetType.STOREROOM_TOOL_TRANSACTIONS, "flexiblewebstoreroomtooltransactions_7", 0, 0,  null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("related", "Related", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("storeroombulkrelationship", "Relationships", "List of relationships and types between records across modules")
                .addWidget("storeroombulkrelationship", "Related", PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET, "flexiblewebbulkrelationshipwidget_6", 0, 0, null, RelationshipWidgetUtil.fetchRelationshipsOfModule(module))
                .widgetDone()
                .sectionDone()
                .addSection("storeroomrelatedlist", "Related List", "List of all related records across modules")
                .addWidget("storeroomrelatedlist", "Related List", PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblewebbulkrelatedlist_6", 0, 0, null, RelatedListWidgetUtil.fetchAllRelatedListForModule(module))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone();

    }




    private static JSONObject getSummaryWidgetDetails(String moduleName, ApplicationContext app) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField siteField = moduleBean.getField("site", moduleName);
        FacilioField approvalNeededField = moduleBean.getField("isApprovalNeeded", moduleName);
        approvalNeededField.setDisplayName("Approval Needed");
        FacilioField gatePassField = moduleBean.getField("isGatePassRequired", moduleName);
        gatePassField.setDisplayName("Gate Pass Required");
        FacilioField ownerField = moduleBean.getField("owner", moduleName);
        FacilioField locationField = moduleBean.getField("location", moduleName);
        FacilioField noOfItemTypes = moduleBean.getField("noOfItemTypes", moduleName);
        FacilioField noOfToolTypes = moduleBean.getField("noOfToolTypes", moduleName);
        FacilioField sysCreatedByField = moduleBean.getField("sysCreatedTime", moduleName);
        sysCreatedByField.setDisplayName("Created Time");

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();

        addSummaryFieldInWidgetGroup(widgetGroup, siteField,1, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, approvalNeededField, 1 , 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, gatePassField, 1, 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, ownerField, 1, 4, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, locationField,2, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, noOfItemTypes,2, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, noOfToolTypes,2, 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, sysCreatedByField,2, 4, 1);


        widgetGroup.setName("primaryDetails");
        widgetGroup.setDisplayName("Primary Details");
        widgetGroup.setColumns(4);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(widgetGroup);

        pageWidget.setDisplayName("");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(app.getId());
        pageWidget.setGroups(widgetGroupList);

        return FieldUtil.getAsJSON(pageWidget);

    }

    private static void addSummaryFieldInWidgetGroup(SummaryWidgetGroup widgetGroup, FacilioField field, int rowIndex, int colIndex, int colSpan) {
        if(field != null) {
            SummaryWidgetGroupFields summaryField = new SummaryWidgetGroupFields();
            summaryField.setName(field.getName());
            summaryField.setDisplayName(field.getDisplayName());
            summaryField.setFieldId(field.getFieldId());
            summaryField.setRowIndex(rowIndex);
            summaryField.setColIndex(colIndex);
            summaryField.setColSpan(colSpan);

            if(widgetGroup.getFields() == null) {
                widgetGroup.setFields(new ArrayList<>(Arrays.asList(summaryField)));
            }
            else {
                widgetGroup.getFields().add(summaryField);
            }
        }
    }
    // to check the implementation of module specific notes
    private static JSONObject getWidgetGroup(boolean isMobile) throws Exception {
        JSONObject notesWidgetParam = new JSONObject();
        notesWidgetParam.put("notesModuleName", "storeRoomNotes");

        JSONObject attachmentWidgetParam = new JSONObject();
        attachmentWidgetParam.put("attachmentsModuleName", "storeRoomAttachments");

        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("notes", "Notes", "")
                .addWidget("commentwidget", "Comment", PageWidget.WidgetType.COMMENT, isMobile?"flexiblemobilecomment_8":"flexiblewebcomment_5", 0, 0, notesWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Documents", "")
                .addWidget("attachmentwidget", "Documents", PageWidget.WidgetType.ATTACHMENT, isMobile?"flexiblemobileattachment_8":"flexiblewebattachment_5", 0, 0, attachmentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();
        return FieldUtil.getAsJSON(widgetGroup);
    }







}

