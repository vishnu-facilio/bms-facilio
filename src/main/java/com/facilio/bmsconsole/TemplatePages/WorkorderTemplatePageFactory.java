package com.facilio.bmsconsole.TemplatePages;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import org.json.simple.JSONObject;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class WorkorderTemplatePageFactory implements TemplatePageFactory{
    @Override
    public String getModuleName() {
        return FacilioConstants.ContextNames.WORK_ORDER;
    }

    @Override
    public PagesContext getTemplatePage(ApplicationContext app, FacilioModule module) throws Exception {


        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule workOrderModule = modBean.getModule("workorder");
        JSONObject multiresourceWidgetParam = new JSONObject();
        multiresourceWidgetParam.put("summaryWidgetName", "multiResourceWidget");
        multiresourceWidgetParam.put("module", "\""+ workOrderModule+"\"");




        return new PagesContext(null, null, "", null, true, false, false)
                .addLayout(PagesContext.PageLayoutType.WEB)
                .addTab("summary", "SUMMARY", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.THREE_QUARTER_WIDTH)
                .addSection("summaryfields", "", null)
                .addWidget("summaryFieldsWidget", "Work order details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexibleworkordersummary_33", 0, 0, null, getSummaryWidgetDetails(module.getName()))
                .widgetDone()
                .sectionDone()
                .addSection("multiresource",null,null)
                .addWidget("workordermultiresource","Multi resouce",PageWidget.WidgetType.MULTIRESOURCE,"flexibleworkordermultiresource_17",0,2,multiresourceWidgetParam,null)
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null,  null)
                .addWidget("widgetGroup", null, PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_20", 0, 4,  null, getWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .addColumn(PageColumnContext.ColumnWidth.QUARTER_WIDTH)
                .addSection("responsibility",null,null)
                .addWidget("workorderresponsibility", "Responsibility",PageWidget.WidgetType.RESPONSIBILITY,"flexibleworkorderresponsibility_14",0,0,null,null)
                .widgetDone()
                .sectionDone()
                .addSection("locationdetails",null,null)
                .addWidget("workorderlocationdetails", "Location Details",PageWidget.WidgetType.RESOURCE,"fixedworkorderlocationdetails_13",0,0,null,null)
                .widgetDone()
                .sectionDone()
                .addSection("timeDetails",null,null)
                .addWidget("workordertimedetails","Time Details",PageWidget.WidgetType.TIME_DETAILS,"flexibleworkordertimedetails_26",0,0,null,null)
                .widgetDone()
                .sectionDone()
                .addSection("costdetails",null,null)
                .addWidget("workordercostdetails", "Cost",PageWidget.WidgetType.QUOTATION,"flexibleworkordercostdetails_11",0,0,null,null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone();

    }
    private static JSONObject getSummaryWidgetDetails(String moduleName) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField descFields = moduleBean.getField("description", moduleName);
        FacilioField priorityField = moduleBean.getField("priority", moduleName);
        FacilioField sourceTypeField=moduleBean.getField("sourceType",moduleName);
        FacilioField categoryField=moduleBean.getField("category",moduleName);
        FacilioField jobplanField=moduleBean.getField("jobPlan",moduleName);
        FacilioField sysCreatedByField = moduleBean.getField("createdBy", moduleName);
        FacilioField sysCreatedTimeField = moduleBean.getField("sysCreatedTime", moduleName);
        FacilioField typeField = moduleBean.getField("type", moduleName);
        FacilioField pmField = moduleBean.getField("pmV2", moduleName);


        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();
        widgetGroup.setName("primaryDetails");
        widgetGroup.setDisplayName("Primary Details");

        addSummaryFieldInWidgetGroup(widgetGroup, descFields,1, 1, 4);
        addSummaryFieldInWidgetGroup(widgetGroup, priorityField, 2 , 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup,typeField,2,2,1);
        addSummaryFieldInWidgetGroup(widgetGroup,sourceTypeField,2,3,1);
        addSummaryFieldInWidgetGroup(widgetGroup,categoryField,2,4,1);
        addSummaryFieldInWidgetGroup(widgetGroup,pmField,3,1,1);
        addSummaryFieldInWidgetGroup(widgetGroup,jobplanField,3,2,1);
        addSummaryFieldInWidgetGroup(widgetGroup, sysCreatedByField, 3, 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, sysCreatedTimeField, 3, 4, 1);


        widgetGroup.setColumns(4);


        FacilioField siteField = moduleBean.getField("siteId", moduleName);
        FacilioField resourceField = moduleBean.getField("resource", moduleName);
        FacilioField assetCategory = moduleBean.getField("category", FacilioConstants.ContextNames.ASSET);
        SummaryWidgetGroupFields resFieldS = new SummaryWidgetGroupFields();
        resFieldS.setDisplayName("Asset Category");
        resFieldS.setParentLookupFieldId(resourceField.getFieldId());
        resFieldS.setFieldId(assetCategory.getFieldId());
        resFieldS.setRowIndex(1);
        resFieldS.setColIndex(2);
        resFieldS.setColSpan(1);




        SummaryWidgetGroup locationWidgetGroup = new SummaryWidgetGroup();
        locationWidgetGroup.setName("locationDetails");
        locationWidgetGroup.setDisplayName("Location Details");
        locationWidgetGroup.setFields(new ArrayList<>(Arrays.asList(resFieldS)));

        addSummaryFieldInWidgetGroup(locationWidgetGroup, siteField,1, 1, 1);


        locationWidgetGroup.setColumns(4);



        FacilioField tenantFields = moduleBean.getField("tenant", moduleName);
        FacilioField clientField = moduleBean.getField("client", moduleName);
        FacilioField requesterField=moduleBean.getField("requester",moduleName);

        SummaryWidgetGroup requestingWidgetGroup = new SummaryWidgetGroup();
        requestingWidgetGroup.setName("requestingUserDetails");
        requestingWidgetGroup.setDisplayName("Requesting User Details");

        addSummaryFieldInWidgetGroup(requestingWidgetGroup,tenantFields ,1, 1, 1);
        addSummaryFieldInWidgetGroup(requestingWidgetGroup, clientField, 1 , 2, 1);
        addSummaryFieldInWidgetGroup(requestingWidgetGroup,requesterField,1,3,1);
        requestingWidgetGroup.setColumns(4);


        FacilioField serviceRequestFields = moduleBean.getField("serviceRequest", moduleName);
        FacilioField parentWOField = moduleBean.getField("parentWO", moduleName);
        FacilioField slaPolicyField=moduleBean.getField("slaPolicyId",moduleName);
        FacilioField stateFlowField=moduleBean.getField("stateFlowId",moduleName);
        FacilioField modifiedTimeField=moduleBean.getField("modifiedTime",moduleName);

        SummaryWidgetGroup otherWidgetGroup = new SummaryWidgetGroup();
        otherWidgetGroup.setName("otherDetails");
        otherWidgetGroup.setDisplayName("Other Details");

        addSummaryFieldInWidgetGroup(otherWidgetGroup,serviceRequestFields ,1, 1, 1);
        addSummaryFieldInWidgetGroup(otherWidgetGroup, parentWOField, 1 , 2, 1);
        addSummaryFieldInWidgetGroup(otherWidgetGroup,slaPolicyField,1,3,1);
        addSummaryFieldInWidgetGroup(otherWidgetGroup,stateFlowField,1,4,1);
        addSummaryFieldInWidgetGroup(otherWidgetGroup,modifiedTimeField,2,1,1);
        addSummaryFieldInWidgetGroup(otherWidgetGroup,sysCreatedByField,2,2,1);
        otherWidgetGroup.setColumns(4);



        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(widgetGroup);
        widgetGroupList.add(requestingWidgetGroup);
        widgetGroupList.add(otherWidgetGroup);

        pageWidget.setDisplayName("Work order Details");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP).getId());
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
                .addWidget("commentwidget", "Comment", PageWidget.WidgetType.COMMENT, isMobile?"flexiblemobilecomment_8":"flexiblewebcomment_27", 0, 0, null, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Documents", "")
                .addWidget("attachmentwidget", "Documents", PageWidget.WidgetType.ATTACHMENT, isMobile?"flexiblemobileattachment_8":"flexiblewebattachment_27", 0, 0, null, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();


        return FieldUtil.getAsJSON(widgetGroup);
    }
}
