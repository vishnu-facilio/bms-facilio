package com.facilio.relation.command;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.relation.context.RelationMappingContext;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

public class AppendRelationFilterCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        RelationMappingContext relationMapping = (RelationMappingContext) context.get(FacilioConstants.ContextNames.RELATION_MAPPING);
        String moduleName = Constants.getModuleName(context);
        long parentId = FacilioUtil.parseLong(Constants.getQueryParamOrThrow(context, "parentId"));

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioField queryField = modBean.getField(relationMapping.getPositionEnum().getFieldName(), moduleName);
        Condition condition = CriteriaAPI.getCondition(queryField, String.valueOf(parentId), NumberOperators.EQUALS);

        context.put(FacilioConstants.ContextNames.FILTER_SERVER_CRITERIA, condition);
        return false;
    }
}
