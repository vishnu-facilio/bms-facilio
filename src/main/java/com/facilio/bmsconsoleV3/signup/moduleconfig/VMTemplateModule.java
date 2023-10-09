package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsole.workflow.rule.CustomButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.SystemButtonRuleContext;
import com.facilio.bmsconsoleV3.context.meter.VirtualMeterTemplateContext;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.*;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.*;
import com.facilio.v3.context.Constants;
import org.json.simple.JSONObject;
import static com.facilio.bmsconsole.util.SystemButtonApi.addSystemButton;

import java.util.*;

public class VMTemplateModule extends BaseModuleConfig{

    public VMTemplateModule() throws Exception {
        setModuleName(FacilioConstants.Meter.VIRTUAL_METER_TEMPLATE);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() throws Exception {

        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> virtualMeterTemplate = new ArrayList<FacilioView>();
        virtualMeterTemplate.add(getAllVirtualMeterTemplateViews().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.Meter.VIRTUAL_METER_TEMPLATE);
        groupDetails.put("views", virtualMeterTemplate);
        groupVsViews.add(groupDetails);

        return groupVsViews;

    }
    private static FacilioView getAllVirtualMeterTemplateViews() throws Exception {

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("sysCreatedTime", "Created Time", FieldType.NUMBER), false));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Virtual Meter Templates");
        allView.setModuleName(FacilioConstants.Meter.VIRTUAL_METER_TEMPLATE);
        allView.setSortFields(sortFields);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.ENERGY_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    public Map<String, List<PagesContext>> fetchSystemPageConfigs() throws Exception {
        Map<String, List<PagesContext>> pageTemp = new HashMap<>();
        pageTemp.put(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP, getSystemPage());
        pageTemp.put(FacilioConstants.ApplicationLinkNames.ENERGY_APP,getSystemPage());
        return  pageTemp;
    }
    private static List<PagesContext> getSystemPage() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule vmTemplateModule = modBean.getModule(FacilioConstants.Meter.VIRTUAL_METER_TEMPLATE);
        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.Meter.VIRTUAL_METER_TEMPLATE_ACTIVITY);
        List<PagesContext> VMTemplatePages = new ArrayList<>();
        PagesContext vmTemplateUnPublishedPage = new PagesContext("virtualmetertemplateunpublishedpage", "Virtual Meter Template UnPublished Page", "VirtualMeterTemplate page in UnPublished state", getVMTemplateUnPublishedCriteria(), false, true, true)
                .addLayout(PagesContext.PageLayoutType.WEB)
                .addTab("virtualmetertemplatesummary", "Summary", PageTabContext.TabType.SIMPLE,true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("vmTemplateSection", null, null)
                .addWidget("vmTemplateWidget", "VM Templates", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_4", 0, 0, null, getSummaryWidgetDetails(vmTemplateModule.getName()))
                .widgetDone()
                .sectionDone()
                .addSection("vmTemplateReadingsSection", null, null)
                .addWidget("vmTemplateReadingsWidget", "List Of Readings", PageWidget.WidgetType.VIRTUAL_METER_TEMPLATE_READINGS, "flexiblewebvmtemplatereadings_6", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("virtualmetertemplatehistory", "History", PageTabContext.TabType.SIMPLE,true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("historySection", null, null)
                .addWidget("historyWidget", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_4", 0, 0, historyWidgetParam, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone();
        PagesContext vmTemplatePublishedPage = new PagesContext("virtualmetertemplatepublishedpage", "Virtual Meter Template Published Page", "VirtualMeterTemplate page in Published state", getVMTemplatePublishedCriteria(), false, false, true)
                .addLayout(PagesContext.PageLayoutType.WEB)
                .addTab("virtualmetertemplatesummary", "Summary", PageTabContext.TabType.SIMPLE,true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("vmTemplateSection", null, null)
                .addWidget("vmTemplateWidget", "VM Templates", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_4", 0, 0, null, getSummaryWidgetDetails(vmTemplateModule.getName()))
                .widgetDone()
                .sectionDone()
                .addSection("vmTemplateReadingsSection", null, null)
                .addWidget("vmTemplateReadingsWidget", "List Of Readings", PageWidget.WidgetType.VIRTUAL_METER_TEMPLATE_READINGS, "flexiblewebvmtemplatereadings_6", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("relatedVMSection", null, null)
                .addWidget("relatedVMWidget", "List Of Related VirtualMeters", PageWidget.WidgetType.RELATED_VIRTUAL_METERS_LIST, "flexiblewebrelatedvmlist_6", 0, 0, null, getSummaryWidgetDetails(vmTemplateModule.getName()))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("virtualmetertemplatehistory", "History", PageTabContext.TabType.SIMPLE,true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("historySection", null, null)
                .addWidget("historyWidget", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_4", 0, 0, historyWidgetParam, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone();
        VMTemplatePages.add(vmTemplateUnPublishedPage);
        VMTemplatePages.add(vmTemplatePublishedPage);
        return VMTemplatePages;
    }
    private static Criteria getVMTemplatePublishedCriteria() {
        Criteria criteria = new Criteria();
        Condition vmTemplateStatusCondition = new Condition();
        vmTemplateStatusCondition.setFieldName("vmTemplateStatus");
        vmTemplateStatusCondition.setColumnName("Virtual_Meter_Template.VM_TEMPLATE_STATUS");
        vmTemplateStatusCondition.setOperator(NumberOperators.EQUALS);
        vmTemplateStatusCondition.setValue("2");
        vmTemplateStatusCondition.setModuleName(FacilioConstants.Meter.VIRTUAL_METER_TEMPLATE);
        criteria.addAndCondition(vmTemplateStatusCondition);
        return criteria;
    }
    private static Criteria getVMTemplateUnPublishedCriteria() {
        Criteria criteria = new Criteria();
        Condition vmTemplateStatusCondition = new Condition();
        vmTemplateStatusCondition.setFieldName("vmTemplateStatus");
        vmTemplateStatusCondition.setColumnName("Virtual_Meter_Template.VM_TEMPLATE_STATUS");
        vmTemplateStatusCondition.setOperator(NumberOperators.EQUALS);
        vmTemplateStatusCondition.setValue("1");
        vmTemplateStatusCondition.setModuleName(FacilioConstants.Meter.VIRTUAL_METER_TEMPLATE);
        criteria.addAndCondition(vmTemplateStatusCondition);
        return criteria;
    }
    private static JSONObject getSummaryWidgetDetails(String moduleName) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);
        FacilioField name = moduleBean.getField("name", moduleName);
        FacilioField utilityType = moduleBean.getField("utilityType", moduleName);
        FacilioField scope = moduleBean.getField("scope",moduleName);
        FacilioField spaceCategory = moduleBean.getField("spaceCategory", moduleName);
        FacilioField description = moduleBean.getField("description", moduleName);
        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();
        addSummaryFieldInWidgetGroup(widgetGroup, name, 1, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, utilityType, 1, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, scope, 1, 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, spaceCategory, 1, 4, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, description, 2, 1, 4);
        widgetGroup.setName("moduleDetails");
        widgetGroup.setDisplayName("General Information");
        widgetGroup.setColumns(4);
        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(widgetGroup);
        pageWidget.setDisplayName("");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP).getId());
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
}
