package com.facilio.remotemonitoring.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.cache.CacheUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.cache.FacilioCache;
import com.facilio.fw.cache.LRUCache;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.BooleanField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.remotemonitoring.context.AlarmFilterRuleContext;
import com.facilio.remotemonitoring.signup.AlarmFilterRuleModule;
import com.facilio.v3.context.Constants;
import com.facilio.v3.context.V3Context;
import org.apache.commons.chain.Context;

import java.util.*;

public class updateAlaramFilterRuleStatusCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        long recordId = (Long) context.get("recordIds");
        boolean status = (boolean) context.get("status");

        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule module = moduleBean.getModule(AlarmFilterRuleModule.MODULE_NAME);
        FacilioField statusfield = moduleBean.getField("status",AlarmFilterRuleModule.MODULE_NAME);
        List<FacilioField> alarmFilterRuleFields = new ArrayList<>();
        alarmFilterRuleFields.add(statusfield);

        AlarmFilterRuleContext statusfieldcontext = new AlarmFilterRuleContext();
        statusfieldcontext.setId(recordId);
        statusfieldcontext.setStatus(status);

        V3RecordAPI.updateRecord(statusfieldcontext,module,alarmFilterRuleFields);

        clearAlarmFilterRuleCache();
        return false;
    }

    private void clearAlarmFilterRuleCache(){
        FacilioCache<String, List<AlarmFilterRuleContext>> alarmFilterRulesCache = LRUCache.getAlarmFilterRuleCache();
        String key = CacheUtil.ORG_KEY(AccountUtil.getCurrentOrg().getId());
        alarmFilterRulesCache.removeStartsWith(key);
    }
}
