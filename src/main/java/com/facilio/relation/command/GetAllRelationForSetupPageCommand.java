package com.facilio.relation.command;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.relation.context.RelationContext;
import com.facilio.relation.context.RelationRequestContext;
import com.facilio.relation.util.RelationUtil;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.List;

public class GetAllRelationForSetupPageCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
        String search = (String) context.get(FacilioConstants.ContextNames.SEARCH);
        int relationCategoryInt = (int) context.getOrDefault(FacilioConstants.Relationship.RELATION_CATEGORY, RelationContext.RelationCategory.NORMAL.getIndex());
        RelationContext.RelationCategory relationCategory = relationCategoryInt <= 0 ? null : RelationContext.RelationCategory.valueOf(relationCategoryInt);
        boolean fromBuilder = (boolean) context.getOrDefault(FacilioConstants.ContextNames.IS_FROM_BUILDER, false);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        if (module == null) {
            throw new IllegalArgumentException("Invalid module");
        }

        List<RelationRequestContext> relationRequests = RelationUtil.getAllRelations(module, true, pagination, search, relationCategory, fromBuilder);
        context.put(FacilioConstants.ContextNames.RELATION_LIST, relationRequests);

        return false;
    }
}
