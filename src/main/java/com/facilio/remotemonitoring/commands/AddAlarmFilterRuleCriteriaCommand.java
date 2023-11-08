package com.facilio.remotemonitoring.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.remotemonitoring.context.AlarmFilterCriteriaType;
import com.facilio.remotemonitoring.context.AlarmFilterRuleContext;
import com.facilio.remotemonitoring.context.FilterRuleCriteriaContext;
import com.facilio.remotemonitoring.context.RuleType;
import com.facilio.remotemonitoring.signup.AlarmFilterRuleCriteriaModule;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddAlarmFilterRuleCriteriaCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<AlarmFilterRuleContext> alarmFilterRules = (List<AlarmFilterRuleContext>) recordMap.get(moduleName);
        List<ModuleBaseWithCustomFields> addCriteriaList = new ArrayList<>();
        List<Long> recordIds = new ArrayList<Long>();
        if(CollectionUtils.isNotEmpty(alarmFilterRules)) {
            for(AlarmFilterRuleContext alarmFilterRule : alarmFilterRules) {
                List<FilterRuleCriteriaContext> filterRuleCriteria = alarmFilterRule.getFilterRuleCriteriaList();
                if(CollectionUtils.isNotEmpty(filterRuleCriteria)) {
                    for(FilterRuleCriteriaContext filterRuleCriteriaObject : filterRuleCriteria) {
                        AlarmFilterRuleContext filterRule = new AlarmFilterRuleContext();
                        filterRule.setId(alarmFilterRule.getId());
                        filterRuleCriteriaObject.setAlarmFilterRule(filterRule);
                        if(filterRuleCriteriaObject.getId() > 0){
                            recordIds.add(filterRuleCriteriaObject.getId());
                        }
                        if(alarmFilterRule.getRuleType().equals(RuleType.ROLL_UP)){
                            filterRuleCriteriaObject.setFilterCriteria(AlarmFilterCriteriaType.ALARM_ROLL_UP);
                        }
                        if(alarmFilterRule.getRuleType().equals(RuleType.SITE_OFFLINE)){
                            filterRuleCriteriaObject.setFilterCriteria(AlarmFilterCriteriaType.SITE_OFFLINE_ALARM);
                        }
                        addCriteriaList.add(filterRuleCriteriaObject);
                    }
                }
            }
            Map<String, Object> deleteObj = new HashMap<>();
            deleteObj.put(AlarmFilterRuleCriteriaModule.MODULE_NAME, recordIds);
            if(!recordIds.isEmpty()){
                V3Util.deleteRecords(AlarmFilterRuleCriteriaModule.MODULE_NAME, deleteObj, null, null, false);
            }
            V3Util.createRecord(modBean.getModule(AlarmFilterRuleCriteriaModule.MODULE_NAME), addCriteriaList);
        }
        return false;
    }
}
