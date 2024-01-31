package com.facilio.utility.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.meter.V3MeterContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.utility.UtilitySDK;
import com.facilio.utility.context.UtilityIntegrationBillContext;
import com.facilio.utility.context.UtilityIntegrationMeterContext;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class CheckAndRaiseDisputeCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        List<UtilityIntegrationBillContext> billList = (List<UtilityIntegrationBillContext>) (((Map<String,Object>)context.get(FacilioConstants.ContextNames.RECORD_MAP)).get(FacilioConstants.UTILITY_INTEGRATION_BILLS));
        if(CollectionUtils.isNotEmpty(billList))
        for(UtilityIntegrationBillContext bills:billList){
            UtilityIntegrationBillContext bill = V3RecordAPI.getRecord(FacilioConstants.UTILITY_INTEGRATION_BILLS,bills.getId());

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(FacilioConstants.UTILITY_INTEGRATION_METER);
            String moduleName= FacilioConstants.UTILITY_INTEGRATION_METER;
            List<FacilioField> fields = modBean.getAllFields(moduleName);
            //get metetuid from meterid
            if(bill.getUtilityIntegrationMeter()!= null){
                SelectRecordsBuilder<UtilityIntegrationMeterContext> builder = new SelectRecordsBuilder<UtilityIntegrationMeterContext>()
                        .moduleName(moduleName)
                        .select(fields)
                        .beanClass(UtilityIntegrationMeterContext.class)
                        .andCondition(CriteriaAPI.getIdCondition(bill.getUtilityIntegrationMeter().getId(),module))
                        ;

                UtilityIntegrationMeterContext utilityIntegrationMeterContexts = builder.fetchFirst();

                UtilitySDK.checkAndRaiseDisputes(utilityIntegrationMeterContexts.getId(),bill.getBillStartDate(),bill.getBillEndDate());
            }



        }

        return false;
    }
}
