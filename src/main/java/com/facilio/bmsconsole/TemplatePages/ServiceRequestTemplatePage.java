package com.facilio.bmsconsole.TemplatePages;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
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

public class ServiceRequestTemplatePage implements TemplatePageFactory {
    @Override
    public String getModuleName() {
        return FacilioConstants.ContextNames.SERVICE_REQUEST;
    }

    @Override
    public PagesContext getTemplatePage(ApplicationContext app, FacilioModule module) throws Exception {
        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.ContextNames.SERVICE_REQUEST_ACTIVITY);

        return new PagesContext(null, null, "", null, true, false, false)
                .addLayout(PagesContext.PageLayoutType.WEB)
                .addTab("conversation", "Conversation", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.THREE_QUARTER_WIDTH)
                .addSection("servicerequestconversationsection", null, null)
                .addWidget("servicerequestconversationwidget", "Conversation", PageWidget.WidgetType.SR_EMAIL_THREAD, "flexibleservicerequestemailthread_50", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .addColumn(PageColumnContext.ColumnWidth.QUARTER_WIDTH)
                .addSection("servicerequestpropertiessection", null, null)
                .addWidget("servicerequestpropertieswidget", "Properties", PageWidget.WidgetType.SR_DETAILS_WIDGET, "servicerequestdetails_50_3", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("properties", "Properties", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("servicerequestsummaryfields", null, null)
                .addWidget("servicerequestsummaryfieldwidget", "Properties", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_26", 0, 0, null, getSummaryWidgetDetails(module.getName(),app))
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_20", 0, 0, null, getWidgetGroup())
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("related", "Related", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("servicerequestrelatedsection", "Relationships", "List of relationships and types between records across modules")
                .addWidget("servicerequestrelatedwidget", "Related", PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET, "flexiblewebbulkrelationshipwidget_29", 0, 0, null, RelationshipWidgetUtil.fetchRelationshipsOfModule(module))
                .widgetDone()
                .sectionDone()
                .addSection("servicerequesttrelatedlist", "Related List", "List of all related records across modules")
                .addWidget("servicerequestbulkrelatedlist", "Related List", PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblewebbulkrelatedlist_29", 0, 0, null, RelatedListWidgetUtil.fetchAllRelatedListForModule(module))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("servicerequesthistory", "History", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("servicerequesthistorysection", null, null)
                .addWidget("servicerequesthistorywidget", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_50", 0, 0, historyWidgetParam, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone();

    }
    private static JSONObject getWidgetGroup() throws Exception {
        JSONObject commentWidgetParam = new JSONObject();
        commentWidgetParam.put("notesModuleName", FacilioConstants.ContextNames.SERVICE_REQUEST_NOTES);
        JSONObject attachmentWidgetParam = new JSONObject();
        attachmentWidgetParam.put("attachmentsModuleName", FacilioConstants.ContextNames.SERVICE_REQUEST_ATTACHMENTS);
        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("notes", "Notes", "")
                .addWidget("commentwidget", "Comment", PageWidget.WidgetType.COMMENT,  "flexiblewebcomment_27", 0, 0, commentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Attachments", "")
                .addWidget("attachmentwidget", "Documents", PageWidget.WidgetType.ATTACHMENT,  "flexiblewebattachment_27", 0, 0, attachmentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();
        return FieldUtil.getAsJSON(widgetGroup);
    }

    private static JSONObject getSummaryWidgetDetails(String moduleName,ApplicationContext app) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        //general information

//        FacilioField description = moduleBean.getField("description", moduleName);
        FacilioField assignedTo = moduleBean.getField("assignedTo", moduleName);
        FacilioField urgency = moduleBean.getField("urgency", moduleName);
        FacilioField actualStartDate = moduleBean.getField("actualStartDate", moduleName);
        FacilioField actualFinishDate = moduleBean.getField("actualFinishDate", moduleName);
        FacilioField dueDate = moduleBean.getField("dueDate", moduleName);

        SummaryWidgetGroup generalInformationwidgetGroup = new SummaryWidgetGroup();

//        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, description, 1, 1, 4);
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, assignedTo, 1, 1, 1);
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, urgency, 1, 2, 1);
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, actualStartDate, 1, 3, 1);
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, actualFinishDate, 1, 4, 1);
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, dueDate, 2, 1, 1);


        //Reported By

//        FacilioField sysCreatedBy = moduleBean.getField("sysCreatedBy", moduleName);
//
//        SummaryWidgetGroup reportedBywidgetGroup = new SummaryWidgetGroup();
//
//        addSummaryFieldInWidgetGroup(reportedBywidgetGroup, sysCreatedBy, 1, 1, 1);

        // Requested By

        FacilioField requesterName = moduleBean.getField("name",FacilioConstants.ContextNames.PEOPLE);
        FacilioField requesterPhone = moduleBean.getField("phone",FacilioConstants.ContextNames.PEOPLE);
        FacilioField requesterEmail = moduleBean.getField("email",FacilioConstants.ContextNames.PEOPLE);
        FacilioField requester = moduleBean.getField("requester", moduleName);


        SummaryWidgetGroup requestedBywidgetGroup = new SummaryWidgetGroup();

        addSummaryFieldInWidgetGroup(requestedBywidgetGroup, requesterName, 1, 1, 1,requester);
        addSummaryFieldInWidgetGroup(requestedBywidgetGroup, requesterPhone, 1, 2, 1,requester);
        addSummaryFieldInWidgetGroup(requestedBywidgetGroup, requesterEmail, 1, 3, 1,requester);


        // Others


        // System Details
        FacilioField sysCreatedByField = moduleBean.getField("sysCreatedBy", moduleName);
        FacilioField sysCreatedTimeField = moduleBean.getField("sysCreatedTime", moduleName);
        FacilioField sysModifiedByField = moduleBean.getField("sysModifiedBy", moduleName);
        FacilioField sysModifiedTimeField = moduleBean.getField("sysModifiedTime", moduleName);

        SummaryWidgetGroup systemDetailswidgetGroup = new SummaryWidgetGroup();

        addSummaryFieldInWidgetGroup(systemDetailswidgetGroup, sysCreatedByField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(systemDetailswidgetGroup, sysCreatedTimeField, 1, 2, 1);
        addSummaryFieldInWidgetGroup(systemDetailswidgetGroup, sysModifiedByField, 1, 3, 1);
        addSummaryFieldInWidgetGroup(systemDetailswidgetGroup, sysModifiedTimeField, 1, 4, 1);

        generalInformationwidgetGroup.setName("moduleDetails");
        generalInformationwidgetGroup.setDisplayName("General Information");
        generalInformationwidgetGroup.setColumns(4);

//        reportedBywidgetGroup.setName("reportedBy");
//        reportedBywidgetGroup.setDisplayName("Reported By");
//        reportedBywidgetGroup.setColumns(4);

        requestedBywidgetGroup.setName("requestedBy");
        requestedBywidgetGroup.setDisplayName("Requested By");
        requestedBywidgetGroup.setColumns(4);

        systemDetailswidgetGroup.setName("systemDetails");
        systemDetailswidgetGroup.setDisplayName("System Details");
        systemDetailswidgetGroup.setColumns(4);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(generalInformationwidgetGroup);
//        widgetGroupList.add(reportedBywidgetGroup);
        widgetGroupList.add(requestedBywidgetGroup);
        widgetGroupList.add(systemDetailswidgetGroup);

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();
        widgetGroup.setName("properties");
        widgetGroup.setDisplayName("Properties");
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
}
