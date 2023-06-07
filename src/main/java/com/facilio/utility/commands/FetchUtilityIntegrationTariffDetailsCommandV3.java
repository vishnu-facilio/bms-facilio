package com.facilio.utility.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.utility.context.UtilityIntegrationTariffContext;
import com.facilio.utility.context.UtilityIntegrationTariffSlabContext;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.CommandUtil;
import org.apache.commons.chain.Context;

import java.util.List;

public class FetchUtilityIntegrationTariffDetailsCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long id = Constants.getRecordIds(context).get(0);
        UtilityIntegrationTariffContext tariffContext = (UtilityIntegrationTariffContext) CommandUtil.getModuleData(context, FacilioConstants.UTILITY_INTEGRATION_TARIFF, id);
        if (tariffContext != null) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            String slabItemModuleName = FacilioConstants.UTILITY_INTEGRATION_TARIFF_SLAB;
            List<FacilioField> fields = modBean.getAllFields(slabItemModuleName);

            SelectRecordsBuilder<UtilityIntegrationTariffSlabContext> builder = new SelectRecordsBuilder<UtilityIntegrationTariffSlabContext>()
                    .moduleName(slabItemModuleName)
                    .select(fields)
                    .beanClass(UtilityIntegrationTariffSlabContext.class)
                    .andCondition(CriteriaAPI.getCondition("UTILITY_INTEGRATION_TARIFF","tariff", String.valueOf(tariffContext.getId()),NumberOperators.EQUALS));

            List<UtilityIntegrationTariffSlabContext> list = builder.get();
            tariffContext.setTarriffSlabs(list);

        }
        return false;
    }
}
