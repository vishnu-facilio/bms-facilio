package com.facilio.bmsconsole.actions;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.context.SetupLayout;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.List;
@Getter @Setter
public class NewUserListAction extends FacilioAction{
    private long appId;
    private int page;
    private int perPage = -1;

    private SetupLayout setup;

    private List<User> users = null;

    private String filters;

    private List<Long> defaultIds;

    private String search;

    private boolean inviteAcceptStatus = true;

    public String newUserList() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getUserList();
        FacilioContext context = chain.getContext();
        setSetup(SetupLayout.getUsersListLayout());
        JSONObject pagination = new JSONObject();
        pagination.put("page", page);
        pagination.put("perPage", perPage);
        context.put(FacilioConstants.ContextNames.PAGINATION, pagination);
        context.put(FacilioConstants.ContextNames.DEFAULT_IDS,defaultIds);
        context.put(FacilioConstants.ContextNames.MODULE_NAME,"people");
        if(getSearch() != null)
        {
            context.put(FacilioConstants.ContextNames.SEARCH, getSearch());
        }
        context.put(FacilioConstants.ContextNames.INVITE_ACCEPT_STATUS,inviteAcceptStatus);
        if(getFilters() != null)
        {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(getFilters());
            context.put(FacilioConstants.ContextNames.FILTERS, json);
        }

        //If appId not set then showing only service portal user list
        if(appId <= 0){
            appId = AccountUtil.getCurrentApp().getId();
        }
        context.put(FacilioConstants.ContextNames.APP_ID, appId);
        chain.execute();
        setUsers((List<User>) context.get(FacilioConstants.ContextNames.USERS));
        return SUCCESS;
        }

}
