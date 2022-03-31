package com.facilio.readingrule.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleFactory;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;

public class DeleteReadingRuleCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        NewReadingRuleContext rule = (NewReadingRuleContext) context.get(FacilioConstants.ContextNames.NEW_READING_RULE);
        FacilioUtil.throwRunTimeException(rule == null, "Rule cannot be null");

        FacilioModule module = ModuleFactory.getNewReadingRuleModule();
        GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
                .table(module.getTableName())
                .andCondition(CriteriaAPI.getIdCondition(rule.getId(), module));
        deleteBuilder.delete();

        return false;
    }
}
