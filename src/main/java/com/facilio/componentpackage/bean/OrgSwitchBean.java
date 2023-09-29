package com.facilio.componentpackage.bean;

import com.facilio.sandbox.context.SandboxConfigContext;

import java.io.InputStream;

public interface OrgSwitchBean {
    InputStream getParentOrgFile(long orgId, long fileId) throws Exception;
    boolean sendSandboxProgress(Integer percentage, Long recordId, String message) throws Exception;
    boolean changeSandboxStatus(SandboxConfigContext sandboxConfigContext) throws Exception;
    SandboxConfigContext getSandboxById(long id) throws Exception;

}
