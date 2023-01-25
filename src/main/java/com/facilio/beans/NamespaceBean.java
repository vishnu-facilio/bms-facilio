package com.facilio.beans;

import com.facilio.ns.context.NSType;
import com.facilio.ns.context.NameSpaceCacheContext;
import com.facilio.ns.context.NameSpaceContext;
import com.facilio.ns.context.NameSpaceField;

import java.util.List;

public interface NamespaceBean {
    NameSpaceCacheContext getNamespace(Long nsId) throws Exception;

    Long addNamespace(NameSpaceContext ns) throws Exception;

    void updateNamespace(NameSpaceContext ns) throws Exception;

    void updateNsStatus(Long ruleId, boolean status, List<NSType> nsList) throws Exception;

    void deleteNameSpacesFromRuleId(Long parentId, List<NSType> nsList) throws Exception;

    List<Long> getNamespaceIdsForFieldId(Long var1) throws Exception;

    void addNamespaceFields(Long nsId, List<NameSpaceField> fields) throws Exception;

}
