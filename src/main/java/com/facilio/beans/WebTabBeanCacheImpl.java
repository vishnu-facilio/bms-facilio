package com.facilio.beans;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.*;
import com.facilio.cache.CacheUtil;
import com.facilio.fw.cache.FWLRUCaches;
import com.facilio.fw.cache.FacilioCache;
import com.facilio.fw.cache.LRUCache;

import java.util.List;

public class WebTabBeanCacheImpl extends WebTabBeanImpl implements WebTabBean {

    @Override
    public WebTabCacheContext getWebTab(long tabId) throws Exception {
        FacilioCache<String, WebTabCacheContext> webTabCache = LRUCache.getWebTabCache();
        String key = CacheUtil.ORG_TAB_KEY(AccountUtil.getCurrentOrg().getId(), tabId);
        return FWLRUCaches.Util.genericGetFromCacheAndHandleMissLogic(webTabCache, key, () -> {
            return super.getWebTab(tabId);
        });
    }

    @Override
    public List<TabIdAppIdMappingCacheContext> getTabIdModules(long tabId) throws Exception {
        FacilioCache<String, List<TabIdAppIdMappingCacheContext>> tabAppModuleCache = LRUCache.getTabAppModuleCache();
        String key = CacheUtil.ORG_TAB_KEY(AccountUtil.getCurrentOrg().getId(), tabId);
        return FWLRUCaches.Util.genericGetFromCacheAndHandleMissLogic(tabAppModuleCache, key, () -> {
            return super.getTabIdModules(tabId);
        });
    }

    @Override
    public List<WebTabGroupCacheContext> getWebTabGroupForLayoutID(ApplicationLayoutContext layout) throws Exception {
        FacilioCache<String, List<WebTabGroupCacheContext>> tabGroupCache = LRUCache.getWebTabGroupCache();
        String key = CacheUtil.ORG_APP_LAYOUT_KEY(AccountUtil.getCurrentOrg().getId(), layout.getId());
        return FWLRUCaches.Util.genericGetFromCacheAndHandleMissLogic(tabGroupCache, key, () -> {
            return super.getWebTabGroupForLayoutID(layout);
        });
    }

    @Override
    public List<WebTabCacheContext> getWebTabsForWebGroup(long webTabGroupId) throws Exception {
        FacilioCache<String, List<WebTabCacheContext>> tabsCache = LRUCache.getWebTabsCache();
        String key = CacheUtil.ORG_TAB_GROUP_KEY(AccountUtil.getCurrentOrg().getId(), webTabGroupId);
        return FWLRUCaches.Util.genericGetFromCacheAndHandleMissLogic(tabsCache, key, () -> {
            return super.getWebTabsForWebGroup(webTabGroupId);
        });
    }

    @Override
    public List<WebTabCacheContext> getWebTabsForApplication(long appId) throws Exception {
        FacilioCache<String, List<WebTabCacheContext>> tabsCache = LRUCache.getWebTabsCache();
        String key = CacheUtil.ORG_APP_KEY(AccountUtil.getCurrentOrg().getId(), appId);
        return FWLRUCaches.Util.genericGetFromCacheAndHandleMissLogic(tabsCache, key, () -> {
            return super.getWebTabsForApplication(appId);
        });
    }

    @Override
    public void updateWebTab(WebTabContext webTab) throws Exception {
        WebTabContext webTabContext = getWebTab(webTab.getId());
        FacilioCache<String, List<WebTabCacheContext>> tabsCache = LRUCache.getWebTabsCache();
        FacilioCache<String, WebTabCacheContext> tabCache = LRUCache.getWebTabCache();
        FacilioCache<String, List<TabIdAppIdMappingCacheContext>> tabAppModuleCache = LRUCache.getTabAppModuleCache();
        super.updateWebTab(webTab);

        tabsCache.removeStartsWith(CacheUtil.ORG_KEY(AccountUtil.getCurrentOrg().getId()));
        tabCache.remove(CacheUtil.ORG_TAB_KEY(AccountUtil.getCurrentOrg().getId(), webTabContext.getId()));
        tabAppModuleCache.remove(CacheUtil.ORG_TAB_KEY(AccountUtil.getCurrentOrg().getId(), webTabContext.getId()));

    }

    @Override
    public void deleteTabMappingEntriesForTab(long tabId) throws Exception {
        WebTabContext webTabContext = getWebTab(tabId);
        FacilioCache<String, List<WebTabCacheContext>> tabsCache = LRUCache.getWebTabsCache();
        FacilioCache<String, WebTabCacheContext> tabCache = LRUCache.getWebTabCache();
        FacilioCache<String, List<TabIdAppIdMappingCacheContext>> tabAppModuleCache = LRUCache.getTabAppModuleCache();
        super.deleteTabMappingEntriesForTab(tabId);

        tabsCache.removeStartsWith(CacheUtil.ORG_KEY(AccountUtil.getCurrentOrg().getId()));
        tabCache.remove(CacheUtil.ORG_TAB_KEY(AccountUtil.getCurrentOrg().getId(), webTabContext.getId()));
        tabAppModuleCache.remove(CacheUtil.ORG_TAB_KEY(AccountUtil.getCurrentOrg().getId(), webTabContext.getId()));

    }

    @Override
    public void deleteTab(long tabId) throws Exception {
        WebTabContext webTabContext = getWebTab(tabId);
        FacilioCache<String, List<WebTabCacheContext>> tabsCache = LRUCache.getWebTabsCache();
        FacilioCache<String, WebTabCacheContext> tabCache = LRUCache.getWebTabCache();
        FacilioCache<String, List<TabIdAppIdMappingCacheContext>> tabAppModuleCache = LRUCache.getTabAppModuleCache();
        super.deleteTab(tabId);

        tabsCache.removeStartsWith(CacheUtil.ORG_KEY(AccountUtil.getCurrentOrg().getId()));
        tabCache.remove(CacheUtil.ORG_TAB_KEY(AccountUtil.getCurrentOrg().getId(), webTabContext.getId()));
        tabAppModuleCache.remove(CacheUtil.ORG_TAB_KEY(AccountUtil.getCurrentOrg().getId(), webTabContext.getId()));
    }

    @Override
    public void deleteWebTabGroup(long groupId) throws Exception {
        WebTabGroupContext tabGroupContext = getWebTabGroup(groupId);
        FacilioCache<String, List<WebTabCacheContext>> tabsCache = LRUCache.getWebTabsCache();
        FacilioCache<String, List<WebTabGroupCacheContext>> tabGroupCache = LRUCache.getWebTabGroupCache();
        String key = CacheUtil.ORG_TAB_GROUP_KEY(AccountUtil.getCurrentOrg().getId(), groupId);
        super.deleteWebTabGroup(groupId);
        tabsCache.remove(key);
        tabGroupCache.remove(CacheUtil.ORG_APP_LAYOUT_KEY(AccountUtil.getCurrentOrg().getId(), tabGroupContext.getLayoutId()));
    }

    @Override
    public void deleteTabForGroupCommand(long groupId) throws Exception {
        WebTabGroupContext tabGroupContext = getWebTabGroup(groupId);
        FacilioCache<String, List<WebTabCacheContext>> tabsCache = LRUCache.getWebTabsCache();
        FacilioCache<String, List<WebTabGroupCacheContext>> tabGroupCache = LRUCache.getWebTabGroupCache();
        String key = CacheUtil.ORG_TAB_GROUP_KEY(AccountUtil.getCurrentOrg().getId(), groupId);
        super.deleteTabForGroupCommand(groupId);
        tabsCache.remove(key);
        tabGroupCache.remove(CacheUtil.ORG_APP_LAYOUT_KEY(AccountUtil.getCurrentOrg().getId(), tabGroupContext.getLayoutId()));
    }

    @Override
    public void updateWebtabWebtabGroup(WebtabWebgroupContext webtabWebgroupContext) throws Exception {
        FacilioCache<String, List<WebTabCacheContext>> tabsCache = LRUCache.getWebTabsCache();
        String key = CacheUtil.ORG_TAB_GROUP_KEY(AccountUtil.getCurrentOrg().getId(), webtabWebgroupContext.getWebTabGroupId());
        super.updateWebtabWebtabGroup(webtabWebgroupContext);
        tabsCache.remove(key);
    }

    @Override
    public long addWebTabGroup(WebTabGroupContext tabGroup) throws Exception {
        FacilioCache<String, List<WebTabGroupCacheContext>> tabGroupCache = LRUCache.getWebTabGroupCache();
        long id = super.addWebTabGroup(tabGroup);
        tabGroupCache.remove(CacheUtil.ORG_APP_LAYOUT_KEY(AccountUtil.getCurrentOrg().getId(), tabGroup.getLayoutId()));
        return id;
    }

    @Override
    public void updateWebTabGroup(WebTabGroupContext tabGroup) throws Exception {
        WebTabGroupContext tabGroupContext = getWebTabGroup(tabGroup.getId());
        FacilioCache<String, List<WebTabGroupCacheContext>> tabGroupCache = LRUCache.getWebTabGroupCache();
        String key = CacheUtil.ORG_APP_LAYOUT_KEY(AccountUtil.getCurrentOrg().getId(), tabGroupContext.getLayoutId());
        super.updateWebTabGroup(tabGroup);
        tabGroupCache.remove(key);
    }


    @Override
    public void disassociateTabGroup(List<Long> tabIds, long groupId) throws Exception {
        WebTabGroupContext tabGroupContext = getWebTabGroup(groupId);
        FacilioCache<String, List<WebTabGroupCacheContext>> tabGroupCache = LRUCache.getWebTabGroupCache();
        FacilioCache<String, List<WebTabCacheContext>> tabsCache = LRUCache.getWebTabsCache();
        String key = CacheUtil.ORG_TAB_GROUP_KEY(AccountUtil.getCurrentOrg().getId(), groupId);
        super.disassociateTabGroup(tabIds,groupId);
        tabsCache.remove(key);
        tabGroupCache.remove(CacheUtil.ORG_APP_LAYOUT_KEY(AccountUtil.getCurrentOrg().getId(), tabGroupContext.getLayoutId()));
    }

    @Override
    public void addWebtabWebtabGroup(WebtabWebgroupContext webtabWebgroupContext) throws Exception {
        FacilioCache<String, List<WebTabCacheContext>> tabsCache = LRUCache.getWebTabsCache();
        String key = CacheUtil.ORG_TAB_GROUP_KEY(AccountUtil.getCurrentOrg().getId(), webtabWebgroupContext.getWebTabGroupId());
        super.addWebtabWebtabGroup(webtabWebgroupContext);
        tabsCache.remove(key);
    }

    @Override
    public void associateTabGroup(List<WebTabGroupContext> tabsGroups) throws Exception {
        FacilioCache<String, List<WebTabGroupCacheContext>> tabGroupCache = LRUCache.getWebTabGroupCache();
        FacilioCache<String, List<WebTabCacheContext>> tabsCache = LRUCache.getWebTabsCache();
        String key = CacheUtil.ORG_KEY(AccountUtil.getCurrentOrg().getId());
        super.associateTabGroup(tabsGroups);
        tabsCache.remove(key);
        tabGroupCache.remove(key);
    }

    @Override
    public long addTab(WebTabContext tab) throws Exception {
        FacilioCache<String, List<WebTabCacheContext>> tabsCache = LRUCache.getWebTabsCache();
        String key = CacheUtil.ORG_APP_KEY(AccountUtil.getCurrentOrg().getId(), tab.getApplicationId());
        long id = super.addTab(tab);
        tabsCache.remove(key);
        return id;
    }

    @Override
    public void insertIntoTabIdAppIdMappingTable(WebTabContext webTab) throws Exception {
        WebTabContext webTabContext = getWebTab(webTab.getId());
        FacilioCache<String, List<WebTabCacheContext>> tabsCache = LRUCache.getWebTabsCache();
        FacilioCache<String, WebTabCacheContext> tabCache = LRUCache.getWebTabCache();
        FacilioCache<String, List<TabIdAppIdMappingCacheContext>> tabAppModuleCache = LRUCache.getTabAppModuleCache();
        super.insertIntoTabIdAppIdMappingTable(webTab);
        tabsCache.removeStartsWith(CacheUtil.ORG_KEY(AccountUtil.getCurrentOrg().getId()));
        tabCache.remove(CacheUtil.ORG_TAB_KEY(AccountUtil.getCurrentOrg().getId(), webTabContext.getId()));
        tabAppModuleCache.remove(CacheUtil.ORG_TAB_KEY(AccountUtil.getCurrentOrg().getId(), webTabContext.getId()));
    }
}