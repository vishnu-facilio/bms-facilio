package com.facilio.bmsconsoleV3.actions.dashboard;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.DashboardFilterUtil;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.bmsconsole.util.FormRuleAPI;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.WidgetSectionContext;
import com.facilio.bmsconsoleV3.context.dashboard.*;
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
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.iam.accounts.util.IAMOrgUtil;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportDataPointContext;
import com.facilio.report.util.ReportUtil;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import lombok.extern.log4j.Log4j;
import org.apache.log4j.Level;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Log4j
public class V3DashboardAPIHandler {

    public static void updateDashboardProp(DashboardContext dashboard, JSONObject dashboardMeta)throws Exception
    {
        dashboard.setDashboardName((String) dashboardMeta.get("dashboardName"));
        if(dashboardMeta.get("dashboardFolderId") != null) {
            dashboard.setDashboardFolderId((Long) dashboardMeta.get("dashboardFolderId"));
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

        widgetContext.setLayoutPosition(Integer.parseInt(widget.get("order").toString()));
        widgetContext.setType(widgetType);

        return widgetContext;
    }

    public static void updateDashboardTabAPI(DashboardContext dashboard, DashboardTabContext dashboardTabContext, JSONObject data)throws Exception
    {
        if(dashboardTabContext != null) {
            FacilioChain updateDashboardChain = TransactionChainFactoryV3.getUpdateDashboardTabChainV3();
            FacilioContext updateTabContext = updateDashboardChain.getContext();
            if (data.containsKey("fromType")) {
                updateTabContext.put("fromType", data.get("fromType"));
            }
            updateTabContext.put(FacilioConstants.ContextNames.DASHBOARD_TAB, dashboardTabContext);
            updateTabContext.put(FacilioConstants.ContextNames.DASHBOARD, dashboard);
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
                long criteriaId = V3DashboardAPIHandler.generateCriteriaId(trigger_widget.getCriteria());
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
            long criteriaId = V3DashboardAPIHandler.generateCriteriaId(dashboard_actions_meta.getCriteria());
            if(criteriaId > 0){
                dashboard_actions_meta.setCriteriaId(criteriaId);
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

                long criteriaId = V3DashboardAPIHandler.generateCriteriaId(target_widget.getCriteria());
                if(criteriaId > 0){
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

    public static long generateCriteriaId(Criteria criteria)throws Exception
    {
        if(criteria != null) {
            long criteriaId = CriteriaAPI.addCriteria(criteria);
            if (criteriaId > 0) {
                return criteriaId;
            }
        }
        return -1;
    }

    public static DashboardRuleContext getDashboardRule(Long dashboard_rule_id)throws Exception
    {
        GenericSelectRecordBuilder select_builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getDashboardRuleModule().getTableName())
                .select(FieldFactory.getDashboardRuleFields())
                .andCustomWhere("ORGID = ?", AccountUtil.getCurrentOrg().getOrgId())
                .andCondition(CriteriaAPI.getCondition("ID", "id", dashboard_rule_id+"", NumberOperators.EQUALS));

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

                GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder();
                deleteRecordBuilder.table(ModuleFactory.getDashboardRuleActionModule().getTableName())
                        .andCondition(CriteriaAPI.getIdCondition(action.getId(), ModuleFactory.getDashboardRuleActionModule()));
                deleteRecordBuilder.delete();
            }
        }
    }

    public static void executeDashboardRules(Long trigger_widget_Id, JSONObject filter_json)throws Exception
    {
        JSONObject result_json = new JSONObject();
        if(trigger_widget_Id != null && trigger_widget_Id > 0) {
            Long dashboard_rule_id = V3DashboardAPIHandler.checkIsDashboardRuleApplied(trigger_widget_Id);
            if(dashboard_rule_id != null && dashboard_rule_id > 0)
            {
                DashboardRuleContext rule_to_execute = V3DashboardAPIHandler.getDashboardRule(dashboard_rule_id);
                if(rule_to_execute.getActions() != null)
                {
                    for(DashboardRuleActionContext dashboard_rule_action : rule_to_execute.getActions())
                    {
                        DashboardRuleActionType.getActionType(dashboard_rule_action.getType()).performAction(rule_to_execute, dashboard_rule_action , (JSONObject) filter_json.get("placeHoldersValuesMap"));
//                            case 5: //script action
//                                JSONObject script_json = V3DashboardAPIHandler.dashboardRuleExecuteScript(dashboard_rule_action.getAction_meta());
//                                result_json.put(actionType, script_json);
//                                break;
//                        }
                    }

                }
            }
            else
            {
                JSONObject widget_vs_field_map = new JSONObject();
                FacilioModule filter_module =null;
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                if(filter_json != null && filter_json.containsKey("module") && filter_json.get("module") != null)
                {
                    filter_module =  modBean.getModule((String)filter_json.get("module"));
                    filter_module.setFields(modBean.getAllFields(filter_module.getName()));
                }
                DashboardContext dashboard = DashboardUtil.getDashboardWithWidgets((Long)filter_json.get("dashboardId"));
                if(dashboard != null && dashboard.getDashboardWidgets() != null && dashboard.getDashboardWidgets().size() > 0)
                {
                    for(DashboardWidgetContext dashboard_widget : dashboard.getDashboardWidgets())
                    {
                        Long widgetModuleId = DashboardFilterUtil.getModuleIdFromWidget(dashboard_widget);
                        if(filter_module != null && widgetModuleId != null && widgetModuleId > 0)
                        {
                            FacilioModule widgetModule = modBean.getModule(widgetModuleId);
                            widgetModule.setFields(modBean.getAllFields(widgetModule.getName()));
                            FacilioField field = DashboardFilterUtil.getFilterApplicableField(filter_module, widgetModule, null);
                            if(field != null)
                            {
                                widget_vs_field_map.put(dashboard_widget.getId(), field);
                            }
                        }
                    }
                }
                result_json.put("widget_vs_filter_field", widget_vs_field_map);
            }

        }
    }

    public static JSONObject setUrlAction(Map<String, Object> placeHolderValues, DashboardRuleActionMetaContext dashboard_rule_meta)throws Exception {
        JSONObject result_json = new JSONObject();
        Long criteriaId = dashboard_rule_meta.getCriteriaId();
        if (criteriaId != null && criteriaId > 0) {
            Criteria target_widget_criteria = CriteriaAPI.getCriteria(criteriaId);
            if (target_widget_criteria != null) {
                for (String key : target_widget_criteria.getConditions().keySet()) {
                    Condition condition = target_widget_criteria.getConditions().get(key);
                    if (condition.getValue() instanceof String && FormRuleAPI.containsPlaceHolders(condition.getValue())) {
                        String value = FormRuleAPI.replacePlaceHoldersAndGetResult(placeHolderValues, condition.getValue());
                        condition.setValue(value);
                        condition.setComputedWhereClause(null);
                    }
                }

            }
        }
        String action_details = dashboard_rule_meta.getAction_deatils();
        if(action_details != null )
        {
            JSONParser parser = new JSONParser();
            JSONObject action_meta = (JSONObject ) parser.parse(action_details);
            if(action_meta != null )
            {
                Long trigger_widget_id = (Long) action_meta.get("trigger_widget_id");
                String url = (String) action_meta.get("trigger_widget_id");
                String replaced_url = FormRuleAPI.replacePlaceHoldersAndGetResult(placeHolderValues, url);
                JSONObject action_data_meta = new JSONObject();
                action_data_meta.put("url", replaced_url);
                result_json.put(trigger_widget_id , action_data_meta);
            }
        }
        return result_json;
    }
    public static JSONObject setAndExecuteFilter(Map<String, Object> placeHolderValues, List<DashboardTriggerAndTargetWidgetContext> target_widgets ) throws Exception
    {
        JSONObject result_json = new JSONObject();
        if(placeHolderValues != null && !placeHolderValues.isEmpty())
        {
            for(DashboardTriggerAndTargetWidgetContext target_widget : target_widgets)
            {
                Long target_widget_id = target_widget.getTarget_widget_id();

                Long criteriaId = target_widget.getCriteriaId();
                if(criteriaId != null  && criteriaId > 0)
                {
                    Criteria target_widget_criteria = CriteriaAPI.getCriteria(criteriaId);
                    if(target_widget_criteria != null)
                    {
                        for(String key : target_widget_criteria.getConditions().keySet())
                        {
                            Condition condition = target_widget_criteria.getConditions().get(key);
                            if(condition.getValue() instanceof String && FormRuleAPI.containsPlaceHolders(condition.getValue())) {
                                String value = FormRuleAPI.replacePlaceHoldersAndGetResult(placeHolderValues, condition.getValue());
                                condition.setValue(value);
                                condition.setComputedWhereClause(null);
                            }
                        }

                    }
                    DashboardWidgetContext widget = DashboardUtil.getWidget(target_widget_id);
                    if(widget != null && widget.getWidgetType().getName().equals(DashboardWidgetContext.WidgetType.FILTER))
                    {
                        result_json.put(target_widget_id , target_widget_criteria);
                    }
                    else if(widget != null && widget.getWidgetType().getName().equals(DashboardWidgetContext.WidgetType.CHART))
                    {
                        WidgetChartContext chart_Widget = (WidgetChartContext) widget;
                        if(chart_Widget.getReportId() != null && chart_Widget.getReportId() > 0)
                        {
                            ReportContext report =  ReportUtil.getReport(chart_Widget.getReportId());
                            if(report.getTypeEnum().equals(ReportContext.ReportType.READING_REPORT))
                            {
                                if(result_json.containsKey(target_widget_id))
                                {
                                    JSONObject temp = new JSONObject();
                                    JSONArray temp_arr = new JSONArray();
                                    temp.put("component_type", "reading_report");
                                    JSONObject temp_datapoint = new JSONObject();
                                    temp_datapoint.put("datapoint_id", target_widget.getDatapoint_id());
                                    temp_datapoint.put("criteria", target_widget_criteria);
                                    temp_arr.add(temp_datapoint);
                                    temp.put("criteria", temp_arr);
                                    result_json.put(target_widget_id , temp);
                                }else{
                                    JSONObject temp = new JSONObject();
                                    JSONArray temp_arr = (JSONArray) temp.get("criteria");
                                    temp.put("datapoint_id", target_widget.getDatapoint_id());
                                    temp.put("criteria" ,target_widget_criteria);
                                    temp_arr.add(temp);
                                }
//                                  List<ReportDataPointContext> report_data_points = report.getDataPoints();
//                                  for(ReportDataPointContext report_data_point : report_data_points)
//                                  {
//                                      if(report_data_point.getyAxis() != null && report_data_point.getyAxis().getField() != null)
//                                      {
//                                          FacilioField field = report_data_point.getyAxis().getField();
//                                          if(field != null &&  field.getFieldId() > 0) {
//                                              Long assetCategoryId = report_data_point.getAssetCategoryId();
//                                              if (assetCategoryId != null && assetCategoryId > 0) {
//                                                  if (target_widget_criteria != null) {
//                                                      List<Long> categoryIds = null;
//                                                      if (assetCategoryId != null && assetCategoryId > 0) {
//                                                          categoryIds = new ArrayList<>();
//                                                          categoryIds.add(assetCategoryId);
//                                                      }
//                                                      JSONObject assetsObj = AssetsAPI.getAssetsWithReadingsForSpecificCategory(null, categoryIds, false, target_widget_criteria);
//                                                      if (assetsObj != null && assetsObj.containsKey("categoryWithFields")) {
//                                                          JSONObject categoryWithFields = (JSONObject) assetsObj.get("categoryWithFields");
//                                                          Long field_id = field.getFieldId();
//                                                          if(categoryWithFields != null && categoryWithFields.containsKey(assetCategoryId))
//                                                          {
//                                                                JSONObject fields_vs_asset = (JSONObject) categoryWithFields.get(assetCategoryId);
//                                                                if(fields_vs_asset != null && fields_vs_asset.containsKey(field_id))
//                                                                {
//                                                                    JSONArray datapoint_asset_list = (JSONArray) fields_vs_asset.get(field_id);
//                                                                    if(datapoint_asset_list != null && datapoint_asset_list.size() > 10)
//                                                                    {
//                                                                        LOGGER.debug("No of assets in this building with this data point are exceeding the limit 10");
//                                                                        return;
//                                                                    }
//                                                                    else
//                                                                    {
//                                                                        String chart_state = report.getChartState();
//                                                                        if(chart_state != null) {
//                                                                            JSONParser parser = new JSONParser();
//                                                                            JSONObject chart_json = (JSONObject) parser.parse(chart_state);
//                                                                        }
//                                                                    }
//
//                                                                }
//                                                          }
//
//                                                      }
//                                                  }
//                                              }
//                                          }
//                                      }
//                                  }
                            }
                            else
                            {
                                result_json.put(target_widget_id, target_widget_criteria);
                            }
                        }
                    }
                }
            }
        }
        return result_json;
    }
    public static JSONObject formDashboardRuleShowHideSection(List<DashboardTriggerAndTargetWidgetContext> target_widgets, Integer actionType)throws Exception
    {
        JSONObject result_json = new JSONObject();
        if(target_widgets != null && target_widgets.size() > 0)
        {
            List<Long> target_widget_list = new ArrayList<>();
            for(DashboardTriggerAndTargetWidgetContext target_widget : target_widgets)
            {
                target_widget_list.add(target_widget.getTarget_widget_id());
            }
            if(target_widget_list.size() > 0)
            {
                result_json.put(actionType , target_widget_list);
            }
        }
        return result_json;
    }

    public static Long checkIsDashboardRuleApplied(Long trigger_widget_id)throws Exception
    {
        GenericSelectRecordBuilder select_builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getDashboardTriggerWidgetModule().getTableName())
                .select(FieldFactory.getDashboardTriggerWidgetFields())
                .andCustomWhere("TRIGGER_WIDGET_ID = ?", trigger_widget_id);

        List<Map<String , Object>> props = select_builder.get();
        if(props != null && props.size() > 0)
        {
            DashboardTriggerWidgetContext trigger_widget = FieldUtil.getAsBeanFromMap(props.get(0), DashboardTriggerWidgetContext.class);
            if(trigger_widget != null){
                return trigger_widget.getDashboard_rule_id();
            }
        }
        return null;
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
                        if(result_link_name.length() > 50){
                            result_link_name = result_link_name.substring(0, 50);
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

}
