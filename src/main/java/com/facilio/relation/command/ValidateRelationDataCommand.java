package com.facilio.relation.command;

import com.facilio.bmsconsoleV3.commands.ReadOnlyChainFactoryV3;
import com.facilio.beans.ModuleBean;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.relation.context.RelationContext;
import com.facilio.relation.context.RelationDataContext;
import com.facilio.relation.context.RelationMappingContext;
import com.facilio.relation.context.RelationRequestContext;
import com.facilio.relation.util.RelationUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.*;

public class ValidateRelationDataCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List<Object>> queryParams = (Map<String, List<Object>>) context.get(FacilioConstants.ContextNames.QUERY_PARAMS);
        String relationLinkName = (String) Constants.getQueryParamOrThrow(context, FacilioConstants.ContextNames.RELATION_NAME);
        long parentId = FacilioUtil.parseLong(Constants.getQueryParamOrThrow(context, FacilioConstants.ContextNames.PARENT_ID));

        RelationMappingContext relationMapping = RelationUtil.getRelationMapping(relationLinkName);
        String relationModuleName = Constants.getModuleName(context);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule relationModule = modBean.getModule(relationModuleName);
        RelationContext relation = RelationUtil.getRelation(relationModule, false);

        if(!V3Util.isIdPresentForModule(parentId, relationMapping.getFromModule())) {
            throw new IllegalArgumentException("Invalid parent record");
        }

        List<ModuleBaseWithCustomFields> relationDataList = Constants.getRecordListFromContext((FacilioContext) context, relationModuleName);
        if (CollectionUtils.isEmpty(relationDataList)) {
            throw new IllegalArgumentException("Relation data should not be empty");
        }

        List<Long> ids = new ArrayList<Long>();
        for (ModuleBaseWithCustomFields data : relationDataList) {
            if(relationMapping.getPositionEnum().getIndex() == RelationMappingContext.Position.LEFT.getIndex()) {
                ids.add(((RelationDataContext) data).getRight().getId());
            } else {
                ids.add(((RelationDataContext) data).getLeft().getId());
            }
        }
        Set<Long> idset = new HashSet<Long>(ids);
        if(idset.size() != ids.size()) {
            throw new IllegalArgumentException("Duplicates present in relation data");
        }

        if(ids.size() > 1 && RelationUtil.isToOneRelationShipType(relationMapping)) {
            throw new IllegalArgumentException("Only one relation is allowed");
        }

        if(relationMapping.getFromModuleId() == relationMapping.getToModuleId() && idset.contains(parentId)) {
            throw new IllegalArgumentException("Same record cannot be related");
        }

        Criteria relationDataCriteria = new Criteria();
        relationDataCriteria.addAndCondition(CriteriaAPI.getCondition("MODULEID", "moduleId", String.valueOf(relation.getRelationModule().getModuleId()), NumberOperators.EQUALS));

        Condition idsCondition = CriteriaAPI.getCondition(relationMapping.getReversePosition().getColumnName(), relationMapping.getReversePosition().getFieldName(), StringUtils.join(ids, ","), NumberOperators.EQUALS);
        Condition parentIdCondition = CriteriaAPI.getCondition(relationMapping.getPositionEnum().getColumnName(), relationMapping.getPositionEnum().getFieldName(), String.valueOf(parentId), NumberOperators.EQUALS);
        Criteria typebasedCriteria = new Criteria();
        if(relationMapping.getRelationType() == RelationRequestContext.RelationType.ONE_TO_ONE.getIndex()) {
            typebasedCriteria.addAndCondition(idsCondition);
            typebasedCriteria.addOrCondition(parentIdCondition);
        }
        else if(relationMapping.getRelationType() == RelationRequestContext.RelationType.MANY_TO_ONE.getIndex()) {
            typebasedCriteria.addAndCondition(parentIdCondition);
        }
        else if(relationMapping.getRelationType() == RelationRequestContext.RelationType.ONE_TO_MANY.getIndex()) {
            typebasedCriteria.addAndCondition(idsCondition);
        }
        else if(relationMapping.getRelationType() == RelationRequestContext.RelationType.MANY_TO_MANY.getIndex()) {
            typebasedCriteria.addAndCondition(idsCondition);
            typebasedCriteria.addAndCondition(parentIdCondition);
        }

        relationDataCriteria.andCriteria(typebasedCriteria);

        if (RelationUtil.isToOneRelationShipType(relationMapping)) {
            Criteria customRelationFetchCriteria = new Criteria();
            customRelationFetchCriteria.addAndCondition(CriteriaAPI.getCondition("MODULEID", "moduleId", String.valueOf(relation.getRelationModule().getModuleId()), NumberOperators.EQUALS));
            customRelationFetchCriteria.addAndCondition(CriteriaAPI.getCondition(relationMapping.getReversePosition().getColumnName(), relationMapping.getReversePosition().getFieldName(), StringUtils.join(ids, ","), NumberOperators.EQUALS));

            checkAndDeleteCustomRelation(modBean, relationModule, relationMapping, customRelationFetchCriteria);
        } else if (V3Util.getCountFromModuleAndCriteria(relation.getRelationModule(), relationDataCriteria) > 0) {
            throw new IllegalArgumentException("Relation already exists");
        }

        if(!V3Util.checkifIdsArePresentForModule(ids, relationMapping.getToModule())) {
            throw new IllegalArgumentException("Invalid relation data passed");
        }

        return false;
    }

    private void checkAndDeleteCustomRelation(ModuleBean moduleBean, FacilioModule relationModule, RelationMappingContext relationMapping,
                                              Criteria customRelationFetchCriteria) throws Exception {
        RelationMappingContext.Position relationPosition = RelationMappingContext.Position.valueOf(relationMapping.getPosition());
        FacilioChain relationDataChain = ReadOnlyChainFactoryV3.getCustomRelationDataChain();

        String relationModuleName = relationModule.getName();
        FacilioContext listContext = relationDataChain.getContext();
        listContext.put(FacilioConstants.ContextNames.MODULE_NAME, relationModuleName);
        listContext.put(FacilioConstants.ContextNames.FILTER_SERVER_CRITERIA, customRelationFetchCriteria);

        relationDataChain.execute();

        JSONObject customRelation = Constants.getJsonRecordMap(listContext);
        List<Map<String, Object>> resultData = (ArrayList<Map<String, Object>>) customRelation.get(relationModuleName);

        if (CollectionUtils.isNotEmpty(resultData)) {
            Map<String, Object> moduleDataObj = (Map<String, Object>) resultData.get(0).get(relationPosition.getFieldName());

            Long recordId = (Long) moduleDataObj.get("id");
            Long customRelationId = (Long) resultData.get(0).get("id");
            FacilioModule fromModule = relationMapping.getFromModule();
            FacilioModule parentOfFromModule = fromModule.getParentModule();

            List<FacilioField> fromModuleFields = new ArrayList<>();
            fromModuleFields.add(FieldFactory.getIdField(fromModule));
            fromModuleFields.add(FieldFactory.getIsDeletedField(parentOfFromModule));

            Map<String, Object> summaryRecord = RelationUtil.getSimpleModuleRecordSummary(fromModule, recordId, fromModuleFields);

            boolean isDeletedRecord = false;
            if (MapUtils.isNotEmpty(summaryRecord)) {
                isDeletedRecord = (boolean) summaryRecord.getOrDefault("deleted", false);
            }

            if (isDeletedRecord) {
                // The associated record is deleted, so we may delete the Custom_Relation established
                RelationUtil.deleteCustomRelation(relationModule, customRelationId);
            } else {
                throw new IllegalArgumentException("Relation already exists");
            }
        }
    }
}
