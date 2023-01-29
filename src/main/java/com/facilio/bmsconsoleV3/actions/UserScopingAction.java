package com.facilio.bmsconsoleV3.actions;

import com.facilio.bmsconsole.context.ScopingConfigContext;
import com.facilio.bmsconsole.context.ScopingContext;
import com.facilio.bmsconsoleV3.commands.ReadOnlyChainFactoryV3;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.V3Action;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class UserScopingAction extends V3Action {
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
        setData(FacilioConstants.ContextNames.MODULE_LIST, context.get(FacilioConstants.ContextNames.MODULE_LIST));
        return SUCCESS;
    }

    public String addOrUpdateUserScoping() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.addOrUpdateUserScopingChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.RECORD, userScoping);
        chain.execute();
        setData(FacilioConstants.ContextNames.SCOPING_CONTEXT, context.get(FacilioConstants.ContextNames.RECORD));
        return SUCCESS;
    }

    public String list() throws Exception {
        FacilioChain chain = ReadOnlyChainFactoryV3.getUserScopingListChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.APP_ID, appId);
        context.put(FacilioConstants.ContextNames.SEARCH_QUERY, searchQuery);
        context.put(FacilioConstants.ContextNames.PAGE, page);
        context.put(FacilioConstants.ContextNames.PER_PAGE, perPage);
        chain.execute();
        setData(FacilioConstants.ContextNames.USER_SCOPING_LIST, context.get(FacilioConstants.ContextNames.USER_SCOPING_LIST));
        setMeta(FacilioConstants.ContextNames.CREATED_BY, context.get(FacilioConstants.ContextNames.CREATED_BY));
        setMeta(FacilioConstants.ContextNames.COUNT, context.get(FacilioConstants.ContextNames.COUNT));
        return SUCCESS;
    }

    public String delete() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.deleteUserScopingChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID, getScopingId());
        chain.execute();
        return SUCCESS;
    }

    public String updateStatus() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.updateUserScopingStatusChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.SCOPING_ID, scopingId);
        context.put(FacilioConstants.ContextNames.STATUS, status);
        chain.execute();
        return SUCCESS;
    }

    public String getUserScopingConfig() throws Exception {
        FacilioChain chain = ReadOnlyChainFactoryV3.getUserScopingConfigChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.SCOPING_ID, scopingId);
        context.put(FacilioConstants.ContextNames.MODULE_ID, moduleId);
        chain.execute();
        setData(FacilioConstants.ContextNames.SCOPING_CONFIG_LIST, context.get(FacilioConstants.ContextNames.SCOPING_CONFIG_LIST));
        return SUCCESS;
    }

    public String updateUserScopingConfig() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.addOrUpdateUserScopingConfigChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.RECORD, userScopingConfigList);
        context.put("scopingId", scopingId);
        chain.execute();
        setData(FacilioConstants.ContextNames.SCOPING_CONFIG_LIST, userScopingConfigList);
        return SUCCESS;

    }
}
