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

public class ItemTemplatePage implements TemplatePageFactory {
    @Override
    public String getModuleName() {
        return FacilioConstants.ContextNames.ITEM;
    }

    @Override
    public PagesContext getTemplatePage(ApplicationContext app, FacilioModule module) throws Exception {
        return getNonRotatingItemsPage(app, module,true,false);
    }

    public static PagesContext getNonRotatingItemsPage(ApplicationContext app, FacilioModule module,Boolean isTemplate, Boolean isDefault) throws Exception {
        Criteria nonRotatingItemTypeCriteria = rotatingItemCriteria(false);
        PagesContext page = new PagesContext("nonRotatingItemPage", "Item Page", null, nonRotatingItemTypeCriteria, isTemplate, isDefault, false);
        return constructItemPage(page, app, module, false);
    }
    public static PagesContext getRotatingItemsPage(ApplicationContext app, FacilioModule module,Boolean isTemplate, Boolean isDefault) throws Exception {
        Criteria rotatingItemTypeCriteria = rotatingItemCriteria(true);
        PagesContext page = new PagesContext("rotatingItemPage", "Rotating Item Page", null, rotatingItemTypeCriteria, isTemplate, isDefault, false);
        return constructItemPage(page, app, module, true);
    }

    private static Criteria rotatingItemCriteria(boolean isRotating) throws Exception {
        Criteria criteriaValue = new Criteria();
        Condition rotatingCondition = CriteriaAPI.getCondition("isRotating", String.valueOf(isRotating), BooleanOperators.IS);
        criteriaValue.addAndCondition(rotatingCondition);

        ModuleBean modBean = Constants.getModBean();
        LookupField itemTypeField = (LookupField) modBean.getField("itemType", "item");
        itemTypeField.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.ITEM_TYPES));

        Condition itemTypeCondition = new Condition();
        itemTypeCondition.setField(itemTypeField);
        itemTypeCondition.setOperator(LookupOperator.LOOKUP);
        itemTypeCondition.setCriteriaValue(criteriaValue);

        Criteria rotatingItemTypeCriteria = new Criteria();
        rotatingItemTypeCriteria.addAndCondition(itemTypeCondition);
        return rotatingItemTypeCriteria;
    }


    private static PagesContext constructItemPage(PagesContext page, ApplicationContext app, FacilioModule module, Boolean isRotating) throws Exception {
        PageColumnContext currentColumn = page.addWebLayout()
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("summaryfields", null, null)
                .addWidget("summaryFieldsWidget", "Summary Widget", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_4", 0, 0, null, getSummaryWidgetDetails(module.getName(), app))
                .widgetDone()
                .sectionDone()
                .addSection("itemDetailsCard", null, null)
                .addWidget("itemDetailsCard", "Item Details Card", PageWidget.WidgetType.ITEM_DETAILS_CARD, "flexiblewebitemdetailscard_2", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("itemBin", null, null)
                .addWidget("itemBin", "Bin", PageWidget.WidgetType.BIN, "flexiblewebitembin_6", 0, 0, null, null)
                .widgetDone()
                .sectionDone();

        if (isRotating) {
            currentColumn = currentColumn.addSection("rotatingAsset", null, null)
                    .addWidget("rotatingAsset", "Rotating Assets", PageWidget.WidgetType.ROTATING_ASSET, "flexiblewebrotatingasset_6", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone();
        } else {
            currentColumn = currentColumn.addSection("purchasedItem", null, null)
                    .addWidget("purchasedItem", "Purchased Item", PageWidget.WidgetType.PURCHASED_ITEMS, "flexiblewebpurchaseditem_6", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone();
        }
        page = currentColumn.addSection("whereUsed", null, null)
                .addWidget("whereUsed", "Where used", PageWidget.WidgetType.WHERE_USED, "flexiblewebwhereused_6", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("PurchasedOrder", null, null)
                .addWidget("PurchasedOrder", "Purchased Order", PageWidget.WidgetType.PURCHASE_ORDER, "flexiblewebpurchaseorder_6", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("itemtransactions", "Transactions", PageTabContext.TabType.SINGLE_WIDGET_TAB, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("itemtransactions", null, null)
                .addWidget("itemtransactions", "Transactions", PageWidget.WidgetType.ITEM_TRANSACTIONS, "flexiblewebitemsummarytransactions_7", 0, 0, null, null)
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

        FacilioField description = moduleBean.getField("description", FacilioConstants.ContextNames.ITEM_TYPES);
        FacilioField costType = moduleBean.getField("costType", moduleName);
        FacilioField isUnderstocked = moduleBean.getField("isUnderstocked", moduleName);
        isUnderstocked.setDisplayName("Understocked");
        FacilioField issuanceCost = moduleBean.getField("issuanceCost", moduleName);
        FacilioField sysModifiedTime = moduleBean.getField("sysModifiedTime", moduleName);
        FacilioField status = moduleBean.getField("status", moduleName);
        FacilioField sysCreatedTime = moduleBean.getField("sysCreatedTime", moduleName);
        sysCreatedTime.setDisplayName("Created Time");

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();
        addSummaryFieldInWidgetGroup(widgetGroup, description, 1, 1, 4);
        addSummaryFieldInWidgetGroup(widgetGroup, costType, 2, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, isUnderstocked, 2, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, issuanceCost,
                2, 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, sysModifiedTime, 2, 4, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, status, 3, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, sysCreatedTime, 3, 2, 1);


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
        notesWidgetParam.put("notesModuleName", "itemnotes");

        JSONObject attachmentWidgetParam = new JSONObject();
        attachmentWidgetParam.put("attachmentsModuleName", "itemattachments");

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
