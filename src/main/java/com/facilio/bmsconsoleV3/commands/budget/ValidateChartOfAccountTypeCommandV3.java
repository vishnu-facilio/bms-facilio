package com.facilio.bmsconsoleV3.commands.budget;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.budget.AccountTypeContext;
import com.facilio.bmsconsoleV3.context.budget.BudgetContext;
import com.facilio.bmsconsoleV3.context.budget.ChartOfAccountContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValidateChartOfAccountTypeCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<ChartOfAccountContext> chartOfAccounts = recordMap.get(moduleName);

        if(CollectionUtils.isNotEmpty(chartOfAccounts)) {
            Map<Long, AccountTypeContext> types = new HashMap<>();
            for(ChartOfAccountContext chartOfAccountContext : chartOfAccounts){
               if(chartOfAccountContext.getType() == null){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Chart of account should be associated to an account type");
                }
                AccountTypeContext type = null;
               if(!types.containsKey(chartOfAccountContext.getType().getId())) {
                  type = (AccountTypeContext) V3RecordAPI.getRecord(FacilioConstants.ContextNames.Budget.ACCOUNT_TYPE, chartOfAccountContext.getType().getId(), AccountTypeContext.class);
                  types.put(type.getId(), type);
               }
               else {
                   type = types.get(chartOfAccountContext.getType().getId());
               }
               if(type != null) {
                   chartOfAccountContext.setAccountType(type.getGroup());
               }
            }

        }
        return false;
    }
}
