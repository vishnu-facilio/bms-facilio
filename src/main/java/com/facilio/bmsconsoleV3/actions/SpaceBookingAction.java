package com.facilio.bmsconsoleV3.actions;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.commands.ReadOnlyChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.V3Action;

import java.util.List;

public class SpaceBookingAction extends V3Action {
    private static final long serialVersionUID = 1L;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Long getExtendTime() {
        return extendTime;
    }

    public void setExtendTime(Long extendTime) {
        this.extendTime = extendTime;
    }

    private String action;
    private Long extendTime;


    public List<Long> getSpaceBookingIds() {
        return spaceBookingIds;
    }

    public void setSpaceBookingIds(List<Long> spaceBookingIds) {
        this.spaceBookingIds = spaceBookingIds;
    }

    private List<Long> spaceBookingIds;

    public String updateBookingAction() throws Exception {

        FacilioChain chain = TransactionChainFactory.getSpaceBookingActionChain();

        FacilioContext context = chain.getContext();

        context.put(FacilioConstants.ContextNames.ACTION, action);
        context.put(FacilioConstants.ContextNames.SpaceBooking.EXTEND_TIME, extendTime);
        context.put(FacilioConstants.ContextNames.ID, spaceBookingIds);

        chain.execute();

        setData(FacilioConstants.ContextNames.SPACE_BOOKING, context.get(FacilioConstants.ContextNames.SPACE_BOOKING));

        return SUCCESS;
    }
}
