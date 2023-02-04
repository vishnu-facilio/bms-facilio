package com.facilio.sandbox.action;

import com.facilio.accounts.dto.Account;
import com.facilio.accounts.dto.IAMAccount;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.sandbox.command.SandboxTransactionChainFactory;
import com.facilio.sandbox.context.SandboxConfigContext;
import com.facilio.sandbox.utils.SandboxAPI;
import com.facilio.sandbox.utils.SandboxConstants;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import java.util.List;

@Log4j @Setter @Getter
public class SandboxAction extends FacilioAction {

    private static final long serialVersionUID = 1L;
    SandboxConfigContext sandbox;
    private long id = -1;
    private int page = -1;
    private int perPage = -1;
    private String search;

    public String list() throws Exception{
        List<SandboxConfigContext> sandboxList = SandboxAPI.getAllSandbox(page, perPage, search);
        setResult(SandboxConstants.SANDBOX_LIST, sandboxList);
        return SUCCESS;
    }

    public String get() throws Exception{
        SandboxConfigContext sandbox = SandboxAPI.getSandboxById(id);
        setResult(SandboxConstants.SANDBOX, sandbox);
        return SUCCESS;
    }

    public String add() throws Exception{
        FacilioChain createSandboxChain = SandboxTransactionChainFactory.getCreateSandboxChain();
        FacilioContext sandboxContext = createSandboxChain.getContext();
        sandboxContext.put(SandboxConstants.SANDBOX, sandbox);
        createSandboxChain.execute();

        IAMAccount sbAccount = (IAMAccount) sandboxContext.get(SandboxConstants.SANDBOX_ACCOUNT);
        Account account = new Account(sbAccount.getOrg(), new User(sbAccount.getUser()));
        AccountUtil.setCurrentAccount(account);

        FacilioChain sandboxDataChain = SandboxTransactionChainFactory.getAddSandboxDefaultDataChain();
        FacilioContext sandboxDataContext = sandboxDataChain.getContext();
        sandboxDataContext.put(FacilioConstants.ContextNames.SIGNUP_INFO, sandboxContext.get(FacilioConstants.ContextNames.SIGNUP_INFO));
        sandboxDataContext.put(SandboxConstants.SANDBOX, sandbox);
        sandboxDataChain.execute();

        setResult(SandboxConstants.SANDBOX_ID, sandbox.getId());
        setResult(FacilioConstants.ContextNames.MESSAGE, "Sandbox added successfully");

        return SUCCESS;
    }

    public String update() throws Exception{
        FacilioChain updateSandboxChain = SandboxTransactionChainFactory.getUpdateSandboxChain();
        FacilioContext sandboxContext = updateSandboxChain.getContext();
        sandboxContext.put(SandboxConstants.SANDBOX, sandbox);
        updateSandboxChain.execute();
        setResult(FacilioConstants.ContextNames.MESSAGE, "Sandbox details updated successfully");

        return SUCCESS;
    }

    public String changeStatus() throws Exception{
        FacilioChain updateSandboxChain = SandboxTransactionChainFactory.getChangeSandboxStatusChain();
        FacilioContext sandboxContext = updateSandboxChain.getContext();
        sandboxContext.put(SandboxConstants.SANDBOX, sandbox);
        updateSandboxChain.execute();
        setResult(FacilioConstants.ContextNames.MESSAGE, "Status changed successfully");

        return SUCCESS;
    }

}
