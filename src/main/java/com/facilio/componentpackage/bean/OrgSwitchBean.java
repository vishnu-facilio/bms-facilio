package com.facilio.componentpackage.bean;

import com.facilio.accounts.dto.User;
import com.facilio.sandbox.context.SandboxConfigContext;

import java.io.InputStream;

public interface OrgSwitchBean {
    public InputStream getParentOrgFile(long orgId, long fileId) throws Exception;
    public boolean sendSandboxProgress(Integer percentage, Long recordId, String message) throws Exception;
    public boolean changeSandboxStatus(SandboxConfigContext sandboxConfigContext) throws Exception;
    public SandboxConfigContext getSandboxById(long id) throws Exception;
    public long getSuperAdminOuidFromSandbox() throws  Exception;
}
