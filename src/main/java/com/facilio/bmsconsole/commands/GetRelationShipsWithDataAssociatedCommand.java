package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.RelationshipContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetRelationShipsWithDataAssociatedCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String fromModuleName= (String) context.getOrDefault(FacilioConstants.ContextNames.MODULE_NAME,null);
        Long recordId= (Long) context.getOrDefault(FacilioConstants.ContextNames.RECORD_ID,null);
        FacilioUtil.throwIllegalArgumentException(StringUtils.isEmpty(fromModuleName) || recordId==null,"From Module Name and record id cannot be null");
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
        context.put(FacilioConstants.ContextNames.RELATIONSHIP_LIST,getRelationShips(perPage,offset,fromModuleName,recordId));
        return false;
    }
    public static List<RelationshipContext> getRelationShips(int perPage , int offset, String moduleName,Long recordId) throws Exception{
        FacilioModule relationShipModule=ModuleFactory.getRelationModule();
        FacilioModule relationMappingShipModule=ModuleFactory.getRelationMappingModule();
        FacilioModule customRelationModule=ModuleFactory.getCustomRelationModule();
        FacilioModule fromModule= Constants.getModBean().getModule(moduleName);


        Map<String,FacilioField> relationMappimgFieldsMap=FieldFactory.getAsMap(FieldFactory.getRelationMappingFields());
        Map<String,FacilioField> customRelationMap=FieldFactory.getAsMap(FieldFactory.getCustomRelationFields());

        Criteria criteria =new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(customRelationMap.get("left"), String.valueOf(recordId),NumberOperators.EQUALS));
        criteria.addOrCondition(CriteriaAPI.getCondition(customRelationMap.get("right"), String.valueOf(recordId),NumberOperators.EQUALS));

        GenericSelectRecordBuilder builder =new GenericSelectRecordBuilder()
                .select(FieldFactory.getRelationFields())
                .table(relationShipModule.getTableName())
                .innerJoin(relationMappingShipModule.getTableName())
                .on(relationShipModule.getTableName()+".ID = "+relationMappingShipModule.getTableName()+".RELATION_ID")
                .innerJoin(customRelationModule.getTableName())
                .on(customRelationModule.getTableName()+".MODULEID = "+relationShipModule.getTableName()+".RELATION_MODULE_ID")
                .andCondition(CriteriaAPI.getCondition(relationMappimgFieldsMap.get("fromModuleId"),String.valueOf(fromModule.getModuleId()), NumberOperators.EQUALS))
                .andCriteria(criteria)
                .groupBy(relationShipModule.getTableName()+".ID")
                .offset(offset)
                .limit(perPage);

        List<Map<String, Object>> props = builder.get();
        List<RelationshipContext> relationships = new ArrayList<RelationshipContext>();
        if (CollectionUtils.isNotEmpty(props)) {
            relationships.addAll(FieldUtil.getAsBeanListFromMapList(props, RelationshipContext.class));
        }
        return relationships;
    }
}
