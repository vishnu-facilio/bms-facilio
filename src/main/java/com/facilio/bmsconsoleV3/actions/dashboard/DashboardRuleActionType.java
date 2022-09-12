package com.facilio.bmsconsoleV3.actions.dashboard;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.DashboardFilterContext;
import com.facilio.bmsconsole.context.DashboardUserFilterContext;
import com.facilio.bmsconsole.context.DashboardWidgetContext;
import com.facilio.bmsconsole.context.WidgetChartContext;
import com.facilio.bmsconsole.util.DashboardFilterUtil;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.bmsconsole.util.FormRuleAPI;
import com.facilio.bmsconsoleV3.context.dashboard.*;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.modules.FieldUtil;
import com.facilio.report.context.ReportContext;
import com.facilio.report.util.ReportUtil;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import com.facilio.workflowv2.util.WorkflowV2Util;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum DashboardRuleActionType {

    FILTER( 1, "filter") {
        @Override
        public void performAction(DashboardRuleContext dashboardRule , DashboardRuleActionContext dashboard_rule_action, JSONObject placeHolders, Long trigger_widget_id, JSONObject timeline_filter, JSONObject user_filter, Map<Long, Map<String, String>> timeline_widget_field_map) throws Exception
        {
            JSONObject result_json = null;
            for(DashboardTriggerAndTargetWidgetContext target_widget : dashboard_rule_action.getTarget_widgets())
            {
                result_json = new JSONObject();
                Long target_widget_id = target_widget.getTarget_widget_id();
                Criteria target_widget_criteria = null;
                Long criteriaId = target_widget.getCriteriaId();
                if(criteriaId != null  && criteriaId > 0)
                {
                    target_widget_criteria = CriteriaAPI.getCriteria(criteriaId);
                    if (target_widget_criteria != null)
                    {
                        for (String key : target_widget_criteria.getConditions().keySet()) {
                            Condition condition = target_widget_criteria.getConditions().get(key);
                            if (condition.getValue() instanceof String && FormRuleAPI.containsPlaceHolders(condition.getValue())) {
                                String value = FormRuleAPI.replacePlaceHoldersAndGetResult(placeHolders, condition.getValue());
                                condition.setValue(value);
                                condition.setComputedWhereClause(null);
                            }
                        }

                    }
                }

                DashboardWidgetContext widget  = DashboardUtil.getWidget(target_widget_id);
                DashboardWidgetContext.WidgetType widgetType = DashboardWidgetContext.WidgetType.getWidgetType(widget.getType());
                if(widgetType != null && widgetType == DashboardWidgetContext.WidgetType.FILTER)
                {
                    setResult(result_json, widgetType, target_widget_id, target_widget_criteria, timeline_filter,user_filter, placeHolders, timeline_widget_field_map);
                }
                else if(widget != null && widgetType == DashboardWidgetContext.WidgetType.CHART)
                {
                    WidgetChartContext chart_Widget = (WidgetChartContext) widget;
                    if(chart_Widget.getNewReportId() != null && chart_Widget.getNewReportId() > 0)
                    {
                        ReportContext report =  ReportUtil.getReport(chart_Widget.getNewReportId().longValue());
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
                            setResult(result_json, widgetType, target_widget_id, target_widget_criteria, timeline_filter, user_filter, placeHolders, timeline_widget_field_map);
                        }
                    }
                }
                if(result_json != null && !result_json.isEmpty()) {
                    dashboardRule.getResult_json().add(result_json);
                }
            }
        }

        private void setResult(JSONObject result_json , DashboardWidgetContext.WidgetType widgetType, Long target_widget_id, Criteria criteria , JSONObject timeline_filter, JSONObject user_filter , JSONObject placeHolders, Map<Long, Map<String, String>> timeline_widget_field_map)throws Exception
        {
            result_json.put("widgetType" , widgetType);
            result_json.put("widget_id", target_widget_id);
            JSONObject actionData = new JSONObject();
            JSONObject temp = new JSONObject();
            temp.put("criteria", criteria);
            actionData.put("FILTER", temp);
            if(timeline_filter != null && timeline_widget_field_map != null && timeline_widget_field_map.containsKey(target_widget_id))
            {
                actionData.put("TIMELINE_FILTER", timeline_filter.get("TIMELINE_FILTER"));
            }
            if(user_filter != null && user_filter.containsKey(target_widget_id))
            {
               JSONObject user_filter_result = V3DashboardAPIHandler.constructUserFilterRepsonse((JSONObject) user_filter.get(target_widget_id));
               if(user_filter_result != null && !user_filter_result.isEmpty()) {
                   actionData.put("USER_FILTER", user_filter_result);
               }
            }
            result_json.put("actionData" , actionData);
        }
    },
    URL(2 , "url") {
        @Override
        public void performAction(DashboardRuleContext dashboard_rule , DashboardRuleActionContext dashboard_rule_action, JSONObject placeholder_json, Long trigger_widget_id, JSONObject timeline_filter, JSONObject user_filter, Map<Long, Map<String, String>> timeline_widget_field_map)throws Exception
        {
            JSONObject action_details = dashboard_rule_action.getAction_meta().getAction_detail();
            if(action_details != null )
            {
                if(action_details != null )
                {
                    JSONObject result_json = new JSONObject();
                    String url = (String) action_details.get("url");
                    String replaced_url = FormRuleAPI.replacePlaceHoldersAndGetResult(placeholder_json, url);
                    DashboardWidgetContext widget  = DashboardUtil.getWidget(trigger_widget_id);
                    DashboardWidgetContext.WidgetType widgetType = DashboardWidgetContext.WidgetType.getWidgetType(widget.getType());
                    result_json.put("widgetType", widgetType);
                    result_json.put("widget_id", trigger_widget_id);
                    JSONObject actionMeta = new JSONObject();
                    JSONObject url_action = new JSONObject();
                    url_action.put("url", replaced_url);
                    actionMeta.put("URL", url_action);
                    result_json.put("actionMeta", actionMeta);
                    dashboard_rule.getResult_json().add(result_json);
                }
            }
        }
    },
    SHOW_SECTIONS(3, "show_sections"){
        @Override
        public void performAction(DashboardRuleContext dashboard_rule , DashboardRuleActionContext dashboard_rule_action, JSONObject placeholder_json ,Long trigger_widget_id, JSONObject timeline_filter, JSONObject user_filter, Map<Long, Map<String, String>> timeline_widget_field_map)throws Exception
        {
            JSONObject action_details = dashboard_rule_action.getAction_meta().getAction_detail();
            if(action_details != null && action_details.containsKey("section_ids"))
            {
                JSONArray section_ids = (JSONArray) action_details.get("section_ids");
                if(section_ids != null && section_ids.size() > 0)
                {
                    JSONObject result_json = new JSONObject();
                    result_json.put("actionType", DashboardRuleActionType.getActionType(dashboard_rule_action.getType()).getName());
                    JSONObject show_section = new JSONObject();
                    JSONObject actionMeta = new JSONObject();
                    int len = section_ids.size();
                    for(int i=0 ;i< len; i++)
                    {
                        Long section_widget_id = (Long) section_ids.get(i);
                        show_section.put("section_widget_id", section_widget_id);
                        actionMeta.put("SHOW_SECTIONS", show_section);
                        result_json.put("actionMeta", actionMeta);
                        dashboard_rule.getResult_json().add(result_json);
                    }
                }
            }
        }
    },
    HIDE_SECTIONS(4, "hide_sections"){
        @Override
        public void performAction(DashboardRuleContext dashboard_rule , DashboardRuleActionContext dashboard_rule_action, JSONObject placeholder_json, Long trigger_widget_id, JSONObject timeline_filter, JSONObject user_filter, Map<Long, Map<String, String>> timeline_widget_field_map)throws Exception
        {
            JSONObject action_details = dashboard_rule_action.getAction_meta().getAction_detail();
            if(action_details != null && action_details.containsKey("section_ids"))
            {
                JSONArray section_ids = (JSONArray) action_details.get("section_ids");
                if(section_ids != null && section_ids.size() > 0)
                {
                    JSONObject result_json = new JSONObject();
                    result_json.put("actionType", DashboardRuleActionType.getActionType(dashboard_rule_action.getType()).getName());
                    JSONObject show_section = new JSONObject();
                    JSONObject actionMeta = new JSONObject();
                    int len = section_ids.size();
                    for(int i=0 ;i< len; i++)
                    {
                        Long section_widget_id = (Long) section_ids.get(i);
                        show_section.put("section_widget_id", section_widget_id);
                        actionMeta.put("HIDE_SECTIONS", show_section);
                        result_json.put("actionMeta", actionMeta);
                        dashboard_rule.getResult_json().add(result_json);
                    }
                }
            }
        }
    },

    SCRIPT_ACTION(5, "script_action"){
        @Override
        public void performAction(DashboardRuleContext dashboard_rule , DashboardRuleActionContext dashboard_rule_action, JSONObject placeholder_json, Long trigger_widget_id, JSONObject timeline_filter, JSONObject user_filter, Map<Long, Map<String, String>> timeline_widget_field_map)throws Exception
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
        public void performAction(DashboardRuleContext dashboard_rule , DashboardRuleActionContext dashboard_rule_action, JSONObject placeholder_json, Long trigger_widget_id , JSONObject timeline_filter, JSONObject user_filter, Map<Long, Map<String, String>> timeline_widget_field_map)throws Exception
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
    abstract public void performAction(DashboardRuleContext dashboard_rule, DashboardRuleActionContext action, JSONObject client_json, Long trigger_widget_id, JSONObject timeline_filter, JSONObject user_filter , Map<Long, Map<String, String>> timeline_widget_field_map) throws Exception;
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
