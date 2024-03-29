package com.facilio.componentpackage.action;

import com.facilio.accounts.bean.OrgBean;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.FileContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.componentpackage.command.PackageChainFactory;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.context.PackageContext;
import com.facilio.componentpackage.context.PackageFolderContext;
import com.facilio.componentpackage.utils.PackageFileUtil;
import com.facilio.componentpackage.utils.PackageUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter @Setter @Log4j
public class PackageAction extends FacilioAction {
    private Map<String, String> oldVsNewPeopleMailMap;
    private String oldVsNewPeopleMail;
    private boolean fromAdminTool = false;
    private boolean patchInstall = false;
    private List<Integer> skipComponents;
    private int packageType = 1;
    private String displayName;
    private Long sourceOrgId;
    private Long targetOrgId;
    private long packageId;
    private File file;

    private Long orgId;
    private String name;
    private String value;

    public String getDisplayName() {
        if(StringUtils.isEmpty(displayName)) {
            displayName = "package_" + sourceOrgId + "_" + System.currentTimeMillis();
        }
        return displayName;
    }

    public List<Integer> getSkipComponents() {
        if (CollectionUtils.isEmpty(skipComponents)) {
            skipComponents = new ArrayList<>();
        }
        return skipComponents;
    }

    public String createAndInstallPackage() throws Exception{
        AccountUtil.setCurrentAccount(sourceOrgId);

        LOGGER.info("####Sandbox - Initiating Package creation");
        FacilioChain createPackageChain = PackageChainFactory.getCreatePackageChain();
        FacilioContext context = createPackageChain.getContext();
        context.put(PackageConstants.DISPLAY_NAME, getDisplayName());
        context.put(PackageConstants.SOURCE_ORG_ID, sourceOrgId);
        context.put(PackageConstants.TARGET_ORG_ID, targetOrgId);
        context.put(PackageConstants.FROM_ADMIN_TOOL, fromAdminTool);
        context.put(PackageConstants.SKIP_COMPONENTS, getSkipComponents());
        context.put(PackageConstants.PACKAGE_TYPE, PackageContext.PackageType.valueOf(packageType));
        createPackageChain.execute();
        LOGGER.info("####Sandbox - Completed Package creation");

        AccountUtil.cleanCurrentAccount();
        AccountUtil.setCurrentAccount(targetOrgId);

        LOGGER.info("####Sandbox - Initiating Package Deployment");
        FacilioChain deployPackageChain = PackageChainFactory.getDeployPackageChain();
        FacilioContext deployContext = deployPackageChain.getContext();
        deployContext.put(PackageConstants.FILE_ID, (Long) context.get(PackageConstants.FILE_ID));
        deployContext.put(PackageConstants.SOURCE_ORG_ID, sourceOrgId);
        deployContext.put(PackageConstants.TARGET_ORG_ID, targetOrgId);
        deployContext.put(PackageConstants.FROM_ADMIN_TOOL, fromAdminTool);
        deployPackageChain.execute();
        LOGGER.info("####Sandbox - Completed Package Deployment");
        CommonCommandUtil.updateOrgInfo("metaMigrationStatus", String.valueOf(1));
        ServletActionContext.getResponse().setStatus(200);
        setResult("result", "success");
        AccountUtil.cleanCurrentAccount();

        return SUCCESS;
    }

    public String createPackage() throws Exception{
        AccountUtil.setCurrentAccount(sourceOrgId);

        LOGGER.info("####Sandbox - Initiating Package creation");
        FacilioChain createPackageChain = PackageChainFactory.getCreatePackageChain();
        FacilioContext context = createPackageChain.getContext();
        context.put(PackageConstants.DISPLAY_NAME, getDisplayName());
        context.put(PackageConstants.SOURCE_ORG_ID, sourceOrgId);
        context.put(PackageConstants.FROM_ADMIN_TOOL, fromAdminTool);
        context.put(PackageConstants.SKIP_COMPONENTS, getSkipComponents());
        context.put(PackageConstants.PACKAGE_TYPE, PackageContext.PackageType.valueOf(packageType));
        createPackageChain.execute();
        LOGGER.info("####Sandbox - Completed Package creation");

        setResult(PackageConstants.DOWNLOAD_URL, context.get(PackageConstants.DOWNLOAD_URL));
        setResult(PackageConstants.FILE_ID, context.get(PackageConstants.FILE_ID));
        setResult("result", "success");

        ServletActionContext.getResponse().setStatus(200);
        AccountUtil.cleanCurrentAccount();

        return SUCCESS;
    }

    public String installPackage() throws Exception{
        AccountUtil.setCurrentAccount(targetOrgId);

        if (StringUtils.isNotEmpty(oldVsNewPeopleMail)) {
            ObjectMapper objectMapper = new ObjectMapper();
            oldVsNewPeopleMailMap = objectMapper.readValue(oldVsNewPeopleMail, Map.class);

            PackageUtil.setUserConfigFromAdminTool();
            PackageUtil.addOldVsNewPeopleMail(oldVsNewPeopleMailMap);
        }

        LOGGER.info("####Sandbox - Initiating Package Deployment");
        FacilioChain deployPackageChain = PackageChainFactory.getDeployPackageChain();
        FacilioContext deployContext = deployPackageChain.getContext();
        try {
            deployContext.put(PackageConstants.FILE, file);
            deployContext.put(PackageConstants.PACKAGE_ID, packageId);
            deployContext.put(PackageConstants.PATCH_INSTALL, patchInstall);
            deployContext.put(PackageConstants.TARGET_ORG_ID, targetOrgId);
            deployContext.put(PackageConstants.FROM_ADMIN_TOOL, fromAdminTool);
            deployPackageChain.execute();
            LOGGER.info("####Sandbox - Completed Package Deployment");

            CommonCommandUtil.updateOrgInfo("metaMigrationStatus", String.valueOf(1));
            ServletActionContext.getResponse().setStatus(200);
            setResult("result", "success");
            AccountUtil.cleanCurrentAccount();
        }catch (Exception ex) {
            LOGGER.error("####Sandbox - Error In Package Deployment", ex);
            throw new Exception(ex);
        }finally {
            PackageFolderContext rootFolder = (PackageFolderContext) deployContext.getOrDefault(PackageConstants.PACKAGE_ROOT_FOLDER, null);
            if(rootFolder != null) {
                File file = new File(rootFolder.getPath());
                FileUtils.deleteDirectory(file);
            }
        }
        return SUCCESS;
    }
    public String addOrgInfoData() throws Exception {
        OrgBean orgBean = AccountUtil.getOrgBean();
        AccountUtil.setCurrentAccount(orgId);
        name = name.trim();
        if(orgBean.getOrg(orgId)!=null){
            Map<String, Object> orgProps = CommonCommandUtil.getOrgInfo(orgId,name);
            if(orgProps == null) {
                CommonCommandUtil.insertOrgInfo(name,value);
            }
            else{
                CommonCommandUtil.updateOrgInfo(name,value);
            }
        }
        JSONObject result = new JSONObject();
        result.put("name", name);
        result.put("value", value);
        ServletActionContext.getResponse().setStatus(200);
        setResult("result", "success");
        AccountUtil.cleanCurrentAccount();
        return SUCCESS;
    }
    public String addSkipComponentFile() throws Exception {
        OrgBean orgBean = AccountUtil.getOrgBean();
        PackageFileUtil.accountSwitch(orgId);
        FileContext fileContext = null;
        try {
             fileContext = PackageFileUtil.addSkipComponentFile(file);
        }catch (Exception e){
            setResult("result", "Input file should be .txt format");
            ServletActionContext.getResponse().setStatus(200);
            return SUCCESS;
        }
        name = name.trim();
        if(orgBean.getOrg(orgId) != null){
            Map<String, Object> orgProps = CommonCommandUtil.getOrgInfo(orgId, name);
            if(orgProps == null) {
                CommonCommandUtil.insertOrgInfo(name, String.valueOf(fileContext.getFileId()));
            }
            else{
                CommonCommandUtil.updateOrgInfo(name, String.valueOf(fileContext.getFileId()));
            }
        }
        ServletActionContext.getResponse().setStatus(200);
        setResult("result", "success");
        AccountUtil.cleanCurrentAccount();
        return SUCCESS;
    }
}
