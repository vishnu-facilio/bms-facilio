package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.relation.context.RelationContext;
import com.facilio.relation.context.RelationMappingContext;
import com.facilio.relation.context.RelationRequestContext;
import com.facilio.relation.util.RelationUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class GetRelationShipAndDataCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String fromModuleName= (String) context.getOrDefault(FacilioConstants.ContextNames.MODULE_NAME,null);
        Long recordId= (Long) context.getOrDefault(FacilioConstants.ContextNames.RECORD_ID,null);
        Integer relationCategory= (Integer) context.getOrDefault(FacilioConstants.ContextNames.RELATION_CATEGORY_NAME,null);
        FacilioUtil.throwIllegalArgumentException(StringUtils.isEmpty(fromModuleName) || recordId==null,"From Module Name and record id  cannot be null");
        RelationContext.RelationCategory category;
        if(relationCategory==null) {
            category= RelationContext.RelationCategory.NORMAL;
        }else{
            category=RelationContext.RelationCategory.valueOf(relationCategory);
        }
        FacilioModule fromModule= Constants.getModBean().getModule(fromModuleName);

        List<RelationRequestContext> allRelationShip= RelationUtil.getAllRelations(fromModule, false, null, null,true,null, category);
        Map<Long,List<RelationRequestContext>> moduleIdVsRelationShip=allRelationShip.stream().collect(Collectors.groupingBy(i->i.getRelationModule().getModuleId(), HashMap::new, Collectors.toCollection(ArrayList::new)));

        Map<String, FacilioField> customRelationFields= FieldFactory.getAsMap(FieldFactory.getCustomRelationFields());


        List<Long> relationModuleIds=new ArrayList<>();
        Criteria criteria=constructCriteria(moduleIdVsRelationShip,recordId,customRelationFields);
        if(criteria.getConditions()!=null) {
            GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                    .select(Collections.singletonList(customRelationFields.get("moduleId")))
                    .table(ModuleFactory.getCustomRelationModule().getTableName())
                    .andCondition(CriteriaAPI.getCondition(customRelationFields.get("isDeleted"),String.valueOf(false), BooleanOperators.IS));
            builder.andCriteria(criteria);
            builder.groupBy("moduleId");
            List<Map<String, Object>> relationshipData = builder.get();
            relationModuleIds = relationshipData.stream().map(i -> (Long) i.get("moduleId")).collect(Collectors.toList());
        }

        List<Long> relationModuleIdsWithRightPosition=new ArrayList<>();
        Criteria sameModuleRelationCriteria =constructCriteriaForSameModuleRelationShipWithRightPosition(moduleIdVsRelationShip,recordId,customRelationFields);
        if(sameModuleRelationCriteria.getConditions()!=null) {
            GenericSelectRecordBuilder builder1 = new GenericSelectRecordBuilder()
                    .select(Collections.singletonList(customRelationFields.get("moduleId")))
                    .table(ModuleFactory.getCustomRelationModule().getTableName())
                    .andCondition(CriteriaAPI.getCondition(customRelationFields.get("isDeleted"),String.valueOf(false), BooleanOperators.IS));
            builder1.andCriteria(sameModuleRelationCriteria);
            builder1.groupBy("moduleId");
            List<Map<String, Object>> props = builder1.get();
            relationModuleIdsWithRightPosition = props.stream().map(i -> (Long) i.get("moduleId")).collect(Collectors.toList());
        }

        context.put(FacilioConstants.ContextNames.RELATION_LIST,allRelationShip);
        context.put(FacilioConstants.ContextNames.MODULEID_VS_RELATION_MAPPING,moduleIdVsRelationShip);
        context.put(FacilioConstants.ContextNames.RELATION_MODULEID_WITH_DATA,relationModuleIds);
        context.put(FacilioConstants.ContextNames.SAME_MODULE_RELATIONSHIP_WITH_DATA_AT_RIGHT_POSITION,relationModuleIdsWithRightPosition);
        return false;
    }

    public static Criteria constructCriteria(Map<Long,List<RelationRequestContext>> moduleIdVsRelationShip, Long recordId, Map<String,FacilioField> customRelationFields) {
        Criteria dataCriteria = new Criteria();
        for (Long moduleId : moduleIdVsRelationShip.keySet()) {
            List<RelationRequestContext> relationMappingList = moduleIdVsRelationShip.get(moduleId);
            Criteria criteria=new Criteria();
            if (CollectionUtils.isNotEmpty(relationMappingList)) {
                if (relationMappingList.get(0).getFromModuleId() == relationMappingList.get(0).getToModuleId()) {
                    criteria.addAndCondition(CriteriaAPI.getCondition(customRelationFields.get("left"),String.valueOf(recordId),NumberOperators.EQUALS));
                }else{
                    RelationMappingContext.Position position= RelationMappingContext.Position.valueOf(relationMappingList.get(0).getPosition());
                    if(position== RelationMappingContext.Position.LEFT) {
                        criteria.addAndCondition(CriteriaAPI.getCondition(customRelationFields.get("left"),String.valueOf(recordId),NumberOperators.EQUALS));
                    }else{
                        criteria.addAndCondition(CriteriaAPI.getCondition(customRelationFields.get("right"),String.valueOf(recordId),NumberOperators.EQUALS));
                    }
                }
                criteria.addAndCondition(CriteriaAPI.getCondition(customRelationFields.get("moduleId"),String.valueOf(moduleId), NumberOperators.EQUALS));
                dataCriteria.orCriteria(criteria);
            }
        }
        return  dataCriteria;
    }
    public static Criteria constructCriteriaForSameModuleRelationShipWithRightPosition(Map<Long,List<RelationRequestContext>> moduleIdVsRelationShip, Long recordId, Map<String,FacilioField> customRelationFields) {
        Criteria dataCriteria = new Criteria();
        for (Long moduleId : moduleIdVsRelationShip.keySet()) {
            List<RelationRequestContext> relationMappingList = moduleIdVsRelationShip.get(moduleId);
            Criteria criteria=new Criteria();
            if (CollectionUtils.isNotEmpty(relationMappingList)) {
                if (relationMappingList.get(0).getFromModuleId() == relationMappingList.get(0).getToModuleId()) {
                    criteria.addAndCondition(CriteriaAPI.getCondition(customRelationFields.get("right"),String.valueOf(recordId),NumberOperators.EQUALS));
                    criteria.addAndCondition(CriteriaAPI.getCondition(customRelationFields.get("moduleId"),String.valueOf(moduleId), NumberOperators.EQUALS));
                    dataCriteria.orCriteria(criteria);
                }
            }
        }
        return  dataCriteria;
    }

}
