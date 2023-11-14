package com.facilio.sandbox.action;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.IAMAccount;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.bmsconsole.context.FileContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.utils.PackageFileUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.iam.accounts.util.IAMOrgUtil;
import com.facilio.sandbox.command.SandboxTransactionChainFactory;
import com.facilio.sandbox.context.SandboxConfigContext;
import com.facilio.sandbox.utils.SandboxAPI;
import com.facilio.sandbox.utils.SandboxConstants;
import com.facilio.identity.client.IdentityClient;
import com.facilio.identity.client.bean.UserBean;
import com.facilio.iam.accounts.util.IAMAppUtil;
import com.facilio.iam.accounts.util.IAMUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.List;

@Log4j @Setter @Getter
public class SandboxAction extends FacilioAction {

    private static final long serialVersionUID = 1L;
    SandboxConfigContext sandbox;
    private long id = -1;
    private int status = 1;
    private int page = -1;
    private int perPage = -1;
    private long sourceOrgId = -1;
    private long targetOrgId;
    private String domainName;
    private String sandboxName;
    private List<Integer> skipComponents;
    private boolean fromAdminTool = false;
    private String search;
    private long fileId = -1L;
    private File file;

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
        if(sandbox.getSubDomain().isEmpty()){
            ServletActionContext.getResponse().setStatus(200);
            setResult(FacilioConstants.ContextNames.MESSAGE, "Sandbox Domain can't be Empty");
            return SUCCESS;
        }
        boolean domainAvailability = SandboxAPI.checkSandboxDomainIfExist(sandbox.getSubDomain());
        if(!domainAvailability){
            ServletActionContext.getResponse().setStatus(200);
            setResult(FacilioConstants.ContextNames.MESSAGE, "This domain is already taken ");
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

        setResult(SandboxConstants.SANDBOX_ID, sandbox.getId());
        return SUCCESS;
    }
    public String sandboxCreation() throws Exception{
        PackageFileUtil.accountSwitch(sourceOrgId);
        boolean isNotaSandboxDomain = SandboxAPI.checkSandboxDomainIfExistForRerun(domainName);
        if (isNotaSandboxDomain) {
            ServletActionContext.getResponse().setStatus(200);
            setResult(FacilioConstants.ContextNames.MESSAGE, "This is not the Correct Sandbox Domain, Please Check the Sandbox Domain Name");
            return SUCCESS;
        }
        long targetOrgId = SandboxAPI.getSandboxTargetOrgId(domainName);
        if (targetOrgId != 0L) {
            ServletActionContext.getResponse().setStatus(200);
            setResult(FacilioConstants.ContextNames.MESSAGE, "Already Sandbox Org Created For this Domain Please Do Rerun or Install");
            return SUCCESS;
        }
        SandboxConfigContext sandboxConfigContext = SandboxAPI.getSandboxByDomainName(domainName);
        FacilioChain createSandboxChain = SandboxTransactionChainFactory.getSandboxOrgCreationChain();
        FacilioContext sandboxContext = createSandboxChain.getContext();
        sandboxContext.put(SandboxConstants.SANDBOX_ID, sandboxConfigContext.getId());
        sandboxContext.put(PackageConstants.SOURCE_ORG_ID, sandboxConfigContext.getOrgId());
        sandboxContext.put(PackageConstants.SANDBOX_DOMAIN_NAME, sandboxConfigContext.getSubDomain());
        createSandboxChain.execute();

        setResult(FacilioConstants.ContextNames.MESSAGE, "DATA CREATION AND INSTALLATION STARTED");
        return SUCCESS;
    }
    public String rerun() throws Exception{
        PackageFileUtil.accountSwitch(sourceOrgId);
        boolean isNotaSandboxDomain = SandboxAPI.checkSandboxDomainIfExist(domainName);
        if(isNotaSandboxDomain){
            ServletActionContext.getResponse().setStatus(200);
            setResult(FacilioConstants.ContextNames.MESSAGE, "This is not the Correct Sandbox Domain or Sandbox might not be Created at all, Please Check the Sandbox Domain Name or check if Sandbox Org is Created");
            return SUCCESS;
        }
        boolean restrictionCondition = SandboxAPI.checkSandboxRestrictionConditionForRerun(domainName);
        if(restrictionCondition){
            ServletActionContext.getResponse().setStatus(200);
            setResult(FacilioConstants.ContextNames.MESSAGE, "Sandbox Creation is in Pending or Failed State For This Domain");
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
        PackageFileUtil.accountSwitch(sourceOrgId);
        if(file != null){
            FileContext fileContext = PackageFileUtil.addFileToStore(file, null);
            fileId = fileContext.getFileId();
        }else{
            fileId = SandboxAPI.getRecentPackageId(domainName);
        }
        if(fileId == -1L){
            ServletActionContext.getResponse().setStatus(200);
            setResult(FacilioConstants.ContextNames.MESSAGE, "There is no Recently Created Package, Please Do Rerun Sandbox Instead!!!");
            return SUCCESS;
        }
        boolean isNotaSandboxDomain = SandboxAPI.checkSandboxDomainIfExist(domainName);
        if(isNotaSandboxDomain){
            ServletActionContext.getResponse().setStatus(200);
            setResult(FacilioConstants.ContextNames.MESSAGE, "This is not the Correct Sandbox Domain or Sandbox might not be Created at all, Please Check the Sandbox Domain Name or check if Sandbox Org is Created");
            return SUCCESS;
        }
        boolean restrictionCondition = SandboxAPI.checkSandboxRestrictionConditionForRerun(domainName);
        if(restrictionCondition){
            ServletActionContext.getResponse().setStatus(200);
            setResult(FacilioConstants.ContextNames.MESSAGE, "Sandbox Creation is in Pending or Failed State For This Domain");
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
        setResult(FacilioConstants.ContextNames.MESSAGE, "DATA INSTALLATION STARTED");
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
        SandboxConfigContext sandboxConfigContext = new SandboxConfigContext();
        sandboxConfigContext.setId(id);
        sandboxConfigContext.setStatus(status);
        FacilioChain updateSandboxChain = SandboxTransactionChainFactory.getChangeSandboxStatusChain();
        FacilioContext sandboxContext = updateSandboxChain.getContext();
        sandboxContext.put(SandboxConstants.SANDBOX, sandboxConfigContext);
        updateSandboxChain.execute();
        setResult(FacilioConstants.ContextNames.MESSAGE, "Status changed successfully");

        return SUCCESS;
    }

    public String goToSandbox() throws Exception {
        String redirectUrl = "/" + sandboxName;
        User currentUser = AccountUtil.getCurrentUser();
        Organization sandboxOrg = IAMUtil.getOrgBean().getOrgv2(sandboxName, Organization.OrgType.SANDBOX);

        Organization currentOrg = AccountUtil.getCurrentOrg();
        String appDomainName = currentOrg != null ? currentOrg.getDomain() + "." + FacilioProperties.getSandboxSubDomain() : null;
        AppDomain appDomainObj = StringUtils.isNotEmpty(appDomainName) ? IAMAppUtil.getAppDomain(appDomainName) : null;
        com.facilio.identity.client.dto.AppDomain appDomain = appDomainObj != null ?
                IdentityClient.getDefaultInstance().getAppDomainBean().getAppDomain(appDomainObj.getDomain()) : null;

        UserBean userBean = IdentityClient.getDefaultInstance().getUserBean();
        String sandboxUrl = userBean.generateSandboxLoginRequest(sandboxOrg.getOrgId(), currentUser.getEmail(), appDomain, redirectUrl);

        HttpServletResponse response = ServletActionContext.getResponse();
        response.sendRedirect(sandboxUrl);

        return SUCCESS;
    }
}
