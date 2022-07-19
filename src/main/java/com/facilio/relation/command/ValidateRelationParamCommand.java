package com.facilio.relation.command;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.relation.context.RelationContext;
import com.facilio.relation.context.RelationMappingContext;
import com.facilio.relation.util.RelationUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

public class ValidateRelationParamCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        long fromModuleId = FacilioUtil.parseLong(Constants.getQueryParamOrThrow(context, "fromModuleId"));

        String relationModuleName = Constants.getModuleName(context);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule relationModule = modBean.getModule(relationModuleName);
        RelationContext relation = RelationUtil.getRelation(relationModule, true);

        if (relation == null) {
            throw new IllegalArgumentException("Invalid relation");
        }

        RelationMappingContext relationMapping = null;
        for (RelationMappingContext mapping : relation.getMappings()) {
            if (mapping.getFromModuleId() == fromModuleId) {
                relationMapping = mapping;
                break;
            }
        }

        if (relationMapping == null) {
            throw new IllegalArgumentException("Invalid relation");
        }

        context.put(FacilioConstants.ContextNames.RELATION, relation);
        context.put(FacilioConstants.ContextNames.RELATION_MAPPING, relationMapping);

        return false;
    }
}
