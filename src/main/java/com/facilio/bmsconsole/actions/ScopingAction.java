package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.ScopingContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.ims.handler.AuditLogHandler;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.function.Function;

public class ScopingAction extends FacilioAction{

    private ScopingContext scopingContext;

    public ScopingContext getScopingContext() {
        return scopingContext;
    }

    public void setScopingContext(ScopingContext scopingContext) {
        this.scopingContext = scopingContext;
    }

    private Long scopingId;
    public Long getScopingId() {
        return scopingId;
    }
    public void setScopingId(Long scopingId) {
        this.scopingId = scopingId;
    }

    private Long appId;

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public String addOrUpdateScopingConfig() throws Exception {
        FacilioChain c = TransactionChainFactory.addOrUpdateScopingConfigChain();
        c.getContext().put(FacilioConstants.ContextNames.SCOPING_CONTEXT, scopingContext);
        c.execute();
        setResult(FacilioConstants.ContextNames.SCOPING_CONTEXT, c.getContext().get(FacilioConstants.ContextNames.SCOPING_CONTEXT));
        return SUCCESS;

    }

    public String addOrUpdateScoping() throws Exception {
        FacilioChain c = TransactionChainFactory.addOrUpdateScopingChain();
        c.getContext().put(FacilioConstants.ContextNames.SCOPING_CONTEXT, scopingContext);
        if(scopingContext.getId() <= 0) {
            addAuditLogs(scopingContext,"added");
        }else{
            addAuditLogs(scopingContext,"updated");
        }
        c.execute();
        setResult(FacilioConstants.ContextNames.SCOPING_CONTEXT, c.getContext().get(FacilioConstants.ContextNames.SCOPING_CONTEXT));
        return SUCCESS;

    }

    public String list() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getAllScopingConfigChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.SCOPING_ID, scopingId);
        chain.execute();

        setResult(FacilioConstants.ContextNames.SCOPING_CONTEXT, context.get(FacilioConstants.ContextNames.SCOPING_CONTEXT));
        return SUCCESS;
    }

    public String listAppScoping() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getScopingListForAppChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.APPLICATION_ID, appId);
        chain.execute();

        setResult(FacilioConstants.ContextNames.SCOPING_CONTEXT_LIST, context.get(FacilioConstants.ContextNames.SCOPING_CONTEXT_LIST));
        return SUCCESS;
    }

    public String listValueGenerators() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getValueGeneratorsList();
        FacilioContext context = chain.getContext();
        chain.execute();

        setResult(FacilioConstants.ContextNames.VALUE_GENERATORS, context.get(FacilioConstants.ContextNames.VALUE_GENERATORS));
        return SUCCESS;
    }

    public String delete() throws Exception {
        FacilioChain chain = TransactionChainFactory.getDeleteScopingChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.SCOPING_ID, scopingId);
        ScopingContext scoping = ApplicationApi.getScoping(scopingId);
        if(scoping != null) {
            addAuditLogs(scoping, "deleted");
        }
        chain.execute();
        setResult(FacilioConstants.ContextNames.SCOPING_ID, scopingId);
        return SUCCESS;
    }

    public String cloneScoping() throws Exception {
        FacilioChain chain = TransactionChainFactory.getCloneScopingChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.SCOPING_ID, scopingId);
        chain.execute();
        ScopingContext scoping = ApplicationApi.getScoping(scopingId);
        if(scoping != null) {
            addAuditLogs(scoping, "cloned");
        }
        setResult(FacilioConstants.ContextNames.SCOPING_ID, context.get(FacilioConstants.ContextNames.SCOPING_ID));
        return SUCCESS;
    }

    private void addAuditLogs(ScopingContext scoping,String action) throws Exception {
        ApplicationContext app = ApplicationApi.getApplicationForId(scoping.getApplicationId());
        AuditLogHandler.ActionType actionType;
        if(action.equals("added") || action.equals("cloned")){
            actionType = AuditLogHandler.ActionType.ADD;
        }else if(action.equals("updated")) {
            actionType = AuditLogHandler.ActionType.UPDATE;
        }
        else if(action.equals("deleted")) {
            actionType = AuditLogHandler.ActionType.DELETE;
        }
        sendAuditLogs(new AuditLogHandler.AuditLogContext(String.format("User scoping {%s} for '%s' application has been %s.", scoping.getScopeName(),  app.getName(), action),
            String.format("User scoping %s for '%s' application has been %s.", scoping.getScopeName(),  app.getName(), action),
            AuditLogHandler.RecordType.SETTING,
            "Scoping", scoping.getId())
            .setActionType(AuditLogHandler.ActionType.ADD)
            .setLinkConfig(((Function<Void, String>) o -> {
                JSONArray array = new JSONArray();
                JSONObject json = new JSONObject();
                json.put("id", scoping.getId());
                json.put("appId", scoping.getApplicationId());
                json.put("navigateTo", "Scoping");
                array.add(json);
                return array.toJSONString();
            }).apply(null))
        );
    }
}
