package com.facilio.emailtemplate.context;

import com.facilio.bmsconsole.templates.BaseMailTemplate;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.json.simple.JSONObject;

// need to check the merits of extending Template
public class EMailStructure extends BaseMailTemplate {

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

    private long sysCreatedTime = -1;
    public void setSysCreatedTime(long sysCreatedTime) {
        this.sysCreatedTime = sysCreatedTime;
    }
    public long getSysCreatedTime() {
        return sysCreatedTime;
    }

    private long sysModifiedTime = -1;
    public void setSysModifiedTime(long sysModifiedTime) {
        this.sysModifiedTime = sysModifiedTime;
    }
    public long getSysModifiedTime() {
        return sysModifiedTime;
    }

    private long sysCreatedBy ;

    public void setSysCreatedBy(long sysCreatedBy) {
        this.sysCreatedBy = sysCreatedBy;
    }

    public long getSysCreatedBy() {
        return sysCreatedBy;
    }

    private long sysModifiedBy;

    public void setSysModifiedBy(long sysModifiedBy) {
        this.sysModifiedBy = sysModifiedBy;
    }

    public long getSysModifiedBy() {
        return sysModifiedBy;
    }

    private Boolean draft;

    public Boolean isDraft(){
        if(draft == null){
            return false;
        }
        return draft;
    }
    public Boolean getDraft() {
        return draft;
    }

    public void setDraft(Boolean draft) {
        this.draft = draft;
    }

    private String message;
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    private Boolean html;
    public Boolean getHtml() {
        return html;
    }
    public void setHtml(Boolean html) {
        this.html = html;
    }
    public Boolean isHtml() {
        if (html != null) {
            return html.booleanValue();
        }
        return false;
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
