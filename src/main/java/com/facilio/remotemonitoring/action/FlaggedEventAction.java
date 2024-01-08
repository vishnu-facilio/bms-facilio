package com.facilio.remotemonitoring.action;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsoleV3.commands.ReadOnlyChainFactoryV3;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.remotemonitoring.RemoteMonitorConstants;
import com.facilio.remotemonitoring.signup.FlaggedEventModule;
import com.facilio.v3.V3Action;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

import java.util.List;

public class FlaggedEventAction extends V3Action {

    @Getter @Setter
    private Long inhibitReasonId;
    @Getter @Setter
    private List<Long> closeIssueValues;

    @Getter @Setter
    private V3PeopleContext assignToPeople;

    private String _default;
    public String getDefault() {
        return _default;
    }
    public void setDefault(String _default) {
        this._default = _default;
    }
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

    public String assignFlaggedEvent() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.assignFlaggedEvent();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID,getId());
        context.put(FacilioConstants.ContextNames.ASSIGNED_TO_ID,getAssignToPeople());
        chain.execute();
        setData(FacilioConstants.ContextNames.MESSAGE,context.get(FacilioConstants.ContextNames.MESSAGE));
        return SUCCESS;
    }

    public String suspendAlarm() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.suspendAlarm();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID,getId());
        chain.execute();
        setData(FacilioConstants.ContextNames.MESSAGE,context.get(FacilioConstants.ContextNames.MESSAGE));
        return SUCCESS;
    }
    public String fetchPeopleOptions() throws Exception {

        FacilioChain chain = ReadOnlyChainFactoryV3.fetchFlaggedEventPeopleOptions();
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.MODULE_FIELD_NAME, FlaggedEventModule.FLAGGED_EVENT_ASSIGNED_PEOPLE_NAME);
        context.put(FacilioConstants.ContextNames.MODULE_NAME, FlaggedEventModule.MODULE_NAME);

        JSONObject pagination = new JSONObject();
        pagination.put("page", this.getPage());
        pagination.put("perPage", this.getPerPage());

        context.put(FacilioConstants.ContextNames.ID,getId());
        context.put(FacilioConstants.ContextNames.PAGINATION, pagination);
        context.put(FacilioConstants.ContextNames.SEARCH, this.getSearch());
        context.put(FacilioConstants.ContextNames.FILTERS, this.getFilters());
        context.put(FacilioConstants.ContextNames.DEFAULT,this.getDefault());
        context.put(FacilioConstants.ContextNames.EXCLUDE_PARENT_FILTER, this.getExcludeParentFilter());
        context.put(FacilioConstants.ContextNames.CLIENT_FILTER_CRITERIA, this.getClientCriteria());
        context.put(FacilioConstants.ContextNames.ORDER_BY, this.getOrderBy());
        context.put(FacilioConstants.ContextNames.ORDER_TYPE, this.getOrderType());
        context.put(FacilioConstants.ContextNames.WITH_COUNT, this.getWithCount());
        chain.execute(context);

        setData(FacilioConstants.ContextNames.PICKLIST, context.get(FacilioConstants.ContextNames.PICKLIST));
        return SUCCESS;
    }
}