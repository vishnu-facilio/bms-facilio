package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.manager.NamedCriteria;
import com.facilio.db.criteria.manager.NamedCriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;

public class DeleteNamedCriteriaCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long id = (Long) context.get(FacilioConstants.ContextNames.ID);
        NamedCriteria namedCriteria = NamedCriteriaAPI.getNamedCriteria(id);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(namedCriteria.getNamedCriteriaModuleId());
        context.put(FacilioConstants.ContextNames.NAMED_CRITERIA,namedCriteria);
        context.put(FacilioConstants.ContextNames.MODULE_NAME,module.getName());
        if (id != null) {
            NamedCriteriaAPI.deleteCriteria(id);
        }
        return false;
    }
}
