package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.CustomPageWidget;
import com.facilio.bmsconsole.context.SummaryWidgetGroup;
import com.facilio.bmsconsole.context.SummaryWidgetGroupFields;
import com.facilio.bmsconsole.forms.*;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.FormRuleAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.SummaryWidgetUtil;
import org.apache.commons.chain.Context;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class WorkOrderItemsModule extends BaseModuleConfig{
    public WorkOrderItemsModule() throws Exception{
        setModuleName(FacilioConstants.ContextNames.WORKORDER_ITEMS);
    }


    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> workOrderItems = new ArrayList<FacilioView>();
        workOrderItems.add(getAllWorkOrderItems().setOrder(order++));
        workOrderItems.add(getAllWorkOrderItemsDetailsView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", getModuleName());
        groupDetails.put("views", workOrderItems);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllWorkOrderItems() {

        FacilioField createdTime = new FacilioField();
        createdTime.setName("sysCreatedTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("SYS_CREATED_TIME");
        createdTime.setModule(ModuleFactory.getWorkOrderItemsModule());
        SortField sortField = new SortField(createdTime, false);

        FacilioModule workOrderItemsModule = ModuleFactory.getWorkOrderItemsModule();
        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Work Order Items");
        allView.setModuleName(workOrderItemsModule.getName());
        allView.setSortFields(Collections.singletonList(sortField));

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }
    private static FacilioView getAllWorkOrderItemsDetailsView() {
        FacilioModule workOrderItemsModule = ModuleFactory.getWorkOrderItemsModule();

        FacilioView detailsView = new FacilioView();
        detailsView.setName("details");
        detailsView.setDisplayName("Work Order Items Details");
        detailsView.setModuleName(workOrderItemsModule.getName());

        return detailsView;
    }

    @Override
    public void addData() throws Exception {
        super.addData();
        List<String> appLinkNamesForSummaryWidget = new ArrayList<>();
        appLinkNamesForSummaryWidget.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNamesForSummaryWidget.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);

        for(String appLinkName: appLinkNamesForSummaryWidget) {
            ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule workOrderItemModule = moduleBean.getModule(FacilioConstants.ContextNames.WORKORDER_ITEMS);
            FacilioField sysCreatedByField = moduleBean.getField("sysCreatedBy", FacilioConstants.ContextNames.WORKORDER_ITEMS);
            FacilioField sysCreatedTimeField = moduleBean.getField("sysCreatedTime", FacilioConstants.ContextNames.WORKORDER_ITEMS);
            FacilioField sysModifiedByField = moduleBean.getField("sysModifiedBy", FacilioConstants.ContextNames.WORKORDER_ITEMS);
            FacilioField sysModifiedTimeField = moduleBean.getField("sysModifiedTime", FacilioConstants.ContextNames.WORKORDER_ITEMS);

            CustomPageWidget pageWidget1 = new CustomPageWidget();
            SummaryWidgetGroup widgetGroup1 = new SummaryWidgetGroup();

            SummaryWidgetGroupFields groupField11 = new SummaryWidgetGroupFields();
            SummaryWidgetGroupFields groupField12 = new SummaryWidgetGroupFields();
            SummaryWidgetGroupFields groupField13 = new SummaryWidgetGroupFields();
            SummaryWidgetGroupFields groupField14 = new SummaryWidgetGroupFields();

            groupField11.setName(sysCreatedByField.getName());
            groupField11.setDisplayName(sysCreatedByField.getDisplayName());
            groupField11.setFieldId(sysCreatedByField.getId());
            groupField11.setRowIndex(1);
            groupField11.setColIndex(1);
            groupField11.setColSpan(2);

            groupField12.setName(sysCreatedTimeField.getName());
            groupField12.setDisplayName(sysCreatedTimeField.getDisplayName());
            groupField12.setFieldId(sysCreatedTimeField.getId());
            groupField12.setRowIndex(1);
            groupField12.setColIndex(3);
            groupField12.setColSpan(2);

            groupField13.setName(sysModifiedByField.getName());
            groupField13.setDisplayName(sysModifiedByField.getDisplayName());
            groupField13.setFieldId(sysModifiedByField.getId());
            groupField13.setRowIndex(2);
            groupField13.setColIndex(1);
            groupField13.setColSpan(2);

            groupField14.setName(sysModifiedTimeField.getName());
            groupField14.setDisplayName(sysModifiedTimeField.getDisplayName());
            groupField14.setFieldId(sysModifiedTimeField.getId());
            groupField14.setRowIndex(2);
            groupField14.setColIndex(3);
            groupField14.setColSpan(2);

            List<SummaryWidgetGroupFields> groupOneFields = new ArrayList<>();
            groupOneFields.add(groupField11);
            groupOneFields.add(groupField12);
            groupOneFields.add(groupField13);
            groupOneFields.add(groupField14);


            widgetGroup1.setName("moreDetails");
            widgetGroup1.setDisplayName("More Details");
            widgetGroup1.setColumns(4);
            widgetGroup1.setFields(groupOneFields);

            List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
            widgetGroupList.add(widgetGroup1);

            pageWidget1.setName("workorderItemsWidget");
            pageWidget1.setDisplayName("Work Order Items Widget");
            pageWidget1.setModuleId(workOrderItemModule.getModuleId());
            pageWidget1.setAppId(ApplicationApi.getApplicationIdForLinkName(appLinkName));
            pageWidget1.setGroups(widgetGroupList);

            SummaryWidgetUtil.addPageWidget(pageWidget1);
        }


    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule workOrderItemsModule = modBean.getModule(FacilioConstants.ContextNames.WORKORDER_ITEMS);

        FacilioForm workOrderItemForm = new FacilioForm();
        workOrderItemForm.setDisplayName("New Work Order Item");
        workOrderItemForm.setName("default_workorderItem_web");
        workOrderItemForm.setModule(workOrderItemsModule);
        workOrderItemForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        workOrderItemForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP, FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));

        int seqNum = 0;
        List<FormField> workOrderItemFormFields = new ArrayList<>();
        workOrderItemFormFields.add(new FormField("storeRoom", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Storeroom", FormField.Required.OPTIONAL, "storeRoom", ++seqNum, 1));
        workOrderItemFormFields.add(new FormField("item", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Item", FormField.Required.REQUIRED, "item", ++seqNum, 1));
        workOrderItemFormFields.add(new FormField("quantity", FacilioField.FieldDisplayType.NUMBER, "Quantity", FormField.Required.REQUIRED, ++seqNum, 1));
        workOrderItemFormFields.add(new FormField("workorder", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Work Order", FormField.Required.OPTIONAL, "ticket", ++seqNum, 1));

        FormSection section = new FormSection("Default", 1, workOrderItemFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        workOrderItemForm.setSections(Collections.singletonList(section));
        workOrderItemForm.setIsSystemForm(true);
        workOrderItemForm.setType(FacilioForm.Type.FORM);

        FormRuleContext singleRule = addItemFilterRule();
        workOrderItemForm.setDefaultFormRules(Arrays.asList(singleRule));

        return Collections.singletonList(workOrderItemForm);
    }

    private FormRuleContext addItemFilterRule() {
        // TODO Auto-generated method stub

        FormRuleContext singleRule = new FormRuleContext();
        singleRule.setName("Set Item Filter Rule");
        singleRule.setRuleType(FormRuleContext.RuleType.ACTION.getIntVal());
        singleRule.setTriggerType(FormRuleContext.TriggerType.FIELD_UPDATE.getIntVal());
        singleRule.setType(FormRuleContext.FormRuleType.FROM_FORM.getIntVal());

        FormRuleTriggerFieldContext triggerField = new FormRuleTriggerFieldContext();
        triggerField.setFieldName("Storeroom");
        singleRule.setTriggerFields(Collections.singletonList(triggerField));

        List<FormRuleActionContext> actions = new ArrayList<FormRuleActionContext>();

        FormRuleActionContext filterAction = new FormRuleActionContext();
        filterAction.setActionType(FormActionType.APPLY_FILTER.getVal());

        FormRuleActionFieldsContext actionField = new FormRuleActionFieldsContext();

        actionField.setFormFieldName("Item");

        Criteria criteria = new Criteria();

        criteria.addAndCondition(CriteriaAPI.getCondition("Item.STORE_ROOM_ID","storeRoom", "${workorderItem.storeRoom.id}", StringOperators.IS));

        actionField.setCriteria(criteria);

        filterAction.setFormRuleActionFieldsContext(Collections.singletonList(actionField));

        actions.add(filterAction);

        singleRule.setActions(actions);
        singleRule.setAppLinkNamesForRule(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));
        return singleRule;
    }
}
