package com.facilio.bmsconsole.actions;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.manager.NamedCriteria;
import com.facilio.fw.BeanFactory;
import com.facilio.ims.handler.AuditLogHandler;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.function.Function;

public class NamedCriteriaAction extends FacilioAction {

    private String moduleName;
    public String getModuleName() {
        return moduleName;
    }
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    private NamedCriteria namedCriteria;
    public NamedCriteria getNamedCriteria() {
        return namedCriteria;
    }
    public void setNamedCriteria(NamedCriteria namedCriteria) {
        this.namedCriteria = namedCriteria;
    }

    public String getAllNamedCriteria() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getAllNamedCriteriaChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);

        chain.execute();
        setResult(FacilioConstants.ContextNames.NAMED_CRITERIA_LIST, context.get(FacilioConstants.ContextNames.NAMED_CRITERIA_LIST));
        return SUCCESS;
    }

    public String addOrUpdateNamedCriteria() throws Exception {
        FacilioChain chain = TransactionChainFactory.getAddNamedCriteriaChain();
        FacilioContext context = chain.getContext();

        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.ContextNames.NAMED_CRITERIA, namedCriteria);

        NamedCriteria namedCriteriaLog = FieldUtil.cloneBean(namedCriteria, NamedCriteria.class);
        chain.execute();
        setResult(FacilioConstants.ContextNames.NAMED_CRITERIA, context.get(FacilioConstants.ContextNames.NAMED_CRITERIA));
        addAuditLogs(namedCriteriaLog,moduleName, namedCriteria.getId());
        return SUCCESS;
    }

    private Long id;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String deleteNamedCriteria() throws Exception {
        FacilioChain chain = TransactionChainFactory.deleteNamedCriteriaChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID, id);
        chain.execute();

        NamedCriteria namedCriteria = (NamedCriteria) context.get(FacilioConstants.ContextNames.NAMED_CRITERIA);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        sendAuditLogs(new AuditLogHandler.AuditLogContext(String.format("Condition Manager %s has been deleted for %s module.",namedCriteria.getName(),moduleName),
                null,
                AuditLogHandler.RecordType.SETTING,
                "ConditionManager",namedCriteria.getId())
                .setActionType(AuditLogHandler.ActionType.DELETE)
        );
        return SUCCESS;
    }

    private long recordId = -1;
    public long getRecordId() {
        return recordId;
    }
    public void setRecordId(long recordId) {
        this.recordId = recordId;
    }

    public String evaluateCriteria() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getEvaluateNamedCriteriaChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.ContextNames.RECORD_ID, recordId);
        context.put(FacilioConstants.ContextNames.ID, id);
        chain.execute();

        setResult(FacilioConstants.ContextNames.NAMED_CRITERIA_RESULT, context.get(FacilioConstants.ContextNames.NAMED_CRITERIA_RESULT));

        return SUCCESS;
    }

    private void addAuditLogs(NamedCriteria namedCriteria,String moduleName,long id){

        String type = namedCriteria.getId() > 0 ? "modified" : "created";
        sendAuditLogs(new AuditLogHandler.AuditLogContext(String.format("Condition Manager {%s} has been %s for %s module", namedCriteria.getName(),type, moduleName),
                null,
                AuditLogHandler.RecordType.SETTING,
                "ConditionManager",id)
                .setActionType(namedCriteria.getId() > 0 ? AuditLogHandler.ActionType.UPDATE : AuditLogHandler.ActionType.ADD)
                .setLinkConfig(((Function<Void, String>) o -> {
                    JSONArray array = new JSONArray();
                    JSONObject json = new JSONObject();
                    json.put("id", id);
                    json.put("moduleName", moduleName);
                    json.put("navigateTo", "ConditionManager");
                    array.add(json);
                    return array.toJSONString();
                }).apply(null))
        );
    }
}
