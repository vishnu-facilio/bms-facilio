package com.facilio.remotemonitoring.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.remotemonitoring.context.FlaggedEventRuleContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import com.facilio.db.criteria.Criteria;

import java.util.List;
import java.util.Map;


public class AddFlaggedEventRuleSiteAndControllerCriteriaCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule siteModule = modBean.getModule(ModuleFactory.getSiteModule().getName());
        FacilioModule controllerModule = modBean.getModule(ModuleFactory.getControllerModule().getName());
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<FlaggedEventRuleContext> flaggedEventRules = (List<FlaggedEventRuleContext>) recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(flaggedEventRules)){
            for(FlaggedEventRuleContext flaggedEventRule : flaggedEventRules){
                Criteria siteCriteria = flaggedEventRule.getSiteCriteria();
                Criteria controllerCriteria = flaggedEventRule.getControllerCriteria();
                if(siteCriteria != null && !siteCriteria.isEmpty()){
                    for (String key : siteCriteria.getConditions().keySet()) {
                        Condition condition = siteCriteria.getConditions().get(key);
                        FacilioField field = modBean.getField(condition.getFieldName(), siteModule.getName());
                        condition.setField(field);
                    }
                    long siteCriteriaId = CriteriaAPI.addCriteria(siteCriteria);
                    flaggedEventRule.setSiteCriteriaId(siteCriteriaId);
                }
                if(controllerCriteria != null && !controllerCriteria.isEmpty()){
                    for (String key : controllerCriteria.getConditions().keySet()) {
                        Condition condition = controllerCriteria.getConditions().get(key);
                        FacilioField field = modBean.getField(condition.getFieldName(), controllerModule.getName());
                        condition.setField(field);
                    }
                    long controllerCriteriaId = CriteriaAPI.addCriteria(controllerCriteria);
                    flaggedEventRule.setControllerCriteriaId(controllerCriteriaId);
                }
            }
        }
        return  false;
    }
}
