package com.facilio.bmsconsole.TemplatePages;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class PlannedMaintenanceTemplatePageFactory implements TemplatePageFactory {
    @Override
    public String getModuleName() {
        return FacilioConstants.ContextNames.PLANNEDMAINTENANCE;
    }

    @Override
    public PagesContext getTemplatePage(ApplicationContext app, FacilioModule module) throws Exception {
        String pageName, pageDisplayName;
        pageName = module.getName().toLowerCase().replaceAll("[^a-zA-Z0-9]+", "") + "defaultpage";
        pageDisplayName = "Default " + module.getDisplayName() + " Page ";


        return new PagesContext(pageName, pageDisplayName, "", null, true, false, false)
                .addWebLayout()

                // Summary Tab
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)

                .addSection("plannedMaintenanceSummaryFieldsWidget", null, null)
                .addWidget("summaryFieldsWidget", "Planned Maintenance Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET,
                        "flexiblewebsummaryfieldswidget_3", 0, 0, null,
                        getPMDetailsSummaryWidgetDetails(module.getName(), app))
                .widgetDone()
                .addWidget("workOrderDetailsSummaryFieldsWidget", "Work Order Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET,
                        "flexiblewebsummaryfieldswidget_4", 0, 0, null,
                        getPMWorkorderDetailsSummaryWidgetDetails(module.getName(), app))
                .widgetDone()
                .sectionDone()

                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                // Planner Tab
                .addTab("planner", "Planner", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("pmPlannerDetailsSection", null, null)
                .addWidget("pmPlannerTriggerDetailsWidget", null, PageWidget.WidgetType.PM_PLANNER_TRIGGER_DETAILS_WIDGET,
                        "flexiblewebpmPlannerTriggerDetails_4", 0, 0, null,
                        null)
                .widgetDone()
                .addWidget("ppResourcePlannerDetailsWidget", null, PageWidget.WidgetType.PM_RESOURCE_PLANNER_DETAILS_WIDGET,
                        "flexiblewebpmResourcePlannerDetails_7", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                // Scheduler tab
                .addTab("scheduler", "Scheduler", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("pmSchedulerDetailsSection", null, null)
                .addWidget("pmSchedulerDetailsWidget", null, PageWidget.WidgetType.PM_SCHEDULER_DETAILS_WIDGET,
                        "flexiblewebpmSchedulerDetails_11", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .layoutDone();
    }


    public static JSONObject getPMWorkorderDetailsSummaryWidgetDetails(String moduleName, ApplicationContext app) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);
        List<FacilioField> fieldList = moduleBean.getAllFields(moduleName);
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fieldList);

        FacilioField subjectField = fieldsMap.get("subject");
        FacilioField descriptionField = fieldsMap.get("description");
        FacilioField typeField = fieldsMap.get("type");
        FacilioField categoryField = fieldsMap.get("category");
        FacilioField priorityField = fieldsMap.get("priority");
        FacilioField dueDateField = fieldsMap.get("dueDate");
        FacilioField estimatedWorkHoursField = fieldsMap.get("estimatedWorkDuration");
        FacilioField dueDurationField = fieldsMap.get("dueDuration");
        FacilioField teamField = fieldsMap.get("assignmentGroup");
        FacilioField sysModifiedByField = fieldsMap.get("sysModifiedBy");

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup workOrderDetailsWidgetGroup = new SummaryWidgetGroup();
        addSummaryFieldInWidgetGroup(workOrderDetailsWidgetGroup, subjectField, 1, 1, 4);
        addSummaryFieldInWidgetGroup(workOrderDetailsWidgetGroup, descriptionField, 2, 1, 4);


        addSummaryFieldInWidgetGroup(workOrderDetailsWidgetGroup, typeField, 3, 1, 1);
        addSummaryFieldInWidgetGroup(workOrderDetailsWidgetGroup, priorityField, 3, 2, 1);
        addSummaryFieldInWidgetGroup(workOrderDetailsWidgetGroup, categoryField, 3, 3, 1);
        addSummaryFieldInWidgetGroup(workOrderDetailsWidgetGroup, dueDateField, 3, 4, 1);

        addSummaryFieldInWidgetGroup(workOrderDetailsWidgetGroup, dueDurationField, 4, 1, 1);
        addSummaryFieldInWidgetGroup(workOrderDetailsWidgetGroup, estimatedWorkHoursField, 4, 2, 1);
        addSummaryFieldInWidgetGroup(workOrderDetailsWidgetGroup, teamField, 4, 3, 1);

        workOrderDetailsWidgetGroup.setName("workOrderDetailsSummaryFieldsWidgetGroup");
        workOrderDetailsWidgetGroup.setColumns(4);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(workOrderDetailsWidgetGroup);

        pageWidget.setDisplayName("");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(app.getId());
        pageWidget.setGroups(widgetGroupList);

        return FieldUtil.getAsJSON(pageWidget);

    }

    public static JSONObject getPMDetailsSummaryWidgetDetails(String moduleName, ApplicationContext app) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);
        List<FacilioField> fieldList = moduleBean.getAllFields(moduleName);
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fieldList);

        FacilioField siteIdField = fieldsMap.get("sites");
        FacilioField scopeCategoryField = fieldsMap.get("assignmentType");
        FacilioField spaceCategoryField = fieldsMap.get("spaceCategory");
        FacilioField assetCategoryField = fieldsMap.get("assetCategory");
        FacilioField sysCreatedByField = fieldsMap.get("createdBy");
        FacilioField sysCreatedTimeField = fieldsMap.get("sysCreatedTime");
        FacilioField sysModifiedByField = fieldsMap.get("sysModifiedBy");
        FacilioField sysModifiedTimeField = fieldsMap.get("modifiedTime");


        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();

        addSummaryFieldInWidgetGroup(widgetGroup, siteIdField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, scopeCategoryField, 1, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, spaceCategoryField, 1, 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, assetCategoryField, 1, 4, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, sysCreatedByField, 2, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, sysCreatedTimeField, 2, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, sysModifiedByField, 2, 3,1);
        addSummaryFieldInWidgetGroup(widgetGroup, sysModifiedTimeField, 2, 4, 1);


        widgetGroup.setName("pmSummaryFieldsWidget");
        widgetGroup.setColumns(4);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(widgetGroup);

        pageWidget.setDisplayName("");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(app.getId());
        pageWidget.setGroups(widgetGroupList);

        return FieldUtil.getAsJSON(pageWidget);

    }

    public static void addSummaryFieldInWidgetGroup(SummaryWidgetGroup widgetGroup, FacilioField field, int rowIndex, int colIndex, int colSpan) {
        if (field != null) {
            SummaryWidgetGroupFields summaryField = new SummaryWidgetGroupFields();
            summaryField.setName(field.getName());
            summaryField.setDisplayName(field.getDisplayName());
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

    public static JSONObject getWidgetGroup(boolean isMobile) throws Exception {

        JSONObject notesWidgetParam = new JSONObject();
        notesWidgetParam.put("notesModuleName", "cmdnotes");

        JSONObject attachmentsWidgetParam = new JSONObject();
        attachmentsWidgetParam.put("attachmentsModuleName", "cmdattachments");

        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("notes", "Comments", null)
                .addWidget("comments", "Comments", PageWidget.WidgetType.COMMENT, isMobile ? "flexiblemobilecomment_8" : "flexiblewebcomment_5", 0, 0, notesWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Attachments", null)
                .addWidget("documents", "Attachments", PageWidget.WidgetType.ATTACHMENT, isMobile ? "flexiblemobileattachment_8" : "flexiblewebattachment_5", 0, 0, attachmentsWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();

        return FieldUtil.getAsJSON(widgetGroup);
    }

    private static JSONObject getMobileRelatedWidgetGroup() throws Exception {

        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("relationshipssection", "Relationships", "List of relationships and types between records across modules")
                .addWidget("relationships", null, PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET, "flexiblemobilebulkrelationshipwidget_8", 0, 0, null, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("relatedlistsection", "Related List", "List of related records across modules")
                .addWidget("relatedlist", null, PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblemobilebulkrelatedlist_8", 0, 0, null, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();

        return FieldUtil.getAsJSON(widgetGroup);
    }
}
