package com.facilio.remotemonitoring.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.remotemonitoring.context.AlarmFilterRuleContext;
import com.facilio.remotemonitoring.context.FilterRuleCriteriaContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class SetSiteAndControllerCriteriaCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<AlarmFilterRuleContext> alarmFilterRules = (List<AlarmFilterRuleContext>) recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(alarmFilterRules)) {
            for(AlarmFilterRuleContext alarmFilterRule : alarmFilterRules) {
                Long controllerCriteriaId = alarmFilterRule.getControllerCriteriaId();
                Long siteCriteriaId = alarmFilterRule.getSiteCriteriaId();
                if(controllerCriteriaId != null && controllerCriteriaId>0){
                    alarmFilterRule.setControllerCrtieria(CriteriaAPI.getCriteria(controllerCriteriaId));
                }
                if(siteCriteriaId != null && siteCriteriaId>0){
                    alarmFilterRule.setSiteCriteria(CriteriaAPI.getCriteria(siteCriteriaId));
                }
            }
        }
        return false;
    }
}
