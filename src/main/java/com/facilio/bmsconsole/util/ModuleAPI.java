package com.facilio.bmsconsole.util;

import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class ModuleAPI {

    public static long getRelatedModulesCount(FacilioModule module, int moduleType, String searchString) throws Exception {
        if (LookupSpecialTypeUtil.isSpecialType(module.getName())) {
            List<FacilioModule> subModules = LookupSpecialTypeUtil.getSubModules(module.getName(), FacilioModule.ModuleType.valueOf(moduleType));
            return subModules.size();
        }

        FacilioModule moduleModule = ModuleFactory.getModuleModule();
        FacilioModule relModule = ModuleFactory.getSubModulesRelModule();
        Map<String, FacilioField> modFieldsMap = FieldFactory.getAsMap(FieldFactory.getModuleFields());
        Map<String, FacilioField> relModFieldsMap = FieldFactory.getAsMap(FieldFactory.getSubModuleRelFields());

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .table(relModule.getTableName())
                .select(FieldFactory.getCountField())
                .innerJoin(moduleModule.getTableName())
                .on(relModule.getTableName() + ".CHILD_MODULE_ID = " + moduleModule.getTableName() + ".MODULEID")
                .andCondition(CriteriaAPI.getCondition(relModFieldsMap.get("parentModuleId"), String.valueOf(module.getModuleId()), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(modFieldsMap.get("type"), getTypes(FacilioModule.ModuleType.valueOf(moduleType)), StringOperators.IS));

        if (StringUtils.isNotEmpty(searchString)) {
            selectBuilder.andCondition(CriteriaAPI.getCondition(modFieldsMap.get("name"), searchString, StringOperators.CONTAINS));
        }

        Map<String, Object> modulesMap = selectBuilder.fetchFirst();
        long count = MapUtils.isNotEmpty(modulesMap) ? (long) modulesMap.get("count") : 0;

        return count;
    }

    public static long getExtendedModulesCount(FacilioModule module, String searchString) throws Exception {
        FacilioModule moduleModule = ModuleFactory.getModuleModule();
        List<FacilioField> moduleFields = FieldFactory.getModuleFields();
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(moduleFields);

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .table(moduleModule.getTableName())
                .select(FieldFactory.getCountField())
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("hideFromParents"), String.valueOf(false), BooleanOperators.IS))
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("extendsId"), String.valueOf(module.getModuleId()), NumberOperators.EQUALS));

        if (StringUtils.isNotEmpty(searchString)) {
            selectBuilder.andCondition(CriteriaAPI.getCondition(fieldsMap.get("name"), searchString, StringOperators.CONTAINS));
        }

        Map<String, Object> modulesMap = selectBuilder.fetchFirst();
        long count = MapUtils.isNotEmpty(modulesMap) ? (long) modulesMap.get("count") : 0;

        return count;
    }

    public static String getTypes(FacilioModule.ModuleType... types) {
        StringJoiner joiner = new StringJoiner(",");
        for (FacilioModule.ModuleType type : types) {
            joiner.add(String.valueOf(type.getValue()));
        }
        return joiner.toString();
    }

    public static boolean hasSubModuleRelation(long parentModuleId, long subModuleId) throws Exception {
        FacilioModule subModulesRelModule = ModuleFactory.getSubModulesRelModule();
        FacilioField parentModuleIdField = Constants.getModBean().getField("parentModuleId", subModulesRelModule.getName());
        FacilioField childModuleIdField = Constants.getModBean().getField("childModuleId", subModulesRelModule.getName());

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getSubModuleRelFields())
                .table(subModulesRelModule.getTableName())
                .andCondition(CriteriaAPI.getCondition(parentModuleIdField,String.valueOf(parentModuleId),NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(childModuleIdField,String.valueOf(subModuleId),NumberOperators.EQUALS));

        List<Map<String, Object>> prop = selectBuilder.get();

        return CollectionUtils.isNotEmpty(prop);
    }

    public static boolean hasSubModuleRelation(String parentModuleName, String subModuleName) throws Exception {
        if(StringUtils.isNotEmpty(parentModuleName) && StringUtils.isNotEmpty(subModuleName)) {
            FacilioModule parentModule = Constants.getModBean().getModule(parentModuleName);
            FacilioModule subModule = Constants.getModBean().getModule(subModuleName);

            long parentModuleId = parentModule != null ? parentModule.getModuleId() : -1L;
            long subModuleId = subModule != null ? subModule.getModuleId() : -1L;

            if(parentModuleId > 0 && subModuleId > 0) {
                return hasSubModuleRelation(parentModuleId, subModuleId);
            }
        }
        return false;
    }
}
