package com.facilio.utility.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.utility.UtilitySDK;
import com.facilio.utility.context.UtilityIntegrationMeterContext;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;

public class ActivateMetersCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long meterId = (long) context.get(FacilioConstants.ContextNames.METERID);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.UTILITY_INTEGRATION_METER);
        String moduleName= FacilioConstants.UTILITY_INTEGRATION_METER;
        List<FacilioField>fields = modBean.getAllFields(moduleName);

        SelectRecordsBuilder<UtilityIntegrationMeterContext> builder = new SelectRecordsBuilder<UtilityIntegrationMeterContext>()
                .moduleName(moduleName)
                .select(fields)
                .beanClass(UtilityIntegrationMeterContext.class)
                .andCondition(CriteriaAPI.getIdCondition(meterId,module))
                ;

        List metersUid = new ArrayList<>();
        List<UtilityIntegrationMeterContext> lists = builder.get();
        for(UtilityIntegrationMeterContext list : lists){
            metersUid.add(list.getMeterUid());
        }

        UtilitySDK.activateMeters(metersUid);



        return false;
    }
}
