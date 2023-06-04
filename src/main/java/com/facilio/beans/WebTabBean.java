package com.facilio.beans;
import com.facilio.bmsconsole.context.*;

import java.util.List;

public interface WebTabBean {

    public WebTabCacheContext getWebTab(long tabId) throws Exception;

    public WebTabGroupCacheContext getWebTabGroup(long groupId) throws Exception;

    public List<TabIdAppIdMappingCacheContext> getTabIdModules(long tabId) throws Exception;

    public List<WebTabGroupCacheContext> getWebTabGroupForLayoutID(ApplicationLayoutContext layout) throws Exception;

    public List<WebTabCacheContext> getWebTabsForWebGroup(long webTabGroupId) throws Exception;

    public void associateTabGroup(List<WebTabGroupContext> tabGroups) throws Exception;

    public void disassociateTabGroup(List<Long> tabIds , long groupId) throws Exception;

    public List<WebTabCacheContext> getWebTabsForApplication(long appId) throws Exception;

    public void updateWebTab(WebTabContext webTab) throws Exception;

    public void insertIntoTabIdAppIdMappingTable(WebTabContext webTab) throws Exception;

    public long addTab(WebTabContext tab) throws Exception;

    public void deleteTabMappingEntriesForTab(long tabId) throws Exception;

    public void deleteTab(long tabId) throws Exception;

    public void deleteWebTabGroup(long groupId) throws Exception;

    public void updateWebtabWebtabGroup(WebtabWebgroupContext webtabWebgroupContexts) throws Exception;

    public void addWebtabWebtabGroup(WebtabWebgroupContext webtabWebgroupContexts) throws Exception;

    public long addWebTabGroup(WebTabGroupContext tabGroup) throws Exception;

    public void updateWebTabGroup(WebTabGroupContext tabGroup) throws Exception;

    public void deleteTabForGroupCommand(long groupId) throws Exception;

    public void deleteWebTabWebGroupForTabId(long webTabId) throws Exception;

}