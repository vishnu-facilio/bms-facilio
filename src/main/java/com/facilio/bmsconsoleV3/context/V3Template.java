package com.facilio.bmsconsoleV3.context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.templates.Template;
import com.facilio.bmsconsole.util.FreeMarkerAPI;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import org.apache.commons.text.StringSubstitutor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class V3Template implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LogManager.getLogger(Template.class.getName());
    private Long id;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    private Long orgId;
    public Long getOrgId() {
        return orgId;
    }
    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    private String name;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    private Long workflowId;
    public Long getWorkflowId() {
        return workflowId;
    }
    public void setWorkflowId(Long workflowId) {
        this.workflowId = workflowId;
    }

    private WorkflowContext workflow;
    public WorkflowContext getWorkflow() {
        return workflow;
    }
    public void setWorkflow(WorkflowContext workflow) {
        this.workflow = workflow;
    }

    private JSONArray placeholder;
    public JSONArray getPlaceholder() {
        return placeholder;
    }
    public void setPlaceholder(JSONArray placeholder) {
        this.placeholder = placeholder;
    }

    public String getPlaceholderStr() {
        if(placeholder != null) {
            return placeholder.toJSONString();
        }
        return null;
    }
    public void setPlaceholderStr(String placeholderStr) throws ParseException {
        if(placeholderStr != null) {
            JSONParser parser = new JSONParser();
            placeholder = (JSONArray) parser.parse(placeholderStr);
        }
    }

    private V3Template.Type type;
    public Integer getType() {
        if(type != null) {
            return type.getIntVal();
        }
        return null;
    }
    public V3Template.Type getTypeEnum() {
        return this.type;
    }
    public void setType(Integer type) {
        if(type != null) {
            this.type = V3Template.Type.TYPE_MAP.get(type);
        }
    }

    private Boolean ftl;
    public Boolean getFtl() {
        return ftl;
    }
    public void setFtl(Boolean ftl) {
        this.ftl = ftl;
    }
    public boolean isFtl() {
        if (ftl != null) {
            return ftl.booleanValue();
        }
        return false;
    }

    public final JSONObject getTemplate(Map<String, Object> parameters) throws Exception {
        JSONObject json = getOriginalTemplate();
        if (json != null && workflow != null) {
            Map<String, Object> params;
            if (workflow.isV2Script()) {
                params = (Map<String, Object>) WorkflowUtil.getWorkflowExpressionResult(workflow, parameters);
            }
            else {
                params = WorkflowUtil.getExpressionResultMap(workflow, parameters);
            }

            JSONObject parsedJson = null;
            if (isFtl()) {
                // StrSubstitutor.replace(jsonStr, params);
                parsedJson = new JSONObject();
                for (Object key : json.keySet()) {
                    Object value = json.get(key);
                    if (value != null) {
                        if (value instanceof JSONArray) {
                            JSONArray newArray = new JSONArray();
                            for(Object arrayVal: (JSONArray)value) {
                                newArray.add(FreeMarkerAPI.processTemplate(arrayVal.toString(), params));
                            }
                            parsedJson.put(key, newArray);
                        }
                        else {
                            parsedJson.put(key, FreeMarkerAPI.processTemplate(value.toString(), params));
                        }
                    }
                }
                parameters.put("mailType", "html");
            }
            else {
                String jsonStr = json.toJSONString();
                if (AccountUtil.getCurrentOrg().getId() == 186 || getId() == 334300) {
                    LOGGER.info("JSON : "+jsonStr);
                    LOGGER.info("Params : "+params);
                }
                try {
                    jsonStr = StringSubstitutor.replace(jsonStr, params);// StrSubstitutor.replace(jsonStr, params);
                }
                catch (Exception e) {
                    LOGGER.error(new StringBuilder("Error occurred during replacing of place holders \n")
                            .append("JSON : ")
                            .append(jsonStr)
                            .append("\nParams : ")
                            .append(params)
                            .append("\nParameters : ")
                            .append(parameters)
                            .toString(), e);
                    throw e;
                }
                JSONParser parser = new JSONParser();
                parsedJson = (JSONObject) parser.parse(jsonStr);
            }

            return parsedJson;
        }
        return json;
    }

    public abstract JSONObject getOriginalTemplate() throws Exception;

    public static enum Type {
        DEFAULT(0),
        EMAIL(1),
        SMS(2),
        JSON(3),
        EXCEL(4),
        WORKORDER(5),
        ALARM(6),
        TASK_GROUP(7),
        PUSH_NOTIFICATION(8),
        WEB_NOTIFICATION(9),
        ASSIGNMENT(10),
        SLA(11),
        TASK_GROUP_TASK(12),
        PM_WORKORDER(13),
        PM_TASK(14),
        PM_TASK_SECTION(15),
        WO_TASK(16),
        WO_TASK_SECTION(17),
        WORKFLOW(18),
        CONTROL_ACTION(19),
        JOB_PLAN_TASK(20),
        JOB_PLAN_SECTION(21),
        PM_PRE_REQUEST(22),
        PM_PRE_REQUEST_SECTION(23),
        PM_PREREQUISITE_APPROVER(24),
        FORM(25),
        WHATSAPP(26),
        CALL(27),
        ;


        private int intVal;

        private Type(int intVal) {
            this.intVal = intVal;
        }

        public int getIntVal() {
            return intVal;
        }

        private static final Map<Integer, V3Template.Type> TYPE_MAP = Collections.unmodifiableMap(initTypeMap());
        private static Map<Integer, V3Template.Type> initTypeMap() {
            Map<Integer, V3Template.Type> typeMap = new HashMap<>();

            for(V3Template.Type type : values()) {
                typeMap.put(type.getIntVal(), type);
            }
            return typeMap;
        }
        public static Map<Integer, V3Template.Type> getAllTypes() {
            return TYPE_MAP;
        }

        public static V3Template.Type getType(int val) throws RESTException {
            if(TYPE_MAP.containsKey(val)) {
                return TYPE_MAP.get(val);
            }
            else {
                throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid Template Type val");
            }
        }
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        try {
            return getOriginalTemplate() == null ? "null" : getOriginalTemplate().toJSONString();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
