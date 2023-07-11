
package com.facilio.remotemonitoring.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.remotemonitoring.context.*;
import com.facilio.remotemonitoring.signup.FlaggedEventAlarmTypeRelModule;
import com.facilio.remotemonitoring.signup.FlaggedEventRuleModule;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddFlaggedEventRuleAlarmTypeCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<FlaggedEventRuleContext> flaggedEventRules = (List<FlaggedEventRuleContext>) recordMap.get(FlaggedEventRuleModule.MODULE_NAME);
        if (CollectionUtils.isEmpty(flaggedEventRules)) {
            List<Long> recordIds = (List<Long>) context.get("recordIds");
            if (CollectionUtils.isNotEmpty(recordIds)) {
                Criteria criteria = new Criteria();
                criteria.addAndCondition(CriteriaAPI.getCondition("ID", "id", StringUtils.join(recordIds, ","), NumberOperators.EQUALS));
                flaggedEventRules = V3RecordAPI.getRecordsListWithSupplements(FlaggedEventRuleModule.MODULE_NAME, null, FlaggedEventRuleContext.class, criteria, null);
            }
        }
        if(CollectionUtils.isNotEmpty(flaggedEventRules)) {
            for(FlaggedEventRuleContext flaggedEventRule : flaggedEventRules) {
                Criteria criteria = new Criteria();
                criteria.addAndCondition(CriteriaAPI.getCondition("FLAGGED_EVENT_RULE","flaggedEventRule",String.valueOf(flaggedEventRule.getId()),NumberOperators.EQUALS));
                V3RecordAPI.deleteRecords(FlaggedEventAlarmTypeRelModule.MODULE_NAME,criteria,false);
                List<ModuleBaseWithCustomFields> flaggedEventRuleAlarmTypeRels = new ArrayList<>();
                if(CollectionUtils.isNotEmpty(flaggedEventRule.getFlaggedEventRuleAlarmTypeRel())) {
                    for(FlaggedEventRuleAlarmTypeRel flaggedEventRuleAlarmTypeRel : flaggedEventRule.getFlaggedEventRuleAlarmTypeRel()) {
                        FlaggedEventRuleContext addFlaggedEventRule = new FlaggedEventRuleContext();
                        addFlaggedEventRule.setId(flaggedEventRule.getId());
                        flaggedEventRuleAlarmTypeRel.setFlaggedEventRule(addFlaggedEventRule);
                        flaggedEventRuleAlarmTypeRels.add(flaggedEventRuleAlarmTypeRel);
                    }
                    V3Util.createRecord(modBean.getModule(FlaggedEventAlarmTypeRelModule.MODULE_NAME), flaggedEventRuleAlarmTypeRels);
                }
            }
        }
        return false;
    }


    private static void deleteFlaggedEventRuleFiles(List<Long> fileIds) {

    }
}
