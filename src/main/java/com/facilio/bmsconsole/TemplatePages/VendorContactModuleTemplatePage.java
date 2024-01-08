package com.facilio.bmsconsole.TemplatePages;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.page.PageWidget;
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

public class VendorContactModuleTemplatePage implements TemplatePageFactory {
    @Override
    public String getModuleName() {
        return FacilioConstants.ContextNames.VENDOR_CONTACT;
    }

    @Override
    public PagesContext getTemplatePage(ApplicationContext app, FacilioModule module) throws Exception {
        if(app.getLinkName().equals(FacilioConstants.ApplicationLinkNames.FSM_APP)){
            return getFSMTemplatePage(app,module);
        }
        return new PagesContext(null, null, "", null, true, false, true)
                .addWebLayout()
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("summaryfields", null, null)
                .addWidget("summaryfieldswidget", "Contact Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, getSummaryWidgetDetails(app, module.getName()))
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getSummaryWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone();
    }
    public PagesContext getFSMTemplatePage(ApplicationContext app, FacilioModule module) throws Exception {
        return new PagesContext(null, null, "", null, true, false, false)
                .addWebLayout()
                .addTab("vendorContactSummary", "Summary",PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("vendorSummaryFields", null, null)
                .addWidget("vendorSummaryFieldsWidget", "Vendor Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, getSummaryWidgetDetails(app,FacilioConstants.ContextNames.VENDOR_CONTACT))
                .widgetDone()
                .sectionDone()
                .addSection("vendorLocationStatus", null, null)
                .addWidget("vendorLastKnownLocationWidget", "Last Known Location", PageWidget.WidgetType.VENDOR_CONTACT_LAST_KNOWN_LOCATION, "webVendorContactLastKnownLocation_4_6", 0, 0, null, null)
                .widgetDone()
                .addWidget("vendorCurrentStatusWidget", "Current Status", PageWidget.WidgetType.VENDOR_CONATCT_CURRENT_STATUS, "webVendorContactCurrentStatus_4_6", 6, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getSummaryWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("vendorSkill", "Skill",PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("vendorSkill", null, null)
                .addWidget("vendorSkillWidget", "SkillS", PageWidget.WidgetType.VENDOR_CONTACT_SKILL, "flexibleVendorContactSkill_10", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("vendorWorkSchedule", "Work Schedule",PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("vendorWorkSchedule", null, null)
                .addWidget("vendorWorkScheduleWidget", "Work Schedule", PageWidget.WidgetType.VENDOR_CONTACT_WORK_SCHEDULE, "flexibleVendorContactWorkSchedule_10", 0, 0, null,null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("vendorHistory", "History",PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("history", null, null)
                .addWidget("historyWidget", "History", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_10", 0, 0, null, getHistoryWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone()
                ;

    }

    private static JSONObject getSummaryWidgetDetails(ApplicationContext app, String moduleName) throws Exception {

        if(app.getLinkName().equals(FacilioConstants.ApplicationLinkNames.FSM_APP)){
            return getFSMSummaryWidgetDetails(app,moduleName);
        }

        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField vendorField = moduleBean.getField("vendor", moduleName);
        FacilioField isPrimaryContactField = moduleBean.getField("isPrimaryContact", moduleName);
        FacilioField emailField = moduleBean.getField("email", moduleName);
        FacilioField phoneField = moduleBean.getField("phone", moduleName);
        FacilioField sysCreatedByField = moduleBean.getField("sysCreatedBy", moduleName);
        FacilioField sysCreatedTimeField = moduleBean.getField("sysCreatedTime", moduleName);
        FacilioField sysModifiedByField = moduleBean.getField("sysModifiedBy", moduleName);
        FacilioField sysModifiedTimeField = moduleBean.getField("sysModifiedTime", moduleName);

        SummaryWidget pageWidget = new SummaryWidget();

        SummaryWidgetGroup generalInformationWidgetGroup = new SummaryWidgetGroup();
        generalInformationWidgetGroup.setName("generalInformation");
        generalInformationWidgetGroup.setColumns(4);

        addSummaryFieldInWidgetGroup(generalInformationWidgetGroup, vendorField, 1, 1, 1,"Vendor");
        addSummaryFieldInWidgetGroup(generalInformationWidgetGroup, isPrimaryContactField, 1, 2, 1);
        addSummaryFieldInWidgetGroup(generalInformationWidgetGroup, emailField, 1, 3, 1);
        addSummaryFieldInWidgetGroup(generalInformationWidgetGroup, phoneField, 1, 4, 1);


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
    private static JSONObject getFSMSummaryWidgetDetails(ApplicationContext app, String moduleName) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField nameField = moduleBean.getField("name", moduleName);
        FacilioField phoneField = moduleBean.getField("phone", moduleName);
        FacilioField emailField = moduleBean.getField("email", moduleName);
        FacilioField dispatchableField = moduleBean.getField("dispatchable", moduleName);
        FacilioField trackGeoLocationField = moduleBean.getField("trackGeoLocation", moduleName);
        FacilioField territoriesField = moduleBean.getField("territories", moduleName);
        FacilioField sysCreatedByField = moduleBean.getField("sysCreatedBy", moduleName);
        FacilioField sysCreatedTimeField = moduleBean.getField("sysCreatedTime", moduleName);
        FacilioField sysModifiedByField = moduleBean.getField("sysModifiedBy", moduleName);
        FacilioField sysModifiedTimeField = moduleBean.getField("sysModifiedTime", moduleName);

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();

        addSummaryFieldInWidgetGroup(widgetGroup, nameField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, phoneField, 1, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, emailField, 1, 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, dispatchableField, 1, 4, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, trackGeoLocationField, 2, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, territoriesField, 2, 2, 1);

        SummaryWidgetGroup systemInformationWidgetGroup = new SummaryWidgetGroup();
        systemInformationWidgetGroup.setName("systemInformation");
        systemInformationWidgetGroup.setDisplayName("System Information");
        systemInformationWidgetGroup.setColumns(4);

        addSummaryFieldInWidgetGroup(systemInformationWidgetGroup, sysCreatedByField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(systemInformationWidgetGroup, sysCreatedTimeField, 1, 2, 1);
        addSummaryFieldInWidgetGroup(systemInformationWidgetGroup, sysModifiedByField, 1, 3, 1);
        addSummaryFieldInWidgetGroup(systemInformationWidgetGroup, sysModifiedTimeField, 1, 4, 1);


        widgetGroup.setName("moduleDetails");
        widgetGroup.setDisplayName("General Information");
        widgetGroup.setColumns(4);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(widgetGroup);
        widgetGroupList.add(systemInformationWidgetGroup);

        pageWidget.setDisplayName("");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(app.getId());
        pageWidget.setGroups(widgetGroupList);

        return FieldUtil.getAsJSON(pageWidget);

    }

    private static void addSummaryFieldInWidgetGroup(SummaryWidgetGroup widgetGroup, FacilioField field, int rowIndex, int colIndex, int colSpan) {
        addSummaryFieldInWidgetGroup(widgetGroup,field,rowIndex,colIndex,colSpan,null);
    }
    private static void addSummaryFieldInWidgetGroup(SummaryWidgetGroup widgetGroup, FacilioField field, int rowIndex, int colIndex, int colSpan,String displayName) {
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

            if (widgetGroup.getFields() == null) {
                widgetGroup.setFields(new ArrayList<>(Arrays.asList(summaryField)));
            } else {
                widgetGroup.getFields().add(summaryField);
            }
        }
    }

    private static JSONObject getSummaryWidgetGroup(boolean isMobile) throws Exception {
        JSONObject commentWidgetParam = new JSONObject();
        commentWidgetParam.put("notesModuleName", "peoplenotes");

        JSONObject attachmentWidgetParam = new JSONObject();
        attachmentWidgetParam.put("attachmentsModuleName", "peopleattachments");

        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("notes", "Notes", "")
                .addWidget("commentWidget", "Comment", PageWidget.WidgetType.COMMENT, isMobile ? "flexiblemobilecomment_8" : "flexiblewebcomment_5", 0, 0, commentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Documents", "")
                .addWidget("attachmentWidget", "Documents", PageWidget.WidgetType.ATTACHMENT, isMobile ? "flexiblemobileattachment_8" : "flexiblewebattachment_5", 0, 0, attachmentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();

        return FieldUtil.getAsJSON(widgetGroup);
    }

    private static JSONObject getHistoryWidgetGroup(boolean isMobile) throws Exception {
        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.ContextNames.VENDOR_CONTACTS_ACTIVITY);

        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("fieldUpdate", "Field Update", "")
                .addWidget("fieldUpdateWidget", "Field Update", PageWidget.WidgetType.ACTIVITY, isMobile ? "flexiblemobilecomment_8" : "flexiblewebactivity_10", 0, 0, historyWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("location", "Location", "")
                .addWidget("locationWidget", "Location", PageWidget.WidgetType.VENDOR_LOCATION, isMobile ? "flexiblemobileattachment_8" : "flexibleVendorLocation_10", 0, 0, null, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();

        return FieldUtil.getAsJSON(widgetGroup);
    }
}
