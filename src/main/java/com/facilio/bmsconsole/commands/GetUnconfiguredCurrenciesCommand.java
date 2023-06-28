package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.CurrencyContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.unitconversion.Metric;
import org.apache.commons.chain.Context;
import com.facilio.unitconversion.Unit;
import com.facilio.util.CurrencyUtil;

import java.util.*;

public class GetUnconfiguredCurrenciesCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Collection<Unit> availableCurrencies = new ArrayList<>(Unit.getUnitsForMetric(Metric.CURRENCY));                     // All Currencies
        List<CurrencyContext> currencyList = CurrencyUtil.getUnconfiguredCurrencies();                      // Configured Currencies

        if (currencyList != null) {
            for (CurrencyContext currencyContext : currencyList) {
                availableCurrencies.removeIf(currency -> currency.getDisplayName().equals(currencyContext.getCurrencyCode()));
            }
        }

        List<Map<String, String>> filteredCurrencies = new ArrayList<>();
        for (Unit currencyUnit : availableCurrencies) {
            Map<String, String> currency = new HashMap<>();
            currency.put("displaySymbol", currencyUnit.getSymbol());
            currency.put("currencyCode", currencyUnit.getDisplayName());
            filteredCurrencies.add(currency);
        }

        context.put(FacilioConstants.ContextNames.CURRENCIES_LIST, filteredCurrencies);
        return false;
    }
}
