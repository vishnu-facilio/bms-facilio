package com.facilio.bmsconsoleV3.commands.invoice;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.InvoiceActivityType;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsoleV3.context.invoice.InvoiceContextV3;
import com.facilio.bmsconsoleV3.context.invoice.InvoiceLineItemsContext;
import com.facilio.bmsconsoleV3.util.InvoiceAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.UpdateRecordBuilder;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ReviseStatusInvoiceDetailsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule invoiceModule = modBean.getModule(FacilioConstants.ContextNames.INVOICE);
        List<InvoiceContextV3> invoices = (List<InvoiceContextV3>) context.get("newInvoices");
        List<ModuleBaseWithCustomFields> invoiceContextList = new ArrayList<>();
        if(context.containsKey(FacilioConstants.INVOICE.INVOICE_ID)) {
            Long invoiceId = (Long) context.get(FacilioConstants.INVOICE.INVOICE_ID);
            if (invoiceId == null) {
                throw new IllegalArgumentException(invoiceId+"Invoice Can't Be Empty");
            }
            InvoiceContextV3 oldInvoice = V3RecordAPI.getRecord(FacilioConstants.ContextNames.INVOICE,invoiceId,InvoiceContextV3.class);
            oldInvoice.setInvoiceStatusEnum(InvoiceContextV3.InvoiceStatus.REVISED);
            oldInvoice.setIsInvoiceRevised(true);
            V3RecordAPI.updateRecord(oldInvoice,invoiceModule,modBean.getAllFields(FacilioConstants.ContextNames.INVOICE));
            context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY,FacilioConstants.ContextNames.INVOICE_ACTIVITY);
            JSONObject json = new JSONObject();
            CommonCommandUtil.addActivityToContext(invoiceId, -1, InvoiceActivityType.REVISE_INVOICE, json, (FacilioContext) context);
        }
        return false;
    }
}
