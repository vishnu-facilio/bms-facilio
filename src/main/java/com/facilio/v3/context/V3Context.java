package com.facilio.v3.context;

import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.failurecode.V3FailureClassContext;
import com.facilio.classification.context.ClassificationContext;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
public class V3Context extends ModuleBaseWithCustomFields {

    public V3Context (Long id) {
        this._setId(id);
    }

    private Map<String, List<SubFormContext>> relations;
    
    public Map<String, List<SubFormContext>> getRelations() {
        return relations;
    }

    public void setRelations(Map<String, List<SubFormContext>> relations) {
        this.relations = relations;
    }

    @Getter
    @Setter
    private Map<String,Object> transitionCommentData;

    @JsonProperty("orgId")
    public Long _getOrgId() {
        long orgId = super.getOrgId();
        if (orgId <= 0) {
            return null;
        }
        return orgId;
    }

    @JsonProperty("orgId")
    public void _setOrgId(Long orgId) {
        if (orgId == null) {
            orgId = -1L;
        }
        super.setOrgId(orgId);
    }

    @JsonProperty("id")
    public Long _getId() {
        long id = super.getId();
        if (id <= 0) {
            return null;
        }
        return id;
    }

    @JsonProperty("id")
    public void _setId(Long id) {
        if (id == null) {
            id = -1L;
        }
        super.setId(id);
    }

    @JsonProperty("moduleId")
    public void _setModuleId(Long moduleId) {
        if (moduleId == null) {
            moduleId = -1L;
        }
        super.setModuleId(moduleId);
    }

    @JsonProperty("moduleId")
    public Long _getModuleId() {
        long moduleId = super.getModuleId();
        if (moduleId <= 0) {
            return null;
        }
        return moduleId;
    }

    @JsonProperty("formId")
    public void _setFormId(Long formId) {
        if (formId == null) {
            formId = -1L;
        }
        super.setFormId(formId);
    }

    @JsonProperty("formId")
    public Long _getFormId() {
        long formId = super.getFormId();
        if (formId <= 0) {
            return null;
        }
        return formId;
    }

    @JsonProperty("siteId")
    public void _setSiteId(Long siteId) {
        if (siteId == null) {
            siteId = -1L;
        }
        super.setSiteId(siteId);
    }

    @JsonProperty("siteId")
    public Long _getSiteId() {
        long siteId = super.getSiteId();
        if (siteId <= 0) {
            return null;
        }
        return siteId;
    }

    @JsonProperty("sysCreatedTime")
    public Long _getSysCreatedTime() {
        long sysCreatedTime = super.getSysCreatedTime();
        if (sysCreatedTime <= 0) {
            return null;
        }
        return sysCreatedTime;
    }

    @JsonProperty("sysCreatedTime")
    public void _setSysCreatedTime(Long sysCreatedTime) {
        if (sysCreatedTime == null) {
            sysCreatedTime = -1L;
        }
        super.setSysCreatedTime(sysCreatedTime);
    }

    @JsonProperty("sysModifiedTime")
    public Long _getSysModifiedTime() {
        long sysModifiedTime = super.getSysModifiedTime();
        if (sysModifiedTime <= 0) {
            return null;
        }
        return sysModifiedTime;
    }

    @JsonProperty("sysModifiedTime")
    public void _setSysModifiedTime(Long sysModifiedTime) {
        if (sysModifiedTime == null) {
            sysModifiedTime = -1L;
        }
        super.setSysModifiedTime(sysModifiedTime);
    }

    @JsonProperty("sysDeletedTime")
    public Long _getSysDeletedTime() {
        long sysDeletedTime = super.getSysDeletedTime();
        if (sysDeletedTime <= 0) {
            return null;
        }
        return sysDeletedTime;
    }

    @JsonProperty("sysDeletedTime")
    public void _setSysDeletedTime(Long sysDeletedTime) {
        if (sysDeletedTime == null) {
            sysDeletedTime = -1L;
        }
        super.setSysDeletedTime(sysDeletedTime);
    }

    @JsonProperty("stateFlowId")
    public void _setStateFlowId(Long stateFlowId) {
        if (stateFlowId == null) {
            stateFlowId = -1L;
        }
        super.setStateFlowId(stateFlowId);
    }

    @JsonProperty("stateFlowId")
    public Long _getStateFlowId() {
        long stateFlowId = super.getStateFlowId();
        if (stateFlowId <= 0) {
            return null;
        }
        return stateFlowId;
    }

    @JsonProperty("slaPolicyId")
    public Long _getSlaPolicyId() {
        long slaPolicyId = super.getSlaPolicyId();
        if (slaPolicyId <= 0) {
            return null;
        }
        return slaPolicyId;
    }

    @JsonProperty("slaPolicyId")
    public void _setSlaPolicyId(Long slaPolicyId) {
        if (slaPolicyId == null) {
            slaPolicyId = -1L;
        }
        super.setSlaPolicyId(slaPolicyId);
    }

    @JsonProperty("offlineModifiedTime")
    public Long _getOfflineModifiedTime() {
        long offlineModifiedTime = super.getOfflineModifiedTime();
        if (offlineModifiedTime <= -1L) {
            return null;
        }
        return offlineModifiedTime;
    }

    @JsonProperty("offlineModifiedTime")
    public void _setOfflineModifiedTime(Long offlineModifiedTime) {
        if (offlineModifiedTime == null) {
            offlineModifiedTime = -1L;
        }
        super.setOfflineModifiedTime(offlineModifiedTime);
    }

    @JsonProperty("approvalFlowId")
    public Long _getApprovalFlowId() {
        long approvalFlowId = super.getApprovalFlowId();
        if (approvalFlowId <= 0) {
            return null;
        }
        return approvalFlowId;
    }

    @JsonProperty("approvalFlowId")
    public void _setApprovalFlowId(Long approvalFlowId) {
        if (approvalFlowId == null) {
            approvalFlowId = -1L;
        }
        super.setApprovalFlowId(approvalFlowId);
    }

    @Getter
    @Setter
    @JsonProperty("failureClass")
    private V3FailureClassContext failureClass;

    @Getter
    @Setter
    @JsonProperty("classification")
    private ClassificationContext classification;

    @Getter
    @Setter
    private Boolean makeRecordOffline;
    @Getter @Setter
    private V3PeopleContext sysCreatedByPeople;
    @Getter @Setter
    private V3PeopleContext sysModifiedByPeople;
    @Getter @Setter
    private V3PeopleContext sysDeletedByPeople;
}
