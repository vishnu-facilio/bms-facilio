package com.facilio.bmsconsoleV3.commands.invoice;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.invoice.InvoiceContextV3;

import com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddInvoiceGroupCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<InvoiceContextV3> invoiceContextList = new ArrayList<>();
        HashMap<String,Object> recordMap = (HashMap<String, Object>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        if(recordMap.containsKey(FacilioConstants.ContextNames.INVOICE)) {
            invoiceContextList.addAll((List<InvoiceContextV3>) recordMap.get(FacilioConstants.ContextNames.INVOICE));
        }
       else if(recordMap.containsKey(FacilioConstants.INVOICE.INVOICE_LIST)) {
           invoiceContextList.addAll( (List<InvoiceContextV3>)recordMap.get(FacilioConstants.INVOICE.INVOICE_LIST));
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule invoiceModule = modBean.getModule(FacilioConstants.ContextNames.INVOICE);

        if(invoiceContextList == null || invoiceContextList.isEmpty()){
            throw new IllegalArgumentException("No Invoice Available");
        }
        for (InvoiceContextV3 invoice : invoiceContextList) {
            if (invoice != null && invoice.getGroup() == null) {
                InvoiceContextV3 gp = new InvoiceContextV3();
                gp.setId(invoice.getId());
                invoice.setGroup(gp);
                invoice.setInvoiceVersion(1.0);
            }
            //Invoice number
            if(invoice.getInvoiceNumber() == null)
            {
                long localId = invoice.getLocalId();
                invoice.setInvoiceNumber(String.valueOf(localId));
            }
            FacilioField invoiceNumberField = modBean.getField("invoiceNumber",FacilioConstants.ContextNames.INVOICE);
            FacilioField groupField = modBean.getField("group",FacilioConstants.ContextNames.INVOICE);
            FacilioField invoiceVersionField = modBean.getField("invoiceVersion",FacilioConstants.ContextNames.INVOICE);
            List<FacilioField> fieldList = new ArrayList<>();
            fieldList.add(invoiceNumberField);
            fieldList.add(groupField);
            fieldList.add(invoiceVersionField);
            V3RecordAPI.updateRecord(invoice,invoiceModule,fieldList);
        }
        if(recordMap.containsKey(FacilioConstants.ContextNames.INVOICE)){
            recordMap.put(FacilioConstants.ContextNames.INVOICE,invoiceContextList);
        }
        else if(recordMap.containsKey(FacilioConstants.INVOICE.INVOICE_LIST)) {
            recordMap.put(FacilioConstants.INVOICE.INVOICE_LIST,invoiceContextList);
        }
        context.put(FacilioConstants.ContextNames.RECORD_MAP,recordMap);
        return false;
    }
}
