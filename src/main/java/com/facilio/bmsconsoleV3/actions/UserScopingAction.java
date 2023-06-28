package com.facilio.bmsconsoleV3.actions;

import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.bmsconsole.context.ScopingConfigContext;
import com.facilio.bmsconsole.context.ScopingContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsoleV3.commands.ReadOnlyChainFactoryV3;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.ims.handler.AuditLogHandler;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.function.Function;


@Getter
@Setter
public class UserScopingAction extends FacilioAction {
    private ScopingContext userScoping;
    private List<ScopingConfigContext> userScopingConfigList;
    private Long scopingId;
    private Long moduleId;
    private Long appId;
    private int page;
    private int perPage;
    private String searchQuery;

    private Boolean status = null;


    public String getUserScopingModules() throws Exception {
        FacilioChain chain = ReadOnlyChainFactoryV3.getUserScopingModulesListChain();
        FacilioContext context = chain.getContext();
        chain.execute();
        setResult(FacilioConstants.ContextNames.MODULE_LIST, context.get(FacilioConstants.ContextNames.MODULE_LIST));
        return SUCCESS;
    }

    public String addOrUpdateUserScoping() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.addOrUpdateUserScopingChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.RECORD, userScoping);
        chain.execute();
        Long id = (Long) context.get(FacilioConstants.ContextNames.ID);
        if(userScoping != null && userScoping.getId() > -1) {
            addAuditLogs(userScoping.getId(),"updated");
        } else if(getScopingId() != null) {
            addAuditLogs(getScopingId(),"updated");
        } else {
            addAuditLogs(id,"added");
        }
        setResult(FacilioConstants.ContextNames.SCOPING_CONTEXT, context.get(FacilioConstants.ContextNames.RECORD));
        return SUCCESS;
    }

    public String list() throws Exception {
        FacilioChain chain = ReadOnlyChainFactoryV3.getUserScopingListChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.SEARCH_QUERY, searchQuery);
        context.put(FacilioConstants.ContextNames.PAGE, page);
        context.put(FacilioConstants.ContextNames.PER_PAGE, perPage);
        chain.execute();
        setResult(FacilioConstants.ContextNames.USER_SCOPING_LIST, context.get(FacilioConstants.ContextNames.USER_SCOPING_LIST));
        setResult(FacilioConstants.ContextNames.CREATED_BY, context.get(FacilioConstants.ContextNames.CREATED_BY));
        setResult(FacilioConstants.ContextNames.COUNT, context.get(FacilioConstants.ContextNames.COUNT));
        return SUCCESS;
    }

    public String delete() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.deleteUserScopingChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID, getScopingId());
        addAuditLogs(getScopingId(),"deleted");
        chain.execute();
        return SUCCESS;
    }

    public String updateStatus() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.updateUserScopingStatusChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.SCOPING_ID, scopingId);
        context.put(FacilioConstants.ContextNames.STATUS, status);
        addAuditLogs(getScopingId(),"updated");
        chain.execute();
        return SUCCESS;
    }

    public String getUserScopingConfig() throws Exception {
        FacilioChain chain = ReadOnlyChainFactoryV3.getUserScopingConfigChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.SCOPING_ID, scopingId);
        context.put(FacilioConstants.ContextNames.MODULE_ID, moduleId);
        chain.execute();
        setResult(FacilioConstants.ContextNames.SCOPING_CONFIG_LIST, context.get(FacilioConstants.ContextNames.SCOPING_CONFIG_LIST));
        return SUCCESS;
    }

    public String updateUserScopingConfig() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.addOrUpdateUserScopingConfigChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.RECORD, userScopingConfigList);
        context.put("scopingId", scopingId);
        chain.execute();
        addAuditLogs(getScopingId(),"updated");
        setResult(FacilioConstants.ContextNames.SCOPING_CONFIG_LIST, userScopingConfigList);
        return SUCCESS;

    }

    private void addAuditLogs(Long userScopeId , String action) throws Exception {
        ScopingContext scoping = ApplicationApi.getScoping(userScopeId);
        if(userScopeId != null) {
            AuditLogHandler.ActionType actionType = null;
            if (action.equals("added")) {
                actionType = AuditLogHandler.ActionType.ADD;
            } else if (action.equals("updated")) {
                actionType = AuditLogHandler.ActionType.UPDATE;
            } else if (action.equals("deleted")) {
                actionType = AuditLogHandler.ActionType.DELETE;
            }
            sendAuditLogs(new AuditLogHandler.AuditLogContext(String.format("User Scoping  {%s}  has been %s.", scoping.getScopeName(), action),
                    String.format("User Scoping  %s  has been %s.", scoping.getScopeName(), action),
                    AuditLogHandler.RecordType.SETTING,
                    "User Scoping", scoping.getId())
                    .setActionType(actionType)
                    .setLinkConfig(((Function<Void, String>) o -> {
                        JSONArray array = new JSONArray();
                        JSONObject json = new JSONObject();
                        json.put("id", scoping.getId());
                        json.put("name", scoping.getScopeName());
                        json.put("navigateTo", "Data Sharing");
                        array.add(json);
                        return array.toJSONString();
                    }).apply(null))
            );
        }
    }
}