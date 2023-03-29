package com.facilio.multiImport.command;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;

public class MultiImportListFilterCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Criteria criteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
        boolean myImport = (boolean) context.get(FacilioConstants.ContextNames.MY_IMPORT);

        if (criteria == null) {
            criteria = new Criteria();
        }

        if (myImport) {
            Condition condition = new Condition();
            Long ouid = AccountUtil.getCurrentUser().getOuid();

            FacilioModule module = ModuleFactory.getImportDataDetailsModule();

            condition.setField(FieldFactory.getNumberField("createdBy", "CREATED_BY", module));
            condition.setValue(ouid.toString());
            condition.setOperator(NumberOperators.EQUALS);

            criteria.addAndCondition(condition);
        }

        if (!criteria.isEmpty()) {
            context.put(FacilioConstants.ContextNames.FILTER_CRITERIA, criteria);
        }
        return false;
    }

}
