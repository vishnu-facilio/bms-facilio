package com.facilio.bmsconsole.workflow.rule.impact.util;

import com.facilio.bmsconsole.workflow.rule.impact.BaseAlarmImpactContext;
import com.facilio.bmsconsole.workflow.rule.impact.ConstantImpactContext;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AlarmImpactAPI {

    public static void addAlarmImpact(BaseAlarmImpactContext alarmImpact) throws Exception {
        if (alarmImpact == null) {
            return;
        }

        alarmImpact.validate();

        FacilioModule module = getModule(alarmImpact.getImpactTypeEnum());
        List<FacilioField> fields = getFields(alarmImpact.getImpactTypeEnum());

        List<FacilioModule> insertModules = new ArrayList<>();
        while (module != null) {
            insertModules.add(0, module);
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

        Map<String, Object> map = FieldUtil.getAsProperties(alarmImpact);
        for (FacilioModule m : insertModules) {
            GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                    .table(m.getTableName())
                    .fields(fieldMap.get(m.getName()));
            builder.addRecord(map);
            builder.save();
        }

        long id = (long) map.get("id");
        alarmImpact.setId(id);
    }

    private static Class getClass(BaseAlarmImpactContext.ImpactType impactType) {
        switch (impactType) {
            case CONSTANT:
                return ConstantImpactContext.class;

            default:
                throw new IllegalArgumentException("Invalid type");
        }
    }

    public static FacilioModule getModule(BaseAlarmImpactContext.ImpactType impactType) {
        switch (impactType) {
            case CONSTANT:
                return ModuleFactory.getConstantAlarmImpactModule();

            default:
                throw new IllegalArgumentException("Invalid type");
        }
    }

    public static List<FacilioField> getFields(BaseAlarmImpactContext.ImpactType impactType) {
        switch (impactType) {
            case CONSTANT:
                return FieldFactory.getConstantAlarmImpactFields();

            default:
                throw new IllegalArgumentException("Invalid type");
        }
    }

    public static List<BaseAlarmImpactContext> getAllAlarmImpacts(Long assetCategoryId) throws Exception {
        if (assetCategoryId == null) {
            throw new IllegalArgumentException("Asset Category ID cannot be empty");
        }

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getBaseAlarmImpactModule().getTableName())
                .select(FieldFactory.getBaseAlarmImpactFields())
                .andCondition(CriteriaAPI.getCondition("CATEGORY_ID", "category", String.valueOf(assetCategoryId), NumberOperators.EQUALS));
        List<BaseAlarmImpactContext> impactList = FieldUtil.getAsBeanListFromMapList(builder.get(), BaseAlarmImpactContext.class);
        return getExtendedAlarmImpacts(impactList);
    }

    public static Map<BaseAlarmImpactContext.ImpactType, List<BaseAlarmImpactContext>> getImpactMap(List<BaseAlarmImpactContext> impacts) {
        Map<BaseAlarmImpactContext.ImpactType, List<BaseAlarmImpactContext>> map = new HashMap<>();
        if (CollectionUtils.isNotEmpty(impacts)) {
            for (BaseAlarmImpactContext impact : impacts) {
                BaseAlarmImpactContext.ImpactType impactType = impact.getImpactTypeEnum();
                List<BaseAlarmImpactContext> baseAlarmImpactContexts = map.get(impactType);
                if (baseAlarmImpactContexts == null) {
                    baseAlarmImpactContexts = new ArrayList<>();
                    map.put(impactType, baseAlarmImpactContexts);
                }
                baseAlarmImpactContexts.add(impact);
            }
        }
        return map;
    }

    private static List<BaseAlarmImpactContext> getExtendedAlarmImpacts(List<BaseAlarmImpactContext> impactList) throws Exception {
        if (CollectionUtils.isEmpty(impactList)) {
            return impactList;
        }

        List<BaseAlarmImpactContext> list = new ArrayList<>();
        Map<BaseAlarmImpactContext.ImpactType, List<BaseAlarmImpactContext>> impactMap = getImpactMap(impactList);
        for (BaseAlarmImpactContext.ImpactType type : impactMap.keySet()) {
            FacilioModule module = getModule(type);
            List<BaseAlarmImpactContext> alarmImpacts = impactMap.get(type);
            List<Long> ids = alarmImpacts.stream().map(BaseAlarmImpactContext::getId).collect(Collectors.toList());
            GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                    .table(ModuleFactory.getBaseAlarmImpactModule().getTableName())
                    .innerJoin(module.getTableName())
                        .on(ModuleFactory.getBaseAlarmImpactModule().getTableName() + ".ID = " + module.getTableName() + ".ID")
                    .select(getFields(type))
                    .andCondition(CriteriaAPI.getIdCondition(ids, module));
            list.addAll(FieldUtil.getAsBeanListFromMapList(builder.get(), getClass(type)));
        }
        return list;
    }

    public static void deleteAlarmImpact(long id) throws Exception {
        GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
                .table(ModuleFactory.getBaseAlarmImpactModule().getTableName())
                .andCondition(CriteriaAPI.getIdCondition(id, ModuleFactory.getBaseAlarmImpactModule()));
        builder.delete();
    }
}
