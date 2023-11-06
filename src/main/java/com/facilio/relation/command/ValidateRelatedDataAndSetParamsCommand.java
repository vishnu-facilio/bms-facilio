package com.facilio.relation.command;

import com.facilio.beans.ModuleBean;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.relation.context.RelationContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants.*;
import com.facilio.relation.util.RelationUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import com.facilio.relation.context.RelationMappingContext;
import com.facilio.relation.context.RelationRequestContext;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.db.criteria.operators.RelationshipOperator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValidateRelatedDataAndSetParamsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long recordId = (long) context.get(ContextNames.ID);
        String widgetType = (String) context.get(ContextNames.WIDGET_TYPE);
        String relationName = (String) context.get(ContextNames.RELATION_NAME);
        String relatedFieldName = (String) context.get(ContextNames.RELATED_FIELD_NAME);
        String relatedModuleName = (String) context.get(ContextNames.RELATED_MODULE_NAME);
        String relationshipActionType = (String) context.get(ContextNames.RELATIONSHIP_ACTION_TYPE);
        boolean unAssociated = (boolean) context.getOrDefault("unAssociated", false);
        Map<String, List<Object>> queryParams = (Map<String, List<Object>>) context.getOrDefault(ContextNames.QUERY_PARAMS, new HashMap<>());

        boolean isRelationshipAction = StringUtils.isNotEmpty(relationshipActionType);
        Criteria filterCriteria = null;

        switch (widgetType) {
            case ContextNames.RELATIONSHIP :
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                RelationMappingContext relationMapping = RelationUtil.getRelationMapping(relationName);
                if (relationMapping == null) {
                    throw new IllegalArgumentException("Invalid relation");
                }
                FacilioModule fromModule = modBean.getModule(relationMapping.getFromModuleId());

                RelationContext relationContext = RelationUtil.getRelation(relationMapping.getRelationId(), false);
                if (isRelationshipAction && relationContext.isVirtual()) {
                    throw new IllegalArgumentException("Cannot add or update Virtual Relationship");
                }
                context.put(ContextNames.RELATION, relationContext);

                if (!isRelationshipAction && !unAssociated && (relationMapping.getRelationType() == RelationRequestContext.RelationType.ONE_TO_ONE.getIndex()
                        || relationMapping.getRelationType() == RelationRequestContext.RelationType.ONE_TO_MANY.getIndex())) {
                    constructRelationListQueryParams(queryParams, recordId, relationMapping);

                    FacilioModule relationMappingModule = modBean.getModule(relationContext.getRelationModuleId());
                    RelationMappingContext.Position relationPosition = RelationMappingContext.Position.valueOf(relationMapping.getPosition());

                    context.put(ContextNames.RELATION_MODULE_NAME, relationMappingModule.getName());                        // ModuleName for V3Util.fetchList()
                    context.put(ContextNames.RELATION_POSITION_TYPE, relationPosition);
                    context.put("fetchSummary", true);
                } else if (!relationContext.isVirtual()) {
                    filterCriteria = getRelationshipFilterCriteria(relationName, recordId, unAssociated);                   // Many-to-One & Many-to-Many
                    context.put(ContextNames.FILTER_SERVER_CRITERIA, filterCriteria);
                }

                if (isRelationshipAction) {                                                                                     // Associate & Dissociate
                    constructRelationshipDataQueryParams(queryParams, relationName, recordId);
                    context.put(ContextNames.QUERY_PARAMS, queryParams);
                }

                context.put(ContextNames.MODULE_NAME, fromModule.getName());                                                // ModuleName for V3Util.fetchList() / V3Util.summary()
                break;

            case ContextNames.RELATED_LIST:
                if (StringUtils.isEmpty(relatedModuleName)) {
                    throw new IllegalArgumentException("Related Module Name cannot be null");
                }
                if (StringUtils.isEmpty(relatedFieldName)) {
                    throw new IllegalArgumentException("Related Field Name cannot be null");
                }
                context.put(ContextNames.MODULE_NAME, relatedModuleName);
                filterCriteria = getRelatedListFilterCriteria(relatedFieldName, relatedModuleName, recordId);
                context.put(ContextNames.FILTER_SERVER_CRITERIA, filterCriteria);

                break;
        }

        return false;
    }

    private Criteria getRelationshipFilterCriteria(String relationName, long id, boolean unAssociated) {
        Criteria criteria = new Criteria();
        if (unAssociated) {
            criteria.addAndCondition(CriteriaAPI.getCondition(relationName, String.valueOf(id), RelationshipOperator.NOT_CONTAINS_RELATION));
        } else {
            criteria.addAndCondition(CriteriaAPI.getCondition(relationName, String.valueOf(id), RelationshipOperator.CONTAINS_RELATION));
        }

        return criteria;
    }

    private Criteria getRelatedListFilterCriteria(String relatedFieldName, String relatedModuleName, long id) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioField lookupField = modBean.getField(relatedFieldName, relatedModuleName);

        if (lookupField == null) {
            throw new IllegalArgumentException("Related Field Name cannot be null");
        }

        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(lookupField.getCompleteColumnName(), lookupField.getName(), String.valueOf(id), PickListOperators.IS));

        return criteria;
    }

    private void constructRelationshipDataQueryParams(Map<String, List<Object>> queryParams, String relationName, long id) {
        queryParams.put("relationName", new ArrayList<Object>(){{
            add(relationName);
        }});
        queryParams.put("parentId", new ArrayList<Object>(){{
            add(id);
        }});
    }

    private void constructRelationListQueryParams(Map<String, List<Object>> queryParams, long id, RelationMappingContext relationMapping) {
        queryParams.put("parentId", new ArrayList<Object>(){{
            add(id);
        }});
        queryParams.put("fromModuleId", new ArrayList<Object>(){{
            add(relationMapping.getToModuleId());                                                       // Module containing Parent Record Id
        }});
        queryParams.put("mappingLinkName", new ArrayList<Object>(){{
            add(relationMapping.getMappingLinkName());
        }});
        queryParams.put("skipSupplementsUpdate", new ArrayList<Object>(){{
            add(Boolean.TRUE);
        }});
    }
}
