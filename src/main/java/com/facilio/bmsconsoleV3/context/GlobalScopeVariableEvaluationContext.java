package com.facilio.bmsconsoleV3.context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.scoping.GlobalScopeVariableContext;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.MultiFieldOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.MultiLookupField;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
public class GlobalScopeVariableEvaluationContext implements Serializable{

    public GlobalScopeVariableEvaluationContext() {

    }

    public GlobalScopeVariableEvaluationContext(String linkName,String applicableModuleName,Long applicableModuleId,List<ScopeVariableModulesFields> scopeVariableModulesFieldsList,List<Long> values,GlobalScopeVariableContext.Type type) {
        this.linkName = linkName;
        this.applicableModuleName = applicableModuleName;
        this.applicableModuleId = applicableModuleId;
        this.scopeVariableModulesFieldsList = scopeVariableModulesFieldsList;
        this.values = values;
        this.type = type;
    }

    private String linkName;
    private String applicableModuleName;
    private Long applicableModuleId;
    private List<ScopeVariableModulesFields> scopeVariableModulesFieldsList;
    private List<Long> values;
    private GlobalScopeVariableContext.Type type;

    public String getLinkName() {
        return linkName;
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
