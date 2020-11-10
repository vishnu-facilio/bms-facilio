package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.manager.NamedCriteria;
import com.facilio.db.criteria.manager.NamedCriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

public class AddNamedCriteriaCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        NamedCriteria namedCriteria = (NamedCriteria) context.get(FacilioConstants.ContextNames.NAMED_CRITERIA);
        if (StringUtils.isNotEmpty(moduleName) && namedCriteria != null) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(moduleName);
            if (module == null) {
                throw new IllegalArgumentException("Invalid module");
            }

            namedCriteria.setNamedCriteriaModuleId(module.getModuleId());
            NamedCriteriaAPI.addOrUpdateNamedCriteria(namedCriteria);
        }
        return false;
    }
}
