package com.facilio.beans;

import com.facilio.bmsconsoleV3.context.scoping.ValueGeneratorCacheContext;
import com.facilio.bmsconsoleV3.context.scoping.ValueGeneratorContext;

import java.util.List;
import java.util.Map;

public interface ValueGeneratorBean {
    public List<ValueGeneratorCacheContext> getValueGenerators(List<Long> ids) throws Exception;

    public ValueGeneratorCacheContext getValueGenerator(Long id) throws Exception;

    public List<Map<String, Object>> addValueGenerators(List<ValueGeneratorContext> valueGenerators) throws Exception;

    public List<ValueGeneratorContext> getValueGenerators(List<String> applicableModuleNames,List<Long> applicableModuleIds) throws Exception;

//    public Map<String, Pair<String,ValueGeneratorContext>> getValueGenFieldMapForModule(FacilioModule module) throws Exception;

    public ValueGeneratorContext getValueGenerator(String linkName) throws Exception;

}
