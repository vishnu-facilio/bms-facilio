package com.facilio.bmsconsoleV3.actions.dashboard;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.DashboardFilterContext;
import com.facilio.bmsconsole.context.DashboardWidgetContext;
import com.facilio.bmsconsole.context.WidgetChartContext;
import com.facilio.bmsconsole.util.DashboardFilterUtil;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.bmsconsole.util.FormRuleAPI;
import com.facilio.bmsconsoleV3.context.dashboard.DashboardRuleActionContext;
import com.facilio.bmsconsoleV3.context.dashboard.DashboardRuleActionMetaContext;
import com.facilio.bmsconsoleV3.context.dashboard.DashboardRuleContext;
import com.facilio.bmsconsoleV3.context.dashboard.DashboardTriggerAndTargetWidgetContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.report.context.ReportContext;
import com.facilio.report.util.ReportUtil;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import com.facilio.workflowv2.util.WorkflowV2Util;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.*;

public enum DashboardRuleActionType {

    FILTER( 1, "filter") {
        @Override
        public void performAction(DashboardRuleContext dashboardRule , DashboardRuleActionContext dashboard_rule_action, JSONObject placeholder_json) throws Exception
        {
                JSONObject result_json = new JSONObject();
                if(placeholder_json != null && !placeholder_json.isEmpty())
                {
                    for(DashboardTriggerAndTargetWidgetContext target_widget : dashboard_rule_action.getTarget_widgets())
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
                                        String value = FormRuleAPI.replacePlaceHoldersAndGetResult(placeholder_json, condition.getValue());
                                        condition.setValue(value);
                                        condition.setComputedWhereClause(null);
                                    }
                                }

                            }
                            DashboardWidgetContext widget = DashboardUtil.getWidget(target_widget_id);
                            if(widget != null && widget.getWidgetType().getName().equals(DashboardWidgetContext.WidgetType.FILTER))
                            {
                                setResult(result_json, DashboardRuleActionType.getActionType(dashboard_rule_action.getType()).getName(), target_widget_id, target_widget_criteria);
                            }
                            else if(widget != null && widget.getWidgetType().getName().equals(DashboardWidgetContext.WidgetType.CHART))
                            {
                                WidgetChartContext chart_Widget = (WidgetChartContext) widget;
                                if(chart_Widget.getReportId() != null && chart_Widget.getReportId() > 0)
                                {
                                    ReportContext report =  ReportUtil.getReport(chart_Widget.getReportId());
                                    if(report.getTypeEnum().equals(ReportContext.ReportType.READING_REPORT))
                                    {
                                        if(result_json.containsKey("widget_id") && (Long) result_json.get("widget_id") == target_widget_id)
                                        {

                                            JSONObject actionMeta= (JSONObject)result_json.get("actionMeta");
                                            JSONArray datapoint_meta = (JSONArray)actionMeta.get("datapoint");
                                            if(datapoint_meta != null && !datapoint_meta.isEmpty()) {
                                                JSONObject datapoint_json = new JSONObject();
                                                datapoint_json.put("datapoint_id", target_widget.getDatapoint_id());
                                                datapoint_json.put("criteria", target_widget_criteria);
                                                datapoint_meta.add(datapoint_json);
                                                actionMeta.put("datapoint", datapoint_meta);
                                                result_json.put("actionMeta", actionMeta);
                                            }

                                        }
                                        else
                                        {
                                            result_json.put("actionType", DashboardRuleActionType.getActionType(dashboard_rule_action.getType()).getName());
                                            result_json.put("widget_id", target_widget_id);
                                            JSONObject actionMeta= new JSONObject();
                                            JSONArray datapoint_meta = new JSONArray();
                                            JSONObject datapoint_json = new JSONObject();
                                            datapoint_json.put("datapoint_id", target_widget.getDatapoint_id());
                                            datapoint_json.put("criteria" ,target_widget_criteria);
                                            datapoint_meta.add(datapoint_json);
                                            actionMeta.put("datapoint", datapoint_meta);
                                            result_json.put("actionMeta", actionMeta);
                                        }
                                    }
                                    else
                                    {
                                        setResult(result_json, DashboardRuleActionType.getActionType(dashboard_rule_action.getType()).getName(), target_widget_id, target_widget_criteria);
                                    }
                                }
                            }
                        }
                    }
                }
                dashboardRule.getResult_json().add(result_json);
            }

            private void setResult(JSONObject result_json , String actionType, Long target_widget_id, Criteria criteria)throws Exception
            {
                result_json.put("actionType" , actionType);
                result_json.put("widget_id", target_widget_id);
                JSONObject temp = new JSONObject();
                temp.put("criteria", criteria);
                result_json.put("action_data" , temp);
            }
    },
    URL(2 , "url") {
        @Override
        public void performAction(DashboardRuleContext dashboard_rule , DashboardRuleActionContext dashboard_rule_action, JSONObject placeholder_json)throws Exception
        {
            JSONObject result_json = new JSONObject();
            Long criteriaId = dashboard_rule_action.getAction_meta().getCriteriaId();
            if (criteriaId != null && criteriaId > 0) {
                Criteria target_widget_criteria = CriteriaAPI.getCriteria(criteriaId);
                if (target_widget_criteria != null) {
                    for (String key : target_widget_criteria.getConditions().keySet()) {
                        Condition condition = target_widget_criteria.getConditions().get(key);
                        if (condition.getValue() instanceof String && FormRuleAPI.containsPlaceHolders(condition.getValue())) {
                            String value = FormRuleAPI.replacePlaceHoldersAndGetResult(placeholder_json, condition.getValue());
                            condition.setValue(value);
                            condition.setComputedWhereClause(null);
                        }
                    }

                }
            }
            String action_details = dashboard_rule_action.getAction_meta().getAction_deatils();
            if(action_details != null )
            {
                JSONParser parser = new JSONParser();
                JSONObject action_meta = (JSONObject ) parser.parse(action_details);
                if(action_meta != null )
                {
                    Long trigger_widget_id = (Long) action_meta.get("trigger_widget_id");
                    String url = (String) action_meta.get("trigger_widget_id");
                    String replaced_url = FormRuleAPI.replacePlaceHoldersAndGetResult(placeholder_json, url);
                    result_json.put("actionType", DashboardRuleActionType.getActionType(dashboard_rule_action.getType()).getName());
                    result_json.put("widget_id", trigger_widget_id);
                    JSONObject actionMeta = new JSONObject();
                    actionMeta.put("openurl", replaced_url);
                    result_json.put("actionMeta", actionMeta);
                }
            }
            dashboard_rule.getResult_json().add(result_json);
        }
    },
    SHOW_SECTIONS(3, "show_sections"){
        @Override
        public void performAction(DashboardRuleContext dashboard_rule , DashboardRuleActionContext dashboard_rule_action, JSONObject placeholder_json)throws Exception
        {
            if(dashboard_rule_action.getTarget_widgets() != null && dashboard_rule_action.getTarget_widgets().size() > 0)
            {
                List<Long> target_widget_list = new ArrayList<>();
                for(DashboardTriggerAndTargetWidgetContext target_widget : dashboard_rule_action.getTarget_widgets())
                {
                    target_widget_list.add(target_widget.getTarget_widget_id());
                }
                if(target_widget_list.size() > 0)
                {
                    JSONObject result_json = new JSONObject();
                    JSONObject actionMeta = new JSONObject();
                    result_json.put("actionType", DashboardRuleActionType.getActionType(dashboard_rule_action.getType()).getName());
                    actionMeta.put("sections", target_widget_list);
                    result_json.put("actionMeta", actionMeta);
                    dashboard_rule.getResult_json().add(result_json);
                }
            }
        }
    },
    HIDE_SECTIONS(4, "hide_sections"){
        @Override
        public void performAction(DashboardRuleContext dashboard_rule , DashboardRuleActionContext dashboard_rule_action, JSONObject placeholder_json)throws Exception
        {
            if(dashboard_rule_action.getTarget_widgets() != null && dashboard_rule_action.getTarget_widgets().size() > 0)
            {
                List<Long> target_widget_list = new ArrayList<>();
                for(DashboardTriggerAndTargetWidgetContext target_widget : dashboard_rule_action.getTarget_widgets())
                {
                    target_widget_list.add(target_widget.getTarget_widget_id());
                }
                JSONObject result_json = new JSONObject();
                JSONObject actionMeta = new JSONObject();
                result_json.put("actionType", DashboardRuleActionType.getActionType(dashboard_rule_action.getType()).getName());
                actionMeta.put("sections", target_widget_list);
                result_json.put("actionMeta", actionMeta);
                dashboard_rule.getResult_json().add(result_json);
            }
        }
    },

    SCRIPT_ACTION(5, "script_action"){
        @Override
        public void performAction(DashboardRuleContext dashboard_rule , DashboardRuleActionContext dashboard_rule_action, JSONObject placeholder_json)throws Exception
        {
            DashboardRuleActionMetaContext dashboard_action_meta = dashboard_rule_action.getAction_meta();
            if(dashboard_action_meta != null)
            {
                Long scriptId = dashboard_action_meta.getScriptId();
                if(scriptId != null && scriptId > 0)
                {
                    WorkflowContext workflow = WorkflowUtil.getWorkflowContext(scriptId);
                    workflow.setLogNeeded(true);
                    FacilioChain chain = TransactionChainFactory.getExecuteWorkflowChain();
                    FacilioContext context = chain.getContext();
                    context.put(WorkflowV2Util.WORKFLOW_CONTEXT, workflow);
                    context.put(WorkflowV2Util.WORKFLOW_PARAMS, Collections.singletonList(placeholder_json));
                    chain.execute();
                    List returnList = (List) workflow.getReturnValue();
                    JSONArray jsonArray = new JSONArray();
                    if(returnList != null && !returnList.isEmpty()) {
                        for(int i=0;i<returnList.size();i++) {
                            Map<String,Object> fieldMap = (Map<String,Object>) returnList.get(i);

                            JSONObject fieldJSON = mapToJson(fieldMap);

                            jsonArray.add(fieldJSON);
                        }
                        JSONObject temp = new JSONObject();
                        temp.put("actionType", DashboardRuleActionType.getActionType(dashboard_rule_action.getType()).getName());
                        JSONObject actionMeta = new JSONObject();
                        actionMeta.put("script_result", jsonArray);
                        temp.put("actionMeta", actionMeta);
                        dashboard_rule.getResult_json().add(temp);
                    }
                }
            }
        }
        private JSONObject mapToJson(Map<String, Object> fieldMap) {
            JSONObject json = new JSONObject();
            json.putAll(fieldMap);
            return json;
        }
    },
    TIMELINE_FILTER(10, "timeline_filter"){
        @Override
        public void performAction(DashboardRuleContext dashboard_rule , DashboardRuleActionContext dashboard_rule_action, JSONObject placeholder_json)throws Exception
        {
            Long dashboardId = dashboard_rule.getDashboardId();
            JSONObject result_json = new JSONObject();
            if(dashboardId != null) {
                DashboardFilterContext dashboard_filter = DashboardFilterUtil.getDashboardFilter(dashboardId, null);
                if(dashboard_filter != null && dashboard_filter.getIsTimelineFilterEnabled())
                {
                    Long date_operator = dashboard_filter.getDateOperator();
                    result_json.put("actionType", "timeline_filter");
                    JSONObject actionMeta = new JSONObject();
                    actionMeta.put("operator", date_operator);
                    result_json.put("actionMeta", actionMeta);
                }
            }
        }
    },

    ;

    private int val;
    private String name;

    public int getVal() {
        return val;
    }

    public String getName() {
        return name;
    }

    private DashboardRuleActionType(int val,String name) {
        this.val = val;
        this.name = name;
    }
    abstract public void performAction(DashboardRuleContext dashboard_rule, DashboardRuleActionContext action, JSONObject client_json) throws Exception;
    private static final Map<Integer, DashboardRuleActionType> TYPE_MAP = Collections.unmodifiableMap(initTypeMap());


    private static Map<Integer, DashboardRuleActionType> initTypeMap() {
        Map<Integer, DashboardRuleActionType> typeMap = new HashMap<>();
        for (DashboardRuleActionType type : values()) {
            typeMap.put(Integer.valueOf(type.getVal()), type);
        }
        return typeMap;
    }

    public static DashboardRuleActionType getActionType(Integer actionTypeVal) {
        return TYPE_MAP.get(actionTypeVal);
    }
}
