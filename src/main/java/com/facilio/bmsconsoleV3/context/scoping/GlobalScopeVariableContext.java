package com.facilio.bmsconsoleV3.context.scoping;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.ScopeVariableModulesFields;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.MultiFieldOperators;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.MultiLookupField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GlobalScopeVariableContext implements Serializable {
    public GlobalScopeVariableContext(){

    }
    private long id = -1L;
    private String linkName;
    private String displayName;
    private Long valueGeneratorId;
    private String applicableModuleName;
    private Long applicableModuleId;
    private long sysCreatedTime = -1L;
    private long sysModifiedTime = -1L;
    private long sysCreatedBy = -1L;
    private long sysModifiedBy = -1L;
    private Boolean showSwitch;
    private Boolean status;
    private Long appId;

    private List<ScopeVariableModulesFields> scopeVariableModulesFieldsList;
    private List<Long> values;

    public String getApplicableModuleName() {
        return applicableModuleName;
    }

    public void setApplicableModuleName(String applicableModuleName) {
        this.applicableModuleName = applicableModuleName;
    }

    public long getId() {
        return id;
    }

    public List<ScopeVariableModulesFields> getScopeVariableModulesFieldsList() {
        return scopeVariableModulesFieldsList;
    }

    public void setScopeVariableModulesFieldsList(List<ScopeVariableModulesFields> scopeVariableModulesFieldsList) {
        this.scopeVariableModulesFieldsList = scopeVariableModulesFieldsList;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLinkName() {
        return linkName;
    }

    public void setLinkName(String linkName) {
        this.linkName = linkName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Long getValueGeneratorId() {
        return valueGeneratorId;
    }

    public void setValueGeneratorId(Long valueGeneratorId) {
        this.valueGeneratorId = valueGeneratorId;
    }

    public long getSysCreatedTime() {
        return sysCreatedTime;
    }

    public Boolean getShowSwitch() {
        return showSwitch;
    }

    public void setShowSwitch(Boolean showSwitch) {
        this.showSwitch = showSwitch;
    }

    public void setSysCreatedTime(long sysCreatedTime) {
        this.sysCreatedTime = sysCreatedTime;
    }

    public long getSysModifiedTime() {
        return sysModifiedTime;
    }

    public void setSysModifiedTime(long sysModifiedTime) {
        this.sysModifiedTime = sysModifiedTime;
    }

    public long getSysCreatedBy() {
        return sysCreatedBy;
    }

    public void setSysCreatedBy(long sysCreatedBy) {
        this.sysCreatedBy = sysCreatedBy;
    }

    public long getSysModifiedBy() {
        return sysModifiedBy;
    }

    public void setSysModifiedBy(long sysModifiedBy) {
        this.sysModifiedBy = sysModifiedBy;
    }

    public Long getApplicableModuleId() {
        return applicableModuleId;
    }

    public void setApplicableModuleId(Long applicableModuleId) {
        this.applicableModuleId = applicableModuleId;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public List<Long> getValues() {
        return values;
    }

    public void setValues(List<Long> values) {
        this.values = values;
    }

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public GlobalScopeVariableContext(String linkName, List<ScopeVariableModulesFields> scopeVariableModulesFieldsList) {
        this.linkName = linkName;
        this.scopeVariableModulesFieldsList = scopeVariableModulesFieldsList;
    }

    public Pair<List<FacilioField>, Criteria> getGlobalScopeVariableCriteriaForModule(String moduleName) throws Exception {
        List<FacilioField> fields = new ArrayList<>();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        List<Long> values = this.values;
        Criteria criteria = new Criteria();
        if(CollectionUtils.isEmpty(values)){
            return null;
        }
        if(module != null) {
            List<ScopeVariableModulesFields> scopeVariableModulesFieldsList = this.scopeVariableModulesFieldsList;
            if (CollectionUtils.isNotEmpty(scopeVariableModulesFieldsList)) {
                for (ScopeVariableModulesFields scopeVariableModulesField : scopeVariableModulesFieldsList) {
                    if (scopeVariableModulesField.getModuleId() == module.getModuleId()) {
                        String fieldName = scopeVariableModulesField.getFieldName();
                        if (fieldName != null && module != null && CollectionUtils.isNotEmpty(values)) {
                            FacilioField field = modBean.getField(fieldName, module.getName());
                            Operator operator = PickListOperators.IS;
                            if (field instanceof MultiLookupField) {
                                operator = MultiFieldOperators.CONTAINS;
                            } else if (field instanceof LookupField) {
                                operator = PickListOperators.IS;
                            }
                            if (field != null) {
                                criteria.addAndCondition(CriteriaAPI.getCondition(field, values, operator));
                                fields.add(field);
                            }
                        }
                    }
                }
                return Pair.of(fields, criteria);
            }
        }
        return null;
    }
}
