package com.facilio.bmsconsoleV3.context.spacebooking;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.actions.ColorPaletteContext;
import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.bmsconsole.homepage.HomePage;
import com.facilio.bmsconsoleV3.commands.ReadOnlyChainFactoryV3;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.V3Action;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter @Setter
public class policyAction extends V3Action {

    private static final long serialVersionUID = 1L;

public V3SpaceBookingPolicyContext getBookingpolicy() {
        return bookingpolicy;
    }

    public void setBookingpolicy(V3SpaceBookingPolicyContext bookingpolicy) {
        this.bookingpolicy = bookingpolicy;
    }

    @Getter @Setter
    private V3SpaceBookingPolicyContext bookingpolicy;




    public String addPolicy() throws Exception {

        FacilioChain chain = TransactionChainFactoryV3.getAddPolicyChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.SpaceBooking.SPACE_BOOKING_POLICY, bookingpolicy);
        chain.execute();
        setData(FacilioConstants.ContextNames.SpaceBooking.SPACE_BOOKING_POLICY, context.get(FacilioConstants.ContextNames.SpaceBooking.SPACE_BOOKING_POLICY));
        return SUCCESS;
    }

//    public String addPolicy () throws Exception {
//        FacilioChain chain = TransactionChainFactoryV3.getAddPolicyChain();
//        FacilioContext context = chain.getContext();
//        context.put(FacilioConstants.ContextNames.SpaceBooking.SPACE_BOOKING_POLICY, bookingpolicy);
//        chain.execute();
//        setResult(FacilioConstants.ContextNames.SpaceBooking.SPACE_BOOKING_POLICY, context.get(FacilioConstants.ContextNames.SpaceBooking.SPACE_BOOKING_POLICY));
//        return SUCCESS;
//    }

}
