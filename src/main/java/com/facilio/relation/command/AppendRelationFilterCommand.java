package com.facilio.relation.command;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.JoinContext;
import com.facilio.modules.fields.FacilioField;
import com.facilio.relation.context.RelationMappingContext;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;

public class AppendRelationFilterCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        RelationMappingContext relationMapping = (RelationMappingContext) context.get(FacilioConstants.ContextNames.RELATION_MAPPING);
        String moduleName = Constants.getModuleName(context);
        long parentId = FacilioUtil.parseLong(Constants.getQueryParamOrThrow(context, "parentId"));

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioField queryField = modBean.getField(relationMapping.getPositionEnum().getFieldName(), moduleName);
        Criteria addonCriteria = new Criteria();
        addonCriteria.addAndCondition(CriteriaAPI.getCondition(queryField, String.valueOf(parentId), NumberOperators.EQUALS));

        List<JoinContext> joinContextList = new ArrayList<>();
        joinContextList.add(new JoinContext(
                                    relationMapping.getToModule(),
                                    modBean.getField(relationMapping.getReversePosition(relationMapping.getPositionEnum()).getFieldName(), moduleName),
                                    FieldFactory.getIdField(relationMapping.getToModule()),
                                    JoinContext.JoinType.INNER_JOIN
                            ));

        if (relationMapping.getToModule().isTrashEnabled()) {
            FacilioModule parentModule = relationMapping.getToModule().getParentModule();
            addonCriteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getIsDeletedField(parentModule), String.valueOf(false), BooleanOperators.IS));
            if(!parentModule.getName().equals(relationMapping.getToModule().getName())) {
                joinContextList.add(new JoinContext(
                        parentModule,
                        FieldFactory.getIdField(parentModule),
                        FieldFactory.getIdField(relationMapping.getToModule()),
                        JoinContext.JoinType.INNER_JOIN
                ));
            }
        }
        context.put(FacilioConstants.ContextNames.FILTER_SERVER_CRITERIA, addonCriteria);
        context.put(Constants.JOINS, joinContextList);
        return false;
    }
}
