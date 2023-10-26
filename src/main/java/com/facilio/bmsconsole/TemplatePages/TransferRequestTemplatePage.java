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
import com.facilio.modules.fields.LookupField;
import com.facilio.relation.util.RelationshipWidgetUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TransferRequestTemplatePage implements TemplatePageFactory{
    @Override
    public String getModuleName() {
        return FacilioConstants.ContextNames.TRANSFER_REQUEST;
    }

    @Override
    public PagesContext getTemplatePage(ApplicationContext app, FacilioModule module) throws Exception {
        return new PagesContext(null, null,"", null, true, false, false)
                .addWebLayout()
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("transferrequestsummaryfields", null, null)
                .addWidget("transferrequestsummaryFieldsWidget", "Transfer Requests",  PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, getSummaryWidgetDetails(module.getName(), app))
                .widgetDone()
                .sectionDone()
                .addSection("summaryDetailsCard",null,null)
                .addWidget("transferrequestcard1","",PageWidget.WidgetType.TRANSFER_REQUEST_DETAILS_CARD,"webtransferrequestitemlist_2_4",0,0,getSummaryCardDetails("Transfer from store","transferFromStore.name","Transfer to store","transferToStore.name",false),null)
                .widgetDone()
                .addWidget("transferrequestcard2","",PageWidget.WidgetType.TRANSFER_REQUEST_DETAILS_CARD,"webtransferrequestitemlist_2_4",4,0,getSummaryCardDetails("Transfer initiated on","transferInitiatedOn","Expected arrival date","expectedCompletionDate",true),null)
                .widgetDone()
                .addWidget("transferrequestcard3","",PageWidget.WidgetType.TRANSFER_REQUEST_DETAILS_CARD,"webtransferrequestitemlist_2_4",8,0,getSummaryCardDetails("Transferred by","transferredBy.name","Created By","sysCreatedBy.name",false),null)
                .widgetDone()
                .sectionDone()
                .addSection("transferItemListItem", null, null)
                .addWidget("transferItemList", "Line Items", PageWidget.WidgetType.LINE_ITEMS_LIST,"flexiblewebtransferitemlist_6", 0, 0,  getLineItemListParams(), null)
                .widgetDone()
                .sectionDone()
                .addSection("transferrequestwidgetGroup", null,  null)
                .addWidget("transferrequestcommentandattachmentwidgetgroupwidget", null, PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0,  null, getWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone();
    }
    public static  JSONObject getSummaryCardDetails(String leftTypeDisplayName,String leftTypeFieldName, String  rightTypeDisplayName, String rightTypeFieldName , Boolean isDateField){
        JSONObject widgetParams = new JSONObject();
        widgetParams.put("leftTypeDisplayName" , leftTypeDisplayName);
        widgetParams.put("leftTypeFieldName" , leftTypeFieldName);
        widgetParams.put("rightTypeDisplayName" , rightTypeDisplayName);
        widgetParams.put("rightTypeFieldName" , rightTypeFieldName);
        widgetParams.put("isDateField" , isDateField);
        return widgetParams;
    }
    public static JSONObject getLineItemListParams(){
        JSONObject widgetParams = new JSONObject();
        widgetParams.put("moduleName",FacilioConstants.ContextNames.TRANSFER_REQUEST_LINE_ITEM);
        widgetParams.put("relatedFieldName",FacilioConstants.ContextNames.TRANSFER_REQUEST);
        widgetParams.put("moduleDisplayName","Line Items");
        widgetParams.put("title","Line Items");
        return widgetParams;
    }
    public static JSONObject getSummaryWidgetDetails(String moduleName, ApplicationContext app) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField description = moduleBean.getField("description", moduleName);
        FacilioField sysCreatedTimeField = moduleBean.getField("sysCreatedTime", moduleName);
        FacilioField sysModifiedByField = moduleBean.getField("sysModifiedBy", moduleName);
        FacilioField sysModifiedTimeField = moduleBean.getField("sysModifiedTime", moduleName);
        FacilioField isShipmentTrackingNeeded = moduleBean.getField("isShipmentTrackingNeeded", moduleName);

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();
        addSummaryFieldInWidgetGroup(widgetGroup,description , 1, 1, 4);
        addSummaryFieldInWidgetGroup(widgetGroup, sysCreatedTimeField ,2, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup,isShipmentTrackingNeeded , 2 , 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup,sysModifiedByField , 2, 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup,sysModifiedTimeField ,2, 4, 1);

        widgetGroup.setName("moduleDetails");
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
    public static JSONObject getWidgetGroup(boolean isMobile) throws Exception {
        JSONObject notesWidgetParam = new JSONObject();
        notesWidgetParam.put("notesModuleName", FacilioConstants.ContextNames.TRANSFER_REQUEST_NOTES);

        JSONObject attachmentsWidgetParam = new JSONObject();
        attachmentsWidgetParam.put("attachmentsModuleName", FacilioConstants.ContextNames.TRANSFER_REQUEST_ATTACHMENT);

        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("notes", "Notes", null)
                .addWidget("commentwidget", "Comment", PageWidget.WidgetType.COMMENT, isMobile ? "flexiblemobilecomment_8" : "flexiblewebcomment_5", 0, 0, notesWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Documents", null)
                .addWidget("attachmentwidget", "Documents", PageWidget.WidgetType.ATTACHMENT, isMobile ? "flexiblemobileattachment_8" : "flexiblewebattachment_5", 0, 0, attachmentsWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();
        return FieldUtil.getAsJSON(widgetGroup);
    }
}
