package com.facilio.bmsconsoleV3.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.relation.context.RelationMappingContext;
import com.facilio.relation.context.RelationRequestContext;
import com.facilio.relation.util.RelationUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FetchFilteredRelationListCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        int relationType = (int) context.getOrDefault(FacilioConstants.Relationship.RELATION_TYPE, -1);
        boolean skipVirtualRelations = (boolean) context.getOrDefault(FacilioConstants.Relationship.SKIP_VIRTUAL_RELATIONS, true);

        FacilioModule module = Constants.getModBean().getModule(moduleName);
        if (module == null) {
            throw new IllegalArgumentException("Invalid module");
        }
        Criteria criteria = new Criteria();
        Map<String, FacilioField> relationMappingFields = FieldFactory.getAsMap(FieldFactory.getRelationMappingFields());
        Map<String, FacilioField> relationFields = FieldFactory.getAsMap(FieldFactory.getRelationFields());
        if (relationType > 0) {
            RelationRequestContext.RelationType relationTypeEnum = RelationRequestContext.RelationType.valueOf(relationType);
            Criteria relTypeCriteria = new Criteria();
            relTypeCriteria.addAndCondition(CriteriaAPI.getCondition(relationMappingFields.get("relationType"), String.valueOf(relationTypeEnum.getIndex()), NumberOperators.EQUALS));
            if (!Objects.equals(relationTypeEnum, RelationRequestContext.RelationType.ONE_TO_ONE)) {
                relTypeCriteria.addOrCondition(CriteriaAPI.getCondition(relationMappingFields.get("relationType"), String.valueOf(RelationRequestContext.RelationType.ONE_TO_ONE.getIndex()), NumberOperators.EQUALS));
            }
            criteria.andCriteria(relTypeCriteria);
        }

        if (skipVirtualRelations) {
            criteria.addAndCondition(CriteriaAPI.getCondition(relationFields.get("isVirtual"), String.valueOf(false), NumberOperators.EQUALS));
        }
        criteria.addAndCondition(CriteriaAPI.getCondition(relationMappingFields.get("position"), String.valueOf(RelationMappingContext.Position.LEFT.getIndex()), NumberOperators.EQUALS));

        List<RelationRequestContext> allRelations = RelationUtil.getAllRelations(module, criteria);
        context.put(FacilioConstants.ContextNames.RELATION_LIST, allRelations);

        return false;
    }
}
