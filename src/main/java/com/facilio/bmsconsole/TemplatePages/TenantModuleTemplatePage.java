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
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TenantModuleTemplatePage implements TemplatePageFactory{
    @Override
    public String getModuleName() {
        return FacilioConstants.ContextNames.TENANT;
    }

    @Override
    public PagesContext getTemplatePage(ApplicationContext app, FacilioModule module) throws Exception {
        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.ContextNames.TENANT_ACTIVITY);
        return new PagesContext(null, null, "", null, true, false, false)
                .addWebLayout()
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("tenantdetailcontact", null, null)
                .addWidget("tenantdetailcontactwidget", "Primary Contact", PageWidget.WidgetType.TENANT_DETAIL_CONTACT, "webtenantdetailcontactwidget_13_6", 0, 0,null,null )
                .widgetDone()
                .addWidget("tenantspecialwidget", "Details", PageWidget.WidgetType.TENANT_DETAIL_OVERVIEW, "webtenantdetailoverviewwidget_13_6", 6, 0,null,null )
                .widgetDone()
                .sectionDone()
                .addSection("occupyingunitsvaccatedunits",null,null)
                .addWidget("occupyingunitsvaccatedunitswidget", "Name", PageWidget.WidgetType.TENANT_SPECIAL_WIDGET, "webtenantspecialwidget_31_9", 0, 0,null,null )
                .widgetDone()
                .addWidget("tenantworkorders", "Workorders", PageWidget.WidgetType.TENANT_WORKORDERS, "webtenantworkorders_13_3", 9, 0,null,null )
                .widgetDone()
                .addWidget("tenantrecentlyclosedworkorder", "Recently Closed Work order", PageWidget.WidgetType.TENANT_RECENTLY_CLOSED_WORKORDER, "webtenantrecentlyclosedworkorder_23_3", 9, 13,null,null )
                .widgetDone()
                .addWidget("tenantfacilitybookingwidget", "Facility Bookings", PageWidget.WidgetType.TENANT_BOOKINGS, "webtenantfacilitybookingwidget_13_3", 9, 36,null,null )
                .widgetDone()
                .addWidget("tenantupcomingfacilitybooking", "Upcoming Facility Booking", PageWidget.WidgetType.TENANT_UPCOMING_BOOKING, "webtenantupcomingfacilitybookingwidget_10_3", 9, 49,null,null )
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("tenantcontact", "Tenant Contacts", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("tenantcontactrelatedlist", "", "")
                .addWidget("tenantcontactrelatedlistWidget", "Tenant Contact", PageWidget.WidgetType.RELATED_LIST, "flexiblewebrelatedlist_29", 0, 0, null, RelatedListWidgetUtil.getSingleRelatedListForModule(module,FacilioConstants.ContextNames.TENANT_CONTACT,"tenant"))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("information", "Notes & Information", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("summaryfields", null, null)
                .addWidget("summaryfieldswidget", "Tenant Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_24", 0, 0, null, getSummaryWidgetDetails(module.getName(), app))
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_20", 0, 0, null, getWidgetGroup())
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("history", "History", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("history", null, null)
                .addWidget("historyWidget", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_20", 0, 0, historyWidgetParam, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone();
    }
    private static JSONObject getSummaryWidgetDetails(String moduleName, ApplicationContext app) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField siteField = moduleBean.getField("siteId", moduleName);
        FacilioField categoryField = moduleBean.getField("tenantType", moduleName);
        FacilioField sysCreatedTimeField = moduleBean.getField("sysCreatedTime", moduleName);
        FacilioField sysModifiedTimeField = moduleBean.getField("sysModifiedTime", moduleName);
        FacilioField streetField = moduleBean.getField("street", FacilioConstants.ContextNames.LOCATION);
        FacilioField cityField = moduleBean.getField("city", FacilioConstants.ContextNames.LOCATION);
        FacilioField stateField = moduleBean.getField("state", FacilioConstants.ContextNames.LOCATION);
        FacilioField zipField = moduleBean.getField("zip", FacilioConstants.ContextNames.LOCATION);
        FacilioField countryField = moduleBean.getField("country", FacilioConstants.ContextNames.LOCATION);
        FacilioField addressField = moduleBean.getField("address", moduleName);

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup generalInformationWidgetGroup = new SummaryWidgetGroup();
        generalInformationWidgetGroup.setName("generalInformation");
        generalInformationWidgetGroup.setDisplayName("General Information");
        generalInformationWidgetGroup.setColumns(4);

        addSummaryFieldInWidgetGroup(generalInformationWidgetGroup, siteField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(generalInformationWidgetGroup, categoryField, 1, 2, 1,null,"Category");

        SummaryWidgetGroup systemInformationWidgetGroup = new SummaryWidgetGroup();
        systemInformationWidgetGroup.setName("systemInformation");
        systemInformationWidgetGroup.setDisplayName("System Information");
        systemInformationWidgetGroup.setColumns(4);

        addSummaryFieldInWidgetGroup(systemInformationWidgetGroup, sysCreatedTimeField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(systemInformationWidgetGroup, sysModifiedTimeField, 1, 2, 1);

        SummaryWidgetGroup addressWidgetGroup = new SummaryWidgetGroup();
        addressWidgetGroup.setName("address");
        addressWidgetGroup.setDisplayName("Address");
        addressWidgetGroup.setColumns(4);

        addSummaryFieldInWidgetGroup(addressWidgetGroup, streetField, 1, 1, 1, addressField);
        addSummaryFieldInWidgetGroup(addressWidgetGroup, cityField, 1, 2, 1, addressField);
        addSummaryFieldInWidgetGroup(addressWidgetGroup, stateField, 1, 3, 1, addressField);
        addSummaryFieldInWidgetGroup(addressWidgetGroup, zipField, 1, 4, 1, addressField);
        addSummaryFieldInWidgetGroup(addressWidgetGroup, countryField, 2, 1, 1, addressField);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(generalInformationWidgetGroup);
        widgetGroupList.add(systemInformationWidgetGroup);
        widgetGroupList.add(addressWidgetGroup);

        pageWidget.setDisplayName("");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(app.getId());
        pageWidget.setGroups(widgetGroupList);

        return FieldUtil.getAsJSON(pageWidget);

    }

    private static void addSummaryFieldInWidgetGroup(SummaryWidgetGroup widgetGroup, FacilioField field, int rowIndex, int colIndex, int colSpan) {
        addSummaryFieldInWidgetGroup(widgetGroup, field, rowIndex, colIndex, colSpan, null,null);
    }
    private static void addSummaryFieldInWidgetGroup(SummaryWidgetGroup widgetGroup, FacilioField field, int rowIndex, int colIndex, int colSpan,FacilioField lookupField) {
        addSummaryFieldInWidgetGroup(widgetGroup, field, rowIndex, colIndex, colSpan, lookupField,null);
    }

    private static void addSummaryFieldInWidgetGroup(SummaryWidgetGroup widgetGroup, FacilioField field, int rowIndex, int colIndex, int colSpan, FacilioField lookupField,String displayName) {
        if (field != null) {
            SummaryWidgetGroupFields summaryField = new SummaryWidgetGroupFields();
            summaryField.setName(field.getName());
            if(StringUtils.isNotEmpty(displayName)){
                summaryField.setDisplayName(displayName);
            }else {
                summaryField.setDisplayName(field.getDisplayName());
            }
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

    private static JSONObject getWidgetGroup() throws Exception {
        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("comments", "Comments", "")
                .addWidget("commentwidget", "Comments", PageWidget.WidgetType.COMMENT, "flexiblewebcomment_27", 0, 0, null, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Documents", "")
                .addWidget("attachmentwidget", "Documents", PageWidget.WidgetType.ATTACHMENT, "flexiblewebattachment_27", 0, 0, null, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();


        return FieldUtil.getAsJSON(widgetGroup);
    }
}
