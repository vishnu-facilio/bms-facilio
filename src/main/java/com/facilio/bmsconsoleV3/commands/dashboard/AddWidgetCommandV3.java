package com.facilio.bmsconsoleV3.commands.dashboard;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.V2ReportAction;
import com.facilio.bmsconsole.commands.AddOrUpdateDashboardFieldMappingCommand;
import com.facilio.bmsconsole.commands.FetchReportDataCommand;
import com.facilio.bmsconsoleV3.context.dashboard.WidgetDashboardFilterContext;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.BaseLineAPI;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsoleV3.context.WidgetSectionContext;
import com.facilio.cards.util.CardUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.report.context.*;
import com.facilio.report.context.ReportContext;
import com.facilio.report.util.ReportUtil;
import com.facilio.workflows.util.WorkflowUtil;
import org.apache.commons.chain.Context;
import com.facilio.bmsconsole.util.ViewAPI;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddWidgetCommandV3 extends FacilioCommand {
    private static final org.apache.log4j.Logger LOGGER = Logger.getLogger(AddWidgetCommandV3.class.getName());
    @Override
    public boolean executeCommand(Context context) throws Exception
    {
        // TODO Auto-generated method stub
        DashboardWidgetContext widget = (DashboardWidgetContext) context.get(FacilioConstants.ContextNames.WIDGET);
        if(widget != null) {

            widget.setOrgId(AccountUtil.getCurrentOrg().getId());

            List<FacilioField> fields = FieldFactory.getWidgetFields();

            GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                    .table(ModuleFactory.getWidgetModule().getTableName())
                    .fields(fields);

            Map<String, Object> props = FieldUtil.getAsProperties(widget);
            insertBuilder.addRecord(props);
            insertBuilder.save();

            widget.setId((Long) props.get("id"));
            if(widget.getWidgetFieldMapping() != null && widget.getWidgetFieldMapping().size() > 0) {
                DashboardUtil.addWidgetFieldMapping(widget);
            }
            if(context.get(FacilioConstants.ContextNames.WIDGET_TYPE).equals(DashboardWidgetContext.WidgetType.STATIC)) {
                WidgetStaticContext widgetStaticContext = (WidgetStaticContext) widget;

                insertBuilder = new GenericInsertRecordBuilder()
                        .table(ModuleFactory.getWidgetStaticModule().getTableName())
                        .fields(FieldFactory.getWidgetStaticFields());

                props = FieldUtil.getAsProperties(widgetStaticContext);
                insertBuilder.addRecord(props);
                insertBuilder.save();

                if(CardUtil.isDynamicWorkflowGenCard(widgetStaticContext.getStaticKey()) && (widget.getWidgetVsWorkflowContexts() == null || widget.getWidgetVsWorkflowContexts().isEmpty())) {
                    throw new Exception("Widget With Static Key "+widgetStaticContext.getStaticKey()+" should have workflow assoiciated with it");
                }

                if(widgetStaticContext.getStaticKey().equals(DashboardUtil.STATIC_WIDGET_WEATHER_CARD) || widgetStaticContext.getStaticKey().equals(DashboardUtil.STATIC_WIDGET_WEATHER_CARD_MINI) || widgetStaticContext.getStaticKey().equals(DashboardUtil.STATIC_WIDGET_ENERGY_CARBON_CARD_MINI)) {

                    if(widgetStaticContext.getStaticKey().equals(DashboardUtil.STATIC_WIDGET_WEATHER_CARD) || widgetStaticContext.getStaticKey().equals(DashboardUtil.STATIC_WIDGET_WEATHER_CARD_MINI)) {

                        Long workflowId = WorkflowUtil.addWorkflow(DashboardUtil.WEATHER_WIDGET_WORKFLOW_STRING);
                        WidgetVsWorkflowContext widgetVsWorkflowContext = new WidgetVsWorkflowContext();

                        widgetVsWorkflowContext.setWidgetId(widget.getId());
                        widgetVsWorkflowContext.setWorkflowId(workflowId);
                        widgetVsWorkflowContext.setWorkflowName("weather");

                        if(widgetStaticContext.getBaseSpaceId() != null) {
                            widgetVsWorkflowContext.setBaseSpaceId(widgetStaticContext.getBaseSpaceId());
                        }

                        DashboardUtil.addWidgetVsWorkflowContext(widgetVsWorkflowContext);
                    }

                    if(widgetStaticContext.getStaticKey().equals(DashboardUtil.STATIC_WIDGET_WEATHER_CARD) || widgetStaticContext.getStaticKey().equals(DashboardUtil.STATIC_WIDGET_ENERGY_CARBON_CARD_MINI)) {
                        Long workflowId = null;

                        if(AccountUtil.getCurrentOrg().getId() == 116l) {
                            workflowId = WorkflowUtil.addWorkflow(DashboardUtil.CARBON_EMISSION_CARBON_MODULE_CARD);
                        }
                        else {
                            workflowId = WorkflowUtil.addWorkflow(DashboardUtil.CARBON_EMISSION_CARD);
                        }
                        WidgetVsWorkflowContext widgetVsWorkflowContext = new WidgetVsWorkflowContext();

                        widgetVsWorkflowContext.setWidgetId(widget.getId());
                        widgetVsWorkflowContext.setWorkflowId(workflowId);
                        widgetVsWorkflowContext.setWorkflowName("carbonEmission");

                        if(widgetStaticContext.getBaseSpaceId() != null) {
                            widgetVsWorkflowContext.setBaseSpaceId(widgetStaticContext.getBaseSpaceId());
                        }

                        DashboardUtil.addWidgetVsWorkflowContext(widgetVsWorkflowContext);
                    }
                }
                else if(widgetStaticContext.getStaticKey().equals(DashboardUtil.STATIC_WIDGET_ENERGY_COST_CARD) || widgetStaticContext.getStaticKey().equals(DashboardUtil.STATIC_WIDGET_ENERGY_COST_CARD_MINI)) {

                    Long workflowId = null;

                    if(AccountUtil.getCurrentOrg().getId() == 116l) {
                        workflowId = WorkflowUtil.addWorkflow(DashboardUtil.ENERGY_COST_THIS_MONTH_CONSUMPTION_COST_MODULE_WORKFLOW);
                    }
                    else {
                        workflowId = WorkflowUtil.addWorkflow(DashboardUtil.ENERGY_COST_THIS_MONTH_CONSUMPTION_WORKFLOW);
                    }

                    WidgetVsWorkflowContext widgetVsWorkflowContext = new WidgetVsWorkflowContext();

                    widgetVsWorkflowContext.setWidgetId(widget.getId());
                    widgetVsWorkflowContext.setWorkflowId(workflowId);
                    widgetVsWorkflowContext.setWorkflowName("currentMonth");

                    if(widgetStaticContext.getBaseSpaceId() != null) {
                        widgetVsWorkflowContext.setBaseSpaceId(widgetStaticContext.getBaseSpaceId());
                    }

                    DashboardUtil.addWidgetVsWorkflowContext(widgetVsWorkflowContext);

                    if(AccountUtil.getCurrentOrg().getId() == 116l) {
                        workflowId = WorkflowUtil.addWorkflow(DashboardUtil.ENERGY_COST_LAST_MONTH_CONSUMPTION_COST_MODULE_WORKFLOW);
                    }
                    else {
                        workflowId = WorkflowUtil.addWorkflow(DashboardUtil.ENERGY_COST_LAST_MONTH_CONSUMPTION_WORKFLOW);
                    }

                    widgetVsWorkflowContext = new WidgetVsWorkflowContext();

                    widgetVsWorkflowContext.setWidgetId(widget.getId());
                    widgetVsWorkflowContext.setWorkflowId(workflowId);
                    widgetVsWorkflowContext.setWorkflowName("lastMonth");
                    if(widgetStaticContext.getBaseSpaceId() != null) {
                        widgetVsWorkflowContext.setBaseSpaceId(widgetStaticContext.getBaseSpaceId());
                    }
                    DashboardUtil.addWidgetVsWorkflowContext(widgetVsWorkflowContext);

                    BaseLineContext baseline = BaseLineAPI.getBaseLine(BaseLineContext.RangeType.PREVIOUS_MONTH);

                    String energyCostLastMonth = null;

                    if(AccountUtil.getCurrentOrg().getId() == 116l) {
                        energyCostLastMonth = DashboardUtil.ENERGY_COST_LAST_MONTH_THIS_DATE_CONSUMPTION_COST_MODULE_WORKFLOW;
                    }
                    else {
                        energyCostLastMonth = DashboardUtil.ENERGY_COST_LAST_MONTH_THIS_DATE_CONSUMPTION_WORKFLOW;
                    }

                    energyCostLastMonth = energyCostLastMonth.replaceAll("\\$\\$BASELINE_ID\\$\\$", baseline.getId()+"");
                    workflowId = WorkflowUtil.addWorkflow(energyCostLastMonth);
                    widgetVsWorkflowContext = new WidgetVsWorkflowContext();

                    widgetVsWorkflowContext.setWidgetId(widget.getId());
                    widgetVsWorkflowContext.setWorkflowId(workflowId);
                    widgetVsWorkflowContext.setWorkflowName("lastMonthThisDate");
                    if(widgetStaticContext.getBaseSpaceId() != null) {
                        widgetVsWorkflowContext.setBaseSpaceId(widgetStaticContext.getBaseSpaceId());
                    }
                    DashboardUtil.addWidgetVsWorkflowContext(widgetVsWorkflowContext);

                    energyCostLastMonth = DashboardUtil.LAST_MONTH_THIS_DATE.replaceAll("\\$\\$BASELINE_ID\\$\\$", baseline.getId()+"");
                    workflowId = WorkflowUtil.addWorkflow(energyCostLastMonth);
                    widgetVsWorkflowContext = new WidgetVsWorkflowContext();

                    widgetVsWorkflowContext.setWidgetId(widget.getId());
                    widgetVsWorkflowContext.setWorkflowId(workflowId);
                    widgetVsWorkflowContext.setWorkflowName("lastMonthDate");
                    if(widgetStaticContext.getBaseSpaceId() != null) {
                        widgetVsWorkflowContext.setBaseSpaceId(widgetStaticContext.getBaseSpaceId());
                    }
                    DashboardUtil.addWidgetVsWorkflowContext(widgetVsWorkflowContext);
                }
                else if(widgetStaticContext.getStaticKey().equals(DashboardUtil.STATIC_WIDGET_ENERGY_CARD_MINI)) {

                    Long workflowId = WorkflowUtil.addWorkflow(DashboardUtil.ENERGY_CONSUMPTION_THIS_MONTH_WORKFLOW);
                    WidgetVsWorkflowContext widgetVsWorkflowContext = new WidgetVsWorkflowContext();

                    widgetVsWorkflowContext.setWidgetId(widget.getId());
                    widgetVsWorkflowContext.setWorkflowId(workflowId);
                    widgetVsWorkflowContext.setWorkflowName("currentMonth");
                    if(widgetStaticContext.getBaseSpaceId() != null) {
                        widgetVsWorkflowContext.setBaseSpaceId(widgetStaticContext.getBaseSpaceId());
                    }
                    DashboardUtil.addWidgetVsWorkflowContext(widgetVsWorkflowContext);

                    workflowId = WorkflowUtil.addWorkflow(DashboardUtil.ENERGY_CONSUMPTION_LAST_MONTH_WORKFLOW);
                    widgetVsWorkflowContext = new WidgetVsWorkflowContext();

                    widgetVsWorkflowContext.setWidgetId(widget.getId());
                    widgetVsWorkflowContext.setWorkflowId(workflowId);
                    widgetVsWorkflowContext.setWorkflowName("lastMonth");
                    if(widgetStaticContext.getBaseSpaceId() != null) {
                        widgetVsWorkflowContext.setBaseSpaceId(widgetStaticContext.getBaseSpaceId());
                    }
                    DashboardUtil.addWidgetVsWorkflowContext(widgetVsWorkflowContext);

                }

                else if(widgetStaticContext.getStaticKey().equals(DashboardUtil.STATIC_WIDGET_PROFILE_CARD_MINI)) {

                    WidgetVsWorkflowContext widgetVsWorkflowContext = new WidgetVsWorkflowContext();

                    widgetVsWorkflowContext.setWidgetId(widget.getId());
                    widgetVsWorkflowContext.setWorkflowId(null);
                    widgetVsWorkflowContext.setWorkflowName("currentMonth");
                    if(widgetStaticContext.getBaseSpaceId() != null) {
                        widgetVsWorkflowContext.setBaseSpaceId(widgetStaticContext.getBaseSpaceId());
                    }
                    DashboardUtil.addWidgetVsWorkflowContext(widgetVsWorkflowContext);

                }
                else if (widget.getWidgetVsWorkflowContexts() != null && !widget.getWidgetVsWorkflowContexts().isEmpty()) {
                    for(WidgetVsWorkflowContext widgetVsWorkflowContext :widget.getWidgetVsWorkflowContexts()) {
                        widgetVsWorkflowContext.setWidgetId(widget.getId());
                        DashboardUtil.addWidgetVsWorkflowContext(widgetVsWorkflowContext);
                    }
                }
            }
            else if(context.get(FacilioConstants.ContextNames.WIDGET_TYPE).equals(DashboardWidgetContext.WidgetType.WEB)) {
                WidgetWebContext widgetWebContext = (WidgetWebContext) widget;
                insertBuilder = new GenericInsertRecordBuilder()
                        .table(ModuleFactory.getWidgetWebModule().getTableName())
                        .fields(FieldFactory.getWidgetWebFields());

                props = FieldUtil.getAsProperties(widgetWebContext);
                insertBuilder.addRecord(props);
                insertBuilder.save();
            }
            else if(context.get(FacilioConstants.ContextNames.WIDGET_TYPE).equals(DashboardWidgetContext.WidgetType.GRAPHICS)) {
                WidgetGraphicsContext widgetGraphicsContext = (WidgetGraphicsContext) widget;
                insertBuilder = new GenericInsertRecordBuilder()
                        .table(ModuleFactory.getWidgetGraphicsModule().getTableName())
                        .fields(FieldFactory.getWidgetGraphicsFields());

                props = FieldUtil.getAsProperties(widgetGraphicsContext);
                insertBuilder.addRecord(props);
                insertBuilder.save();
            }
            else if(context.get(FacilioConstants.ContextNames.WIDGET_TYPE).equals(DashboardWidgetContext.WidgetType.CARD)) {
                WidgetCardContext widgetCardContext = (WidgetCardContext) widget;

                if (widgetCardContext.getScriptModeInt() == 3) {
                    widgetCardContext.setCustomScriptId((long) -99);
                    widgetCardContext.setCustomScript(null);
                    widgetCardContext.setScriptModeInt(3);
                }
                else if (widgetCardContext.getCustomScript() != null && !widgetCardContext.getCustomScript().trim().isEmpty()) {
                    Long customScriptId = WorkflowUtil.addWorkflow(widgetCardContext.getCustomScript());
                    widgetCardContext.setCustomScriptId(customScriptId);
                }
                if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.DASHBOARD_V2)){
                    JSONObject cardParams = widgetCardContext.getCardParams();
                    if(cardParams != null) {
                        widgetCardContext.setCategoryId((Long) cardParams.get("categoryId"));
                        Criteria criteriaObj = FieldUtil.getAsBeanFromMap((Map<String, Object>) cardParams.get("criteria"), Criteria.class);
                        long criteriaId = DashboardUtil.generateCriteriaId(criteriaObj, (String) cardParams.get("parentModuleName"));
                        if(criteriaId > 0){
                            widgetCardContext.setCriteriaId(criteriaId);
                        }
                    }
                }
                insertBuilder = new GenericInsertRecordBuilder()
                        .table(ModuleFactory.getWidgetCardModule().getTableName())
                        .fields(FieldFactory.getWidgetCardFields());

                props = FieldUtil.getAsProperties(widgetCardContext);
                insertBuilder.addRecord(props);
                insertBuilder.save();
            }
            else if(context.get(FacilioConstants.ContextNames.WIDGET_TYPE).equals(DashboardWidgetContext.WidgetType.CHART)) {
                WidgetChartContext widgetChartContext = (WidgetChartContext) widget;
                insertBuilder = new GenericInsertRecordBuilder()
                        .table(ModuleFactory.getWidgetChartModule().getTableName())
                        .fields(FieldFactory.getWidgetChartFields());
                try {
                    if (context.containsKey("isCloneToAnotherAPP") && (Boolean) context.containsKey("isCloneToAnotherAPP")) {
                        DashboardWidgetContext widgets = (DashboardWidgetContext) context.get(FacilioConstants.ContextNames.WIDGET);
                        Long reportId = ((WidgetChartContext)widgets).getNewReportId();
                        Long new_reportId = ReportUtil.cloneReport(reportId,(Long) context.get("target_app_id"),(Long) context.get("cloned_app_id"));
                        if(new_reportId != null && new_reportId >0){
                            widgetChartContext.setNewReportId(new_reportId);
                        }
                    }
                } catch (Exception e) {
                    LOGGER.debug("Error while creating report during dashboard clone", e);
                }
                props = FieldUtil.getAsProperties(widgetChartContext);
                insertBuilder.addRecord(props);
                insertBuilder.save();
            }
            else if(context.get(FacilioConstants.ContextNames.WIDGET_TYPE).equals(DashboardWidgetContext.WidgetType.LIST_VIEW)) {

                WidgetListViewContext widgetListViewContext = (WidgetListViewContext) widget;
                insertBuilder = new GenericInsertRecordBuilder()
                        .table(ModuleFactory.getWidgetListViewModule().getTableName())
                        .fields(FieldFactory.getWidgetListViewFields());
                try{
                    if(context.containsKey("isCloneToAnotherAPP") && (Boolean)context.containsKey("isCloneToAnotherAPP") ){
                        String moduleName = widgetListViewContext.getModuleName();
                        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean" );
                        FacilioModule module= modBean.getModule(moduleName);
                        long moduleId = module.getModuleId();
                        long orgId = widgetListViewContext.getOrgId();
                        String name = widgetListViewContext.getViewName();
                        Long appId = (Long) context.get("cloned_app_id");
                        Long targetId = (Long) context.get("target_app_id");
                        if(moduleName != null && !"".equals(moduleName) &&  appId != null && appId > 0 && targetId != null && targetId > 0){
                            FacilioView views = ViewAPI.getView(name, moduleId, orgId, targetId);
                            if(views == null) {
                                FacilioView view = ViewAPI.getView(name, moduleId, orgId, appId);
                                view.setId(-1l);
                                view.setAppId(targetId);
                                if(view.getFields() != null){
                                    for(ViewField field : view.getFields())
                                    {
                                        field.setId(-1);
                                    }
                                }
                                ViewAPI.addView(view, orgId);
                            }
                        }
                    }
                }catch (Exception e){
                    LOGGER.debug("Error while creating view during dashboard clone", e);
                }
                props = FieldUtil.getAsProperties(widgetListViewContext);
                insertBuilder.addRecord(props);
                insertBuilder.save();
            }
            else if(context.get(FacilioConstants.ContextNames.WIDGET_TYPE).equals(DashboardWidgetContext.WidgetType.SECTION))
            {
                WidgetSectionContext section_widget = (WidgetSectionContext) widget;
                insertBuilder = new GenericInsertRecordBuilder()
                        .table(ModuleFactory.getWidgetSectionModule().getTableName())
                        .fields(FieldFactory.getWidgetSectionFields());

                props = FieldUtil.getAsProperties(section_widget);
                insertBuilder.addRecord(props);
                insertBuilder.save();
            }
            else if(context.get(FacilioConstants.ContextNames.WIDGET_TYPE).equals(DashboardWidgetContext.WidgetType.FILTER))
            {
                WidgetDashboardFilterContext filter_widget = (WidgetDashboardFilterContext) widget;
                if(filter_widget.getDashboardFilterId() > 0){
                    filter_widget.setType(DashboardWidgetContext.WidgetType.FILTER.getValue());
                    filter_widget.setWidget_id(widget.getId());
                    if(filter_widget.getCriteria() != null){
                        long criteriaId=CriteriaAPI.addCriteria(filter_widget.getCriteria());
                        filter_widget.setCriteriaId(criteriaId);
                    }
                    insertBuilder = new GenericInsertRecordBuilder()
                            .table(ModuleFactory.getDashboardUserFilterModule().getTableName())
                            .fields(FieldFactory.getDashboardUserFilterFields());

                    props = FieldUtil.getAsProperties(filter_widget);
                    insertBuilder.addRecord(props);
                    insertBuilder.save();

                    AddOrUpdateDashboardFieldMappingCommand fieldMappingCommand = new AddOrUpdateDashboardFieldMappingCommand();
                    context.put(FacilioConstants.ContextNames.DASHBOARD_USER_FILTER_ID,props.get("id"));
                    context.put("fieldMappings",filter_widget.getFieldMappingMap());
                    fieldMappingCommand.executeCommand(context);
                }
                else{
                    FacilioChain dbFilterUpdateChain=TransactionChainFactory.getNewAddOrUpdateDashboardFilterChain(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.DASHBOARD_V2));
                    FacilioContext filterContext=dbFilterUpdateChain.getContext();
                    DashboardFilterContext dashboardFilter = filter_widget.getDashboardFilter();
                    filterContext.put(FacilioConstants.ContextNames.WIDGET_ID,widget.getId());
                    filterContext.put(FacilioConstants.ContextNames.DASHBOARD_FILTER, dashboardFilter);
                    if(dashboardFilter.getDashboardId()!=-1)
                    {
                        filterContext.put(FacilioConstants.ContextNames.DASHBOARD, DashboardUtil.getDashboardWithWidgets(dashboardFilter.getDashboardId()));
                    }
                    else if(dashboardFilter.getDashboardTabId()!=-1)
                    {
                        filterContext.put(FacilioConstants.ContextNames.DASHBOARD_TAB, DashboardUtil.getDashboardTabWithWidgets(dashboardFilter.getDashboardTabId()));
                    }
                    dbFilterUpdateChain.execute();
                }
            }
        }
        context.put(FacilioConstants.ContextNames.WIDGET, widget);
        return false;
    }
}
