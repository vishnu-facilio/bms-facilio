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
import lombok.Getter;
import lombok.Setter;

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

    @Getter
    @Setter
    private boolean filterSetUpTab=false;
    private long layoutId;

    public long getLayoutId() {
        return layoutId;
    }

    public void setLayoutId(long layoutId) {
        this.layoutId = layoutId;
    }

    private Long roleId;
    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
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

    @Getter
    @Setter
    private boolean fetchSetupTabs=false;

    @Getter @Setter
    private boolean checkBool = false;

    public String addOrUpdateTabGroup() throws Exception {
        FacilioChain chain = TransactionChainFactory.getAddOrUpdateTabGroup();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.WEB_TAB_GROUP, tabGroup);

        chain.execute();
        setResult(FacilioConstants.ContextNames.WEB_TAB_GROUP, context.get(FacilioConstants.ContextNames.WEB_TAB_GROUP));
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
    
    public String reorderTabGroup() throws Exception {
    	FacilioChain chain = TransactionChainFactory.getReorderTabGroupChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.WEB_TAB_GROUPS, groupList);
        chain.execute();
    	return SUCCESS;
    }
    
	private List<WebTabGroupContext> groupList;

	public List<WebTabGroupContext> getGroupList() {
		return groupList;
	}		
	
	public void setGroupList(List<WebTabGroupContext> groupList) {
		this.groupList = groupList;
	}
	
	private List<WebtabWebgroupContext> tabsGroupsList;

	public List<WebtabWebgroupContext> getTabsGroupsList() {
		return tabsGroupsList;
	}

	public void setTabsGroupsList(List<WebtabWebgroupContext> tabsGroupsList) {
		this.tabsGroupsList = tabsGroupsList;
	}
	

    private WebTabContext tab;
    public WebTabContext getTab() {
        return tab;
    }
    public void setTab(WebTabContext tab) {
        this.tab = tab;
    }
    
    private List<WebTabContext> tabList;
    
    public List<WebTabContext> getTabList() {
		return tabList;
	}

    public void setTabList(List<WebTabContext> tabList) {
		this.tabList = tabList;
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
        setResult(FacilioConstants.ContextNames.WEB_TAB, context.get(FacilioConstants.ContextNames.WEB_TAB));
        return SUCCESS;
    }

    public String deleteTab() throws Exception {
        FacilioChain chain = TransactionChainFactory.getDeleteTabChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID, getId());
        chain.execute();

        return SUCCESS;
    }

    public String getTabListForGroup() throws Exception {
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
        context.put(FacilioConstants.ContextNames.FILTER_SET_UP_TAP,isFilterSetUpTab());
        context.put(FacilioConstants.ContextNames.FETCH_SETUP_TABS,isFetchSetupTabs());
        context.put(FacilioConstants.ContextNames.CHECK_BOOL,isCheckBool());
        context.put(FacilioConstants.ContextNames.ROLE_ID,roleId);
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

    public String addTabsToGroup() throws Exception {
        FacilioChain chain = TransactionChainFactory.getUpdateTabsToGroupChain();
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
    
    public String reorderTab() throws Exception {
    	FacilioChain chain = TransactionChainFactory.getReorderTabChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.WEB_TAB_WEB_GROUP, tabsGroupsList);
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
