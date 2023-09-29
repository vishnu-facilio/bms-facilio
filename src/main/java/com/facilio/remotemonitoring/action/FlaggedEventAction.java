package com.facilio.remotemonitoring.action;

import com.facilio.bmsconsoleV3.commands.ReadOnlyChainFactoryV3;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.remotemonitoring.RemoteMonitorConstants;
import com.facilio.v3.V3Action;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class FlaggedEventAction extends V3Action {

    @Getter @Setter
    private Long inhibitReasonId;
    @Getter @Setter
    private List<Long> closeIssueValues;
    public String createWorkorder() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.createFlaggedEventWorkorder();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID,getId());
        chain.execute();
        return SUCCESS;
    }

    public String takeCustody() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.flaggedEventTakeCustody();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID,getId());
        chain.execute();
        return SUCCESS;
    }

    public String passToNextBureau() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.passToNextBureau();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID,getId());
        chain.execute();
        return SUCCESS;
    }

    public String inhibit() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.inhibit();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID,getId());
        context.put(RemoteMonitorConstants.INHIBIT_REASON_ID,getInhibitReasonId());
        chain.execute();
        return SUCCESS;
    }

    public String close() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.closeFlaggedEvent();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID,getId());
        context.put(RemoteMonitorConstants.CLOSE_VALUES,getCloseIssueValues());
        chain.execute();
        return SUCCESS;
    }

    public String closeButtonDetail() throws Exception {
        FacilioChain chain = ReadOnlyChainFactoryV3.closeButtonDetails();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID,getId());
        chain.execute();
        setData(FacilioConstants.ContextNames.MESSAGE,context.get(FacilioConstants.ContextNames.MESSAGE));
        return SUCCESS;
    }

}