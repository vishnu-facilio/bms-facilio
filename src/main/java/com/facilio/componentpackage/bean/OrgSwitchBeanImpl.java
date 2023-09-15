package com.facilio.componentpackage.bean;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.backgroundactivity.util.BackgroundActivityAPI;
import com.facilio.backgroundactivity.util.BackgroundActivityService;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.sandbox.context.SandboxConfigContext;
import com.facilio.sandbox.utils.SandboxAPI;
import com.facilio.services.factory.FacilioFactory;
import lombok.extern.log4j.Log4j;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log4j
public class OrgSwitchBeanImpl implements OrgSwitchBean{
    @Override
    public InputStream getParentOrgFile(long orgId, long fileId) throws Exception {
        InputStream inputStream = null;
        try {
            inputStream = FacilioFactory.getFileStoreFromOrg(orgId).readFile(fileId);
        }catch (Exception e){
            LOGGER.info("Error while reading file", e);
        }
        return inputStream;
    }


    @Override
    public boolean sendSandboxProgress(Integer percentage, Long recordId, String message) throws Exception {
        try {
            BackgroundActivityService backgroundActivityService = new BackgroundActivityService(BackgroundActivityAPI.parentActivityForRecordIdAndType(recordId, "sandbox"));
            if (backgroundActivityService != null && backgroundActivityService.getActivityId() != null) {
                backgroundActivityService.updateActivity(percentage, message);
            }
            return true;
        }catch (Exception ex){
            return false;
        }
    }

    @Override
    public boolean changeSandboxStatus(SandboxConfigContext sandboxConfigContext) throws Exception {
        List<FacilioField> fields = new ArrayList<>();
        Map<String, FacilioField> sandboxFieldMap = FieldFactory.getAsMap(FieldFactory.getFacilioSandboxFields());
        fields.add(sandboxFieldMap.get("id"));
        fields.add(sandboxFieldMap.get("status"));
        SandboxAPI.updateSandboxConfig(sandboxConfigContext, fields);
        return true;
    }

    @Override
    public SandboxConfigContext getSandboxById(long id) throws Exception {
        return SandboxAPI.getSandboxById(id);
    }

    @Override
    public long getSuperAdminOuidFromSandbox() throws Exception {
        return AccountUtil.getOrgBean().getSuperAdmin(AccountUtil.getCurrentOrg().getOrgId()).getOuid();
    }
}
