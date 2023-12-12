package com.facilio.bmsconsoleV3.commands.invoice;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.InvoiceActivityType;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsoleV3.context.invoice.InvoiceContextV3;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddInvoiceCloneActivityType extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        if(context.containsKey(FacilioConstants.INVOICE.INVOICE_ID)) {
            Long invoiceId = (Long) context.get(FacilioConstants.INVOICE.INVOICE_ID);
            if (invoiceId == null) {
                throw new IllegalArgumentException(invoiceId+"Invoice Can't Be Empty");
            }
            context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY,FacilioConstants.ContextNames.INVOICE_ACTIVITY);
            InvoiceContextV3 invoiceContextV3 = V3RecordAPI.getRecord(FacilioConstants.ContextNames.INVOICE,invoiceId,InvoiceContextV3.class);
            Long activityInvoiceId = invoiceContextV3.getId();
            if(invoiceContextV3.getGroup() !=  null)
            {
                activityInvoiceId =invoiceContextV3.getGroup().getId();
            }
            JSONObject json = new JSONObject();
            CommonCommandUtil.addActivityToContext(activityInvoiceId, -1, InvoiceActivityType.CLONE_INVOICE, json, (FacilioContext) context);
        }
        return false;
    }
}
