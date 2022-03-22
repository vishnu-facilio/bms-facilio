package com.facilio.readingrule.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleFactory;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import org.apache.commons.chain.Context;

public class DeleteReadingRuleCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long ruleId = (Long) context.get(FacilioConstants.ContextNames.ID);
        NewReadingRuleContext rule = NewReadingRuleAPI.getRule(ruleId);
        if (rule == null) {
            throw new Exception("Rule (" + ruleId + ") is not found!!");
        }

        FacilioModule module = ModuleFactory.getNewReadingRuleModule();
        GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
                .table(module.getTableName())
                .andCondition(CriteriaAPI.getIdCondition(ruleId, module));
        deleteBuilder.delete();

        return false;
    }
}
