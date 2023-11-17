package com.facilio.fsm.commands;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsoleV3.signup.util.PagesUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.*;

@Log4j
public class ServiceOrderDefaultPageCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        FacilioModule module = (FacilioModule) context.get(FacilioConstants.ContextNames.MODULE);
        String moduleName = FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER;
        try {
            List<String> appNames = new ArrayList<>();
            appNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
            appNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
            appNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);
            PagesUtil.addSystemPages(moduleName, getCustomModuleDefaultPagesForApps(module, appNames));
        } catch (Exception e) {
            LOGGER.info("Error occurred while creating default page for the module -- " + moduleName + ", error message - " + e.getMessage());
        }
        return false;
    }

    private static Map<String, List<PagesContext>> getCustomModuleDefaultPagesForApps(FacilioModule module, List<String> appNames) throws Exception {
        Map<String,List<PagesContext>> appNameVsPage = new HashMap<>();
        for (String appName : appNames) {
            ApplicationContext app = ApplicationApi.getApplicationForLinkName(appName);
            appNameVsPage.put(appName,createCustomPage(app, module, true,false));
        }
        return appNameVsPage;
    }

    public static List<PagesContext> createCustomPage(ApplicationContext app, FacilioModule module, boolean isTemplate, boolean isDefault) throws Exception {
        String pageName, pageDisplayName;
        pageName = module.getName().toLowerCase().replaceAll("[^a-zA-Z0-9]+", "") + "defaultpage";
        pageDisplayName = "Default "+module.getDisplayName()+" Page ";

        JSONObject classificationWidgetParam = new JSONObject();
        classificationWidgetParam.put("activityModuleName", FacilioConstants.ContextNames.SITE_ACTIVITY);


        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.ContextNames.CUSTOM_ACTIVITY);


        JSONObject failureReportWidgetParam = new JSONObject();
        failureReportWidgetParam.put("card", "failurereports");


        JSONObject timeLogWidgetParam = new JSONObject();
        timeLogWidgetParam.put("card", "timeLog");

        if(app.getDomainType() == AppDomain.AppDomainType.FACILIO.getIndex()) {
            return   new ModulePages()
                    .addPage(pageName, pageDisplayName,"", null, isTemplate, isDefault, false)
                    .addWebLayout()
                    .addTab("summary", "Summary",PageTabContext.TabType.SIMPLE,  true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("summaryFields", null, null)
                    .addWidget("summaryFieldsWidget", "Summary", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, getSummaryWidgetDetails(app,module.getName()))
                    .widgetDone()
                    .sectionDone()
                    .addSection("widgetGroup", null, null)
                    .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 4, null, getWidgetGroup(false))
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()
                    .addTab("task", "Task",PageTabContext.TabType.SIMPLE,  true, null)
                    .addColumn( PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("task", "Tasks", "List of Tasks created for this Work Order")
                    .addWidget("bulkRelationshipWidget", "Relationships", PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET, "", 0, 0,  null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()
                    .layoutDone()
                    .pageDone()
                    .getCustomPages();

        }
        else {
            return    new ModulePages()
                    .addPage(pageName, pageDisplayName,"", null, isTemplate, isDefault, false)
                    .addWebLayout()
                    .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("summaryFields", null, null)
                    .addWidget("summaryFieldsWidget", "Summary Widget", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, getSummaryWidgetDetails(app,module.getName()))
                    .widgetDone()
                    .sectionDone()
                    .addSection("widgetGroup", null, null)
                    .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 4, null, getWidgetGroup(false))
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()
                    .addTab("task", "Task",PageTabContext.TabType.SIMPLE,  true, null)
                    .addColumn( PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("task", "Tasks", "List of Tasks created for this Work Order")
                    .addWidget("bulkRelationshipWidget", "Relationships", PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET, "flexiblewebbulkrelationshipwidget_6", 0, 0,  null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()
                    .layoutDone()
                    .pageDone()
                    .getCustomPages();
        }
    }

    private static JSONObject getSummaryWidgetDetails(ApplicationContext app, String moduleName) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField nameField = moduleBean.getField("name", moduleName);
        FacilioField categoryField = moduleBean.getField("category", moduleName);
        FacilioField statusField = moduleBean.getField("status", moduleName);
        FacilioField priorityField = moduleBean.getField("priority", moduleName);

        FacilioField sourceTypeField = moduleBean.getField("sourceType", moduleName);
        FacilioField maintenanceTypeField = moduleBean.getField("maintenancetype", moduleName);
        FacilioField autoCreateSaField = moduleBean.getField("autoCreateSa", moduleName);

        FacilioField descriptionField = moduleBean.getField("description", moduleName);

        FacilioField siteField = moduleBean.getField("site", moduleName);
        FacilioField locationField = moduleBean.getField("location", moduleName);
        FacilioField spaceField = moduleBean.getField("space", moduleName);
        FacilioField assetField = moduleBean.getField("asset", moduleName);

        FacilioField fieldAgentField = moduleBean.getField("fieldAgent", moduleName);
        FacilioField vendorField = moduleBean.getField("vendor", moduleName);
        FacilioField clientField = moduleBean.getField("client", moduleName);

        FacilioField prefStartTimeField = moduleBean.getField("preferredStartTime", moduleName);
        FacilioField prefEndTimeField = moduleBean.getField("preferredEndTime", moduleName);

        FacilioField responseDueDurationField = moduleBean.getField("responseDueDate", moduleName);
        FacilioField resolutionDueDurationField = moduleBean.getField("resolutionDueDate", moduleName);
        FacilioField responseDueDateField = moduleBean.getField("responseDueDate", moduleName);
        FacilioField resolutionDueDateField = moduleBean.getField("resolutionDueDate", moduleName);
        FacilioField responseDueStatusField = moduleBean.getField("status", moduleName);
        FacilioField resolutionDueStatusField = moduleBean.getField("status", moduleName);

        FacilioField sysCreatedByField = moduleBean.getField("sysCreatedBy", moduleName);
        FacilioField sysCreatedTimeField = moduleBean.getField("sysCreatedTime", moduleName);
        FacilioField sysModifiedByField = moduleBean.getField("sysModifiedBy", moduleName);
        FacilioField sysModifiedTimeField = moduleBean.getField("sysModifiedTime", moduleName);

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();

        addSummaryFieldInWidgetGroup(widgetGroup, nameField, 1 , 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, categoryField, 1 , 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, statusField, 1 , 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, priorityField, 1 , 4, 1);

        addSummaryFieldInWidgetGroup(widgetGroup, sourceTypeField, 2 , 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, maintenanceTypeField, 2 , 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, autoCreateSaField, 2 , 4, 1);

        addSummaryFieldInWidgetGroup(widgetGroup, descriptionField, 3 , 1, 4);

        addSummaryFieldInWidgetGroup(widgetGroup, siteField, 4 , 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, locationField, 4 , 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, spaceField, 4 , 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, assetField, 4 , 4, 1);

        addSummaryFieldInWidgetGroup(widgetGroup, fieldAgentField, 5 , 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, vendorField, 5, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, clientField, 5, 3, 1);

        addSummaryFieldInWidgetGroup(widgetGroup, prefStartTimeField, 6 , 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, prefEndTimeField, 6, 2, 1);

        addSummaryFieldInWidgetGroup(widgetGroup, responseDueDurationField, 7 , 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, resolutionDueDurationField, 7, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, responseDueDateField, 7 , 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, resolutionDueDateField, 7 , 4, 1);

        addSummaryFieldInWidgetGroup(widgetGroup, responseDueStatusField, 8 , 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, resolutionDueStatusField, 8, 2, 1);


        addSummaryFieldInWidgetGroup(widgetGroup, sysCreatedByField, 9, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, sysCreatedTimeField, 9, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, sysModifiedByField,9, 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, sysModifiedTimeField, 9, 4, 1);


        widgetGroup.setName("moduleDetails");
        widgetGroup.setDisplayName("Module Details");
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

    private static JSONObject getWidgetGroup(boolean isMobile) throws Exception {
        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("notes", "Notes", "")
                .addWidget("commentWidget", "Comment", PageWidget.WidgetType.COMMENT, isMobile?"flexiblemobilecomment_8":"flexiblewebcomment_5", 0, 4, null, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Documents", "")
                .addWidget("attachmentWidget", "Documents", PageWidget.WidgetType.ATTACHMENT, isMobile?"flexiblemobileattachment_8":"flexiblewebattachment_5", 0, 4, null, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();


        return FieldUtil.getAsJSON(widgetGroup);
    }

}
