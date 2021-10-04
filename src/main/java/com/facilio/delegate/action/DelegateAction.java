package com.facilio.delegate.action;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.delegate.context.DelegationContext;
import com.facilio.delegate.context.DelegationType;
import com.facilio.delegate.util.DelegationUtil;

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

    private User user;
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    private long time;
    public long getTime() {
        return time;
    }
    public void setTime(long time) {
        this.time = time;
    }

    private int delegationType;
    public int getDelegationType() {
        return delegationType;
    }
    public void setDelegationType(int delegationType) {
        this.delegationType = delegationType;
    }

    public String testDelegation() throws Exception {
        DelegationUtil.getDelegatedUser(user, time, DelegationType.valueOf(delegationType));

        return SUCCESS;
    }
}
