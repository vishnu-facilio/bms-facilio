package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.relation.context.RelationContext;
import com.facilio.relation.context.RelationMappingContext;
import com.facilio.relation.util.RelationUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GetAllRelationsCountCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        String searchString = (String) context.get(FacilioConstants.ContextNames.SEARCH);
        boolean includeHiddenRelations = (boolean) context.getOrDefault(FacilioConstants.Relationship.INCLUDE_HIDDEN_RELATIONS, false);
        int relationCategoryInt = (int) context.getOrDefault(FacilioConstants.Relationship.RELATION_CATEGORY, RelationContext.RelationCategory.NORMAL.getIndex());
        RelationContext.RelationCategory relationCategory = relationCategoryInt <= 0 ? null : RelationContext.RelationCategory.valueOf(relationCategoryInt);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        if (module == null) {
            throw new IllegalArgumentException("Invalid module");
        }

        long moduleId = module.getModuleId();
        List<Long> moduleIds = new ArrayList<>(Collections.singleton(moduleId));

        Map<String, FacilioField> relationFields = FieldFactory.getAsMap(FieldFactory.getRelationFields());
        Map<String, FacilioField> mappingFields = FieldFactory.getAsMap(FieldFactory.getRelationMappingFields());

        StringBuilder joinCondition = new StringBuilder();
        joinCondition.append(relationFields.get("id").getCompleteColumnName() + " = " + mappingFields.get("relationId").getCompleteColumnName());
        joinCondition.append(" AND ((")
                .append(mappingFields.get("fromModuleId").getCompleteColumnName()).append(" = ").append(mappingFields.get("toModuleId").getCompleteColumnName()).append(" AND ")
                .append(mappingFields.get("position").getCompleteColumnName()).append(" = ").append(RelationMappingContext.Position.LEFT.getIndex()).append(") OR (")
                .append(mappingFields.get("fromModuleId").getCompleteColumnName()).append(" != ").append(mappingFields.get("toModuleId").getCompleteColumnName() + ")")
                .append(")");

        if (FacilioConstants.Relationship.CHILD_MODULE_FETCH_RELATION.contains(module.getName())) {
            List<FacilioModule> childModules = Constants.getModBean().getChildModules(module, null, null, false);
            List<Long> childModIds = childModules.stream().map(FacilioModule::getModuleId).collect(Collectors.toList());
            moduleIds.addAll(childModIds);
        }

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getRelationModule().getTableName())
                .innerJoin(ModuleFactory.getRelationMappingModule().getTableName())
                .on(joinCondition.toString())
                .select(FieldFactory.getCountField())
                .andCondition(CriteriaAPI.getCondition(mappingFields.get("fromModuleId"), StringUtils.join(moduleIds, ','), NumberOperators.EQUALS));

        if (StringUtils.isNotEmpty(searchString)) {
            builder.andCondition(CriteriaAPI.getCondition(relationFields.get("name"), searchString, StringOperators.CONTAINS));
        }

        if (!includeHiddenRelations) {
            builder.andCondition(CriteriaAPI.getCondition(relationFields.get("relationCategory"), String.valueOf(RelationContext.RelationCategory.HIDDEN.getIndex()), NumberOperators.NOT_EQUALS));
        }

        RelationUtil.addRelationCategoryCriteriaToBuilder(relationCategory, builder, relationFields.get("relationCategory"));

        Map<String, Object> modulesMap = builder.fetchFirst();
        long count = MapUtils.isNotEmpty(modulesMap) ? (long) modulesMap.get("count") : 0;

        context.put(FacilioConstants.ContextNames.COUNT, count);
        return false;
    }
}
