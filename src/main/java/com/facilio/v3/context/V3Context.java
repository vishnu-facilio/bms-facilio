package com.facilio.v3.context;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class V3Context extends ModuleBaseWithCustomFields {

    @Override
    public void setId(Long id) {
        if (id == null) {
            super.setId(-1L);
        } else {
            super.setId(id);
        }
    }

    @Override
    public Long getId() {
        if (super.getId() == -1L) {
            return null;
        }
        return super.getId();
    }

    @Override
    public void setModuleId(Long moduleId) {
        if (moduleId == null) {
            super.setModuleId(-1L);
        } else {
            super.setModuleId(moduleId);
        }
    }

    @Override
    public Long getModuleId() {
        if (super.getModuleId() == -1L) {
            return null;
        }
        return super.getModuleId();
    }

    @Override
    public void setOrgId(Long orgId) {
        if (orgId == null) {
            super.setOrgId(-1L);
        } else {
            super.setOrgId(orgId);
        }

    }

    @Override
    public Long getOrgId() {
        if (super.getOrgId() == -1L)  {
            return null;
        }
        return super.getOrgId();
    }

    @Override
    public void setFormId(Long formId) {
        if (formId == null) {
            super.setFormId(-1L);
        } else {
            super.setFormId(formId);
        }
    }

    @Override
    public Long getFormId() {
        if (super.getFormId() == -1L) {
            return null;
        }
        return super.getFormId();
    }

    @Override
    public void setSiteId(Long siteId) {
        if (siteId == null) {
            super.setSiteId(-1L);
        } else {
            super.setSiteId(siteId);
        }
    }

    @Override
    public Long getSiteId() {
        if (super.getSiteId() == -1L) {
            return null;
        }
        return super.getSiteId();
    }

    @Override
    public void setStateFlowId(Long stateFlowId) {
        if (stateFlowId == null) {
            super.setStateFlowId(-1L);
        } else {
            super.setStateFlowId(stateFlowId);
        }
    }

    @Override
    public Long getStateFlowId() {
        if (super.getStateFlowId() == -1L) {
            return null;
        }
        return super.getStateFlowId();
    }

    @Override
    public void setSlaPolicyId(Long slaPolicyId) {
        if (slaPolicyId == null) {
            super.setSlaPolicyId(-1L);
        } else {
            super.setSlaPolicyId(slaPolicyId);
        }
    }

    @Override
    public Long getSlaPolicyId() {
        if (super.getSlaPolicyId() == -1L) {
            return null;
        }
        return super.getSlaPolicyId();
    }

    @Override
    public void setOfflineModifiedTime(Long offlineModifiedTime) {
        if (offlineModifiedTime == null) {
            super.setOfflineModifiedTime(-1L);
        } else {
            super.setOfflineModifiedTime(offlineModifiedTime);
        }
    }

    @Override
    public Long getOfflineModifiedTime() {
        if (super.getOfflineModifiedTime() == -1L) {
            return null;
        }
        return super.getOfflineModifiedTime();
    }

    @Override
    public void setApprovalFlowId(Long approvalFlowId) {
        if (approvalFlowId == null) {
            super.setApprovalFlowId(-1L);
        } else {
            super.setApprovalFlowId(approvalFlowId);
        }
    }

    @Override
    public Long getApprovalFlowId() {
        if (super.getApprovalFlowId() == -1L) {
            return null;
        }
        return super.getApprovalFlowId();
    }

    @Override
    public void setSysDeletedTime(Long sysDeletedTime) {
        if (sysDeletedTime == null) {
            super.setSysDeletedTime(-1L);
        } else {
            super.setSysDeletedTime(sysDeletedTime);
        }
    }


    @Override
    public Long getSysDeletedTime() {
        if (super.getSysDeletedTime() == -1L) {
            return null;
        }
        return super.getSysDeletedTime();
    }

    @Override
    public void setLocalId(Long localId) {
        if (localId == null) {
            super.setLocalId(-1L);
        } else {
            super.setLocalId(localId);
        }
    }

    @Override
    public Long getLocalId() {
        if (super.getLocalId() == -1L) {
            return null;
        }
        return super.getLocalId();
    }
}
