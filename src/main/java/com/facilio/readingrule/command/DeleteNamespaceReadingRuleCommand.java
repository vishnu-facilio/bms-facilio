package com.facilio.readingrule.command;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.command.FacilioCommand;
import com.facilio.ns.NamespaceAPI;
import com.facilio.ns.context.NSType;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DeleteNamespaceReadingRuleCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        NewReadingRuleContext newReadingRuleContext =(NewReadingRuleContext) recordMap.get(moduleName).get(0);
        List<NSType> list=new ArrayList<>();
        list.add(NSType.READING_RULE);
        list.add(NSType.FAULT_IMPACT_RULE);
        NamespaceAPI.deleteNameSpacesFromRuleId(newReadingRuleContext.getId(),list);
        return false;
    }
}