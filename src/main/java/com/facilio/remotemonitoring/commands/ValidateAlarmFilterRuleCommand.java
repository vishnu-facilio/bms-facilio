package com.facilio.remotemonitoring.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.EnumOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringSystemEnumOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.remotemonitoring.context.AlarmFilterRuleContext;
import com.facilio.remotemonitoring.signup.AlarmFilterRuleModule;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class ValidateAlarmFilterRuleCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String moduleName = Constants.getModuleName(context);
        FacilioModule module = modBean.getModule(moduleName);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<AlarmFilterRuleContext> alarmFilterRules = (List<AlarmFilterRuleContext>) recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(alarmFilterRules) ) {
            for(AlarmFilterRuleContext alarmFilterRule : alarmFilterRules) {
                FacilioUtil.throwIllegalArgumentException(alarmFilterRule.getAlarmType() == null,"Alarm Type is mandatory");
                FacilioUtil.throwIllegalArgumentException(alarmFilterRule.getClient() == null,"Client is mandatory");
                FacilioUtil.throwIllegalArgumentException(alarmFilterRule.getAlarmApproach() == null,"Alarm Approach is mandatory");
                Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(AlarmFilterRuleModule.MODULE_NAME));
                Criteria criteria = new Criteria();
                criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("alarmType"),String.valueOf(alarmFilterRule.getAlarmType().getId()), NumberOperators.EQUALS));
                criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("alarmApproach"),String.valueOf(alarmFilterRule.getAlarmApproachEnum().getIndex()), NumberOperators.EQUALS));
                criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("client"),String.valueOf(alarmFilterRule.getClient().getId()), NumberOperators.EQUALS));
                if(alarmFilterRule.getId() > 0) {
                    criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(module),String.valueOf(alarmFilterRule.getId()), NumberOperators.NOT_EQUALS));
                }
                List<AlarmFilterRuleContext> existingAlarmFilterRules = V3RecordAPI.getRecordsListWithSupplements(AlarmFilterRuleModule.MODULE_NAME,null,AlarmFilterRuleContext.class,criteria,null);
                FacilioUtil.throwIllegalArgumentException(CollectionUtils.isNotEmpty(existingAlarmFilterRules),"Rule with same configuration already exists");
            }
        }
        return false;
    }
}