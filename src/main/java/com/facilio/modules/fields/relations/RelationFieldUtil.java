package com.facilio.modules.fields.relations;

import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RelationFieldUtil {

    public static List<BaseRelationContext> getAllRelations(List<FacilioField> fields) throws Exception {
        List<Long> list = fields.stream().map(FacilioField::getFieldId).collect(Collectors.toList());
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getBaseFieldRelationModule().getTableName())
                .select(FieldFactory.getBaseFieldRelationFields())
                .andCondition(CriteriaAPI.getCondition("BASE_FIELD_ID", "baseFieldId",
                        StringUtils.join(list, ","), NumberOperators.EQUALS));
        List<BaseRelationContext> baseRelations =
                FieldUtil.getAsBeanListFromMapList(builder.get(), BaseRelationContext.class);
        return fetchExtended(baseRelations);
    }

    private static List<BaseRelationContext> fetchExtended(List<BaseRelationContext> baseRelations) throws Exception {
        if (CollectionUtils.isEmpty(baseRelations)) {
            return null;
        }

        List<BaseRelationContext> list = new ArrayList<>();
        Map<BaseRelationContext.Type, List<BaseRelationContext>> map = getRelationMap(baseRelations);
        for (BaseRelationContext.Type type : map.keySet()) {
            FacilioModule module = getModule(type);
            List<BaseRelationContext> baseRelationContexts = map.get(type);
            List<Long> ids = baseRelationContexts.stream().map(BaseRelationContext::getId).collect(Collectors.toList());
            GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                    .table(ModuleFactory.getBaseFieldRelationModule().getTableName())
                    .innerJoin(module.getTableName())
                    .on(ModuleFactory.getBaseFieldRelationModule().getTableName() + ".ID = " + module.getTableName() + ".ID")
                    .select(getFields(type))
                    .andCondition(CriteriaAPI.getIdCondition(ids, module));
            List<? extends BaseRelationContext> baseRelationList = FieldUtil.getAsBeanListFromMapList(builder.get(), getClass(type));
            list.addAll(baseRelationList);
        }
        return list;
    }

    public static Map<BaseRelationContext.Type, List<BaseRelationContext>> getRelationMap(List<BaseRelationContext> baseRelations) {
        Map<BaseRelationContext.Type, List<BaseRelationContext>> map = new HashMap<>();
        for (BaseRelationContext baseRelation : baseRelations) {
            List<BaseRelationContext> baseRelationContexts = map.get(baseRelation.getTypeEnum());
            if (baseRelationContexts == null) {
                baseRelationContexts = new ArrayList<>();
                map.put(baseRelation.getTypeEnum(), baseRelationContexts);
            }
            baseRelationContexts.add(baseRelation);
        }
        return map;
    }

    public static void addRelations(List<BaseRelationContext> relations) throws Exception {
        if (CollectionUtils.isEmpty(relations)) {
            return;
        }

        Map<BaseRelationContext.Type, List<BaseRelationContext>> map = getRelationMap(relations);

        for (BaseRelationContext.Type type : map.keySet()) {
            Class clazz = getClass(type);
            FacilioModule module = getModule(type);
            List<FacilioField> fields = getFields(type);

            List<FacilioModule> modules = new ArrayList<>();
            while (module != null) {
                modules.add(0, module);
                module = module.getExtendModule();
            }

            Map<String, List<FacilioField>> fieldMap = new HashMap<>();
            for (FacilioField f : fields) {
                FacilioModule m = f.getModule();
                List<FacilioField> list = fieldMap.get(m.getName());
                if (list == null) {
                    list = new ArrayList<>();
                    fieldMap.put(m.getName(), list);
                }
                list.add(f);
            }

            List<BaseRelationContext> baseRelations = map.get(type);
            List<Map<String, Object>> mapList = FieldUtil.getAsMapList(baseRelations, clazz);
            for (FacilioModule m : modules) {
                GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                        .table(m.getTableName())
                        .fields(fieldMap.get(m.getName()));
                builder.addRecords(mapList);
                builder.save();
            }
        }
    }

    private static FacilioModule getModule(BaseRelationContext.Type type) {
        switch (type) {
            case DELTA:
                return null;
            case TIME_DELTA:
                return ModuleFactory.getTimeDeltaFieldRelation();
            default:
                throw new IllegalArgumentException("Invalid type");
        }
    }

    private static List<FacilioField> getFields(BaseRelationContext.Type type) {
        switch (type) {
            case DELTA:
                return null;
            case TIME_DELTA:
                return FieldFactory.getTimeDeltaFieldRelationFields();
            default:
                throw new IllegalArgumentException("Invalid type");
        }
    }

    private static Class getClass(BaseRelationContext.Type type) {
        switch (type) {
            case DELTA:
                return DeltaCalculation.class;
            case TIME_DELTA:
                return TimeDeltaRelationContext.class;
            default:
                throw new IllegalArgumentException("Invalid type");
        }
    }
}
