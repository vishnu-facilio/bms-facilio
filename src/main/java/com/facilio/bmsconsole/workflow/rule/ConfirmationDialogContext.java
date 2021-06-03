package com.facilio.bmsconsole.workflow.rule;

import com.facilio.db.criteria.manager.NamedCriteria;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.workflows.context.WorkflowContext;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.text.StringSubstitutor;
import org.apache.struts2.json.annotations.JSON;

import java.util.Collections;
import java.util.Map;

public class ConfirmationDialogContext {
    private long id = -1;
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    private long parentId = -1;
    public long getParentId() {
        return parentId;
    }
    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    private String name;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    private String message;
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    private NamedCriteria namedCriteria;
    public NamedCriteria getNamedCriteria() {
        return namedCriteria;
    }
    public void setNamedCriteria(NamedCriteria namedCriteria) {
        this.namedCriteria = namedCriteria;
    }

    private long namedCriteriaId = -1;
    public long getNamedCriteriaId() {
        return namedCriteriaId;
    }
    public void setNamedCriteriaId(long namedCriteriaId) {
        this.namedCriteriaId = namedCriteriaId;
    }

    private long messagePlaceHolderScriptId = -1;
    public long getMessagePlaceHolderScriptId() {
        return messagePlaceHolderScriptId;
    }
    public void setMessagePlaceHolderScriptId(long messagePlaceHolderScriptId) {
        this.messagePlaceHolderScriptId = messagePlaceHolderScriptId;
    }

    private WorkflowContext messagePlaceHolderScript;
    public WorkflowContext getMessagePlaceHolderScript() {
        return messagePlaceHolderScript;
    }
    public void setMessagePlaceHolderScript(WorkflowContext messagePlaceHolderScript) {
        this.messagePlaceHolderScript = messagePlaceHolderScript;
    }

    @JsonIgnore
    @JSON(serialize = false)
    public String getResolvedMessage(ModuleBaseWithCustomFields moduleData) throws Exception {
        if (messagePlaceHolderScript != null) {
            if(moduleData != null) {
                messagePlaceHolderScript.setParams(Collections.singletonList(FieldUtil.getAsProperties(moduleData)));
            }
            Object o = messagePlaceHolderScript.executeWorkflow();
            if (o instanceof Map) {
                return StringSubstitutor.replace(message, (Map) o);
            }
        }
        return message;
    }
}
