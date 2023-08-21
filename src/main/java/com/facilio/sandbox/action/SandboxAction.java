package com.facilio.sandbox.action;

import com.facilio.accounts.dto.IAMAccount;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.constants.FacilioConstants;
import com.facilio.sandbox.command.SandboxTransactionChainFactory;
import com.facilio.sandbox.context.SandboxConfigContext;
import com.facilio.sandbox.utils.SandboxAPI;
import com.facilio.sandbox.utils.SandboxConstants;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.apache.struts2.ServletActionContext;

import java.util.List;

@Log4j @Setter @Getter
public class SandboxAction extends FacilioAction {

    private static final long serialVersionUID = 1L;
    SandboxConfigContext sandbox;
    private long id = -1;
    private int page = -1;
    private int perPage = -1;
    private long sourceOrgId;
    private long targetOrgId;
    private String domainName;
    private List<Integer> skipComponents;
    private boolean fromAdminTool = false;
    private String search;
    private long fileId = -1L;
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

    public String count() throws Exception{
        long count = SandboxAPI.getSandboxCount(search);
        setResult(SandboxConstants.SANDBOX_COUNT, count);
        return SUCCESS;
    }

    public String add() throws Exception{
        boolean domainAvailability = SandboxAPI.checkSandboxDomainIfExist(sandbox.getSubDomain());
        if(!domainAvailability){
            ServletActionContext.getResponse().setStatus(200);
            setResult(FacilioConstants.ContextNames.MESSAGE, "This domain is already taken");
            return SUCCESS;
        }
        boolean restrictionCondition = SandboxAPI.checkSandboxRestrictionCondition();
        if(restrictionCondition){
            ServletActionContext.getResponse().setStatus(200);
            setResult(FacilioConstants.ContextNames.MESSAGE, "Existing Sandbox Creation is IN-PROGRESS ");
            return SUCCESS;
        }
        FacilioChain createSandboxChain = SandboxTransactionChainFactory.getCreateSandboxChain();
        FacilioContext sandboxContext = createSandboxChain.getContext();
        sandboxContext.put(SandboxConstants.SANDBOX, sandbox);
        createSandboxChain.execute();

        IAMAccount sbAccount = (IAMAccount) sandboxContext.get(SandboxConstants.SANDBOX_ACCOUNT);

        FacilioChain sandboxDataChain = SandboxTransactionChainFactory.getAddSandboxDefaultDataAndCreationInstallationChain();
        FacilioContext sandboxDataContext = sandboxDataChain.getContext();
        sandboxDataContext.put(SandboxConstants.SANDBOX_ORG, sbAccount.getOrg());
        sandboxDataContext.put(SandboxConstants.SANDBOX_ORG_USER, sbAccount.getUser());
        sandboxDataContext.put(FacilioConstants.ContextNames.SIGNUP_INFO, sandboxContext.get(FacilioConstants.ContextNames.SIGNUP_INFO));
        sandboxDataContext.put(SandboxConstants.SANDBOX, sandbox);
        sandboxDataChain.execute();

        setResult(SandboxConstants.SANDBOX_ID, sandbox.getId());
        setResult(PackageConstants.TARGET_ORG_ID, sandbox.getSandboxOrgId());
        setResult(FacilioConstants.ContextNames.MESSAGE, "Sandbox will be created shortly, Please Wait!!");

        return SUCCESS;
    }
    public String rerun() throws Exception{
        AccountUtil.setCurrentAccount(sourceOrgId);
        boolean isNotaSandboxDomain = SandboxAPI.checkSandboxDomainIfExist(domainName);
        if(isNotaSandboxDomain){
            ServletActionContext.getResponse().setStatus(200);
            setResult(FacilioConstants.ContextNames.MESSAGE, "This is not a Sandbox Org, Please Check the Sandbox Domain Name!!");
            return SUCCESS;
        }
        targetOrgId = SandboxAPI.getSandboxTargetOrgId(domainName);
        FacilioChain sandboxDataChain = SandboxTransactionChainFactory.getSandboxDataCreationInstallationChain();
        FacilioContext sandboxDataContext = sandboxDataChain.getContext();
        sandboxDataContext.put(PackageConstants.SOURCE_ORG_ID, sourceOrgId);
        sandboxDataContext.put(PackageConstants.TARGET_ORG_ID, targetOrgId);
        sandboxDataContext.put(PackageConstants.SKIP_COMPONENTS, skipComponents);
        sandboxDataContext.put(PackageConstants.FROM_ADMIN_TOOL, fromAdminTool);
        sandboxDataChain.execute();
        ServletActionContext.getResponse().setStatus(200);
        setResult(FacilioConstants.ContextNames.MESSAGE, "DATA CREATION AND INSTALLATION STARTED");
        AccountUtil.cleanCurrentAccount();
        return SUCCESS;
    }
    public String doInstall() throws Exception{
        AccountUtil.setCurrentAccount(sourceOrgId);
        fileId = SandboxAPI.getRecentPackageId(domainName);
        if(fileId == -1L){
            ServletActionContext.getResponse().setStatus(200);
            setResult(FacilioConstants.ContextNames.MESSAGE, "There is no Recently Created Package, Please Do Rerun Sandbox Instead!!!");
            return SUCCESS;
        }
        boolean isNotaSandboxDomain = SandboxAPI.checkSandboxDomainIfExist(domainName);
        if(isNotaSandboxDomain){
            ServletActionContext.getResponse().setStatus(200);
            setResult(FacilioConstants.ContextNames.MESSAGE, "This is not a Sandbox Org, Please Check the Sandbox Domain Name!!");
            return SUCCESS;
        }
        targetOrgId = SandboxAPI.getSandboxTargetOrgId(domainName);
        FacilioChain sandboxDataChain = SandboxTransactionChainFactory.getSandboxDataInstallationChain();
        FacilioContext sandboxDataContext = sandboxDataChain.getContext();
        sandboxDataContext.put(PackageConstants.SOURCE_ORG_ID, sourceOrgId);
        sandboxDataContext.put(PackageConstants.TARGET_ORG_ID, targetOrgId);
        sandboxDataContext.put(PackageConstants.FROM_ADMIN_TOOL, fromAdminTool);
        sandboxDataContext.put(PackageConstants.FILE_ID, fileId);
        sandboxDataContext.put(PackageConstants.SKIP_COMPONENTS, skipComponents);
        sandboxDataChain.execute();
        ServletActionContext.getResponse().setStatus(200);
        setResult(FacilioConstants.ContextNames.MESSAGE, "DATA CREATION AND INSTALLATION STARTED");
        AccountUtil.cleanCurrentAccount();
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
