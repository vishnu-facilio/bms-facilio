package com.facilio.bmsconsoleV3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.beans.GlobalScopeBean;
import com.facilio.bmsconsoleV3.context.ScopeVariableModulesFields;
import com.facilio.bmsconsoleV3.context.scoping.GlobalScopeVariableContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class AddOrUpdateScopeVariable extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        GlobalScopeVariableContext scopeVariable = (GlobalScopeVariableContext) context.get(FacilioConstants.ContextNames.RECORD);
        Long scopeVariableId = -1l;
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        GlobalScopeBean scopeBean = (GlobalScopeBean) BeanFactory.lookup("ScopeBean");
        if(scopeVariable != null){
            if(scopeVariable.getId() > 0){
                scopeVariableId = scopeBean.updateScopeVariable(scopeVariable);
            } else {
                scopeVariableId = scopeBean.addScopeVariable(scopeVariable);
            }
            scopeBean.deleteScopeVariableModulesFieldsByScopeVariableId(scopeVariable.getId());
            if(CollectionUtils.isNotEmpty(scopeVariable.getScopeVariableModulesFieldsList())){
                List<ScopeVariableModulesFields> scopeVariableModulesFieldsList = new ArrayList<>();
                for(ScopeVariableModulesFields item : scopeVariable.getScopeVariableModulesFieldsList()){
                    if(item.getModuleId() == null || modBean.getModule(item.getModuleId()) == null){
                        throw new IllegalArgumentException("Invalid module");
                    }
                    FacilioModule module = modBean.getModule(item.getModuleId());
                    FacilioField field = modBean.getField(item.getFieldName(),module.getName());
                    if(item.getFieldName() != null && field == null){
                        throw new IllegalArgumentException("Invalid field");
                    }
                    item.setScopeVariableId(scopeVariableId);
                    scopeVariableModulesFieldsList.add(item);
                }
                scopeBean.addScopeVariableModulesFields(scopeVariableModulesFieldsList);
            }
        }
        context.put(FacilioConstants.ContextNames.RECORD_ID,scopeVariableId);
        return false;
    }
}
