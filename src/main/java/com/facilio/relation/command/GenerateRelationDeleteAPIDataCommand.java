package com.facilio.relation.command;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.relation.context.RelationContext;
import com.facilio.relation.context.RelationMappingContext;
import com.facilio.relation.util.RelationUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GenerateRelationDeleteAPIDataCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        JSONObject data = (JSONObject) context.get(FacilioConstants.ContextNames.DATA);

        String relationMappingLinkName = (String) Constants.getQueryParamOrThrow(context, FacilioConstants.ContextNames.RELATION_NAME);
        long parentId = FacilioUtil.parseLong(Constants.getQueryParamOrThrow(context, FacilioConstants.ContextNames.PARENT_ID));

        RelationMappingContext relationMapping = RelationUtil.getRelationMapping(relationMappingLinkName);
        RelationContext relation = RelationUtil.getRelation(relationMapping.getRelationId(), false);

        String toModuleName = relationMapping.getToModule().getName();
        List associateArray = (ArrayList) data.get(toModuleName);
        if(CollectionUtils.isEmpty(associateArray)) {
            throw new IllegalArgumentException("Invalid data passed");
        }

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioField moduleIdField = FieldFactory.getModuleIdField(relation.getRelationModule());
        FacilioField parentField = modBean.getField(relationMapping.getPositionEnum().getFieldName(), relation.getRelationModule().getName());
        FacilioField childField = modBean.getField(relationMapping.getReversePosition().getFieldName(), relation.getRelationModule().getName());
        Criteria addonCriteria = new Criteria();
        addonCriteria.addAndCondition(CriteriaAPI.getCondition(moduleIdField, String.valueOf(relation.getRelationModuleId()), NumberOperators.EQUALS));
        addonCriteria.addAndCondition(CriteriaAPI.getCondition(parentField, String.valueOf(parentId), NumberOperators.EQUALS));
        addonCriteria.addAndCondition(CriteriaAPI.getCondition(childField, StringUtils.join(associateArray, ","), NumberOperators.EQUALS));

        List<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getIdField(relation.getRelationModule()));
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(relation.getRelationModule().getTableName())
                .select(fields)
                .andCriteria(addonCriteria);

        List<Map<String, Object>> relatedProps = selectRecordBuilder.get();
        if(CollectionUtils.isEmpty(relatedProps)) {
            throw new RESTException(ErrorCode.RESOURCE_NOT_FOUND, "Relationship not present");
        }
        List<Long> ids = new ArrayList<>();
        for(Map<String,Object> relationData : relatedProps) {
            ids.add((Long)relationData.get("id"));
        }

        data.remove(toModuleName);
        data.put(relation.getRelationModule().getName(), ids);
        data.put(FacilioConstants.ContextNames.MODULE_NAME , relation.getRelationModule().getName());
        context.put(FacilioConstants.ContextNames.RELATION_MODULE_NAME, relation.getRelationModule().getName());
        return false;
    }
}
