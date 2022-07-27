package com.facilio.relation.command;

import com.facilio.beans.ModuleBean;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
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
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ValidateRelationDataCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

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

        Criteria relationDataCriteria = new Criteria();
        if(relationMapping.getRelationType() == RelationRequestContext.RelationType.ONE_TO_ONE.getIndex()
                || relationMapping.getRelationType() == RelationRequestContext.RelationType.MANY_TO_ONE.getIndex()) {
            if(ids.size() > 1) {
                throw new IllegalArgumentException("Only one relation is allowed");
            }
        }
        else {
            relationDataCriteria.addAndCondition(CriteriaAPI.getCondition(relationMapping.getReversePosition().getColumnName(), relationMapping.getReversePosition().getFieldName(), StringUtils.join(ids, ","), NumberOperators.EQUALS));
        }

        if(relationMapping.getRelationType() == RelationRequestContext.RelationType.ONE_TO_ONE.getIndex()){
            Criteria oneCriteria = new Criteria();
            oneCriteria.addAndCondition(CriteriaAPI.getCondition(relationMapping.getReversePosition().getColumnName(), relationMapping.getReversePosition().getFieldName(), StringUtils.join(ids, ","), NumberOperators.EQUALS));
            oneCriteria.addOrCondition(CriteriaAPI.getCondition(relationMapping.getPositionEnum().getColumnName(), relationMapping.getPositionEnum().getFieldName(), String.valueOf(parentId), NumberOperators.EQUALS));
            relationDataCriteria.andCriteria(oneCriteria);
        }
        else {
            relationDataCriteria.addAndCondition(CriteriaAPI.getCondition(relationMapping.getPositionEnum().getColumnName(), relationMapping.getPositionEnum().getFieldName(), String.valueOf(parentId), NumberOperators.EQUALS));
        }

        relationDataCriteria.addAndCondition(CriteriaAPI.getCondition("MODULEID", "moduleId", String.valueOf(relation.getRelationModule().getModuleId()), NumberOperators.EQUALS));

        if(V3Util.getCountFromModuleAndCriteria(relation.getRelationModule(), relationDataCriteria) > 0) {
            throw new IllegalArgumentException("Relation already exists");
        }

        if(!V3Util.checkifIdsArePresentForModule(ids, relationMapping.getToModule())) {
            throw new IllegalArgumentException("Invalid relation data passed");
        }

        return false;
    }
}
