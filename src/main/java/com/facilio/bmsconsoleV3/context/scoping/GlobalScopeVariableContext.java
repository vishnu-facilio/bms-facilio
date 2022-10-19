package com.facilio.bmsconsoleV3.context.scoping;

import com.facilio.agent.alarms.AgentAlarmContext;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsoleV3.context.ScopeVariableModulesFields;
import com.facilio.cb.context.ChatBotSuggestionContext;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.*;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
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
    private String description;
    private Long valueGeneratorId;
    private String valueGeneratorDisplayName;
    private String applicableModuleName;
    private String applicableModuleDisplayName;
    private Long applicableModuleId;
    private long sysCreatedTime = -1L;
    private long sysModifiedTime = -1L;
    private long sysDeletedTime = -1L;
    private Long sysCreatedBy;
    private Long sysModifiedBy;
    private Long sysDeletedBy;
    private Boolean showSwitch;
    private Boolean status;
    private Long appId;

    private List<ScopeVariableModulesFields> scopeVariableModulesFieldsList;
    private List<Long> values;
    private Boolean deleted;

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    private Type type;

    public int getType() {
        if (type != null) {
            return type.getIndex();
        }
        return -1;
    }

    public void setType(int type) {
        this.type = Type.valueOf(type);
    }

    public Type getTypeEnum() {
        return type;
    }

    public enum Type implements FacilioIntEnum {
        SCOPED, ALL;

        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name();
        }

        public static Type valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }


    public String getApplicableModuleName() {
        return applicableModuleName;
    }

    public void setApplicableModuleName(String applicableModuleName) {
        this.applicableModuleName = applicableModuleName;
    }

    public String getApplicableModuleDisplayName() {
        return applicableModuleDisplayName;
    }

    public void setApplicableModuleDisplayName(String applicableModuleDisplayName) {
        this.applicableModuleDisplayName = applicableModuleDisplayName;
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

    public boolean isSwitch() {
        if(showSwitch == null) {
            return false;
        }
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

    public Long getSysCreatedBy() {
        return sysCreatedBy;
    }

    public void setSysCreatedBy(Long sysCreatedBy) {
        this.sysCreatedBy = sysCreatedBy;
    }

    public Long getSysModifiedBy() {
        return sysModifiedBy;
    }

    public void setSysModifiedBy(Long sysModifiedBy) {
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

    public boolean isActive() {
        if(status == null) {
            return false;
        }
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
    
    public long getSysDeletedTime() {
        return sysDeletedTime;
    }

    public void setSysDeletedTime(long sysDeletedTime) {
        this.sysDeletedTime = sysDeletedTime;
    }

    public Long getSysDeletedBy() {
        return sysDeletedBy;
    }

    public void setSysDeletedBy(Long sysDeletedBy) {
        this.sysDeletedBy = sysDeletedBy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getValueGeneratorDisplayName() {
        return valueGeneratorDisplayName;
    }

    public void setValueGeneratorDisplayName(String valueGeneratorDisplayName) {
        this.valueGeneratorDisplayName = valueGeneratorDisplayName;
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
        if(values == null){
            return null;
        }
        if(module != null) {
            String modName = module.getName();
            FacilioModule applicableModule = null;
            if(applicableModuleId != null) {
                applicableModule = modBean.getModule(applicableModuleId);
            }
            if(StringUtils.isNotEmpty(modName) && (modName.equals(applicableModuleName) || (applicableModule != null && modName.equals(applicableModule.getName())))){
                FacilioField idField = FieldFactory.getIdField(modBean.getModule(modName));
                fields.add(idField);
                criteria.addAndCondition(CriteriaAPI.getCondition(idField, StringUtils.join(values,","), NumberOperators.EQUALS));
                return Pair.of(fields, criteria);
            }
            List<ScopeVariableModulesFields> scopeVariableModulesFieldsList = this.scopeVariableModulesFieldsList;
            if (CollectionUtils.isNotEmpty(scopeVariableModulesFieldsList)) {
                for (ScopeVariableModulesFields scopeVariableModulesField : scopeVariableModulesFieldsList) {
                    if (scopeVariableModulesField.getModuleId() == module.getModuleId()) {
                        String fieldName = scopeVariableModulesField.getFieldName();
                        if (fieldName != null && module != null) {
                            FacilioField field = modBean.getField(fieldName, module.getName());
                            boolean relRecField = false;
                            if (field.getDataTypeEnum().isRelRecordField()) {
                                relRecField = true;
                            }
                            if (CollectionUtils.isNotEmpty(values)) {
                                Operator operator = PickListOperators.IS;
                                if (field instanceof MultiLookupField) {
                                    operator = MultiFieldOperators.CONTAINS;
                                } else if (field instanceof LookupField) {
                                    operator = PickListOperators.IS;
                                }
                                if (field != null) {
                                    criteria.addAndCondition(CriteriaAPI.getCondition(field, values, operator));
                                    if(!relRecField) {
                                        fields.add(field);
                                    }
                                }
                            }
                            else {
                                if (field != null) {
                                    criteria.addAndCondition(CriteriaAPI.getCondition(field, StringUtils.EMPTY, MultiFieldOperators.CONTAINS));
                                    if(!relRecField) {
                                        fields.add(field);
                                    }
                                }
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
