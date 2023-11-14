package com.facilio.componentpackage.bean;

import com.facilio.sandbox.context.SandboxConfigContext;

import java.io.InputStream;
import java.util.List;

public interface OrgSwitchBean {
    public InputStream getParentOrgFile(long orgId, long fileId) throws Exception;
    public boolean sendSandboxProgress(Integer percentage, Long recordId, String message) throws Exception;
    public boolean changeSandboxStatus(SandboxConfigContext sandboxConfigContext) throws Exception;
    public SandboxConfigContext getSandboxById(long id) throws Exception;
    public List<SandboxConfigContext> getAllSandbox(int page, int perPage, String search) throws  Exception;
    public long getSandboxCount(String search) throws  Exception;

}
