package com.facilio.readingrule.command;


import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.ns.NamespaceConstants;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.v3.context.Constants;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;

import java.util.List;
import java.util.Map;

@Log4j
public class AddNewReadingRuleCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<NewReadingRuleContext> list = recordMap.get(moduleName);
        if (CollectionUtils.isNotEmpty(list)) {
            for (NewReadingRuleContext readingRule : list) {
                setReadingModuleNdField(context, readingRule);

                if (readingRule.getImpact() != null) {
                    readingRule.setImpactId(readingRule.getImpact().getId());
                }
                readingRule.setStatus(BooleanUtils.toBooleanDefaultIfNull(readingRule.getStatus(), Boolean.TRUE));
                readingRule.setAutoClear(Boolean.TRUE);

                context.put(NamespaceConstants.NAMESPACE, readingRule.getNs());
                context.put(FacilioConstants.ContextNames.ASSETS, readingRule.getMatchedResources());
            }
        }
        return Boolean.FALSE;
    }

    private void setReadingModuleNdField(Context context, NewReadingRuleContext readingRule) throws Exception {
        FacilioModule module;
        if (context.get(FacilioConstants.ContextNames.MODULE_ID) != null) {
            module = Constants.getModBean().getModule((Long) context.get(FacilioConstants.ContextNames.MODULE_ID));
            readingRule.setReadingModuleId(module.getModuleId());
            readingRule.setReadingFieldId(getReadingFieldId(context));
        } else {
            module = Constants.getModBean().getModule(readingRule.getReadingModuleId());
        }
        readingRule.setLinkName(module.getName());

    }

    private Long getReadingFieldId(Context ctx) throws Exception {
        List<FacilioModule> modules = (List<FacilioModule>) ctx.get(FacilioConstants.ContextNames.MODULE_LIST);
        if (modules != null) {
            FacilioModule module = modules.get(0);
            ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioField ruleResult = bean.getField("ruleResult", module.getName());
            return ruleResult.getFieldId();
        }
        return null;
    }

}
