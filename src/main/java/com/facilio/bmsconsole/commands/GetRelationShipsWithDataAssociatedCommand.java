package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.RelationshipContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.EnumOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.relation.context.RelationContext;
import com.facilio.relation.context.RelationRequestContext;
import com.facilio.relation.util.RelationUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class GetRelationShipsWithDataAssociatedCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String fromModuleName= (String) context.getOrDefault(FacilioConstants.ContextNames.MODULE_NAME,null);
        Long recordId= (Long) context.getOrDefault(FacilioConstants.ContextNames.RECORD_ID,null);
        Integer relationCategory= (Integer) context.getOrDefault(FacilioConstants.ContextNames.RELATION_CATEGORY_NAME,null);
        FacilioUtil.throwIllegalArgumentException(StringUtils.isEmpty(fromModuleName) || recordId==null || relationCategory==null,"From Module Name and record id and relationCategoryName cannot be null");
        RelationContext.RelationCategory category=RelationContext.RelationCategory.valueOf(relationCategory);
        JSONObject pagination = (JSONObject) context.getOrDefault(FacilioConstants.ContextNames.PAGINATION,null);
        int page=1;
        int perPage = 10;
        int offset = 0;
        if (pagination != null) {
            page = (int) ((int)pagination.get("page") <=0 ? 1 : pagination.get("page"));
            perPage = (int) ((int)pagination.get("perPage") <=0 ? 10 : pagination.get("perPage"));
            offset = ((page-1) * perPage);
            if (offset < 0) {
                offset = 0;}
        }
        context.put(FacilioConstants.ContextNames.RELATIONSHIP_LIST,getRelationShips(perPage,offset,fromModuleName,recordId,category));
        return false;
    }
    public static List<RelationRequestContext> getRelationShips(int perPage , int offset, String moduleName,Long recordId,RelationContext.RelationCategory category) throws Exception{
        FacilioModule relationShipModule=ModuleFactory.getRelationModule();
        FacilioModule relationMappingShipModule=ModuleFactory.getRelationMappingModule();
        FacilioModule customRelationModule=ModuleFactory.getCustomRelationModule();
        FacilioModule fromModule= Constants.getModBean().getModule(moduleName);


        Map<String,FacilioField> relationMappingFieldsMap=FieldFactory.getAsMap(FieldFactory.getRelationMappingFields());
        Map<String,FacilioField> customRelationMap=FieldFactory.getAsMap(FieldFactory.getCustomRelationFields());

        List<FacilioField> selectedFields=FieldFactory.getRelationFields();
        Map<String,FacilioField> relationFields=FieldFactory.getAsMap(selectedFields);

        Criteria criteria =new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(customRelationMap.get("left"), String.valueOf(recordId),NumberOperators.EQUALS));
        criteria.addOrCondition(CriteriaAPI.getCondition(customRelationMap.get("right"), String.valueOf(recordId),NumberOperators.EQUALS));

        GenericSelectRecordBuilder builder =new GenericSelectRecordBuilder()
                .select(selectedFields)
                .table(relationShipModule.getTableName())
                .innerJoin(relationMappingShipModule.getTableName())
                .on(relationShipModule.getTableName()+".ID = "+relationMappingShipModule.getTableName()+".RELATION_ID")
                .innerJoin(customRelationModule.getTableName())
                .on(customRelationModule.getTableName()+".MODULEID = "+relationShipModule.getTableName()+".RELATION_MODULE_ID")
                .andCondition(CriteriaAPI.getCondition(relationMappingFieldsMap.get("fromModuleId"),String.valueOf(fromModule.getModuleId()), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(relationFields.get("relationCategory"),String.valueOf(category.getIndex()), EnumOperators.IS))
                .andCriteria(criteria)
                .groupBy(relationShipModule.getTableName()+".ID")
                .offset(offset)
                .limit(perPage);

        List<Map<String, Object>> props = builder.get();
        List<RelationContext> relationships = new ArrayList<RelationContext>();

        for(Map<String,Object> prop:props){
            RelationContext relation = FieldUtil.getAsBeanFromMap(prop, RelationContext.class);
            if (relation != null) {
                RelationUtil.fillRelation(Collections.singletonList(relation));
            }
            relationships.add(relation);
        }
        List<RelationRequestContext> relationRequests = RelationUtil.convertToRelationRequest(relationships, fromModule.getModuleId());
        return relationRequests;
    }
}
