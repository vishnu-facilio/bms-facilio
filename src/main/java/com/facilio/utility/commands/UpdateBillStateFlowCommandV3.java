package com.facilio.utility.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsoleV3.context.V3TermsAndConditionContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.EnumOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.utility.context.UtilityDisputeContext;
import com.facilio.utility.context.UtilityIntegrationBillContext;
import com.facilio.utility.context.UtilityIntegrationTariffSlabContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class UpdateBillStateFlowCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<UtilityIntegrationBillContext> billList = recordMap.get(moduleName);
        FacilioModule billModule = modBean.getModule(FacilioConstants.UTILITY_INTEGRATION_BILLS);

        if (CollectionUtils.isNotEmpty(billList)) {
            for (UtilityIntegrationBillContext bill : billList) {

//                FacilioModule disputeModule = modBean.getModule(FacilioConstants.UTILITY_DISPUTE);
//                String disputeModuleName= FacilioConstants.UTILITY_DISPUTE;
//                List<FacilioField> fields = modBean.getAllFields(disputeModuleName);
//
//
//                SelectRecordsBuilder<UtilityDisputeContext> builder = new SelectRecordsBuilder<UtilityDisputeContext>()
//                        .moduleName(disputeModuleName)
//                        .select(fields)
//                        .beanClass(UtilityDisputeContext.class)
//                        .andCondition(CriteriaAPI.getCondition("UTILITY_INTEGRATION_BILL","utilityBill", String.valueOf(bill.getId()), NumberOperators.EQUALS))
//                        .andCondition(CriteriaAPI.getCondition("BILL_STATUS","billStatus",UtilityDisputeContext.BillStatus.RESOLVED.getIndex()+"", EnumOperators.ISN_T))
//                        ;
//
//                List<UtilityDisputeContext> disputeLists = builder.get();
//
//                if(CollectionUtils.isNotEmpty(disputeLists)){
//
//                    if(disputeLists.size() == 1 ){
//
//                        FacilioStatus partialStatus = TicketAPI.getStatus(billModule,"partiallydisputed");
//                        StateFlowRulesAPI.updateState(bill, modBean.getModule(moduleName), partialStatus, false, context);
//
//                    }
//                    else{
//                        FacilioStatus disputedStatus = TicketAPI.getStatus(billModule,"disputed");
//                        StateFlowRulesAPI.updateState(bill, modBean.getModule(moduleName), disputedStatus, false, context);
//                    }
//                }
//                else{
//                    FacilioStatus clearStatus = TicketAPI.getStatus(billModule,"clear");
//                    StateFlowRulesAPI.updateState(bill, modBean.getModule(moduleName), clearStatus, false, context);
//
//                }

                FacilioStatus disputedStatus = TicketAPI.getStatus(billModule,"disputed");
                StateFlowRulesAPI.updateState( bill, modBean.getModule(moduleName),disputedStatus, false, context);
            }
        }

        return false;
    }
}
