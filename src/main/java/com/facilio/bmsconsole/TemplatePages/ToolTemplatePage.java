package com.facilio.bmsconsole.TemplatePages;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.LookupOperator;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.v3.context.Constants;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ToolTemplatePage implements TemplatePageFactory {
    @Override
    public String getModuleName() {
        return FacilioConstants.ContextNames.TOOL;
    }

    @Override
    public PagesContext getTemplatePage(ApplicationContext app, FacilioModule module) throws Exception {
        return getNonRotatingToolsPage(app, module,true,false);
    }

    public static PagesContext getNonRotatingToolsPage(ApplicationContext app, FacilioModule module, Boolean isTemplate, Boolean isDefault) throws Exception {
        Criteria nonRotatingToolTypeCriteria = rotatingToolCriteria(false);
        PagesContext page = new PagesContext("nonRotatingToolPage", "Tool Page", null, nonRotatingToolTypeCriteria, isTemplate, isDefault, false);
        return constructToolPage(page, app, module, false);
    }
    public static PagesContext getRotatingToolsPage(ApplicationContext app, FacilioModule module, Boolean isTemplate, Boolean isDefault) throws Exception {
        Criteria rotatingToolTypeCriteria = rotatingToolCriteria(true);
        PagesContext page = new PagesContext("rotatingToolPage", "Rotating Tool Page", null, rotatingToolTypeCriteria, isTemplate, isDefault, false);
        return constructToolPage(page, app, module, true);
    }

    private static Criteria rotatingToolCriteria(boolean isRotating) throws Exception {
        Criteria criteriaValue = new Criteria();
        Condition rotatingCondition = CriteriaAPI.getCondition("isRotating", String.valueOf(isRotating), BooleanOperators.IS);
        criteriaValue.addAndCondition(rotatingCondition);

        ModuleBean modBean = Constants.getModBean();
        LookupField toolTypeField = (LookupField) modBean.getField("toolType", "tool");
        toolTypeField.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.TOOL_TYPES));

        Condition toolTypeCondition = new Condition();
        toolTypeCondition.setField(toolTypeField);
        toolTypeCondition.setOperator(LookupOperator.LOOKUP);
        toolTypeCondition.setCriteriaValue(criteriaValue);

        Criteria rotatingToolTypeCriteria = new Criteria();
        rotatingToolTypeCriteria.addAndCondition(toolTypeCondition);
        return rotatingToolTypeCriteria;
    }


    private static PagesContext constructToolPage(PagesContext page, ApplicationContext app, FacilioModule module, Boolean isRotating) throws Exception {
        PageColumnContext currentColumn = page.addWebLayout()
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("summaryfields", null, null)
                .addWidget("summaryFieldsWidget", "Summary Widget", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_4", 0, 0, null, getSummaryWidgetDetails(module.getName(), app))
                .widgetDone()
                .sectionDone()
                .addSection("tooldetailscard", null, null)
                .addWidget("storeRoomDetailsCard", null, PageWidget.WidgetType.TOOL_DETAILS_CARD, "webtooldetailscard_3_4", 0, 0, null,null)
                .widgetDone()
                .addWidget("quantityDetailsCard", null, PageWidget.WidgetType.TOOL_DETAILS_CARD, "webtooldetailscard_3_4", 4, 0, null,null)
                .widgetDone()
                .addWidget("rateDetailsCard", null, PageWidget.WidgetType.TOOL_DETAILS_CARD, "webtooldetailscard_3_4", 8, 0, null,null)
                .widgetDone()
                .sectionDone()

                .addSection("toolBin", null, null)
                .addWidget("toolBin", "Bin", PageWidget.WidgetType.BIN, "flexiblewebtoolbin_6", 0, 0, null, null)
                .widgetDone()
                .sectionDone();

        if (isRotating) {
            currentColumn = currentColumn.addSection("rotatingAsset", null, null)
                    .addWidget("rotatingAsset", "Rotating Assets", PageWidget.WidgetType.ROTATING_ASSET, "flexiblewebrotatingtoolasset_6", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone();
        } else {
            currentColumn = currentColumn.addSection("purchasedTool", null, null)
                    .addWidget("purchasedTool", "Purchased Tool", PageWidget.WidgetType.PURCHASED_TOOLS, "flexiblewebpurchasedtool_6", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone();
        }
        page = currentColumn.addSection("whereUsed", null, null)
                .addWidget("whereUsed", "Where used", PageWidget.WidgetType.WHERE_USED, "flexiblewebtoolwhereused_6", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("PurchasedOrder", null, null)
                .addWidget("PurchasedOrder", "Purchased Order", PageWidget.WidgetType.PURCHASE_ORDER, "flexiblewebpurchaseordertool_6", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("tooltransactions", "Transactions", PageTabContext.TabType.SINGLE_WIDGET_TAB, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("tooltransactions", null, null)
                .addWidget("tooltransactions", "Transactions", PageWidget.WidgetType.TOOL_TRANSACTIONS, "flexiblewebtoolsummarytransactions_7", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone();
        return page;
    }

    private static JSONObject getSummaryWidgetDetails(String moduleName, ApplicationContext app) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField description = moduleBean.getField("description", FacilioConstants.ContextNames.TOOL_TYPES);
        FacilioField isUnderstocked = moduleBean.getField("isUnderstocked", moduleName);
        isUnderstocked.setDisplayName("Understocked");
        FacilioField sysModifiedTime = moduleBean.getField("sysModifiedTime", moduleName);
        FacilioField status = moduleBean.getField("status", moduleName);
        FacilioField sysCreatedTime = moduleBean.getField("sysCreatedTime", moduleName);
        sysCreatedTime.setDisplayName("Created Time");

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();
        addSummaryFieldInWidgetGroup(widgetGroup, description, 1, 1, 4);
        addSummaryFieldInWidgetGroup(widgetGroup, isUnderstocked, 2, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, sysModifiedTime, 2, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, sysCreatedTime,
                2, 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, status, 2, 4, 1);



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

    private static JSONObject getWidgetGroup(boolean isMobile) throws Exception {
        JSONObject notesWidgetParam = new JSONObject();
        notesWidgetParam.put("notesModuleName", "toolnotes");

        JSONObject attachmentWidgetParam = new JSONObject();
        attachmentWidgetParam.put("attachmentsModuleName", "toolattachments");

        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("notes", "Notes", "")
                .addWidget("commentwidget", "Comment", PageWidget.WidgetType.COMMENT, isMobile ? "flexiblemobilecomment_8" : "flexiblewebcomment_5", 0, 0, notesWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Documents", "")
                .addWidget("attachmentwidget", "Documents", PageWidget.WidgetType.ATTACHMENT, isMobile ? "flexiblemobileattachment_8" : "flexiblewebattachment_5", 0, 0, attachmentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();
        return FieldUtil.getAsJSON(widgetGroup);
    }
}
