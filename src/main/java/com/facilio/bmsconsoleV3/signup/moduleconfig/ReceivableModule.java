package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.TemplatePages.ReceivablesTemplatePage;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.SystemButtonApi;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsole.workflow.rule.CustomButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.SystemButtonRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class ReceivableModule extends BaseModuleConfig{
    public ReceivableModule(){
        setModuleName(FacilioConstants.ContextNames.RECEIVABLE);
    }
    public void addData() throws Exception {
        addSystemButtons();
    }

    public static void addSystemButtons() throws Exception {

        SystemButtonRuleContext addReceiptButton = new SystemButtonRuleContext();
        addReceiptButton.setName("Add Receipt");
        addReceiptButton.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        addReceiptButton.setIdentifier("addReceiptButton");
        addReceiptButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.RECEIVABLE,addReceiptButton);

        SystemButtonRuleContext returnReceiptButton = new SystemButtonRuleContext();
        returnReceiptButton.setName("Return");
        returnReceiptButton.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        returnReceiptButton.setIdentifier("returnReceiptButton");
        returnReceiptButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.RECEIVABLE,returnReceiptButton);

    }
    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> receivable = new ArrayList<FacilioView>();
        receivable.add(getAllReceivableView().setOrder(order++));
        receivable.add(getReceivableForStatus("pending", "Pending", 1).setOrder(order++));
        receivable.add(getReceivableForStatus("partial", "Partially Received", 2).setOrder(order++));
        receivable.add(getReceivableForStatus("received", "Received", 3).setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.RECEIVABLE);
        groupDetails.put("views", receivable);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllReceivableView() {
        FacilioField localId = new FacilioField();
        localId.setName("localId");
        localId.setColumnName("LOCAL_ID");
        localId.setDataType(FieldType.NUMBER);
        localId.setModule(ModuleFactory.getReceivableModule());

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All");
        allView.setSortFields(Arrays.asList(new SortField(localId, false)));

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    private static FacilioView getReceivableForStatus(String viewName, String viewDisplayName, int status) {
        FacilioModule receivableModule = ModuleFactory.getReceivableModule();

        FacilioField createdTime = new FacilioField();
        createdTime.setName("sysCreatedTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("SYS_CREATED_TIME");
        createdTime.setModule(receivableModule);

        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

        Criteria criteria = getReceivableStatusCriteria(receivableModule, status);

        FacilioView statusView = new FacilioView();
        statusView.setName(viewName);
        statusView.setDisplayName(viewDisplayName);
        statusView.setSortFields(sortFields);
        statusView.setCriteria(criteria);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);
        statusView.setAppLinkNames(appLinkNames);

        return statusView;
    }

    private static Criteria getReceivableStatusCriteria(FacilioModule module, int status) {

        FacilioField receivableStatusField = new FacilioField();
        receivableStatusField.setName("status");
        receivableStatusField.setColumnName("STATUS");
        receivableStatusField.setDataType(FieldType.NUMBER);
        receivableStatusField.setModule(ModuleFactory.getReceivableModule());

        Condition statusCond = new Condition();
        statusCond.setField(receivableStatusField);
        statusCond.setOperator(NumberOperators.EQUALS);
        statusCond.setValue(String.valueOf(status));

        Criteria receivableStatusCriteria = new Criteria();
        receivableStatusCriteria.addAndCondition(statusCond);

        return receivableStatusCriteria;
    }
    public Map<String, List<PagesContext>> fetchSystemPageConfigs() throws Exception {
        Map<String,List<PagesContext>> appNameVsPage = new HashMap<>();
        List<String> appNames = new ArrayList<>();
        appNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(getModuleName());
        for (String appName : appNames) {
            ApplicationContext app = ApplicationApi.getApplicationForLinkName(appName);
            appNameVsPage.put(appName,getReceivableViewPage(app, module));
        }
        return appNameVsPage;
    }
    private List<PagesContext> getReceivableViewPage(ApplicationContext app, FacilioModule module) throws Exception {

        return new ModulePages()
                .addPage("receivableViewPage", "Default Receivable View Page", "", null, false, true, true)
                .addLayout(PagesContext.PageLayoutType.WEB)
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("summaryfields", null, null)
                .addWidget("summaryFieldsWidget", "Receivable Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, ReceivablesTemplatePage.getSummaryWidgetDetails(module.getName(), app))
                .widgetDone()
                .sectionDone()
                .addSection("receipts", null, null)
                .addWidget("receiptsWidget", "Receipts", PageWidget.WidgetType.RECEIVABLE_RECEIPT_LIST, "flexiblewebreceiptlist_6", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null,  null)
                .addWidget("commentandattachmentwidgetgroupwidget", null, PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0,  null, ReceivablesTemplatePage.getWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone()
                .pageDone()
                .getCustomPages();
    }

}
