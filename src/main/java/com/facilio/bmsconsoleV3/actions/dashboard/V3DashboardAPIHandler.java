package com.facilio.bmsconsoleV3.actions.dashboard;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.DashboardFieldMappingContext;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.*;
import com.facilio.bmsconsoleV3.actions.DashboardExecuteMetaContext;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.WidgetSectionContext;
import com.facilio.bmsconsoleV3.context.dashboard.*;
import com.facilio.cards.util.CardLayout;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.*;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.report.context.ReportDataPointContext;
import com.facilio.report.context.ReportYAxisContext;
import com.facilio.time.DateRange;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import lombok.extern.log4j.Log4j;
import org.apache.log4j.Level;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

@Log4j
public class V3DashboardAPIHandler {

    public static final String VARIABLE_PLACE_HOLDER = "\\$\\{(.+?)\\}";
    public static void updateDashboardProp(DashboardContext dashboard, JSONObject dashboardMeta)throws Exception
    {
        dashboard.setDashboardName((String) dashboardMeta.get("dashboardName"));
        if(dashboardMeta.get("dashboardFolderId") != null) {
            dashboard.setDashboardFolderId((Long) dashboardMeta.get("dashboardFolderId"));
        }
        if(dashboardMeta.get("description") != null) {
            dashboard.setDescription((String) dashboardMeta.get("description"));
        }
        if (dashboardMeta.get("dashboardTabPlacement") != null) {
            int dashboardTabPlacement  = ((Long) dashboardMeta.get("dashboardTabPlacement")).intValue();
            dashboard.setDashboardTabPlacement(dashboardTabPlacement);
        }
    }
    public static void updateDashboardTabProp(DashboardTabContext dashboardTabContext, JSONObject dashboardMeta)throws Exception
    {
        List dashboardWidgets = (List) dashboardMeta.get("dashboardWidgets");
        List<DashboardWidgetContext> widgets = V3DashboardAPIHandler.getDashboardSectionWidgetFromWidgetMeta(dashboardWidgets);
        dashboardTabContext.setDashboardWidgets(widgets);
    }
    public static void updateDashboardData(DashboardContext dashboard, JSONObject dashboardMeta)throws Exception
    {
        V3DashboardAPIHandler.updateDashboardProp(dashboard, dashboardMeta);
        if(dashboardMeta.get("mobileEnabled") != null) {
            dashboard.setMobileEnabled((boolean)dashboardMeta.get("mobileEnabled"));
        }
        if(dashboard.isTabEnabled() && dashboardMeta.get("tabEnabled") != null && !((Boolean)dashboardMeta.get("tabEnabled")))
        {
            dashboard.setSkipDefaultWidgetDeletion(true);
        }
        if (dashboardMeta.get("tabEnabled") != null) {
            dashboard.setTabEnabled((boolean) dashboardMeta.get("tabEnabled"));
        }
        if(dashboardMeta.get("clientMetaJsonString") != null) {
            dashboard.setClientMetaJsonString(dashboardMeta.get("clientMetaJsonString").toString());
        }
        List dashboardWidgets = (List) dashboardMeta.get("dashboardWidgets");

        List<DashboardWidgetContext> widgets = getDashboardSectionWidgetFromWidgetMeta(dashboardWidgets);
        dashboard.setDashboardWidgets(widgets);

    }

    public static List<DashboardWidgetContext> getDashboardSectionWidgetFromWidgetMeta(List dashboardWidgets)throws Exception
    {
        List<DashboardWidgetContext> widgets = new ArrayList<>();
        if (dashboardWidgets != null)
        {
            for (int i = 0; i < dashboardWidgets.size(); i++) {
                Map widget = (Map) dashboardWidgets.get(i);
                Integer widgetType = DashboardWidgetContext.WidgetType.getWidgetType(widget.get("type").toString()).getValue();

                DashboardWidgetContext widgetContext = null;
                if (widgetType == DashboardWidgetContext.WidgetType.SECTION.getValue()) {
                    widgetContext = new WidgetSectionContext();
                    widgetContext = FieldUtil.getAsBeanFromMap(widget, WidgetSectionContext.class);
                    widgetContext.setType(widgetType);
                    widgets.add(widgetContext);
                    List widgets_in_section = (ArrayList) widget.get("section");
                    if(widgets_in_section != null && widgets_in_section.size() > 0) {
                        int size = widgets_in_section.size();
                        List<DashboardWidgetContext> widget_list = new ArrayList<DashboardWidgetContext>();
                        for(int j=0;j<size; j++)
                        {
                            Map widget_map = (Map) widgets_in_section.get(j);
                            DashboardWidgetContext widget_context = getDashboardWidgetFromWidgetMeta(widget_map);
                            if(widget_context != null)
                            {
                                widget_list.add(widget_context);
                            }
                        }
                        if(widget_list.size() > 0)
                        {
                            ((WidgetSectionContext) widgetContext).setWidgets_in_section(widget_list);
                        }
                    }
                }
                else {
                    DashboardWidgetContext widget_context = getDashboardWidgetFromWidgetMeta(widget);
                    widgets.add(widget_context);
                }
            }
        }
        return widgets;
    }
    private static DashboardWidgetContext getDashboardWidgetFromWidgetMeta(Map widget)
    {
        Integer widgetType = DashboardWidgetContext.WidgetType.getWidgetType(widget.get("type").toString()).getValue();

        DashboardWidgetContext widgetContext = null;
        if (widgetType == DashboardWidgetContext.WidgetType.CHART.getValue()) {
            widgetContext = new WidgetChartContext();
            widgetContext = FieldUtil.getAsBeanFromMap(widget, WidgetChartContext.class);
        }
        else if (widgetType == DashboardWidgetContext.WidgetType.LIST_VIEW.getValue()) {
            widgetContext = new WidgetListViewContext();
            widgetContext = FieldUtil.getAsBeanFromMap(widget, WidgetListViewContext.class);
        }
        else if (widgetType == DashboardWidgetContext.WidgetType.STATIC.getValue()) {
            widgetContext = new WidgetStaticContext();
            widgetContext = FieldUtil.getAsBeanFromMap(widget, WidgetStaticContext.class);
        }
        else if (widgetType == DashboardWidgetContext.WidgetType.WEB.getValue()) {
            widgetContext = new WidgetWebContext();
            widgetContext = FieldUtil.getAsBeanFromMap(widget, WidgetWebContext.class);
        }
        else if (widgetType == DashboardWidgetContext.WidgetType.GRAPHICS.getValue()) {
            widgetContext = new WidgetGraphicsContext();
            widgetContext = FieldUtil.getAsBeanFromMap(widget, WidgetGraphicsContext.class);
        }
        else if (widgetType == DashboardWidgetContext.WidgetType.CARD.getValue()) {
            widgetContext = new WidgetCardContext();
            widgetContext = FieldUtil.getAsBeanFromMap(widget, WidgetCardContext.class);
        }
        else if(widgetType == DashboardWidgetContext.WidgetType.FILTER.getValue()) {
            widgetContext = new WidgetDashboardFilterContext();
            widgetContext = FieldUtil.getAsBeanFromMap(widget, WidgetDashboardFilterContext.class);
        }

        widgetContext.setLayoutPosition(Integer.parseInt(widget.get("order").toString()));
        widgetContext.setType(widgetType);

        return widgetContext;
    }

    public static void updateDashboardTabAPI(DashboardContext dashboard, DashboardTabContext dashboardTabContext, Boolean isFromReport)throws Exception
    {
        if(dashboardTabContext != null) {
            FacilioChain updateDashboardChain = TransactionChainFactoryV3.getUpdateDashboardTabChainV3();
            FacilioContext updateTabContext = updateDashboardChain.getContext();
            updateTabContext.put(FacilioConstants.ContextNames.DASHBOARD_TAB, dashboardTabContext);
            updateTabContext.put(FacilioConstants.ContextNames.DASHBOARD, dashboard);
            updateTabContext.put(FacilioConstants.ContextNames.IS_FROM_REPORT, isFromReport);
            updateDashboardChain.execute();
        }
    }


    public static void createDashboardRule(DashboardRuleContext dashboard_rule)throws Exception
    {
        GenericInsertRecordBuilder insert_builder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getDashboardRuleModule().getTableName())
                .fields(FieldFactory.getDashboardRuleFields());

        Map<String, Object> props = FieldUtil.getAsProperties(dashboard_rule);
        insert_builder.addRecord(props);
        insert_builder.save();
        dashboard_rule.setId((Long) props.get("id"));
    }

    public static void addTriggerWidgetMapping(Long dashboard_rule_Id, List<DashboardTriggerWidgetContext>  trigger_widgets)throws Exception
    {
        if(trigger_widgets != null && trigger_widgets.size() > 0)
        {
            for(DashboardTriggerWidgetContext trigger_widget : trigger_widgets)
            {
                trigger_widget.setDashboard_rule_id(dashboard_rule_Id);
                Long criteriaId = V3DashboardAPIHandler.generateDashboardCriteriaId(trigger_widget.getCriteria() , trigger_widget.getModuleName());
                if(criteriaId > 0){
                    trigger_widget.setCriteriaId(criteriaId);
                }
                GenericInsertRecordBuilder insert_builder = new GenericInsertRecordBuilder()
                        .table(ModuleFactory.getDashboardTriggerWidgetModule().getTableName())
                        .fields(FieldFactory.getDashboardTriggerWidgetFields());

                Map<String, Object> props = FieldUtil.getAsProperties(trigger_widget);
                insert_builder.addRecord(props);
                insert_builder.save();
                trigger_widget.setId((Long) props.get("id"));
            }
        }
    }

    public static void addDashboardRuleActions(Long dashboard_rule_Id, List<DashboardRuleActionContext>  dashboard_actions)throws Exception
    {
        if(dashboard_actions != null && dashboard_actions.size() > 0)
        {
            for(DashboardRuleActionContext action : dashboard_actions)
            {
                action.setDashboard_rule_id(dashboard_rule_Id);
                GenericInsertRecordBuilder insert_builder = new GenericInsertRecordBuilder()
                        .table(ModuleFactory.getDashboardRuleActionModule().getTableName())
                        .fields(FieldFactory.getDashboardRuleActionsFields());

                Map<String, Object> props = FieldUtil.getAsProperties(action);
                insert_builder.addRecord(props);
                insert_builder.save();
                action.setId((Long) props.get("id"));

                V3DashboardAPIHandler.addDashboardRuleActionsMeta(action.getId(), action.getAction_meta());
                V3DashboardAPIHandler.insertDashboardRuleTargetWidgetMapping(action.getId(), action.getTarget_widgets());
            }
        }
    }

    public static void addDashboardRuleActionsMeta(Long actionId, DashboardRuleActionMetaContext  dashboard_actions_meta)throws Exception
    {
        if(dashboard_actions_meta != null)
        {
            dashboard_actions_meta.setActionId(actionId);
            Long criteriaId = V3DashboardAPIHandler.generateDashboardCriteriaId(dashboard_actions_meta.getCriteria(), dashboard_actions_meta.getModuleName());
            if(criteriaId > 0){
                dashboard_actions_meta.setCriteriaId(criteriaId);
            }
            if(dashboard_actions_meta.getScript() != null){
                Long scriptId = V3DashboardAPIHandler.generateWorkflowScriptId(dashboard_actions_meta.getScript());
                if(scriptId == null){
                    throw new Exception("Invalid Script");
                }
                dashboard_actions_meta.setScriptId(scriptId != null ? (Long)scriptId : null);
            }
//          Criteria criteria = FieldUtil.getAsBeanFromMap(criteriaMap, Criteria.class);
            GenericInsertRecordBuilder insert_builder = new GenericInsertRecordBuilder()
                    .table(ModuleFactory.getDashboardRuleActionMetaModule().getTableName())
                    .fields(FieldFactory.getDashboardRuleActionMetaFields());

            Map<String, Object> props = FieldUtil.getAsProperties(dashboard_actions_meta);
            insert_builder.addRecord(props);
            insert_builder.save();
            dashboard_actions_meta.setId((Long) props.get("id"));
        }
    }

    public static void insertDashboardRuleTargetWidgetMapping(Long actionId, List<DashboardTriggerAndTargetWidgetContext> target_widgets)throws Exception
    {
        if(target_widgets != null && target_widgets.size() > 0)
        {
            for(DashboardTriggerAndTargetWidgetContext target_widget : target_widgets)
            {
                target_widget.setActionId(actionId);
                if(target_widget.getDataPointList() != null && target_widget.getDataPointList().size() > 0)
                {
                    for(DashboardReadingTargetWidgetContext reading_target_widget: target_widget.getDataPointList())
                    {
                        if(reading_target_widget != null)
                        {
                            if(reading_target_widget.getCriteria() != null) {
                                Long criteriaId = V3DashboardAPIHandler.generateDashboardCriteriaId(reading_target_widget.getCriteria(), reading_target_widget.getModuleName());
                                if (criteriaId > 0) {
                                    reading_target_widget.setCriteriaId(criteriaId);
                                }
                            }
                            reading_target_widget.setActionId(actionId);
                            reading_target_widget.setTarget_widget_id(target_widget.getTarget_widget_id());
                            reading_target_widget.setDataPointMetaStr(reading_target_widget.toDataPointJson().toJSONString());
                            GenericInsertRecordBuilder insert_builder = new GenericInsertRecordBuilder()
                                    .table(ModuleFactory.getDashboardRuleTargetWidgetModule().getTableName())
                                    .fields(FieldFactory.getDashboardRuleTargetWidgetFields());

                            Map<String, Object> props = FieldUtil.getAsProperties(reading_target_widget);
                            insert_builder.addRecord(props);
                            insert_builder.save();
                        }
                    }
                }
                else
                {
                    Long criteriaId = V3DashboardAPIHandler.generateDashboardCriteriaId(target_widget.getCriteria(), target_widget.getModuleName());
                    if (criteriaId > 0) {
                        target_widget.setCriteriaId(criteriaId);
                    }
                    GenericInsertRecordBuilder insert_builder = new GenericInsertRecordBuilder()
                            .table(ModuleFactory.getDashboardRuleTargetWidgetModule().getTableName())
                            .fields(FieldFactory.getDashboardRuleTargetWidgetFields());

                    Map<String, Object> props = FieldUtil.getAsProperties(target_widget);
                    insert_builder.addRecord(props);
                    insert_builder.save();
                    target_widget.setId((Long) props.get("id"));
                }





            }
        }
    }

    public static DashboardRuleContext getDashboardRule(Long dashboard_rule_id, Integer triggerType)throws Exception
    {
        GenericSelectRecordBuilder select_builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getDashboardRuleModule().getTableName())
                .select(FieldFactory.getDashboardRuleFields())
                .andCustomWhere("ORGID = ?", AccountUtil.getCurrentOrg().getOrgId())
                .andCondition(CriteriaAPI.getCondition("ID", "id", dashboard_rule_id+"", NumberOperators.EQUALS));

        if(triggerType != null){
            select_builder.andCustomWhere("TRIGGER_TYPE = ?", triggerType);
        }

        List<Map<String, Object>> props = select_builder.get();
        if(props != null && !props.isEmpty()){
            DashboardRuleContext dashboard_rule = FieldUtil.getAsBeanFromMap(props.get(0), DashboardRuleContext.class);
            V3DashboardAPIHandler.getDashboardRuleTriggerWidgets(dashboard_rule);
            V3DashboardAPIHandler.getDashboardRuleActions(dashboard_rule);
            return dashboard_rule;
        }
        return null;
    }

    public static List<DashboardRuleContext> getDashboardRules(Long dashboardId, Long dashboardTabId)throws Exception
    {
        List<DashboardRuleContext> dashboard_rules = new ArrayList<>();
        GenericSelectRecordBuilder select_builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getDashboardRuleModule().getTableName())
                .select(FieldFactory.getDashboardRuleFields())
                .andCustomWhere("ORGID = ?", AccountUtil.getCurrentOrg().getOrgId());

        if(dashboardId != null && dashboardId > 0 && dashboardTabId != null && dashboardTabId > 0)
        {
            select_builder.andCondition(CriteriaAPI.getCondition("DASHBOARD_TAB_ID", "dashboardTabId", "" + dashboardTabId, NumberOperators.EQUALS));
        }
        else if(dashboardId != null && dashboardId > 0 && dashboardTabId == null) {
            select_builder.andCondition(CriteriaAPI.getCondition("DASHBOARD_ID", "dashboardId", "" + dashboardId, NumberOperators.EQUALS));
        }
        else{
            return null;
        }

        List<Map<String, Object>> props = select_builder.get();
        if (props != null && !props.isEmpty()) {

            int rule_len = props.size();
            for(int i=0; i < rule_len ;i++)
            {
                DashboardRuleContext dash_rule_context = FieldUtil.getAsBeanFromMap(props.get(i), DashboardRuleContext.class);
                V3DashboardAPIHandler.getDashboardRuleTriggerWidgets(dash_rule_context);
                V3DashboardAPIHandler.getDashboardRuleActions(dash_rule_context);
                dashboard_rules.add(dash_rule_context);
            }
        }
        return dashboard_rules;
    }

    public static void getDashboardRuleActions(DashboardRuleContext dashboard_rule)throws Exception
    {
        GenericSelectRecordBuilder select_builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getDashboardRuleActionModule().getTableName())
                .select(FieldFactory.getDashboardRuleActionsFields())
                .andCondition(CriteriaAPI.getCondition("DASHBOARD_RULE_ID","dashboard_rule_id" ,dashboard_rule.getId()+"", NumberOperators.EQUALS));

        List<Map<String, Object>> props = select_builder.get();
        if (props != null && !props.isEmpty()) {
            int actions_len = props.size();
            List<DashboardRuleActionContext> actions_list = new ArrayList<>();
            for(int i=0; i< actions_len ; i++)
            {
                DashboardRuleActionContext dash_rule_action = FieldUtil.getAsBeanFromMap(props.get(i), DashboardRuleActionContext.class);
                V3DashboardAPIHandler.getDashboardRuleActionMeta(dash_rule_action);
                V3DashboardAPIHandler.getDashboardRuleTargetWidgets(dash_rule_action);
                actions_list.add(dash_rule_action);
            }
            dashboard_rule.setActions(actions_list);
        }
    }

    public static void getDashboardRuleTriggerWidgets(DashboardRuleContext dashboard_rule)throws Exception
    {
        GenericSelectRecordBuilder select_builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getDashboardTriggerWidgetModule().getTableName())
                .select(FieldFactory.getDashboardTriggerWidgetFields())
                .andCondition(CriteriaAPI.getCondition("DASHBOARD_RULE_ID","dashboard_rule_id" ,dashboard_rule.getId()+"", NumberOperators.EQUALS));

        List<Map<String, Object>> props = select_builder.get();
        if(props != null && !props.isEmpty())
        {
            int trigger_widget_len = props.size();
            List<DashboardTriggerWidgetContext> trigger_widget_list = new ArrayList<>();
            for(int i=0 ; i < trigger_widget_len ; i++)
            {
                DashboardTriggerWidgetContext trigger_widget_context = FieldUtil.getAsBeanFromMap(props.get(i), DashboardTriggerWidgetContext.class);
                if(trigger_widget_context.getCriteriaId() != null && trigger_widget_context.getCriteriaId() > 0) {
                    trigger_widget_context.setCriteria(CriteriaAPI.getCriteria(AccountUtil.getCurrentOrg().getOrgId(), trigger_widget_context.getCriteriaId()));
                }
                trigger_widget_list.add(trigger_widget_context);
            }
            dashboard_rule.setTrigger_widgets(trigger_widget_list);
        }
    }
    public static void getDashboardRuleActionMeta(DashboardRuleActionContext dashboard_rule_action)throws Exception
    {
        GenericSelectRecordBuilder select_builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getDashboardRuleActionMetaModule().getTableName())
                .select(FieldFactory.getDashboardRuleActionMetaFields())
                .andCondition(CriteriaAPI.getCondition("ACTION_ID", "actionId", dashboard_rule_action.getId().toString() , NumberOperators.EQUALS));

        List<Map<String, Object>> props = select_builder.get();
        if(props != null && !props.isEmpty())
        {
            DashboardRuleActionMetaContext action_meta = FieldUtil.getAsBeanFromMap(props.get(0), DashboardRuleActionMetaContext.class);
            if(action_meta.getCriteriaId() != null && action_meta.getCriteriaId() > 0) {
                action_meta.setCriteria(CriteriaAPI.getCriteria(AccountUtil.getCurrentOrg().getOrgId(), action_meta.getCriteriaId()));
            }
            if(action_meta.getScriptId() != null && action_meta.getScriptId() > 0){
                WorkflowContext scriptWorkflow = WorkflowUtil.getWorkflowContext(action_meta.getScriptId());
                action_meta.setScript(scriptWorkflow != null ? scriptWorkflow.getWorkflowV2String() : null);
            }
            dashboard_rule_action.setAction_meta(action_meta);
        }
    }

    public static void getDashboardRuleTargetWidgets(DashboardRuleActionContext dashboard_rule_action)throws Exception
    {
        GenericSelectRecordBuilder select_builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getDashboardRuleTargetWidgetModule().getTableName())
                .select(FieldFactory.getDashboardRuleTargetWidgetFields())
                .andCondition(CriteriaAPI.getCondition("ACTION_ID", "actionId", dashboard_rule_action.getId().toString() , NumberOperators.EQUALS));

        List<Map<String, Object>> props = select_builder.get();
        if(props != null && !props.isEmpty())
        {
            int target_widget_len = props.size();
            List<DashboardTriggerAndTargetWidgetContext> target_widget_list = new ArrayList<>();
            for(int i=0 ; i < target_widget_len ; i++)
            {
                DashboardTriggerAndTargetWidgetContext target_widget = FieldUtil.getAsBeanFromMap(props.get(i), DashboardTriggerAndTargetWidgetContext.class);
                if(target_widget.getCriteriaId() != null && target_widget.getCriteriaId() > 0) {
                    target_widget.setCriteria(CriteriaAPI.getCriteria(AccountUtil.getCurrentOrg().getOrgId(), target_widget.getCriteriaId()));
                }
                target_widget_list.add(target_widget);
            }
            dashboard_rule_action.setTarget_widgets(target_widget_list);
        }
    }
    public static void deleteDashboardRule(Long dashboard_rule_id)throws Exception
    {
        if(dashboard_rule_id != null && dashboard_rule_id > 0) {
            GenericDeleteRecordBuilder delete_builder = new GenericDeleteRecordBuilder()
                    .table(ModuleFactory.getDashboardRuleModule().getTableName())
                    .andCustomWhere("ID = ?", dashboard_rule_id);
            delete_builder.delete();
        }
    }

    public static void updateDashboardRule(DashboardRuleContext dashboard_rule)throws Exception
    {
        GenericUpdateRecordBuilder update_builder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getDashboardRuleModule().getTableName())
                .fields(FieldFactory.getDashboardRuleFields())
                .andCustomWhere("ID = ?", dashboard_rule.getId());

        Map<String, Object> props = FieldUtil.getAsProperties(dashboard_rule);
        update_builder.update(props);
    }

    public static void deleteOldDashboardRuleData(DashboardRuleContext dashboard_rule)throws Exception{
        V3DashboardAPIHandler.deleteTriggerWidgetMapping(dashboard_rule.getTrigger_widgets());
        V3DashboardAPIHandler.deleteDashboardRuleAction(dashboard_rule.getActions());
    }
    public static void deleteTriggerWidgetMapping(List<DashboardTriggerWidgetContext>  old_trigger_widgets)throws Exception
    {
        if(old_trigger_widgets != null && old_trigger_widgets.size() > 0)
        {
            for(DashboardTriggerWidgetContext trigger_widget : old_trigger_widgets)
            {
                if(trigger_widget.getCriteriaId() != null && trigger_widget.getCriteriaId() > 0)
                {
                    CriteriaAPI.deleteCriteria(trigger_widget.getCriteriaId());
                }
                GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder();
                deleteRecordBuilder.table(ModuleFactory.getDashboardTriggerWidgetsModule().getTableName())
                        .andCondition(CriteriaAPI.getIdCondition(trigger_widget.getId(), ModuleFactory.getDashboardTriggerWidgetsModule()));
                deleteRecordBuilder.delete();
            }
        }
    }
    public static void deleteDashboardRuleAction(List<DashboardRuleActionContext>  dashboard_actions)throws Exception
    {
        if(dashboard_actions != null && dashboard_actions.size() > 0)
        {
            for(DashboardRuleActionContext action : dashboard_actions)
            {
                List<DashboardTriggerAndTargetWidgetContext> target_widgets = action.getTarget_widgets();
                if(target_widgets != null && target_widgets.size() > 0)
                {
                    for(DashboardTriggerAndTargetWidgetContext target_widget : target_widgets)
                    {
                        if(target_widget.getCriteriaId() != null && target_widget.getCriteriaId() > 0)
                        {
                            CriteriaAPI.deleteCriteria(target_widget.getCriteriaId());
                        }
                    }
                }
                DashboardRuleActionMetaContext  action_meta = action.getAction_meta();
                if(action_meta != null && action_meta.getCriteriaId() != null && action_meta.getCriteriaId() > 0 )
                {
                    CriteriaAPI.deleteCriteria(action_meta.getCriteriaId());
                }
                if(action_meta != null && action_meta.getScriptId() != null && action_meta.getScriptId() > 0 )
                {
                    WorkflowUtil.deleteWorkflow(action_meta.getScriptId());
                }

                GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder();
                deleteRecordBuilder.table(ModuleFactory.getDashboardRuleActionModule().getTableName())
                        .andCondition(CriteriaAPI.getIdCondition(action.getId(), ModuleFactory.getDashboardRuleActionModule()));
                deleteRecordBuilder.delete();
            }
        }
    }

    public static JSONArray executeDashboardRules(Long trigger_widget_Id, DashboardExecuteMetaContext dashboard_execute_meta)throws Exception
    {
        if(dashboard_execute_meta.getDashboardId() != null)
        {
            JSONArray result_json_arr = dashboard_execute_meta.getMain_result_array();
            /**code to construct dashboard data*/
            List<DashboardWidgetContext> widgets_list = new ArrayList<>();
            DashboardFilterContext dashboard_filter = null;
            if(dashboard_execute_meta.getDashboardTabId() == null) {
                DashboardContext dashboard = V3DashboardAPIHandler.constructDashboardData(dashboard_execute_meta.getDashboardId());
                if(dashboard != null){
                    widgets_list = dashboard.getDashboardWidgets();
                    dashboard_filter = dashboard.getDashboardFilter();
                }
            }
            else if(dashboard_execute_meta.getDashboardTabId() != null && dashboard_execute_meta.getDashboardTabId() > 0) {
                DashboardTabContext dashboard_tab = V3DashboardAPIHandler.constructDashboardTabData(dashboard_execute_meta.getDashboardTabId());
                if(dashboard_tab != null){
                    widgets_list = dashboard_tab.getDashboardWidgets();
                    dashboard_filter = dashboard_tab.getDashboardFilter();
                }
            }

            /**code to set default and live datetime and user filter details in these objects*/
            V3DashboardAPIHandler.constructTimelineAndUserFilter(dashboard_filter, dashboard_execute_meta);
            if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.DASHBOARD_V2)){
                V3DashboardAPIHandler.constructReadingFilter(dashboard_filter, dashboard_execute_meta);
            }
            if (trigger_widget_Id != null && trigger_widget_Id > 0)
            {
                /** code to construct dashboard rules data for trigger widget id*/
                Long dashboard_rule_id = V3DashboardAPIHandler.checkIsDashboardRuleApplied(trigger_widget_Id, dashboard_execute_meta.getDashboardId(), dashboard_execute_meta.getDashboardTabId());
                if(dashboard_rule_id != null && dashboard_rule_id > 0) {
                    result_json_arr = V3DashboardAPIHandler.executeDashboardActions(dashboard_rule_id, trigger_widget_Id, dashboard_execute_meta, 2 );
                    dashboard_execute_meta.setMain_result_array(result_json_arr);
                }
            }
            if (dashboard_filter != null)
            {
                dashboard_execute_meta.setTimeline_widget_field_map(dashboard_filter.getWidgetTimelineFilterMap());
                List<Long> rule_target_widget_ids = dashboard_execute_meta.getRule_applied_widget_ids();
                if(result_json_arr != null && result_json_arr.size() > 0)
                {
                    int result_json_len = result_json_arr.size();
                    for(int i =0 ; i< result_json_len ;i++){
                        JSONObject result_widget1 = (JSONObject) result_json_arr.get(i);
                        if(result_widget1.containsKey("widget_id")){
                            rule_target_widget_ids.add((Long)result_widget1.get("widget_id"));
                        }
                    }
                }
                /** code to set user/timelinefilter data for response widgets*/
//                List<DashboardWidgetContext> widgets_list = dashboard.getDashboardWidgets();
                if (widgets_list != null && widgets_list.size() > 0)
                {
                    V3DashboardAPIHandler.constructWidgetsCriteriaResp(widgets_list, dashboard_execute_meta);
                }
                return dashboard_execute_meta.getMain_result_array();
            }
        }
        return null;
    }
    public static JSONArray executeNewDashboardRules(Long trigger_widget_Id, DashboardExecuteMetaContext dashboard_execute_meta)throws Exception
    {
        if(dashboard_execute_meta.getDashboardId() != null)
        {
            JSONArray result_json_arr = dashboard_execute_meta.getMain_result_array();
            /**code to construct dashboard data*/
            List<DashboardWidgetContext> widgets_list = new ArrayList<>();
            DashboardFilterContext dashboard_filter = null;
            if(dashboard_execute_meta.getDashboardTabId() == null) {
                DashboardContext dashboard = V3DashboardAPIHandler.constructDashboardData(dashboard_execute_meta.getDashboardId());
                if(dashboard != null){
                    widgets_list = dashboard.getDashboardWidgets();
                    dashboard_filter = dashboard.getDashboardFilter();
                }
            }
            else if(dashboard_execute_meta.getDashboardTabId() != null && dashboard_execute_meta.getDashboardTabId() > 0) {
                DashboardTabContext dashboard_tab = V3DashboardAPIHandler.constructDashboardTabData(dashboard_execute_meta.getDashboardTabId());
                if(dashboard_tab != null){
                    widgets_list = dashboard_tab.getDashboardWidgets();
                    dashboard_filter = dashboard_tab.getDashboardFilter();
                }
            }

            /**code to set default and live datetime and user filter details in these objects*/
            V3DashboardAPIHandler.constructNewTimelineAndUserFilter(dashboard_filter, dashboard_execute_meta);

            if (trigger_widget_Id != null && trigger_widget_Id > 0)
            {
                /** code to construct dashboard rules data for trigger widget id*/
                Long dashboard_rule_id = V3DashboardAPIHandler.checkIsDashboardRuleApplied(trigger_widget_Id, dashboard_execute_meta.getDashboardId(), dashboard_execute_meta.getDashboardTabId());
                if(dashboard_rule_id != null && dashboard_rule_id > 0) {
                    result_json_arr = V3DashboardAPIHandler.executeDashboardActions(dashboard_rule_id, trigger_widget_Id, dashboard_execute_meta, 2 );
                    dashboard_execute_meta.setMain_result_array(result_json_arr);
                }
            }
            if (dashboard_filter != null)
            {
                dashboard_execute_meta.setTimeline_widget_field_map(dashboard_filter.getWidgetTimelineFilterMap());
                List<Long> rule_target_widget_ids = dashboard_execute_meta.getRule_applied_widget_ids();
                if(result_json_arr != null && result_json_arr.size() > 0)
                {
                    int result_json_len = result_json_arr.size();
                    for(int i =0 ; i< result_json_len ;i++){
                        JSONObject result_widget1 = (JSONObject) result_json_arr.get(i);
                        if(result_widget1.containsKey("widget_id")){
                            rule_target_widget_ids.add((Long)result_widget1.get("widget_id"));
                        }
                    }
                }
                /** code to set user/timelinefilter data for response widgets*/
//                List<DashboardWidgetContext> widgets_list = dashboard.getDashboardWidgets();
                if (widgets_list != null && widgets_list.size() > 0)
                {
                    V3DashboardAPIHandler.constructWidgetsCriteriaResp(widgets_list, dashboard_execute_meta);
                }
                return dashboard_execute_meta.getMain_result_array();
            }
        }
        return null;
    }
    public static void constructWidgetsCriteriaResp(List<DashboardWidgetContext> widgets, DashboardExecuteMetaContext dashboard_execute_data)throws Exception
    {
        Boolean is_trigger_widget_type = false;
        DashboardWidgetContext trigger_widget  = DashboardUtil.getWidget(dashboard_execute_data.getTrigger_widget_id());
        if(trigger_widget != null && trigger_widget.getType() != Integer.valueOf(DashboardWidgetContext.WidgetType.FILTER.getValue())) {
            is_trigger_widget_type = true;
        }

        for (DashboardWidgetContext dashboardWidgetContext : widgets)
        {
            if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.DASHBOARD_V2) && trigger_widget != null && trigger_widget.getId() == dashboardWidgetContext.getId()){
                continue;
            }
            if(dashboardWidgetContext.getWidgetType() != null && dashboardWidgetContext.getWidgetType().equals(DashboardWidgetContext.WidgetType.SECTION))
            {
                continue;
            }
            Long widget_id = dashboardWidgetContext.getId();
            if(dashboard_execute_data.getRule_applied_widget_ids().contains(widget_id) || (is_trigger_widget_type && !dashboard_execute_data.getRule_applied_widget_ids().contains(widget_id))){
                continue;
            }
            if(dashboardWidgetContext.getWidgetType().equals(DashboardWidgetContext.WidgetType.CARD)){
                WidgetCardContext cardContext = (WidgetCardContext) dashboardWidgetContext;
                if(cardContext.getCardLayout().equals(CardLayout.COMBO_CARD.getName())){
                    if(cardContext.getChildCards() != null && cardContext.getChildCards().size() > 0){
                        for(WidgetCardContext child: cardContext.getChildCards()){
                            constructFilterResp(dashboard_execute_data, child, true,cardContext.getId());
                        }
                    }
                }
                else{
                    constructFilterResp(dashboard_execute_data, dashboardWidgetContext, false, null);
                }
            }
            else{
                constructFilterResp(dashboard_execute_data, dashboardWidgetContext, false, null);
            }
        }
    }
    public static void constructFilterResp(DashboardExecuteMetaContext dashboard_execute_data, DashboardWidgetContext dashboardWidgetContext, Boolean isCombo, Long parentId)throws Exception

    {
        Long widget_id = dashboardWidgetContext.getId();
        JSONObject widget_json = new JSONObject();
        widget_json.put("widget_id", widget_id);
        widget_json.put("parent_id", isCombo ?  parentId : null);
        widget_json.put("widgetType", isCombo ? "card" : dashboardWidgetContext.getWidgetType().getName());
        widget_json.put("actionMeta", new JSONObject());
        JSONObject actionMeta = new JSONObject();
        V3DashboardAPIHandler.setTimeLineFilterResp(actionMeta, widget_id, dashboard_execute_data.getTimeline_widget_field_map(), dashboard_execute_data.getGlobal_timeline_filter_widget_map());
        V3DashboardAPIHandler.setUserFilterResp(actionMeta, widget_id, dashboard_execute_data.getGlobal_filter_widget_map(), dashboard_execute_data.getPlaceHolders());
        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.DASHBOARD_V2)) {
            V3DashboardAPIHandler.setFilterForReadingResp(actionMeta, widget_id, dashboard_execute_data.getReading_filter_widget_map(), dashboard_execute_data.getPlaceHolders());
        }
        if (actionMeta != null && !actionMeta.isEmpty()) {
            widget_json.put("actionMeta", actionMeta);
            dashboard_execute_data.getMain_result_array().add(widget_json);
        } else {
            dashboard_execute_data.getMain_result_array().add(widget_json);
        }
    }
    public static DashboardTabContext constructDashboardTabData(Long dashboardTabId)throws Exception
    {
        DashboardTabContext dashboard_tab = DashboardUtil.getDashboardTabWithWidgets(dashboardTabId);
        FacilioChain getDashboardFilterChain = ReadOnlyChainFactory.getFetchDashboardFilterAndWidgetFilterMappingChain();
        FacilioContext getDashboardFilterContext = getDashboardFilterChain.getContext();
        getDashboardFilterContext.put(FacilioConstants.ContextNames.DASHBOARD_TAB, dashboard_tab);
        getDashboardFilterChain.execute();
        return dashboard_tab;
    }
    public static DashboardContext constructDashboardData(Long dashboardId)throws Exception
    {
        DashboardContext dashboard = DashboardUtil.getDashboardWithWidgets(dashboardId);
        FacilioChain getDashboardFilterChain = ReadOnlyChainFactory.getFetchDashboardFilterAndWidgetFilterMappingChain();
        FacilioContext getDashboardFilterContext = getDashboardFilterChain.getContext();
        getDashboardFilterContext.put(FacilioConstants.ContextNames.DASHBOARD, dashboard);
        getDashboardFilterChain.execute();
        return dashboard;
    }
    public static void constructTimelineAndUserFilter(DashboardFilterContext dashboard_filter, DashboardExecuteMetaContext dashboard_execute_data)throws Exception
    {
        if (dashboard_filter != null)
        {
            JSONObject placeHolders = dashboard_execute_data.getPlaceHolders();
            dashboard_execute_data.setTimeline_widget_field_map(dashboard_filter.getWidgetTimelineFilterMap());
            if (dashboard_filter.getDashboardUserFilters() != null) {
                for (DashboardUserFilterContext user_filter : dashboard_filter.getDashboardUserFilters()) {
                    V3DashboardAPIHandler.constructDefaultUserFilterValues(user_filter, dashboard_execute_data);
                }
            }
            if (dashboard_filter.getIsTimelineFilterEnabled())
            {
                long operatorId = dashboard_filter.getDateOperator();
                String rangeValue = null;
                if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.DASHBOARD_V2) && addToOperators().contains((int) operatorId)){
                    rangeValue = dashboard_filter.getDateLabel().split(" ")[1];
                }
                DateOperators date_operator = (DateOperators) Operator.getOperator((int) operatorId);
                DateRange dateRange = new DateRange();
                if(operatorId != 20) {
                    dateRange = date_operator.getRange(rangeValue);
                }else{
                    String[] range = dashboard_filter.getDateValue().split(",");
                    dateRange = new DateRange(Long.parseLong(range[0]),Long.parseLong(range[1]));
                }
                JSONObject timeline_filter = new JSONObject();
                timeline_filter.put("startTime", placeHolders.containsKey("startTime") ? placeHolders.get("startTime") : dateRange.getStartTime());
                timeline_filter.put("endTime", placeHolders.containsKey("endTime") ? placeHolders.get("endTime") : dateRange.getEndTime());
                timeline_filter.put("dateOperator", placeHolders.containsKey("dateOperator") ? placeHolders.get("dateOperator") : operatorId);
                if(placeHolders.containsKey("dateValueString")){
                    timeline_filter.put("dateValueString",placeHolders.get("dateValueString"));
                }
                if(placeHolders.containsKey("dateLabel")){
                    timeline_filter.put("dateLabel",placeHolders.get("dateLabel"));
                }
                dashboard_execute_data.getGlobal_timeline_filter_widget_map().put("TIMELINE_FILTER", timeline_filter);
            }
        }
    }
    public static void constructReadingFilter(DashboardFilterContext dashboard_filter, DashboardExecuteMetaContext dashboard_execute_data) throws Exception {
        if (dashboard_filter != null) {
            JSONObject placeHolders = dashboard_execute_data.getPlaceHolders();
            if (dashboard_filter.getDashboardUserFilters() != null) {
                for (DashboardUserFilterContext user_filter : dashboard_filter.getDashboardUserFilters()) {
                    V3DashboardAPIHandler.constructReadingFilterValues(user_filter, dashboard_execute_data);
                }
            }
        }
    }
    public static void constructReadingFilterValues(DashboardUserFilterContext user_filter, DashboardExecuteMetaContext dashboard_executed_data )throws Exception {
//        String []default_values = user_filter.getDefaultValues();
        FacilioField field = user_filter.getField();
        JSONObject placeHolders = dashboard_executed_data.getPlaceHolders();
        if(field == null || (field != null && field.getDataTypeEnum() != FieldType.DATE_TIME))
        {
            if (placeHolders.containsKey(String.valueOf(user_filter.getLink_name())))
            {
                HashMap selected_filter_map = (HashMap) placeHolders.get(String.valueOf(user_filter.getLink_name()));
                List<String> selected_filter_values = (ArrayList<String>) selected_filter_map.get("value");
                V3DashboardAPIHandler.setReadingUserFilterValues(user_filter, selected_filter_values, dashboard_executed_data, null);
            }
        }
    }

    public static void constructNewTimelineAndUserFilter(DashboardFilterContext dashboard_filter, DashboardExecuteMetaContext dashboard_execute_data)throws Exception
    {
        if (dashboard_filter != null)
        {
            JSONObject placeHolders = dashboard_execute_data.getPlaceHolders();
            dashboard_execute_data.setTimeline_widget_field_map(dashboard_filter.getWidgetTimelineFilterMap());
            if (dashboard_filter.getDashboardUserFilters() != null) {
                for (DashboardUserFilterContext user_filter : dashboard_filter.getDashboardUserFilters()) {
                    V3DashboardAPIHandler.constructNewDefaultUserFilterValues(user_filter, dashboard_execute_data);
                }
            }
            if (dashboard_filter.getIsTimelineFilterEnabled())
            {
                long operatorId = dashboard_filter.getDateOperator();
                DateOperators date_operator = (DateOperators) Operator.getOperator((int) operatorId);
                DateRange dateRange = date_operator.getRange(null);
                JSONObject timeline_filter = new JSONObject();
                timeline_filter.put("startTime", placeHolders.containsKey("startTime") ? placeHolders.get("startTime") : dateRange.getStartTime());
                timeline_filter.put("endTime", placeHolders.containsKey("endTime") ? placeHolders.get("endTime") : dateRange.getEndTime());
                timeline_filter.put("dateOperator", placeHolders.containsKey("dateOperator") ? placeHolders.get("dateOperator") : operatorId);
                if(placeHolders.containsKey("dateValueString")){
                    timeline_filter.put("dateValueString",placeHolders.get("dateValueString"));
                }
                if(placeHolders.containsKey("dateLabel")){
                    timeline_filter.put("dateLabel",placeHolders.get("dateLabel"));
                }
                dashboard_execute_data.getGlobal_timeline_filter_widget_map().put("TIMELINE_FILTER", timeline_filter);
            }
        }
    }
    public static void setTimeLineFilterResp(JSONObject actionMeta , Long widget_id, Map<Long, Map<String, String>> timeline_widget_field_map, JSONObject global_timeline_filter_widget_map)throws Exception
    {
        if (global_timeline_filter_widget_map != null && timeline_widget_field_map != null && timeline_widget_field_map.containsKey(widget_id)) {
            JSONObject timeline_filter_original = (JSONObject) global_timeline_filter_widget_map.get("TIMELINE_FILTER");
            if(timeline_filter_original != null && !timeline_filter_original.isEmpty())
            {
                JSONObject timeline_filter = (JSONObject) timeline_filter_original.clone();
                HashMap dateField = (HashMap) timeline_widget_field_map.get(widget_id);
                if (dateField != null && timeline_filter != null && dateField.containsKey("dateField")) {
                    timeline_filter.put("dateField", dateField.get("dateField"));
                }
                actionMeta.put("TIMELINE_FILTER", timeline_filter);
            }
        }
    }
    public static void setFilterForReadingResp(JSONObject actionMeta, Long widget_id , JSONObject reading_filter_widget_map, JSONObject placeHolders)throws Exception
    {
        if (reading_filter_widget_map != null && reading_filter_widget_map.containsKey(widget_id))
        {
            JSONObject reading_filter_json = (JSONObject) reading_filter_widget_map.get(widget_id);
            if(reading_filter_json != null && reading_filter_json.isEmpty()){
                actionMeta.put("READING_FILTER", new JSONObject());
            }
            else {
                Set<String> user_filter_set = reading_filter_json.keySet();
                JSONObject result = new JSONObject();
                for(String user_filter_id : user_filter_set) {
                    result.put(user_filter_id, V3DashboardAPIHandler.constructReadingFilterRepsonse((JSONObject) reading_filter_json.get(user_filter_id)));
                }
                actionMeta.put("READING_FILTER", result);
            }
        }
    }

    public static void setUserFilterResp(JSONObject actionMeta, Long widget_id , JSONObject global_filter_widget_map, JSONObject placeHolders)throws Exception
    {
        if (global_filter_widget_map != null && global_filter_widget_map.containsKey(widget_id))
        {
            JSONObject user_filter_json = (JSONObject) global_filter_widget_map.get(widget_id);
            if(user_filter_json != null && user_filter_json.isEmpty()){
                actionMeta.put("USER_FILTER", new JSONObject());
            }
            else {
                actionMeta.put("USER_FILTER", V3DashboardAPIHandler.constructUserFilterRepsonse(user_filter_json, placeHolders));
            }
        }
    }
    public static JSONArray executeDashboardActions(Long dashboard_rule_id, Long trigger_widget_id , DashboardExecuteMetaContext dashboard_execute_meta, Integer triggerType)throws Exception
    {
        if(dashboard_rule_id != null && dashboard_rule_id > 0)
        {
            DashboardRuleContext rule_to_execute = V3DashboardAPIHandler.getDashboardRule(dashboard_rule_id, triggerType);
            if (rule_to_execute.getTrigger_type() == 1) {

            }
            else if (rule_to_execute.getTrigger_type() == 2 && rule_to_execute.getActions() != null)
            {
                for (DashboardRuleActionContext dashboard_rule_action : rule_to_execute.getActions()) {
                    DashboardRuleActionType.getActionType(dashboard_rule_action.getType()).performAction(rule_to_execute, dashboard_rule_action, dashboard_execute_meta, trigger_widget_id);//, dashboard_execute_meta.getGlobal_timeline_filter_widget_map(), dashboard_execute_meta.getGlobal_filter_widget_map(), dashboard_execute_meta.getTimeline_widget_field_map());
                }
            }
            return rule_to_execute.getResult_json();
        }
        return null;

    }

    public static void constructDefaultUserFilterValues(DashboardUserFilterContext user_filter, DashboardExecuteMetaContext dashboard_executed_data )throws Exception
    {
        String []default_values = user_filter.getDefaultValues();
        FacilioField field = user_filter.getField();
        JSONObject placeHolders = dashboard_executed_data.getPlaceHolders();
        if(field == null || (field != null && field.getDataTypeEnum() != FieldType.DATE_TIME))
        {
            if (placeHolders.containsKey(String.valueOf(user_filter.getLink_name())))
            {
                HashMap selected_filter_map = (HashMap) placeHolders.get(String.valueOf(user_filter.getLink_name()));
                List<String> selected_filter_values = (ArrayList<String>) selected_filter_map.get("value");
                V3DashboardAPIHandler.setUserFilterValues(user_filter, selected_filter_values, dashboard_executed_data, null);
            }
            else if (default_values != null && default_values.length > 0 && !( default_values[0].equals("all") || default_values[0].equals("ALL")|| default_values[0].equals("All")))
            {
                V3DashboardAPIHandler.setUserFilterValues(user_filter, Arrays.asList(default_values), dashboard_executed_data, null);
            }
        }
        else if((field != null && field.getDataTypeEnum() == FieldType.DATE_TIME))
        {
            if (placeHolders.containsKey(String.valueOf(user_filter.getLink_name())))
            {
                HashMap selected_filter_map = (HashMap) placeHolders.get(String.valueOf(user_filter.getLink_name()));
                List<String> selected_filter_values = (ArrayList<String>) selected_filter_map.get("value");
                if(selected_filter_values != null && selected_filter_values.size() > 0 && !"".equals(selected_filter_values.get(0)))
                {
                    ArrayList<String> operatorId_list = (ArrayList) selected_filter_map.get("operatorId");
                    if(operatorId_list != null && operatorId_list.size()>0)
                    {
                        Integer operatorId = Integer.parseInt(operatorId_list.get(0));
                        V3DashboardAPIHandler.setUserFilterValues(user_filter, selected_filter_values, dashboard_executed_data, operatorId);
                    }
                }
            }
        }
    }
    public static void setReadingUserFilterValues(DashboardUserFilterContext user_filter, List<String> selected_values, DashboardExecuteMetaContext dashboard_executed_data, Integer operatorId)throws Exception
    {
        Map<Object, Map<String, FacilioField>> widget_field_map = user_filter.getReadingWidgetFieldMap();
        for (Map.Entry<Object,  Map<String, FacilioField>> widget_and_field : widget_field_map.entrySet())
        {
            Long widget_id = (Long) widget_and_field.getKey();
            for(Map.Entry<String, FacilioField> moduleField : widget_and_field.getValue().entrySet()) {
                if(selected_values != null && selected_values.size() > 0 && (!"".equals(selected_values.get(0)) && !"all".equals(selected_values.get(0))))
                {
                    String moduleName = moduleField.getKey();
                    JSONObject check = new JSONObject();
                    FacilioField applied_widget_field = moduleField.getValue();
                    operatorId = PickListOperators.IS.getOperatorId();
                    if((applied_widget_field !=null && applied_widget_field.getName() != null &&
                            (applied_widget_field.getName().equals(FacilioConstants.ContextNames.RESOURCE)
                                    || applied_widget_field.getName().equals(FacilioConstants.ContextNames.SPACE)))){
                        operatorId = BuildingOperator.BUILDING_IS.getOperatorId();
                    }
                    Operator operator = Operator.getOperator(operatorId != null && operatorId > 0 ? operatorId : 36);
                    Condition condition = new Condition();
                    condition.setField(applied_widget_field);
                    condition.setOperatorId(operator.getOperatorId());

                    StringBuilder values = new StringBuilder();
                    for (String value : selected_values) {
                        values.append(value);
                        values.append(",");
                    }
                    String filter_value = values.toString();
                    if (filter_value != null && !"".equals(filter_value)) {
                        filter_value = filter_value.substring(0, filter_value.length() - 1);
                        condition.setValue(filter_value);
                    }
                    JSONObject criteria_obj = new JSONObject();
                    if (dashboard_executed_data.getReading_filter_widget_map().containsKey(widget_id)) {
                        JSONObject presentMap = new JSONObject();
                        presentMap = (JSONObject) dashboard_executed_data.getReading_filter_widget_map().get(widget_id);
                        if(presentMap.containsKey(moduleName)){
                            criteria_obj = (JSONObject) presentMap.get(moduleName);
                        }else {
                            criteria_obj = new JSONObject();
                            check = presentMap;
                        }
                    } else {
                        criteria_obj = new JSONObject();
                    }
                    criteria_obj.put(user_filter.getId(), condition);
                    check.put(moduleName,criteria_obj);
                    dashboard_executed_data.getReading_filter_widget_map().put(widget_id,check);
                }
            }
        }
    }
    public static void constructNewDefaultUserFilterValues(DashboardUserFilterContext user_filter, DashboardExecuteMetaContext dashboard_executed_data )throws Exception
    {
        String []default_values = user_filter.getDefaultValues();
        FacilioField field = user_filter.getField();
        JSONObject placeHolders = dashboard_executed_data.getPlaceHolders();
        if(field == null || (field != null && field.getDataTypeEnum() != FieldType.DATE_TIME))
        {
            if (placeHolders.containsKey(String.valueOf(user_filter.getLink_name())))
            {
                HashMap selected_filter_map = (HashMap) placeHolders.get(String.valueOf(user_filter.getLink_name()));
                List<String> selected_filter_values = (ArrayList<String>) selected_filter_map.get("value");
                V3DashboardAPIHandler.setNewUserFilterValues(user_filter, selected_filter_values, dashboard_executed_data, null);
            }
            else if (default_values != null && default_values.length > 0 && !( default_values[0].equals("all") || default_values[0].equals("ALL")|| default_values[0].equals("All")))
            {
                V3DashboardAPIHandler.setUserFilterValues(user_filter, Arrays.asList(default_values), dashboard_executed_data, null);
            }
        }
        else if((field != null && field.getDataTypeEnum() == FieldType.DATE_TIME))
        {
            if (placeHolders.containsKey(String.valueOf(user_filter.getLink_name())))
            {
                HashMap selected_filter_map = (HashMap) placeHolders.get(String.valueOf(user_filter.getLink_name()));
                List<String> selected_filter_values = (ArrayList<String>) selected_filter_map.get("value");
                if(selected_filter_values != null && selected_filter_values.size() > 0 && !"".equals(selected_filter_values.get(0)))
                {
                    ArrayList<String> operatorId_list = (ArrayList) selected_filter_map.get("operatorId");
                    if(operatorId_list != null && operatorId_list.size()>0)
                    {
                        Integer operatorId = Integer.parseInt(operatorId_list.get(0));
                        V3DashboardAPIHandler.setUserFilterValues(user_filter, selected_filter_values, dashboard_executed_data, operatorId);
                    }
                }
            }
        }
    }
    public static void setUserFilterValues(DashboardUserFilterContext user_filter, List<String> selected_values, DashboardExecuteMetaContext dashboard_executed_data, Integer operatorId)throws Exception
    {
        Map<Object, FacilioField> widget_field_map = user_filter.getWidgetFieldMap();
        for (Map.Entry<Object, FacilioField> widget_and_field : widget_field_map.entrySet())
        {
            Long widget_id = (Long) widget_and_field.getKey();
            if(selected_values != null && selected_values.size() > 0 && (!"".equals(selected_values.get(0)) && !"all".equals(selected_values.get(0))))
            {
                FacilioField applied_widget_field = widget_and_field.getValue();
                operatorId = PickListOperators.IS.getOperatorId();
                if((applied_widget_field !=null && applied_widget_field.getName() != null &&
                        (applied_widget_field.getName().equals(FacilioConstants.ContextNames.RESOURCE)
                                || applied_widget_field.getName().equals(FacilioConstants.ContextNames.SPACE)))){
                    operatorId = BuildingOperator.BUILDING_IS.getOperatorId();
                }
                Operator operator = Operator.getOperator(operatorId != null && operatorId > 0 ? operatorId : 36);
                Condition condition = new Condition();
                condition.setField(applied_widget_field);
                condition.setOperatorId(operator.getOperatorId());

                StringBuilder values = new StringBuilder();
                for (String value : selected_values) {
                    values.append(value);
                    values.append(",");
                }
                String filter_value = values.toString();
                if (filter_value != null && !"".equals(filter_value)) {
                    filter_value = filter_value.substring(0, filter_value.length() - 1);
                    condition.setValue(filter_value);
                }

                if (dashboard_executed_data.getGlobal_filter_widget_map().containsKey(widget_id)) {
                    JSONObject criteria_obj = (JSONObject) dashboard_executed_data.getGlobal_filter_widget_map().get(widget_id);
                    criteria_obj.put(user_filter.getId(), condition);
                } else {
                    JSONObject criteria_obj = new JSONObject();
                    criteria_obj.put(user_filter.getId(), condition);
                    dashboard_executed_data.getGlobal_filter_widget_map().put(widget_id, criteria_obj);
                }
            }
            else if((selected_values != null && selected_values.isEmpty() ) || (selected_values != null && selected_values.size() > 0 && ("".equals(selected_values.get(0))|| "all".equals(selected_values.get(0)))))
            {
                if(dashboard_executed_data.getGlobal_filter_widget_map() != null && !dashboard_executed_data.getGlobal_filter_widget_map().containsKey(widget_id)) {
                    dashboard_executed_data.getGlobal_filter_widget_map().put(widget_id, new JSONObject());
                }
            }
        }
    }
    public static void setNewUserFilterValues(DashboardUserFilterContext user_filter, List<String> selected_values, DashboardExecuteMetaContext dashboard_executed_data, Integer operatorId)throws Exception
    {
        Map<Object, FacilioField> widget_field_map = user_filter.getWidgetFieldMap();
        List<DashboardFieldMappingContext> fieldMappingContexts = DashboardFilterUtil.getFilterMappingsForFilterId(user_filter.getId());
        for (Map.Entry<Object, FacilioField> widget_and_field : widget_field_map.entrySet())
        {
            Long widget_id = (Long) widget_and_field.getKey();
            if(selected_values != null && selected_values.size() > 0 && (!"".equals(selected_values.get(0)) && !"all".equals(selected_values.get(0))))
            {
                List<Long> filteredList = new ArrayList<>();
                FacilioField applied_widget_field = widget_and_field.getValue();
                filteredList = fieldMappingContexts.stream()
                        .filter(mapping -> mapping.getModuleId() == (applied_widget_field.getModuleId()))
                        .map(mapping -> mapping.getFieldId())
                        .collect(Collectors.toList());
                if(!filteredList.isEmpty() && filteredList.contains(applied_widget_field.getFieldId())){
                    if(operatorId == null && applied_widget_field !=null && applied_widget_field.getName() != null &&
                            applied_widget_field.getName().equals(FacilioConstants.ContextNames.RESOURCE)){
                        operatorId = BuildingOperator.BUILDING_IS.getOperatorId();
                    }
                    Operator operator = Operator.getOperator(operatorId != null && operatorId > 0 ? operatorId : 36);
                    Condition condition = new Condition();
                    condition.setField(applied_widget_field);
                    condition.setOperatorId(operator.getOperatorId());

                    StringBuilder values = new StringBuilder();
                    for (String value : selected_values) {
                        values.append(value);
                        values.append(",");
                    }
                    String filter_value = values.toString();
                    if (filter_value != null && !"".equals(filter_value)) {
                        filter_value = filter_value.substring(0, filter_value.length() - 1);
                        condition.setValue(filter_value);
                    }

                    if (dashboard_executed_data.getGlobal_filter_widget_map().containsKey(widget_id)) {
                        JSONObject criteria_obj = (JSONObject) dashboard_executed_data.getGlobal_filter_widget_map().get(widget_id);
                        criteria_obj.put(user_filter.getId(), condition);
                    } else {
                        JSONObject criteria_obj = new JSONObject();
                        criteria_obj.put(user_filter.getId(), condition);
                        dashboard_executed_data.getGlobal_filter_widget_map().put(widget_id, criteria_obj);
                    }
                }
            }
            else if((selected_values != null && selected_values.isEmpty() ) || (selected_values != null && selected_values.size() > 0 && ("".equals(selected_values.get(0))|| "all".equals(selected_values.get(0)))))
            {
                if(dashboard_executed_data.getGlobal_filter_widget_map() != null && !dashboard_executed_data.getGlobal_filter_widget_map().containsKey(widget_id)) {
                    dashboard_executed_data.getGlobal_filter_widget_map().put(widget_id, new JSONObject());
                }
            }
        }
    }
    public static JSONObject constructUserFilterRepsonse(JSONObject user_filters_obj , JSONObject placeHolders)throws Exception
    {
        List<Integer> tTimeOperators = Arrays.asList(103, 85,101);
        if(user_filters_obj != null && !user_filters_obj.isEmpty())
        {
            JSONObject result_json = new JSONObject();
            Set<Long> user_filter_set = user_filters_obj.keySet();
            for(Long user_filter_id : user_filter_set)
            {
                if(user_filters_obj.containsKey(user_filter_id))
                {
                    Condition criteria_obj = (Condition) user_filters_obj.get(user_filter_id);
                    JSONObject temp = new JSONObject();
                    temp.put("operatorId", criteria_obj.getOperatorId());
                    String value = criteria_obj.getValue();
                    if(value != null && !"".equals(value))
                    {
                        String []val_arr =value.split(",");
                        temp.put("value", val_arr);
                    }else {
                        temp.put("value", value);
                    }
                    if(criteria_obj.getFieldName() != null && criteria_obj.getFieldName().equals("ttime") && tTimeOperators.contains(criteria_obj.getOperatorId()))
                    {
                        result_json.put(new StringBuilder(criteria_obj.getFieldName()).append("_").append(criteria_obj.getOperatorId()).toString(), temp);
                    }else {
                        result_json.put(criteria_obj.getFieldName(), temp);
                    }
                }
            }
            return result_json;
        }
        return null;
    }
    public static JSONObject constructReadingFilterRepsonse(JSONObject reading_filters_obj)throws Exception
    {
        List<Integer> tTimeOperators = Arrays.asList(103, 85,101);
        if(reading_filters_obj != null && !reading_filters_obj.isEmpty())
        {
            JSONObject result_json = new JSONObject();
            Set<Long> user_filter_set = reading_filters_obj.keySet();
            for(Long user_filter_id : user_filter_set)
            {
                if(reading_filters_obj.containsKey(user_filter_id))
                {
                    Condition criteria_obj = (Condition) reading_filters_obj.get(user_filter_id);
                    JSONObject temp = new JSONObject();
                    temp.put("operatorId", criteria_obj.getOperatorId());
                    if(criteria_obj.getField().getDataTypeEnum().equals(FieldType.ENUM)) {
                        temp.put("moduleName", criteria_obj.getField().getModule().getName());
                    }
                    String value = criteria_obj.getValue();
                    if(value != null && !"".equals(value))
                    {
                        String []val_arr =value.split(",");
                        temp.put("value", val_arr);
                    }else {
                        temp.put("value", value);
                    }
                    if(criteria_obj.getFieldName() != null && criteria_obj.getFieldName().equals("ttime") && tTimeOperators.contains(criteria_obj.getOperatorId()))
                    {
                        result_json.put(new StringBuilder(criteria_obj.getFieldName()).append("_").append(criteria_obj.getOperatorId()).toString(), temp);
                    }else {
                        result_json.put(criteria_obj.getFieldName(), temp);
                    }
                }
            }
            return result_json;
        }
        return null;
    }

    public static Long checkIsDashboardRuleApplied(Long trigger_widget_id, Long dashboardId, Long dashboardTabId)throws Exception
    {
        GenericSelectRecordBuilder select_builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getDashboardTriggerWidgetModule().getTableName())
                .select(FieldFactory.getDashboardTriggerWidgetFields())
                .andCustomWhere("TRIGGER_WIDGET_ID = ?", trigger_widget_id);

        List<Map<String , Object>> props = select_builder.get();
        List<Long> dashboard_rules_list = new ArrayList<>();
        if(props != null && props.size() > 0)
        {
            int rule_len = props.size();
            for(int i=0;i<rule_len;i++){
                DashboardTriggerWidgetContext trigger_widget = FieldUtil.getAsBeanFromMap(props.get(i), DashboardTriggerWidgetContext.class);
                if(trigger_widget != null) {
                    dashboard_rules_list.add(trigger_widget.getDashboard_rule_id());
                }
            }
        }
        if(dashboard_rules_list.size() > 0)
        {
            for(Long rule_id : dashboard_rules_list)
            {
                Boolean isActiveRule = V3DashboardAPIHandler.isActiveRule(rule_id, dashboardId, dashboardTabId);
                if(isActiveRule){
                    return rule_id;
                }
            }
        }
        return null;
    }

    public static Boolean isActiveRule(Long rule_id, Long dashboardId, Long dashboardTabId)throws Exception
    {
        GenericSelectRecordBuilder select_builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getDashboardRuleModule().getTableName())
                .select(FieldFactory.getDashboardRuleFields())
                .andCustomWhere("ID = ?", rule_id)
                .andCustomWhere("STATUS = ?", true);

        List<Map<String , Object>> props = select_builder.get();
        if(props.size() > 0){
            int len =props.size() ;
            for(int i =0 ;i <len ;i++ )
            {
                DashboardRuleContext dashboardRule = FieldUtil.getAsBeanFromMap(props.get(0), DashboardRuleContext.class);
                if(dashboardTabId != null && dashboardRule != null && dashboardRule.getDashboardTabId() != null && dashboardRule.getDashboardTabId().equals(dashboardTabId)){
                    return true;
                }
                if(dashboardRule != null && dashboardRule.getDashboardId() != null && dashboardRule.getDashboardId().equals(dashboardId)){
                    return true;
                }
            }
        }
        return false;
    }



    public static String checkAndGenerateWidgetLinkName(List<DashboardWidgetContext> widgets, Long dashboardId, Long dashboardTabId)throws Exception
    {
        try
        {
            JSONObject link_name_map = new JSONObject();
            GenericSelectRecordBuilder select_builder = new GenericSelectRecordBuilder()
                    .table(ModuleFactory.getWidgetModule().getTableName())
                    .select(FieldFactory.getWidgetFields());
            if (dashboardTabId != null && dashboardTabId > 0) {
                select_builder.andCondition(CriteriaAPI.getCondition("DASHBOARD_TAB_ID", "dashboardTabId", "" +dashboardTabId,
                        NumberOperators.EQUALS));
            } else if (dashboardId != null && dashboardId > 0) {
                select_builder.andCondition(CriteriaAPI.getCondition("DASHBOARD_ID", "dashboardId", "" + dashboardId,
                        NumberOperators.EQUALS));
            }
            List<Map<String, Object>> prop_res = select_builder.get();
            if(prop_res != null && prop_res.size() > 0)
            {
                int len = prop_res.size();
                for (int i = 0; i < len; i++) {
                    DashboardWidgetContext.WidgetType widgetType = DashboardWidgetContext.WidgetType.getWidgetType((Integer) prop_res.get(i).get("type"));
                    DashboardWidgetContext dashboard_widget = (DashboardWidgetContext) FieldUtil.getAsBeanFromMap(prop_res.get(i), widgetType.getWidgetContextClass());
                    if (dashboard_widget.getLinkName() != null) {
                        link_name_map.put(dashboard_widget.getLinkName(), dashboard_widget.getLinkName());
                    }
                }
            }
            for(DashboardWidgetContext dashboard_widget: widgets)
            {
                if(dashboard_widget.getId() <= 0 )
                {
                    String result_link_name = dashboard_widget.getHeaderText();
                    if (result_link_name != null && !"".equals(result_link_name)) {
                        if(result_link_name.length() > 40){
                            result_link_name = result_link_name.substring(0, 40);
                        }
                        result_link_name = result_link_name.replaceAll("[^a-zA-Z0-9]", "_");
                        result_link_name = result_link_name.toLowerCase();
                    }
                    else
                    {
                        StringBuilder sb = new StringBuilder(dashboard_widget.getWidgetType().name()).append("_");
                        result_link_name = sb.toString();
                        result_link_name = new StringBuilder(result_link_name).append(1).toString();
                        result_link_name = result_link_name.replaceAll("[^a-zA-Z0-9]", "_");
                        result_link_name = result_link_name.toLowerCase();
                    }
                    if(result_link_name != null && link_name_map.containsKey(result_link_name)){
                        String link_name= null;
                        if(dashboard_widget.getHeaderText() != null && !"".equals(dashboard_widget.getHeaderText()))
                        {
                            link_name = result_link_name.toString();
                        }
                        else
                        {
                            link_name = new StringBuilder(dashboard_widget.getWidgetType().name()).append("_").toString();
                            link_name = link_name.replaceAll("[^a-zA-Z0-9]", "_");
                            link_name = link_name.toLowerCase();
                        }
                        int i = 1;
                        while (i < 1000) {
                            if (link_name_map.containsKey(new StringBuilder(link_name).append(i).toString())) {
                                i++;
                            } else {
                                link_name = new StringBuilder(link_name).append(i).toString();
                                break;
                            }
                        }
                        link_name_map.put(link_name, link_name);
                        dashboard_widget.setLinkName(link_name);
                    }
                    else if(result_link_name != null && !link_name_map.containsKey(result_link_name)){
                        dashboard_widget.setLinkName(result_link_name);
                        link_name_map.put(result_link_name, result_link_name);
                    }
                }
            }
        }
        catch (Exception e){
            LOGGER.log(Level.INFO, "error while generating  widget link name" , e);
        }
        return null;
    }

    public static String generateWidgetLinkName(String widget_label)throws Exception
    {
        if(widget_label.length() > 50)
        {
            widget_label = widget_label.substring(0 , 50);
            widget_label = widget_label.replaceAll("[^a-zA-Z0-9]", "_");
            widget_label = widget_label.toLowerCase();
        }
        return widget_label;
    }
    public static void runMigrationForWidgetLinkName()throws Exception
    {
        /**
         code to fetch all dashboards
         */
        List<Long> dashboard_list = new ArrayList<>();
        GenericSelectRecordBuilder select_builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getDashboardModule().getTableName())
                .select(FieldFactory.getDashboardFields())
                .andCustomWhere("ORGID = ?", AccountUtil.getCurrentOrg().getOrgId());
        List<Map<String, Object>> dashboard_prop = select_builder.get();
        if(dashboard_prop != null && dashboard_prop.size() > 0 )
        {
            int len = dashboard_prop.size();
            for(int i =0 ;i< len ;i++)
            {
                DashboardContext dashboard = (DashboardContext) FieldUtil.getAsBeanFromMap(dashboard_prop.get(i), DashboardContext.class);
                dashboard_list.add(dashboard.getId());
            }
        }

        /**
         * code to fetch all dashboard tabs
         */
        List<Long> dashboard_tab_list = new ArrayList<>();
        select_builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getDashboardTabModule().getTableName())
                .select(FieldFactory.getDashboardTabFields())
                .andCustomWhere("ORGID = ?", AccountUtil.getCurrentOrg().getOrgId());
        List<Map<String, Object>> dashboard_tab_prop = select_builder.get();
        if(dashboard_tab_prop != null && dashboard_tab_prop.size() > 0 )
        {
            int len = dashboard_tab_prop.size();
            for(int i =0 ;i< len ;i++)
            {
                DashboardTabContext dashboard_tab = (DashboardTabContext) FieldUtil.getAsBeanFromMap(dashboard_tab_prop.get(i), DashboardTabContext.class);
                dashboard_tab_list.add(dashboard_tab.getId());
            }
        }

        /**
         * code to get all widgets
         */

        for(Long dashboardId : dashboard_list)
        {
            V3DashboardAPIHandler.getAllWidgetByDashboardId(dashboardId, null);
        }
        for(Long dashboardTabId : dashboard_tab_list)
        {
            V3DashboardAPIHandler.getAllWidgetByDashboardId(null, dashboardTabId);
        }

    }

    private static void getAllWidgetByDashboardId(Long dashboardId, Long dashboardTabId)throws Exception
    {
        List<DashboardWidgetContext> widgets = new ArrayList<>();
        List<DashboardWidgetContext> update_widget_list = null;
        HashMap<String, String> link_name_map = new HashMap<String, String>();

        if(dashboardTabId != null){
            DashboardTabContext dashboardTab = DashboardUtil.getDashboardTabWithWidgets(dashboardTabId);
            widgets = dashboardTab.getDashboardWidgets();
        }
        else if(dashboardId != null) {
            DashboardContext dashboard = DashboardUtil.getDashboardWithWidgets(dashboardId);
            widgets = dashboard.getDashboardWidgets();
        }
        for(DashboardWidgetContext widget : widgets)
        {
            if(widget.getLinkName() != null)
            {
                link_name_map.put(widget.getLinkName() , widget.getLinkName());
                continue;
            }
            String  widget_header_text = widget.getHeaderText();
            widget_header_text = V3DashboardAPIHandler.generateWidgetLinkNameMigration(widget , link_name_map);
            link_name_map.put(widget_header_text , widget_header_text);
            widget.setLinkName(widget_header_text);
            update_widget_list.add(widget);
        }
        V3DashboardAPIHandler.updateWidgetLinkName(update_widget_list);
    }

    private static void updateWidgetLinkName(List<DashboardWidgetContext> update_widget_list)throws Exception
    {
        GenericUpdateRecordBuilder update_builder = null;
        for(DashboardWidgetContext  widget : update_widget_list)
        {
            update_builder = new GenericUpdateRecordBuilder()
                    .table(ModuleFactory.getWidgetModule().getTableName())
                    .fields(FieldFactory.getWidgetFields())
                    .andCustomWhere("ID = ?", widget.getId());

            Map<String, Object> props = FieldUtil.getAsProperties(widget);
            update_builder.update(props);
        }
    }
    private static String generateWidgetLinkNameMigration(DashboardWidgetContext widget , HashMap existed_link_name)throws Exception
    {
        String widget_header_text = null;
        if(widget.getHeaderText() != null && !widget.getHeaderText().equals(""))
        {
            widget_header_text = V3DashboardAPIHandler.generateWidgetLinkName(widget.getHeaderText());
        }
        else
        {
            widget_header_text= new StringBuilder(widget.getWidgetType().name()).append("_").toString();
            StringBuilder sb = new StringBuilder(widget_header_text).append('1');
            if (existed_link_name.containsKey(sb.toString())) {
                int i = 2;
                while (i < 1000) {
                    sb = new StringBuilder(widget_header_text).append(i);
                    i++;
                    if (!existed_link_name.containsKey(sb.toString())) {
                        break;
                    }
                }
            }
            widget_header_text = sb.toString();
        }
        widget_header_text = widget_header_text.replaceAll("[^a-zA-Z0-9]", "_");
        widget_header_text = widget_header_text.toLowerCase();
        return widget_header_text;
    }

    public static Long generateDashboardCriteriaId(Criteria criteria, String moduleName)throws Exception
    {
        if(criteria != null) {
            if (moduleName != null) {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                for (String key : criteria.getConditions().keySet()) {
                    Condition condition = criteria.getConditions().get(key);
                    FacilioField field = modBean.getField(condition.getFieldName(), moduleName);
                    condition.setField(field);
                }
            }
            return CriteriaAPI.addCriteria(criteria, AccountUtil.getCurrentOrg().getId());
        }
        return -1l;
    }
    public static Map<String, Object> fetchTriggerWidgetSuppliments(String moduleName, Long recordId)throws Exception
    {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        List<FacilioField> fields = modBean.getAllFields(moduleName);
        List<FacilioField> supplementFields = fields.stream().filter(f -> f instanceof SupplementRecord).collect(Collectors.toList());

        SelectRecordsBuilder<? extends ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<>()
                .select(fields)
                .module(module)
                .beanClass(FacilioConstants.ContextNames.getClassFromModule(module))
                .fetchSupplements(supplementFields)
                .andCondition(CriteriaAPI.getIdCondition(recordId, module));
        ModuleBaseWithCustomFields moduleData = builder.fetchFirst();
        Map<String, Object> params = WorkflowRuleAPI.getRecordPlaceHolders(moduleName, moduleData, WorkflowRuleAPI.getOrgPlaceHolders());
        return params;
    }
    public static void returnDashboardPlaceholders(Map<String,Object> placeholder_vs_value_map, Map<String, Object> value_placeholders_map, String moduleName, String replacedKey)throws Exception
    {
        for(Map.Entry<String, Object> entry : value_placeholders_map.entrySet())
        {
            String newkey = String.valueOf(entry.getKey());
            if(newkey != null && newkey.startsWith(moduleName)){
                newkey = newkey.replaceFirst(moduleName, replacedKey);
            }
            placeholder_vs_value_map.put(newkey, entry.getValue());
        }
    }

    public static void constructDashboardRulePlaceHolders(DashboardWidgetContext trigger_widget, DashboardExecuteMetaContext dashboard_execute_meta, String recordId, ArrayList value_arr, HashMap widget_value)throws Exception
    {
        Map<String, Object> placeholder_vs_value_map = new HashMap<>();
        JSONObject placeHoldersMeta = dashboard_execute_meta.getPlaceHoldersMeta();
        if (placeHoldersMeta.containsKey(trigger_widget.getLinkName()))
        {
            HashMap widget_meta = (HashMap) placeHoldersMeta.get(trigger_widget.getLinkName());
            if (widget_meta != null && widget_meta.containsKey("moduleName")) {
                String moduleName = (String) widget_meta.get("moduleName");
                if (recordId != null) {
                    Map<String, Object> value_placeholders_map = null;
                    try{
                       Long newRecordId= Long.parseLong(recordId);
                       value_placeholders_map = V3DashboardAPIHandler.fetchTriggerWidgetSuppliments(moduleName, newRecordId);
                    }catch(Exception e){
                        value_placeholders_map = new HashMap<>();
                    }
                    value_placeholders_map = value_placeholders_map != null ? value_placeholders_map : new HashMap<>();
//                    Map<String, Object> value_placeholders_map = V3DashboardAPIHandler.fetchTriggerWidgetSuppliments(moduleName, Long.parseLong(recordId));
                    if (value_arr != null && value_arr.size() > 1) {
                        StringBuilder sb = new StringBuilder();
                        for (Object val : value_arr) {
                            sb.append(val).append(',');
                        }
                        String selected_val_string = sb.toString();
                        selected_val_string = selected_val_string.substring(0, selected_val_string.length() - 1);
                        if (widget_value != null && widget_value.containsKey("value")) {
                            placeholder_vs_value_map.put(new StringBuilder(trigger_widget.getLinkName()).append(".value.id").toString(), selected_val_string);
                            V3DashboardAPIHandler.returnDashboardPlaceholders(placeholder_vs_value_map, value_placeholders_map, moduleName, new StringBuilder(trigger_widget.getLinkName()).append(".value").toString());
                        } else if (widget_value != null && widget_value.containsKey("dimension")) {
                            placeholder_vs_value_map.put(new StringBuilder(trigger_widget.getLinkName()).append(".dimension.id").toString(), selected_val_string);
                            V3DashboardAPIHandler.returnDashboardPlaceholders(placeholder_vs_value_map, value_placeholders_map, moduleName, new StringBuilder(trigger_widget.getLinkName()).append(".dimension").toString());
                        }
                    } else {
                        if (widget_value != null && widget_value.containsKey("value")) {
                            placeholder_vs_value_map.put(new StringBuilder(trigger_widget.getLinkName()).append(".value.id").toString(), recordId);
                            V3DashboardAPIHandler.returnDashboardPlaceholders(placeholder_vs_value_map, value_placeholders_map, moduleName, new StringBuilder(trigger_widget.getLinkName()).append(".value").toString());
                        }
                        if (widget_value != null && widget_value.containsKey("dimension")) {
                            placeholder_vs_value_map.put(new StringBuilder(trigger_widget.getLinkName()).append(".dimension.id").toString(), recordId);
                            V3DashboardAPIHandler.returnDashboardPlaceholders(placeholder_vs_value_map, value_placeholders_map, moduleName, new StringBuilder(trigger_widget.getLinkName()).append(".dimension").toString());
                        }
                        if (widget_value != null && widget_value.containsKey("group_by")) {
                            placeholder_vs_value_map.put(new StringBuilder(trigger_widget.getLinkName()).append(".group_by.id").toString(), widget_value.get("group_by"));
                            if(dashboard_execute_meta.getGroupByMeta() != null && dashboard_execute_meta.getGroupByMeta().containsKey(trigger_widget.getLinkName())){
                                JSONObject group_by_meta_obj = dashboard_execute_meta.getGroupByMeta();
                                HashMap group_by_module_obj = (HashMap) group_by_meta_obj.get(trigger_widget.getLinkName());
                                if(group_by_module_obj != null ) {
                                    String groupByModuleName = (String) group_by_module_obj.get("groupByModuleName");
                                    if(groupByModuleName != null && !groupByModuleName.equals(moduleName))
                                    {
                                        String group_by_id = (String)widget_value.get("group_by");
                                        if(group_by_id != null) {
                                            Map<String, Object> groupBy_placeholder_vs_value = new HashMap<>();
                                            Map<String, Object> groupby_value_placeholders_map = V3DashboardAPIHandler.fetchTriggerWidgetSuppliments(groupByModuleName, Long.parseLong(group_by_id));
                                            V3DashboardAPIHandler.returnDashboardPlaceholders(groupBy_placeholder_vs_value, groupby_value_placeholders_map, groupByModuleName, new StringBuilder(trigger_widget.getLinkName()).append(".group_by").toString());
                                            dashboard_execute_meta.setGroupby_placeholder_vs_value_map(groupBy_placeholder_vs_value);
                                        }

                                    }
                                }
                            }
                        }
                        //                                           else if (widget_value != null && widget_value.containsKey("criteria")) {
                        //                                               placeholder_vs_value_map.put(new StringBuilder(trigger_widget.getLinkName()).append(".group_by").toString(), widget_value.get("group_by"));
                        //                                           }
                    }
                }

            }
            if(dashboard_execute_meta.getPlaceHolders() != null){
                JSONObject placeHolders = dashboard_execute_meta.getPlaceHolders();
                if(placeHolders != null && placeHolders.containsKey("startTime") && placeHolders.containsKey("endTime"))
                {
                    placeholder_vs_value_map.put("startTime", placeHolders.get("startTime"));
                    placeholder_vs_value_map.put("endTime", placeHolders.get("endTime"));
                }
            }
            dashboard_execute_meta.setPlaceholder_vs_value_map(placeholder_vs_value_map);
        }
    }

    public static Long generateWorkflowScriptId(String script)throws Exception
    {
        WorkflowContext workflow = new WorkflowContext();
        workflow.setIsV2Script(true);
        List<Object> paramsList = new ArrayList<Object>();
        workflow.setWorkflowV2String(script);
        workflow.setParams(paramsList);
        return WorkflowUtil.addWorkflow(workflow);
    }
    private static List<Integer> addToOperators() {
        List<Integer> operators = new ArrayList<>();
        operators.add(DateOperators.LAST_N_MONTHS.getOperatorId());
        operators.add(DateOperators.LAST_N_WEEKS.getOperatorId());
        operators.add(DateOperators.LAST_N_DAYS.getOperatorId());
        return operators;
    }
}
