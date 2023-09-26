package com.facilio.bmsconsole.TemplatePages;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FacilityModuleTemplatePage implements TemplatePageFactory {

    @Override
    public String getModuleName() {
        return FacilioConstants.ContextNames.FacilityBooking.FACILITY;
    }

    @Override
    public PagesContext getTemplatePage(ApplicationContext app, FacilioModule module) throws Exception {
        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.ContextNames.VENDOR_ACTIVITY);

        FacilioModule vendorContactModule = Constants.getModBean().getModule(FacilioConstants.ContextNames.VENDOR_CONTACT);

        return new PagesContext(null, null, "", null, true, false, false)
                .addWebLayout()
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("summaryfields", null, null)
                .addWidget("summaryfieldswidget", "Facility Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_24", 0, 0, null, getSummaryWidgetDetails(module.getName(), app))
                .widgetDone()
                .sectionDone()
                .addSection("facilityphotoes", null, null)
                .addWidget("facilityphotoeswidget", "Photoes", PageWidget.WidgetType.FACILITY_PHOTOS, "webfacilityphotos_15_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_20", 0, 0, null, getWidgetGroup())
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("availability", "Availability", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("featureavailability", null, null)
                .addWidget("featureavailability", "Features available", PageWidget.WidgetType.FACILITY_FEATURES, "flexiblewebfacilityfeatures_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("slotinformation", null, null)
                .addWidget("slotinformation", "Slot Information", PageWidget.WidgetType.FACILITY_SLOT_INFORMATION, "webfacilityslotinformation_49_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("specialavailability", null, null)
                .addWidget("specialavailability", "Special Availabilities", PageWidget.WidgetType.FACILITY_SPECIAL_AVAILABILITY, "flexiblewebfacilityspecialavalability_28_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone();
    }

    private static JSONObject getSummaryWidgetDetails(String moduleName, ApplicationContext app) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField descriptionField = moduleBean.getField("description", moduleName);
        FacilioField facilityType = moduleBean.getField("facilityType", moduleName);
        FacilioField siteId = moduleBean.getField("siteId", moduleName);
        FacilioField location = moduleBean.getField("location", moduleName);
        FacilioField category = moduleBean.getField("category", moduleName);
        FacilioField manager = moduleBean.getField("manager", moduleName);
        FacilioField usageCapacity = moduleBean.getField("usageCapacity", moduleName);
        FacilioField maxAttendeeCountPerBooking = moduleBean.getField("maxAttendeeCountPerBooking", moduleName);
        FacilioField slotGeneratedUpto = moduleBean.getField("slotGeneratedUpto", moduleName);
        FacilioField allowCancellationBefore = moduleBean.getField("allowCancellationBefore", moduleName);
        FacilioField pricePerSlot = moduleBean.getField("pricePerSlot", moduleName);
        FacilioField securityDeposit = moduleBean.getField("securityDeposit", moduleName);


        FacilioField sysCreatedByField = moduleBean.getField("sysCreatedBy", moduleName);
        FacilioField sysCreatedTimeField = moduleBean.getField("sysCreatedTime", moduleName);
        FacilioField sysModifiedByField = moduleBean.getField("sysModifiedBy", moduleName);
        FacilioField sysModifiedTimeField = moduleBean.getField("sysModifiedTime", moduleName);

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup generalInformationWidgetGroup = new SummaryWidgetGroup();


        addSummaryFieldInWidgetGroup(generalInformationWidgetGroup, descriptionField, 1, 1, 4);
        addSummaryFieldInWidgetGroup(generalInformationWidgetGroup, facilityType, 2, 1, 1);
        addSummaryFieldInWidgetGroup(generalInformationWidgetGroup, siteId, 2, 2, 1);
        addSummaryFieldInWidgetGroup(generalInformationWidgetGroup, location, 2, 3, 1);
        addSummaryFieldInWidgetGroup(generalInformationWidgetGroup, category, 2, 4, 1);
        addSummaryFieldInWidgetGroup(generalInformationWidgetGroup, manager, 3, 1, 1);
        addSummaryFieldInWidgetGroup(generalInformationWidgetGroup, usageCapacity, 3, 2, 1);
        addSummaryFieldInWidgetGroup(generalInformationWidgetGroup, maxAttendeeCountPerBooking, 3, 3, 1);
        addSummaryFieldInWidgetGroup(generalInformationWidgetGroup, slotGeneratedUpto, 3, 4, 1);
        addSummaryFieldInWidgetGroup(generalInformationWidgetGroup, allowCancellationBefore, 4, 1, 1);
        addSummaryFieldInWidgetGroup(generalInformationWidgetGroup, pricePerSlot, 4, 2, 1);
        addSummaryFieldInWidgetGroup(generalInformationWidgetGroup, securityDeposit, 4, 3, 1);

        generalInformationWidgetGroup.setName("generalInformation");
        generalInformationWidgetGroup.setDisplayName("Primary Details");
        generalInformationWidgetGroup.setColumns(4);


        SummaryWidgetGroup systemInformationWidgetGroup = new SummaryWidgetGroup();
        systemInformationWidgetGroup.setName("systemInformation");
        systemInformationWidgetGroup.setDisplayName("System Information");
        systemInformationWidgetGroup.setColumns(4);

        addSummaryFieldInWidgetGroup(systemInformationWidgetGroup, sysCreatedByField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(systemInformationWidgetGroup, sysCreatedTimeField, 1, 2, 1);
        addSummaryFieldInWidgetGroup(systemInformationWidgetGroup, sysModifiedByField, 1, 3, 1);
        addSummaryFieldInWidgetGroup(systemInformationWidgetGroup, sysModifiedTimeField, 1, 4, 1);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(generalInformationWidgetGroup);
        widgetGroupList.add(systemInformationWidgetGroup);

        pageWidget.setDisplayName("");
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

    private static JSONObject getWidgetGroup() throws Exception {
        JSONObject commentWidgetParam = new JSONObject();
        commentWidgetParam.put("notesModuleName", FacilioConstants.ContextNames.FacilityBooking.FACILITY_NOTES);
        JSONObject attachmentWidgetParam = new JSONObject();
        attachmentWidgetParam.put("attachmentsModuleName", FacilioConstants.ContextNames.FacilityBooking.FACILITY_ATTACHMENTS);
        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("comments", "Comments", "")
                .addWidget("commentwidget", "Comments", PageWidget.WidgetType.COMMENT, "flexiblewebcomment_27", 0, 0, commentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Attachments", "")
                .addWidget("attachmentwidget", "Documents", PageWidget.WidgetType.ATTACHMENT, "flexiblewebattachment_27", 0, 0, attachmentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();
        return FieldUtil.getAsJSON(widgetGroup);
    }

}