package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ApplicationLayoutContext;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.context.WebTabGroupContext;
import com.facilio.bmsconsole.context.WebtabWebgroupContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

import java.util.List;

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

    private long layoutId;

    public long getLayoutId() {
        return layoutId;
    }

    public void setLayoutId(long layoutId) {
        this.layoutId = layoutId;
    }

    private WebTabGroupContext tabGroup;
    public WebTabGroupContext getTabGroup() {
        return tabGroup;
    }
    public void setTabGroup(WebTabGroupContext tabGroup) {
        this.tabGroup = tabGroup;
    }

    private ApplicationLayoutContext layout;

    public ApplicationLayoutContext getLayout() {
        return layout;
    }

    public void setLayout(ApplicationLayoutContext layout) {
        this.layout = layout;
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
        context.put(FacilioConstants.ContextNames.LAYOUT_ID, layoutId);

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

    public String getTabListForApplication() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getAllTabForApplicationChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.APPLICATION_ID, getAppId());
        chain.execute();

        setResult(FacilioConstants.ContextNames.WEB_TABS, context.get(FacilioConstants.ContextNames.WEB_TABS));

        return SUCCESS;
    }

    public String associateTabToGroupTab() throws Exception {
        FacilioChain chain = TransactionChainFactory.getCreateAndAssociateTabGroupChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.WEB_TABS,  getTabList());
        context.put(FacilioConstants.ContextNames.WEB_TAB_GROUP_ID, getTabGroupId());
        chain.execute();
        return SUCCESS;
    }

    public String disAssociateTabToGroupTab() throws Exception {
        FacilioChain chain = TransactionChainFactory.getDisAssociateTabGroupChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.WEB_TABS, getTabList());
        context.put(FacilioConstants.ContextNames.WEB_TAB_GROUP_ID, getTabGroupId());
        chain.execute();
        return SUCCESS;
    }

    public String addOrUpdateApplicationLayout() throws Exception {
        FacilioChain chain = TransactionChainFactory.getAddOrUpdateApplicationLayoutChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.APPLICATION_LAYOUT, getLayout());
        chain.execute();
        return SUCCESS;
    }

    public String getApplicationLayoutList() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getAllApplicationLayoutChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.APP_ID, appId);

        chain.execute();

        setResult(FacilioConstants.ContextNames.APPLICATION_LAYOUT, context.get(FacilioConstants.ContextNames.APPLICATION_LAYOUT));
        return SUCCESS;
    }




}
