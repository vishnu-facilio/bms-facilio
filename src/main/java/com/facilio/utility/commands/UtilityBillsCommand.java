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

public class UtilityBillsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long meterId = (Long) context.get(FacilioConstants.ContextNames.METERID);
        Long startTime = (Long) context.get(FacilioConstants.ContextNames.STARTTIME);
        Long endTime = (Long) context.get(FacilioConstants.ContextNames.ENDTIME);


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

        //check for bill and dispute already exists and mark as delete if already exists
        UtilitySDK.checkAndDeleteDuplicateBill(meterId,startTime,endTime);

        //fetch bill and raise disputes for that bill
        UtilitySDK.getBillsForMeter(meterUid,startTime,endTime);


        return false;
    }
}
