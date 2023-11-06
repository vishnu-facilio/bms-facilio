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

import java.util.Collections;
import java.util.List;

public class ViewRelationCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        long id = (long) context.get(FacilioConstants.ContextNames.ID);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        if (module == null) {
            throw new IllegalArgumentException("Invalid module");
        }

        RelationContext relation = RelationUtil.getRelation(id, true);
        if (relation == null) {
            throw new IllegalArgumentException("Invalid Relation");
        }
        RelationRequestContext relationRequestContext = RelationUtil.convertToRelationRequest(relation, module.getModuleId());
        if (relation.isVirtual()) {
            RelationUtil.setVirtualRelationData(relation, relationRequestContext);
        }

        context.put(FacilioConstants.ContextNames.RELATION, relationRequestContext);
        return false;
    }
}
