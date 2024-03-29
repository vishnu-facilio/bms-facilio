package com.facilio.beans;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.cache.CacheUtil;
import com.facilio.connected.ResourceType;
import com.facilio.fw.cache.FWLRUCaches;
import com.facilio.fw.cache.FacilioCache;
import com.facilio.fw.cache.LRUCache;
import com.facilio.ns.NamespaceAPI;
import com.facilio.ns.context.NSType;
import com.facilio.ns.context.NameSpaceCacheContext;
import com.facilio.ns.context.NameSpaceContext;
import com.facilio.ns.context.NameSpaceField;

import java.util.List;
import java.util.stream.Collectors;

public class NamespaceBeanCacheImpl extends NamespaceBeanImpl implements NamespaceBean {

    @Override
    public NameSpaceCacheContext getNamespace(Long nsId) throws Exception {
        FacilioCache<String, NameSpaceCacheContext> nameSpaceCache = LRUCache.getNameSpaceCache();
        String key = CacheUtil.NAMESPACE_KEY(AccountUtil.getCurrentOrg().getId(), nsId);
        return FWLRUCaches.Util.genericGetFromCacheAndHandleMissLogic(nameSpaceCache, key, () -> super.getNamespace(nsId));
    }
    
    @Override
    public NameSpaceCacheContext getNamespaceForParent(Long parentId, NSType type) throws Exception {
        FacilioCache<String, NameSpaceCacheContext> nameSpaceParentCache = LRUCache.getNameSpaceParentCache();
        String key = CacheUtil.NAMESPACE_PARENT_KEY(AccountUtil.getCurrentOrg().getId(), parentId, type.getIndex());
        return FWLRUCaches.Util.genericGetFromCacheAndHandleMissLogic(nameSpaceParentCache, key, () -> super.getNamespaceForParent(parentId,type));
    }

    @Override
    public List<Long> getNamespaceIdsForFieldId(Long fieldId) throws Exception {
        FacilioCache<String, List<Long>> nameSpaceIdCache = LRUCache.getNameSpaceIdCache();
        String key = CacheUtil.NAMESPACE_IDS_KEY(AccountUtil.getCurrentOrg().getId(), fieldId);
        return FWLRUCaches.Util.genericGetFromCacheAndHandleMissLogic(nameSpaceIdCache, key, () -> super.getNamespaceIdsForFieldId(fieldId));
    }


    @Override
    public Long addNamespace(NameSpaceContext ns, ResourceType resourceType) throws Exception {
        Long nsId = super.addNamespace(ns,resourceType);
        removeCacheWithFieldsList(ns.getFields());
        return nsId;
    }

    @Override
    public void addNamespaceFields(Long nsId, List<NameSpaceField> fields) throws Exception {
        super.addNamespaceFields(nsId, fields);
        removeCacheWithFieldsList(fields);
    }

    private void removeCacheWithFieldsList(List<NameSpaceField> fields) {
        List<Long> readingFieldIds = fields.stream().map(NameSpaceField::getFieldId).collect(Collectors.toList());
        FacilioCache<String, List<Long>> nameSpaceIdCache = LRUCache.getNameSpaceIdCache();
        for (Long fieldId : readingFieldIds) {
            nameSpaceIdCache.remove(CacheUtil.NAMESPACE_IDS_KEY(AccountUtil.getCurrentOrg().getId(), fieldId));
        }
    }

    @Override
    public void updateNamespace(NameSpaceContext ns) throws Exception {
        super.updateNamespace(ns);

        FacilioCache<String, NameSpaceCacheContext> nameSpaceCache = LRUCache.getNameSpaceCache();
        nameSpaceCache.remove(CacheUtil.NAMESPACE_KEY(AccountUtil.getCurrentOrg().getId(), ns.getId()));
        FacilioCache<String, NameSpaceCacheContext> parentNameSpaceCache = LRUCache.getNameSpaceParentCache();
        if(ns.getParentRuleId() != null && ns.getType() > 0) {
            parentNameSpaceCache.remove(CacheUtil.NAMESPACE_PARENT_KEY(AccountUtil.getCurrentOrg().getId(), ns.getParentRuleId(), ns.getType()));
        }
        FacilioCache<String, List<Long>> nameSpaceIdCache = LRUCache.getNameSpaceIdCache();
        List<Long> fieldIds = NamespaceAPI.getFieldIdsForNamespace(ns.getId());
        for (Long fieldId : fieldIds) {
            nameSpaceIdCache.remove(CacheUtil.NAMESPACE_IDS_KEY(AccountUtil.getCurrentOrg().getId(), fieldId));
        }
    }

    @Override
    public void updateNsStatus(Long ruleId, boolean status, List<NSType> nsList) throws Exception {
        super.updateNsStatus(ruleId, status, nsList);
        removeNsCacheWithRule(ruleId, nsList);
    }

    @Override
    public void updateNsCacheWithCategory(Long categoryId, List<NSType> nsList) throws Exception {
        removeNsCacheWithCategory(categoryId, nsList);
    }

    public void deleteNameSpacesFromRuleId(Long ruleId, List<NSType> nsTypeList) throws Exception {
        super.deleteNameSpacesFromRuleId(ruleId, nsTypeList);
        removeNsCacheWithRule(ruleId, nsTypeList);
    }

    private void removeNsCacheWithRule(Long ruleId, List<NSType> nsTypeList) throws Exception {
        FacilioCache<String, NameSpaceCacheContext> nameSpaceCache = LRUCache.getNameSpaceCache();
        List<Long> nsIds = NamespaceAPI.getNsIdForRuleId(ruleId, nsTypeList);
        for (Long nsId : nsIds) {
            nameSpaceCache.remove(CacheUtil.NAMESPACE_KEY(AccountUtil.getCurrentOrg().getId(), nsId));
        }
        FacilioCache<String, List<Long>> nameSpaceIdCache = LRUCache.getNameSpaceIdCache();
        List<Long> fieldIds = NamespaceAPI.getNsFieldIdsForRuleId(nsIds, nsTypeList);
        for (Long fieldId : fieldIds) {
            nameSpaceIdCache.remove(CacheUtil.NAMESPACE_IDS_KEY(AccountUtil.getCurrentOrg().getId(), fieldId));
        }
    }

    private void removeNsCacheWithCategory(Long categoryId, List<NSType> nsTypeList) throws Exception {
        FacilioCache<String, NameSpaceCacheContext> nameSpaceCache = LRUCache.getNameSpaceCache();
        List<Long> nsIds = NamespaceAPI.getNsIdForCategoryId(categoryId, nsTypeList);
        for (Long nsId : nsIds) {
            nameSpaceCache.remove(CacheUtil.NAMESPACE_KEY(AccountUtil.getCurrentOrg().getId(), nsId));
        }
        FacilioCache<String, List<Long>> nameSpaceIdCache = LRUCache.getNameSpaceIdCache();
        List<Long> fieldIds = NamespaceAPI.getNsFieldIdsForRuleId(nsIds, nsTypeList);
        for (Long fieldId : fieldIds) {
            nameSpaceIdCache.remove(CacheUtil.NAMESPACE_IDS_KEY(AccountUtil.getCurrentOrg().getId(), fieldId));
        }
    }
}
