package com.facilio.delegate.action;

import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.delegate.context.DelegationContext;

public class DelegateAction extends FacilioAction {

    private DelegationContext delegation;
    public DelegationContext getDelegation() {
        return delegation;
    }
    public void setDelegation(DelegationContext delegation) {
        this.delegation = delegation;
    }

    private boolean onlyMyDelegation = false;
    public boolean isOnlyMyDelegation() {
        return onlyMyDelegation;
    }
    public void setOnlyMyDelegation(boolean onlyMyDelegation) {
        this.onlyMyDelegation = onlyMyDelegation;
    }

    private long appId = -1;
    public long getAppId() {
        return appId;
    }
    public void setAppId(long appId) {
        this.appId = appId;
    }

    public String list() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getAllMyDelegationChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ONLY_MY_DELEGATION, onlyMyDelegation);
        context.put(FacilioConstants.ContextNames.APP_ID, appId);
        context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.USER_DELEGATION);
        constructListContext(context);

        chain.execute();

        setResult(FacilioConstants.ContextNames.DELEGATION_LIST, context.get(FacilioConstants.ContextNames.DELEGATION_LIST));

        return SUCCESS;
    }

    public String addOrUpdateDelegation() throws Exception {
        FacilioChain chain = TransactionChainFactory.getAddOrUpdateUserDelegationChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.DELEGATION_CONTEXT, delegation);

        chain.execute();
        setResult(FacilioConstants.ContextNames.DELEGATION_CONTEXT, delegation);
        return SUCCESS;
    }

    private long id = -1;
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String delete() throws Exception {
        FacilioChain chain = TransactionChainFactory.getDeleteUserDelegationChain();
        FacilioContext context = chain.getContext();

        context.put(FacilioConstants.ContextNames.ID, getId());
        chain.execute();

        return SUCCESS;
    }
}
