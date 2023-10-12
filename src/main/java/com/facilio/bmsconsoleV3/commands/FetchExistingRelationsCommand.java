package com.facilio.bmsconsoleV3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.relation.context.RelationContext;
import com.facilio.relation.context.RelationRequestContext;
import com.facilio.relation.util.RelationUtil;
import org.apache.commons.chain.Context;

import java.util.List;

public class FetchExistingRelationsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long fromModuleId = (Long) context.get("fromModuleId");
        Long toModuleId = (Long) context.get("toModuleId");
        int relationType = (int) context.get("relationType");
        int relationCategory = (int) context.get("relationCategory");
        String search = (String) context.get(FacilioConstants.ContextNames.SEARCH);

        if(fromModuleId > 0 && toModuleId > 0 && relationType > 0 && relationCategory > 0) {
            List<RelationRequestContext> relationList = RelationUtil.getAllRelationships(fromModuleId, toModuleId, relationType, relationCategory, search);
            context.put(FacilioConstants.ContextNames.RELATION_LIST, relationList);
        }

        return false;
    }
}
