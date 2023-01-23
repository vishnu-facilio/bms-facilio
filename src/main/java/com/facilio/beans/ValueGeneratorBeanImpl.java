package com.facilio.beans;

import com.facilio.bmsconsoleV3.context.scoping.ValueGeneratorCacheContext;
import com.facilio.bmsconsoleV3.context.scoping.ValueGeneratorContext;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ValueGeneratorBeanImpl implements ValueGeneratorBean {

    @Override
    public List<ValueGeneratorCacheContext> getValueGenerators(List<Long> ids) throws Exception {
        if (CollectionUtils.isNotEmpty(ids)) {
            GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                    .table(ModuleFactory.getValueGeneratorModule().getTableName())
                    .select(FieldFactory.getValueGeneratorFields())
                    .andCondition(CriteriaAPI.getIdCondition(ids, ModuleFactory.getValueGeneratorModule()));
            List<Map<String, Object>> props = selectRecordBuilder.get();
            if (CollectionUtils.isNotEmpty(props)) {
                List<ValueGeneratorContext> valueGenerators = FieldUtil.getAsBeanListFromMapList(props, ValueGeneratorContext.class);
                List<ValueGeneratorCacheContext> valueGeneratorsCache = new ArrayList<>();
                for(ValueGeneratorContext valGen : valueGenerators) {
                    valueGeneratorsCache.add(new ValueGeneratorCacheContext(valGen));
                }
                return valueGeneratorsCache;
            }
        }
        return null;
    }

    @Override
    public ValueGeneratorCacheContext getValueGenerator(Long id) throws Exception {
        List<ValueGeneratorCacheContext> valGens = getValueGenerators(Collections.singletonList(id));
        if (CollectionUtils.isNotEmpty(valGens)) {
            return valGens.get(0);
        }
        return null;
    }

    @Override
    public void addValueGenerators(List<ValueGeneratorContext> valueGenerators) throws Exception {
        if (CollectionUtils.isNotEmpty(valueGenerators)) {
            List<Map<String, Object>> props = FieldUtil.getAsMapList(valueGenerators, ValueGenerator.class);
            GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                    .table(ModuleFactory.getValueGeneratorModule().getTableName())
                    .fields(FieldFactory.getValueGeneratorFields());
            insertBuilder.addRecords(props);
            insertBuilder.save();
        }
    }

    @Override
    public List<ValueGeneratorContext> getValueGenerators(List<String> applicableModuleNames, List<Long> applicableModuleIds) throws Exception{
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getValueGeneratorModule().getTableName())
                .select(FieldFactory.getValueGeneratorFields());
        if(CollectionUtils.isNotEmpty(applicableModuleNames)) {
            selectRecordBuilder.andCondition(CriteriaAPI.getCondition("SPECIAL_MODULE_NAME","specialModuleName", StringUtils.join(applicableModuleNames,","), StringOperators.IS));
        }
        if(CollectionUtils.isNotEmpty(applicableModuleIds)) {
            selectRecordBuilder.andCondition(CriteriaAPI.getCondition("MODULEID","moduleId", StringUtils.join(applicableModuleIds,","), NumberOperators.EQUALS));
        }
        List<Map<String, Object>> props = selectRecordBuilder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            List<ValueGeneratorContext> valueGenerators = FieldUtil.getAsBeanListFromMapList(props, ValueGeneratorContext.class);
            return valueGenerators;
        }
        return null;
    }

    @Override
    public ValueGeneratorContext getValueGenerator(String linkName) throws Exception {
        if (StringUtils.isNotEmpty(linkName)) {
            GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                    .table(ModuleFactory.getValueGeneratorModule().getTableName())
                    .select(FieldFactory.getValueGeneratorFields())
                    .andCondition(CriteriaAPI.getCondition("LINK_NAME","linkname",linkName,StringOperators.IS));
            Map<String, Object> prop = selectRecordBuilder.fetchFirst();
            if (MapUtils.isNotEmpty(prop)) {
                ValueGeneratorContext valueGenerator = FieldUtil.getAsBeanFromMap(prop, ValueGeneratorContext.class);
                return valueGenerator;
            }
        }
        return null;
    }
}
