package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.context.WebTabGroupContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class WebTabAction extends FacilioAction {

    private long id = -1;
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    private long appId = -1;
    public long getAppId() {
        return appId;
    }
    public void setAppId(long appId) {
        this.appId = appId;
    }

    private WebTabGroupContext tabGroup;
    public WebTabGroupContext getTabGroup() {
        return tabGroup;
    }
    public void setTabGroup(WebTabGroupContext tabGroup) {
        this.tabGroup = tabGroup;
    }

    public String addOrUpdateTabGroup() throws Exception {
        FacilioChain chain = TransactionChainFactory.getAddOrUpdateTabGroup();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.WEB_TAB_GROUP, tabGroup);

        chain.execute();
        return SUCCESS;
    }

    public String getAllWebTabGroup() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getAllWebTabGroupChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.APP_ID, appId);
        chain.execute();

        setResult(FacilioConstants.ContextNames.WEB_TAB_GROUPS, context.get(FacilioConstants.ContextNames.WEB_TAB_GROUPS));
        return SUCCESS;
    }

    public String deleteTabGroup() throws Exception {
        FacilioChain chain = TransactionChainFactory.getDeleteTabGroupChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID, getId());
        chain.execute();

        return SUCCESS;
    }

    private WebTabContext tab;
    public WebTabContext getTab() {
        return tab;
    }
    public void setTab(WebTabContext tab) {
        this.tab = tab;
    }

    private Long tabGroupId;
    public Long getTabGroupId() {
        return tabGroupId;
    }
    public void setTabGroupId(Long tabGroupId) {
        this.tabGroupId = tabGroupId;
    }

    public String addOrUpdateTab() throws Exception {
        FacilioChain chain = TransactionChainFactory.getAddOrUpdateTabChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.WEB_TAB, getTab());
        context.put(FacilioConstants.ContextNames.NEW_PERMISSIONS, tab.getPermissions());
        chain.execute();
        return SUCCESS;
    }

    public String deleteTab() throws Exception {
        FacilioChain chain = TransactionChainFactory.getDeleteTabChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID, getId());
        chain.execute();

        return SUCCESS;
    }

    public String getTabList() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getAllTabChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.WEB_TAB_GROUP_ID, getTabGroupId());
        chain.execute();

        setResult(FacilioConstants.ContextNames.WEB_TABS, context.get(FacilioConstants.ContextNames.WEB_TABS));

        return SUCCESS;
    }
}
