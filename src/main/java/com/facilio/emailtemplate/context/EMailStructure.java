package com.facilio.emailtemplate.context;

import com.facilio.bmsconsole.templates.Template;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.json.simple.JSONObject;

// need to check the merits of extending Template
public class EMailStructure extends Template {

    private long moduleId = -1l;
    public long getModuleId() {
        return moduleId;
    }
    public void setModuleId(long moduleId) {
        this.moduleId = moduleId;
    }

    private String subject;
    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }

    private long bodyId;
    public long getBodyId() {
        return bodyId;
    }
    public void setBodyId(long bodyId) {
        this.bodyId = bodyId;
    }

    private String message;
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public JSONObject getOriginalTemplate() throws Exception {
        JSONObject obj = new JSONObject();
        obj.put("subject", getSubject());
        obj.put("message", getMessage());
        return obj;
    }

    @Override
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public int getType() {
        return getTypeEnum().getIntVal();
    }

    @Override
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public Type getTypeEnum() {
        return Type.EMAIL_STRUCTURE;
    }
}
