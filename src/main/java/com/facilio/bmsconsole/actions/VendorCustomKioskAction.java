package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import lombok.Getter;
import lombok.Setter;

public class VendorCustomKioskAction extends FacilioAction {

    @Getter@Setter
    public long id;
    public String vendorCheckout () throws Exception {

        FacilioChain chain = TransactionChainFactory.getKioskVendorCheckoutChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.VENDOR_CONTACT, id);
        chain.execute();

        setResult(FacilioConstants.ContextNames.VISITOR_LOG,chain.getContext().get(FacilioConstants.ContextNames.VISITOR_LOG));

        return SUCCESS;
    }
}