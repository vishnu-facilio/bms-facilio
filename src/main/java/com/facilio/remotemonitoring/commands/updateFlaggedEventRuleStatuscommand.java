package com.facilio.remotemonitoring.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.cache.CacheUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.fw.cache.FacilioCache;
import com.facilio.fw.cache.LRUCache;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.remotemonitoring.context.FlaggedEventRuleContext;
import com.facilio.remotemonitoring.signup.FlaggedEventRuleModule;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.*;

public class updateFlaggedEventRuleStatuscommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long recordId = (Long) context.get("recordIds");
        boolean status = (boolean) context.get("status");

        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule module = moduleBean.getModule(FlaggedEventRuleModule.MODULE_NAME);
        FacilioField statusfield = moduleBean.getField("status",FlaggedEventRuleModule.MODULE_NAME);
        List<FacilioField> flaggedEventRuleFields = new ArrayList<>();
        flaggedEventRuleFields.add(statusfield);

        FlaggedEventRuleContext statusfieldcontext = new FlaggedEventRuleContext();
        statusfieldcontext.setId(recordId);
        statusfieldcontext.setStatus(status);

        V3RecordAPI.updateRecord(statusfieldcontext,module,flaggedEventRuleFields);

        clearFlaggedRuleEventRuleCache(recordId);
        return false;
    }

    private void clearFlaggedRuleEventRuleCache(long id){
        FacilioCache<String, List<FlaggedEventRuleContext>> flaggedEventsRuleCache = LRUCache.getFlaggedEventsRuleCache();
        FacilioCache<String, FlaggedEventRuleContext> flaggedEventRuleCache = LRUCache.getFlaggedEventRuleCache();

        String clientIdKey = CacheUtil.ORG_KEY(AccountUtil.getCurrentOrg().getId());
        flaggedEventsRuleCache.removeStartsWith(clientIdKey);

        String flaggedEventIdKey = CacheUtil.FLAGGED_EVENT_ID_KEY(AccountUtil.getCurrentOrg().getId(), id);
        flaggedEventRuleCache.remove(flaggedEventIdKey);
    }
}