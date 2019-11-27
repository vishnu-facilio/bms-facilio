package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.SLAContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class GetAllSLACommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        if (StringUtils.isNotEmpty(moduleName)) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(moduleName);

            if (module == null) {
                throw new IllegalArgumentException("Invalid module");
            }

            SelectRecordsBuilder<SLAContext> builder = new SelectRecordsBuilder<SLAContext>()
                    .beanClass(SLAContext.class)
                    .module(modBean.getModule(FacilioConstants.ContextNames.SLA_MODULE))
                    .select(modBean.getAllFields(FacilioConstants.ContextNames.SLA_MODULE))
                    .andCondition(CriteriaAPI.getCondition("SLA_MODULE_ID", "slaModuleId", String.valueOf(module.getModuleId()), NumberOperators.EQUALS));
            List<SLAContext> slaContexts = builder.get();

            context.put(FacilioConstants.ContextNames.SLA_LIST, slaContexts);
        }
        return false;
    }
}
