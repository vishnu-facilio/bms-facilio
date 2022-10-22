package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.CurrencyContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.command.FacilioCommand;
import com.facilio.util.CurrencyUtil;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GetAllCurrenciesCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
        String searchString = (String) context.get(FacilioConstants.ContextNames.SEARCH);

        List<CurrencyContext> currencyList = CurrencyUtil.getCurrencyList(pagination, searchString);
        if (currencyList == null) {
            currencyList = new ArrayList<>();
        }

        CurrencyContext baseCurrency = CurrencyUtil.getBaseCurrency();
        for (CurrencyContext currency : currencyList) {
            if (baseCurrency != null && baseCurrency.getCurrencyCode().equals(currency.getCurrencyCode())) {
                currency.setBaseCurrency(true);
                break;
            }
        }

        context.put(FacilioConstants.ContextNames.CURRENCIES_LIST, currencyList);
        return false;
    }
}