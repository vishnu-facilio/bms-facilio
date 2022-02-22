package com.facilio.readingrule.command;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.ns.command.NSContextNames;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import org.apache.commons.chain.Context;

public class AddNewReadingRuleCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        NewReadingRuleContext readingRule = (NewReadingRuleContext) context.get(FacilioConstants.ContextNames.NEW_READING_RULE);

        User currentUser = AccountUtil.getCurrentUser();
        readingRule.setOrgId(currentUser.getOrgId());
        readingRule.setCreatedBy(currentUser.getId());
        readingRule.setCreatedTime(System.currentTimeMillis());
        readingRule.setModuleId((Long)context.get(FacilioConstants.ContextNames.MODULE_ID));

        NewReadingRuleAPI.addReadingRule(readingRule);

        context.put(NSContextNames.NAMESPACE, readingRule.getCondition().getNs());
        context.put(NSContextNames.PARENT_RULE_ID, readingRule.getCondition().getId());
        context.put(NSContextNames.NAMESPACE_FIELDS, readingRule.getCondition().getNs().getFields());
        context.put(FacilioConstants.ContextNames.ASSETS, readingRule.getAssetContexts());

        return Boolean.FALSE;
    }

}
