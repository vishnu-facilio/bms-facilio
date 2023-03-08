package com.facilio.bmsconsoleV3.actions.dashboard;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.DashboardFilterUtil;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.bmsconsole.util.FormRuleAPI;
import com.facilio.bmsconsoleV3.actions.DashboardExecuteMetaContext;
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
import com.facilio.scriptengine.context.WorkflowFieldType;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import com.facilio.workflowv2.util.WorkflowV2Util;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.text.StringSubstitutor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum DashboardRuleActionType {

    FILTER( 1, "filter") {
        @Override
        public void performAction(DashboardRuleContext dashboardRule , DashboardRuleActionContext dashboard_rule_action, DashboardExecuteMetaContext dashboard_execute_meta, Long trigger_widget_id) throws Exception
        {
            JSONObject result_json = null;
            JSONObject datapoint_result_json = new JSONObject();

            JSONObject placeHolders = dashboard_execute_meta.getPlaceHolders();
            JSONObject placeHoldersMeta = dashboard_execute_meta.getPlaceHoldersMeta();
            for(DashboardTriggerAndTargetWidgetContext target_widget : dashboard_rule_action.getTarget_widgets())
            {
                if(dashboard_rule_action.getAction_meta() != null && dashboard_rule_action.getAction_meta().getAction_detail() != null) {
                    target_widget.setTargetWidgetMeta(dashboard_rule_action.getAction_meta().getAction_detail());
                }
                result_json = new JSONObject();
                Long target_widget_id = target_widget.getTarget_widget_id();
                DashboardWidgetContext trigger_widget  = DashboardUtil.getWidget(trigger_widget_id);
                Criteria target_widget_criteria = null;
                Long criteriaId = target_widget.getCriteriaId();
                DashboardWidgetContext widget  = DashboardUtil.getWidget(target_widget_id);
                if(DashboardWidgetContext.WidgetType.getWidgetType(widget.getType()) != DashboardWidgetContext.WidgetType.CARD && criteriaId != null  && criteriaId > 0)
                {
                    target_widget_criteria = CriteriaAPI.getCriteria(criteriaId);
                    if (target_widget_criteria != null)
                    {
                        Map<String, Object> placeholder_vs_value_map = new HashMap<>();
                        String recordId = null;
                        if(trigger_widget.getLinkName() != null && placeHoldersMeta  != null)
                        {
                            ArrayList value_arr = null;
                            HashMap widget_value = null;
                            if(placeHolders.containsKey(trigger_widget.getLinkName()))
                            {
                                widget_value = (HashMap)  placeHolders.get(trigger_widget.getLinkName());
                                if(widget_value != null && (widget_value.containsKey("value") || widget_value.containsKey("dimension")))
                                {
                                    value_arr = widget_value.containsKey("value") ? (ArrayList) widget_value.get("value") : (ArrayList) widget_value.get("dimension");
                                    if(value_arr.size() > 0 )
                                    {
                                        String value = (String)value_arr.get(0);
                                        if(value != null  && !value.equals("") && !value.equals("all") && !value.equals("All") && !value.equals("ALL"))
                                        {
                                            recordId = value;
                                        }
                                    }
                                }
                                V3DashboardAPIHandler.constructDashboardRulePlaceHolders(trigger_widget, dashboard_execute_meta, recordId, value_arr, widget_value);
                            }
                        }
                        if(recordId != null && !recordId.equals(""))
                        {
                            for (String key : target_widget_criteria.getConditions().keySet()) {
                                Condition condition = target_widget_criteria.getConditions().get(key);
                                if (condition.getValue() instanceof String && FormRuleAPI.containsPlaceHolders(condition.getValue())) {
                                    String replaced_value = StringSubstitutor.replace(condition.getValue(), dashboard_execute_meta.getPlaceholder_vs_value_map());
                                    replaced_value = StringSubstitutor.replace(replaced_value, dashboard_execute_meta.getGroupby_placeholder_vs_value_map());
                                    condition.setValue(replaced_value);
                                    condition.setComputedWhereClause(null);
                                }
                            }
                        }else{
                            target_widget_criteria = null;
                        }

                    }
                }
                else if( DashboardWidgetContext.WidgetType.getWidgetType(widget.getType()) == DashboardWidgetContext.WidgetType.CARD )
                {
                    WidgetCardContext target_card_widget= (WidgetCardContext) widget;
                    JSONObject cardParams = target_card_widget.getCardParams();
                    if(cardParams != null && cardParams.containsKey("reading") )
                    {
                        JSONObject reading = (JSONObject) cardParams.get("reading");
                        if (reading != null && reading.containsKey("parentId") && reading.get("parentId") != null && placeHolders.containsKey(trigger_widget.getLinkName()))
                        {
                            HashMap widget_value = (HashMap) placeHolders.get(trigger_widget.getLinkName());
                            if (widget_value != null && (widget_value.containsKey("value"))) {
                                ArrayList value_arr = widget_value.containsKey("value") ? (ArrayList) widget_value.get("value") : (ArrayList) widget_value.get("dimension");
                                if (value_arr.size() > 0) {
                                    String value = (String) value_arr.get(0);
                                    setCardResult(result_json, DashboardWidgetContext.WidgetType.CARD, target_widget_id, dashboard_execute_meta, value);
                                }
                            }
                        }
                    }
                }

                DashboardWidgetContext.WidgetType widgetType = DashboardWidgetContext.WidgetType.getWidgetType(widget.getType());
                if(widgetType != null && widgetType == DashboardWidgetContext.WidgetType.FILTER)
                {
                    setResult(result_json, widgetType, target_widget_id, target_widget_criteria, dashboard_execute_meta, trigger_widget.getLinkName(), target_widget);
                }
                else if(widget != null && widgetType == DashboardWidgetContext.WidgetType.CHART)
                {
                    WidgetChartContext chart_Widget = (WidgetChartContext) widget;
                    if(chart_Widget.getNewReportId() != null && chart_Widget.getNewReportId() > 0)
                    {
                        ReportContext report =  ReportUtil.getReport(chart_Widget.getNewReportId().longValue());
                        if(report.getTypeEnum().equals(ReportContext.ReportType.READING_REPORT))
                        {
                            if(datapoint_result_json.containsKey(target_widget_id) &&  datapoint_result_json.get(target_widget_id) != null )
                            {
                                result_json = (JSONObject) datapoint_result_json.get(target_widget_id);
                                JSONObject actionMeta= (JSONObject)result_json.get("actionMeta");
                                JSONObject filter = (JSONObject)actionMeta.get("FILTER");
                                JSONArray datapoint_meta = (JSONArray)filter.get("datapoint");
                                if(datapoint_meta != null && !datapoint_meta.isEmpty()) {
                                    JSONObject datapoint_json = new JSONObject();
                                    if(target_widget.getDataPointMeta() != null)
                                    {
                                        if(target_widget_criteria != null)
                                        {
                                            JSONObject dataPoing_obj = (JSONObject) target_widget.getDataPointMeta();
                                            datapoint_json.put("datapoint_link", dataPoing_obj.containsKey("datapoint_link") ? dataPoing_obj.get("datapoint_link") : null);
                                            datapoint_json.put("criteria", target_widget_criteria);
                                            datapoint_meta.add(datapoint_json);
                                        }
                                    }
                                }

                            }
                            else
                            {
                                result_json.put("widgetType", widgetType);
                                result_json.put("widget_id", target_widget_id);
                                JSONObject actionMeta= new JSONObject();
                                JSONArray datapoint_meta = new JSONArray();
                                JSONObject datapoint_json = new JSONObject();
                                if(target_widget.getDataPointMeta() != null)
                                {
                                    JSONObject dataPoing_obj = (JSONObject) target_widget.getDataPointMeta();
                                    if(target_widget_criteria != null) {
                                        datapoint_json.put("datapoint_link", dataPoing_obj.containsKey("datapoint_link") ? dataPoing_obj.get("datapoint_link") : null);
                                        datapoint_json.put("criteria", target_widget_criteria);
                                        datapoint_meta.add(datapoint_json);
                                        actionMeta.put("datapoint", datapoint_meta);
                                    }
                                }
                                JSONObject temp = new JSONObject();
                                temp.put("FILTER", actionMeta);
                                V3DashboardAPIHandler.setUserFilterResp(temp, target_widget_id, dashboard_execute_meta.getGlobal_filter_widget_map(), dashboard_execute_meta.getPlaceHolders());
                                V3DashboardAPIHandler.setTimeLineFilterResp(temp, target_widget_id, dashboard_execute_meta.getTimeline_widget_field_map(), dashboard_execute_meta.getGlobal_timeline_filter_widget_map());
                                result_json.put("actionMeta", temp);
                                datapoint_result_json.put(target_widget_id, result_json);
                            }
                            continue;
                        }
                        else
                        {
                            setResult(result_json, widgetType, target_widget_id, target_widget_criteria, dashboard_execute_meta, trigger_widget.getLinkName(), target_widget);
                        }
                    }
                }
                if(result_json != null && !result_json.isEmpty()) {
                    dashboardRule.getResult_json().add(result_json);
                }
            }
            if(datapoint_result_json != null && !datapoint_result_json.isEmpty()){
                Iterator<Long> iter = datapoint_result_json.keySet().iterator();
                while(iter.hasNext())
                {
                    dashboardRule.getResult_json().add(datapoint_result_json.get(iter.next()));
                }
            }

        }
        private void setCardResult(JSONObject result_json , DashboardWidgetContext.WidgetType widgetType, Long target_widget_id, DashboardExecuteMetaContext dashboard_execute_data, String value)throws Exception
        {
            result_json.put("widgetType" , widgetType);
            result_json.put("widget_id", target_widget_id);
            JSONObject actionData = new JSONObject();
            V3DashboardAPIHandler.setTimeLineFilterResp(actionData, target_widget_id, dashboard_execute_data.getTimeline_widget_field_map(), dashboard_execute_data.getGlobal_timeline_filter_widget_map());
            actionData.put("card_parent_id", value);
            result_json.put("actionMeta" , actionData);
        }
        private void setResult(JSONObject result_json , DashboardWidgetContext.WidgetType widgetType, Long target_widget_id, Criteria criteria, DashboardExecuteMetaContext dashboard_execute_data, String trigger_widget_link_name, DashboardTriggerAndTargetWidgetContext dashboard_widget)throws Exception
        {
            result_json.put("widgetType" , widgetType);
            result_json.put("widget_id", target_widget_id);
            JSONObject actionData = new JSONObject();
            JSONObject temp = new JSONObject();
            temp.put("criteria", criteria);
            JSONObject placeHoldes = dashboard_execute_data.getPlaceHolders();
            if(placeHoldes != null && placeHoldes.containsKey(trigger_widget_link_name))
            {
                JSONObject target_widget_meta = dashboard_widget.getTargetWidgetMeta();
                if(target_widget_meta != null && target_widget_meta.containsKey("isTriggerCriteriaEnabled"))
                {
                    JSONObject temp_rule_info = new JSONObject();
                    HashMap trigger_widget_obj = (HashMap) placeHoldes.get(trigger_widget_link_name);
                    if (trigger_widget_obj != null && trigger_widget_obj.containsKey("criteria")) {
                        temp_rule_info.put("trigger_widget_criteria", trigger_widget_obj.get("criteria"));
                    }
                    temp.put("ruleInfo", temp_rule_info);
                }
            }
            actionData.put("FILTER", temp);
            V3DashboardAPIHandler.setUserFilterResp(actionData, target_widget_id, dashboard_execute_data.getGlobal_filter_widget_map(), dashboard_execute_data.getPlaceHolders());
            V3DashboardAPIHandler.setTimeLineFilterResp(actionData, target_widget_id, dashboard_execute_data.getTimeline_widget_field_map(), dashboard_execute_data.getGlobal_timeline_filter_widget_map());

            result_json.put("actionMeta" , actionData);
        }
    },
    URL(2 , "url") {
        @Override
        public void performAction(DashboardRuleContext dashboard_rule , DashboardRuleActionContext dashboard_rule_action, DashboardExecuteMetaContext dashboard_execute_meta, Long trigger_widget_id)throws Exception
        {
            JSONObject action_details = dashboard_rule_action.getAction_meta().getAction_detail();
            if(action_details != null )
            {
                if(action_details != null )
                {
                    JSONObject result_json = new JSONObject();
                    String url = (String) action_details.get("url");
                    DashboardWidgetContext trigger_widget  = DashboardUtil.getWidget(trigger_widget_id);
                    JSONObject placeHoldersMeta = dashboard_execute_meta.getPlaceHoldersMeta();
                    JSONObject placeHolders = dashboard_execute_meta.getPlaceHolders();
                    ArrayList value_arr = null;
                    HashMap widget_value = null;
                    String recordId = null;
                    if(trigger_widget.getLinkName() != null && placeHoldersMeta  != null)
                    {
                        if (placeHolders.containsKey(trigger_widget.getLinkName())) {
                            widget_value = (HashMap) placeHolders.get(trigger_widget.getLinkName());
                            if (widget_value != null && (widget_value.containsKey("value") || widget_value.containsKey("dimension"))) {
                                value_arr = widget_value.containsKey("value") ? (ArrayList) widget_value.get("value") : (ArrayList) widget_value.get("dimension");
                                if (value_arr.size() > 0) {
                                    String value = (String) value_arr.get(0);
                                    if (value != null && !value.equals("") && !value.equals("all") && !value.equals("All") && !value.equals("ALL")) {
                                        recordId = value;
                                    }
                                }
                            }
                        }
                    }
                    V3DashboardAPIHandler.constructDashboardRulePlaceHolders(trigger_widget, dashboard_execute_meta, recordId, value_arr, widget_value);
                    String replaced_url = StringSubstitutor.replace(url, dashboard_execute_meta.getPlaceholder_vs_value_map());
                    replaced_url = StringSubstitutor.replace(replaced_url, dashboard_execute_meta.getGroupby_placeholder_vs_value_map());
                    result_json.put("actionType", "url");
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
        public void performAction(DashboardRuleContext dashboard_rule , DashboardRuleActionContext dashboard_rule_action, DashboardExecuteMetaContext dashboard_execute_meta ,Long trigger_widget_id)throws Exception
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
                    show_section.put("section_ids", section_ids);
                    actionMeta.put("SHOW_SECTIONS", show_section);
                    result_json.put("actionMeta", actionMeta);
                    dashboard_rule.getResult_json().add(result_json);
                }
            }
        }
    },
    HIDE_SECTIONS(4, "hide_sections"){
        @Override
        public void performAction(DashboardRuleContext dashboard_rule , DashboardRuleActionContext dashboard_rule_action, DashboardExecuteMetaContext dashboard_execute_meta, Long trigger_widget_id)throws Exception
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
                    show_section.put("section_ids", section_ids);
                    actionMeta.put("HIDE_SECTIONS", show_section);
                    result_json.put("actionMeta", actionMeta);
                    dashboard_rule.getResult_json().add(result_json);
                }
            }
        }
    },

    SCRIPT_ACTION(5, "script_action"){
        @Override
        public void performAction(DashboardRuleContext dashboard_rule , DashboardRuleActionContext dashboard_rule_action, DashboardExecuteMetaContext dashboard_execute_meta, Long trigger_widget_id)throws Exception
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
                    String recordId = null;
                    DashboardWidgetContext trigger_widget  = DashboardUtil.getWidget(trigger_widget_id);
                    if(trigger_widget.getLinkName() != null && dashboard_execute_meta.getPlaceHoldersMeta() != null )
                    {
                        JSONObject placeHolders =  dashboard_execute_meta.getPlaceHolders();
                        ArrayList value_arr = null;
                        HashMap widget_value = null;
                        if(placeHolders.containsKey(trigger_widget.getLinkName()))
                        {
                            widget_value = (HashMap)  placeHolders.get(trigger_widget.getLinkName());
                            if(widget_value != null && (widget_value.containsKey("value") || widget_value.containsKey("dimension")))
                            {
                                value_arr = widget_value.containsKey("value") ? (ArrayList) widget_value.get("value") : (ArrayList) widget_value.get("dimension");
                                if(value_arr.size() > 0 )
                                {
                                    String value = (String)value_arr.get(0);
                                    if(value != null  && !value.equals("") && !value.equals("all") && !value.equals("All") && !value.equals("ALL"))
                                    {
                                        recordId = value;
                                    }
                                }
                            }
                            V3DashboardAPIHandler.constructDashboardRulePlaceHolders(trigger_widget, dashboard_execute_meta, recordId, value_arr, widget_value);
                        }
                    }
                    dashboard_execute_meta.getPlaceholder_vs_value_map().putAll(dashboard_execute_meta.getGroupby_placeholder_vs_value_map());
                    context.put(WorkflowV2Util.WORKFLOW_PARAMS, Collections.singletonList(dashboard_execute_meta.getPlaceholder_vs_value_map()));
                    chain.execute();
                    if(workflow != null && workflow.getReturnType() == WorkflowFieldType.LIST.getIntValue())
                    {
                        List returnList = (List) workflow.getReturnValue();
                        JSONArray jsonArray = new JSONArray();
                        JSONParser parser = new JSONParser();
                        if(returnList != null && !returnList.isEmpty()) {
                            for(int i=0;i<returnList.size();i++) {
                                Map<String,Object> fieldMap = (Map<String,Object>) returnList.get(i);
                                String object_str = new ObjectMapper().writeValueAsString(fieldMap);
                                JSONObject fieldJSON = (JSONObject)parser.parse(object_str);
                                jsonArray.add(fieldJSON);
                            }
                            dashboard_rule.setResult_json(jsonArray);
                        }
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
        public void performAction(DashboardRuleContext dashboard_rule , DashboardRuleActionContext dashboard_rule_action, DashboardExecuteMetaContext dashboard_execute_meta, Long trigger_widget_id)throws Exception
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
    abstract public void performAction(DashboardRuleContext dashboard_rule, DashboardRuleActionContext action, DashboardExecuteMetaContext dashboard_execute_meta, Long trigger_widget_id) throws Exception;
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
