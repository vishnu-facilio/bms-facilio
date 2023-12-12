package com.facilio.bmsconsoleV3.actions;

import com.facilio.bmsconsoleV3.commands.ReadOnlyChainFactoryV3;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.invoice.InvoiceSettingContext;
import com.facilio.bmsconsoleV3.util.InvoiceAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.V3Action;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

public class InvoiceSettingAction extends V3Action {
    public InvoiceSettingContext getInvoiceSetting() {
        return invoiceSetting;
    }

    public void setInvoiceSetting(InvoiceSettingContext invoiceSetting) {
        this.invoiceSetting = invoiceSetting;
    }

    public InvoiceSettingContext invoiceSetting;

    private JSONObject data;

    @Setter
    @Getter
    long id;
    public JSONObject getData() {
        return this.data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }

    public void setData(String key, Object result) {
        if (this.data == null) {
            this.data = new JSONObject();
        }
        this.data.put(key, result);
    }

    public String addOrUpdateInvoiceSetting() throws Exception {

        FacilioChain chain = TransactionChainFactoryV3.addOrUpdateInvoiceSettingChain();
        FacilioContext context = chain.getContext();

        context.put(FacilioConstants.ContextNames.INVOICE_SETTING, getInvoiceSetting());
        chain.execute();

        setData(FacilioConstants.ContextNames.INVOICE_SETTING, context.get(FacilioConstants.ContextNames.INVOICE_SETTING));

        return V3Action.SUCCESS;
    }


    public String fetchInvoiceSetting() throws Exception {

        FacilioChain chain = ReadOnlyChainFactoryV3.getInvoiceSettingData();
        FacilioContext context = chain.getContext();

        context.put(FacilioConstants.ContextNames.INVOICE_SETTING, getInvoiceSetting());
        chain.execute();

        setData(FacilioConstants.ContextNames.INVOICE_SETTING, context.get(FacilioConstants.ContextNames.INVOICE_SETTING));

        return V3Action.SUCCESS;
    }

    public String deleteInvoiceSetting() throws Exception {

        FacilioChain chain = TransactionChainFactoryV3.getDeleteInvoiceSettingChain();

        FacilioContext context = chain.getContext();

        chain.execute();
        setData(FacilioConstants.ContextNames.INVOICE_SETTING, context.get(FacilioConstants.ContextNames.INVOICE_SETTING));


        return V3Action.SUCCESS;
    }
}
