package com.facilio.beans;

import com.facilio.bmsconsoleV3.context.scoping.ValueGeneratorContext;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ValueGenerator;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;

public interface ValueGeneratorBean {
    public List<ValueGeneratorContext> getValueGenerators(List<Long> ids) throws Exception;

    public ValueGeneratorContext getValueGenerator(Long id) throws Exception;

    public void addValueGenerators(List<ValueGeneratorContext> valueGenerators) throws Exception;

    public List<ValueGeneratorContext> getValueGenerators(List<String> applicableModuleNames,List<Long> applicableModuleIds) throws Exception;

//    public Map<String, Pair<String,ValueGeneratorContext>> getValueGenFieldMapForModule(FacilioModule module) throws Exception;
}
