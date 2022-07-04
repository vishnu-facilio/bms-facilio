package com.facilio.readingrule.command;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.ns.NamespaceConstants;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import org.apache.commons.chain.Context;

import java.util.List;

public class AddNewReadingRuleCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        NewReadingRuleContext readingRule = (NewReadingRuleContext) context.get(FacilioConstants.ContextNames.NEW_READING_RULE);

        User currentUser = AccountUtil.getCurrentUser();
        readingRule.setOrgId(currentUser.getOrgId());
        readingRule.setCreatedBy(currentUser.getId());
        readingRule.setCreatedTime(System.currentTimeMillis());
        readingRule.setReadingModuleId((Long) context.get(FacilioConstants.ContextNames.MODULE_ID));
        readingRule.setReadingFieldId(getReadingFieldId(context));
        readingRule.setWorkflowId(readingRule.getWorkflowContext().getId());
        if (readingRule.getImpact() != null) {
            readingRule.setImpactId(readingRule.getImpact().getId());
        }

        NewReadingRuleAPI.addReadingRule(readingRule);

        context.put(NamespaceConstants.NAMESPACE, readingRule.getNs());
        context.put(NamespaceConstants.PARENT_RULE_ID, readingRule.getId());
        context.put(NamespaceConstants.NAMESPACE_FIELDS, readingRule.getNs().getFields());
        context.put(FacilioConstants.ContextNames.ASSETS, readingRule.getMatchedResources());

        return Boolean.FALSE;
    }

    private Long getReadingFieldId(Context ctx) throws Exception {
        List<FacilioModule> modules = (List<FacilioModule>) ctx.get(FacilioConstants.ContextNames.MODULE);
        if (modules != null) {
            FacilioModule module = modules.get(0);
            ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioField ruleResult = bean.getField("ruleResult", module.getName());
            return ruleResult.getFieldId();
        }
        return null;
    }

}
