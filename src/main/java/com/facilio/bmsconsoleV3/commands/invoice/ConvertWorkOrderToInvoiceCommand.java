package com.facilio.bmsconsoleV3.commands.invoice;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsoleV3.context.V3ClientContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.context.invoice.InvoiceContextV3;
import com.facilio.bmsconsoleV3.context.invoice.InvoiceLineItemsContext;
import com.facilio.bmsconsoleV3.context.quotation.QuotationContext;
import com.facilio.bmsconsoleV3.context.quotation.QuotationLineItemsContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.SelectRecordsBuilder;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConvertWorkOrderToInvoiceCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Integer workOrderId = (Integer) context.get(FacilioConstants.ContextNames.RECORD_ID);
        Integer invoiceType = (Integer)  context.get(FacilioConstants.ContextNames.TYPE);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");


        if(workOrderId !=null) {

            InvoiceContextV3 invoiceContextV3 = new InvoiceContextV3();

            V3WorkOrderContext workOrderContext = V3RecordAPI.getRecord(FacilioConstants.ContextNames.WORK_ORDER,workOrderId,V3WorkOrderContext.class);
            invoiceContextV3.setWorkorder(workOrderContext);

           if(invoiceType.equals(InvoiceContextV3.InvoiceType.VENDOR.getIndex()))
            {
                invoiceContextV3.setInvoiceType(InvoiceContextV3.InvoiceType.VENDOR.getIndex());
                invoiceContextV3.setVendor(workOrderContext.getVendor());
            }
            else if(invoiceType.equals(InvoiceContextV3.InvoiceType.TENANT.getIndex()))
            {
                invoiceContextV3.setInvoiceType(InvoiceContextV3.InvoiceType.TENANT.getIndex());
                invoiceContextV3.setTenant(workOrderContext.getTenant());
            }
            else if(invoiceType.equals(InvoiceContextV3.InvoiceType.CLIENT.getIndex()))
            {
                invoiceContextV3.setInvoiceType(InvoiceContextV3.InvoiceType.CLIENT.getIndex());

                if(workOrderContext.getClient() != null)
                {
                    V3ClientContext v3ClientContext = new V3ClientContext();
                    v3ClientContext.setId(workOrderContext.getClient().getId());
                    invoiceContextV3.setClient(v3ClientContext);
                }

            }


            context.put(FacilioConstants.ContextNames.RECORD, invoiceContextV3);
        }

        return false;
    }

}
