package com.facilio.utility.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.EnumOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.utility.context.UtilityDisputeContext;
import com.facilio.utility.context.UtilityIntegrationBillContext;
import com.facilio.utility.context.UtilityIntegrationMeterContext;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;

public class UtilityDisputeResolveCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long recordId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.UTILITY_DISPUTE);
        String moduleName= FacilioConstants.UTILITY_DISPUTE;
        List<FacilioField> fields = modBean.getAllFields(moduleName);

        UtilityDisputeContext utilityDisputeContext = V3RecordAPI.getRecord(moduleName,recordId);

        utilityDisputeContext.setBillStatus(UtilityDisputeContext.BillStatus.RESOLVED.getIntVal());
        V3Util.processAndUpdateSingleRecord(FacilioConstants.UTILITY_DISPUTE, utilityDisputeContext.getId(), FieldUtil.getAsJSON(utilityDisputeContext), null, null, null, null, null,null,null, null,null);
        //V3RecordAPI.updateRecord(utilityDisputeContext,module,fields);

        FacilioStatus resolvedStatus = TicketAPI.getStatus("Resolved");
        StateFlowRulesAPI.updateState(utilityDisputeContext, modBean.getModule(moduleName), resolvedStatus, false, context);


        SelectRecordsBuilder<UtilityDisputeContext> builder = new SelectRecordsBuilder<UtilityDisputeContext>()
                .moduleName(moduleName)
                .select(fields)
                .beanClass(UtilityDisputeContext.class)
                .andCondition(CriteriaAPI.getCondition("UTILITY_INTEGRATION_BILL","utilityBill",String.valueOf(utilityDisputeContext.getUtilityBill().getId()), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("BILL_STATUS","billStatus",UtilityDisputeContext.BillStatus.RESOLVED.getIndex()+"", EnumOperators.ISN_T))
                ;
                ;

        List<UtilityDisputeContext> disputeLists = builder.get();

        List<UtilityDisputeContext> updatedDisputeLists = new ArrayList<>();
        for (UtilityDisputeContext dispute : disputeLists) {
            if (!dispute.equals(utilityDisputeContext)) {
                updatedDisputeLists.add(dispute);
            }
        }
        disputeLists = updatedDisputeLists;

        //bill module status updated
        updateStatus(disputeLists,utilityDisputeContext.getUtilityBill().getId(),context);

        return false;
    }
    private void updateStatus(List<UtilityDisputeContext>disputeLists ,Long id,Context context) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.UTILITY_INTEGRATION_BILLS);
        String moduleName= FacilioConstants.UTILITY_INTEGRATION_BILLS;
        List<FacilioField> fields = modBean.getAllFields(moduleName);

        UtilityIntegrationBillContext utilityIntegrationBillContext = V3RecordAPI.getRecord(moduleName,id);

        if (!disputeLists.isEmpty()) {
            if (disputeLists.size() == 1) {
                utilityIntegrationBillContext.setUtilityBillStatus(UtilityIntegrationBillContext.UtilityBillStatus.PARTIALLYDISPUTED.getIntVal());
                //V3Util.processAndUpdateSingleRecord(FacilioConstants.UTILITY_INTEGRATION_BILLS, utilityIntegrationBillContext.getId(), FieldUtil.getAsJSON(utilityIntegrationBillContext), null, null, null, null, null,null,null, null,null);
                V3RecordAPI.updateRecord(utilityIntegrationBillContext,module,fields);
                FacilioStatus partialStatus = TicketAPI.getStatus(module,"partiallydisputed");
                StateFlowRulesAPI.updateState(utilityIntegrationBillContext, modBean.getModule(moduleName), partialStatus, false, new FacilioContext());



            } else {
                utilityIntegrationBillContext.setUtilityBillStatus(UtilityIntegrationBillContext.UtilityBillStatus.DISPUTED.getIntVal());
                //V3Util.processAndUpdateSingleRecord(FacilioConstants.UTILITY_INTEGRATION_BILLS, utilityIntegrationBillContext.getId(), FieldUtil.getAsJSON(utilityIntegrationBillContext), null, null, null, null, null,null,null, null,null);
                V3RecordAPI.updateRecord(utilityIntegrationBillContext,module,fields);
                FacilioStatus disputedStatus = TicketAPI.getStatus(module,"underDispute");
                StateFlowRulesAPI.updateState(utilityIntegrationBillContext, modBean.getModule(moduleName), disputedStatus, false, new FacilioContext());


            }
        } else {
            utilityIntegrationBillContext.setUtilityBillStatus(UtilityIntegrationBillContext.UtilityBillStatus.CLEAR.getIntVal());
           // V3Util.processAndUpdateSingleRecord(FacilioConstants.UTILITY_INTEGRATION_BILLS, utilityIntegrationBillContext.getId(), FieldUtil.getAsJSON(utilityIntegrationBillContext), null, null, null, null, null,null,null, null,null);
            V3RecordAPI.updateRecord(utilityIntegrationBillContext,module,fields);
            FacilioStatus clearStatus = TicketAPI.getStatus(module,"undisputed");
            StateFlowRulesAPI.updateState(utilityIntegrationBillContext, modBean.getModule(moduleName), clearStatus, false, new FacilioContext());


        }
    }
}
