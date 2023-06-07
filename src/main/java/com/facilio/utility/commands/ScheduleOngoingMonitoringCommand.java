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

import java.util.List;

public class ScheduleOngoingMonitoringCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long meterId = (Long) context.get(FacilioConstants.ContextNames.METERID);
        String freq = (String) context.get(FacilioConstants.FREQUENCY);
        String prepay = (String) context.get(FacilioConstants.PREPAY);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.UTILITY_INTEGRATION_METER);
        String moduleName= FacilioConstants.UTILITY_INTEGRATION_METER;
        List<FacilioField> fields = modBean.getAllFields(moduleName);
        //get metetuid from meterid

        SelectRecordsBuilder<UtilityIntegrationMeterContext> builder = new SelectRecordsBuilder<UtilityIntegrationMeterContext>()
                .moduleName(moduleName)
                .select(fields)
                .beanClass(UtilityIntegrationMeterContext.class)
                .andCondition(CriteriaAPI.getIdCondition(meterId,module))
                ;

        UtilityIntegrationMeterContext utilityIntegrationMeterContexts = builder.fetchFirst();

            String meterUid = utilityIntegrationMeterContexts.getMeterUid();
            //schedule ongoing monitoring and update ongoingmonitoring in  meter table
            UtilitySDK.enableOnGoingMonitoring(meterUid,freq,prepay);

        return false;
    }
}
