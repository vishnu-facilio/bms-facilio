package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.util.CurrencyUtil;
import org.apache.commons.chain.Context;

public class GetCurrenciesCountCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String searchString = (String) context.get(FacilioConstants.ContextNames.SEARCH);

        long currenciesCount = CurrencyUtil.getCurrenciesCount(searchString);

        context.put(FacilioConstants.ContextNames.COUNT, currenciesCount);
        return false;
    }
}
