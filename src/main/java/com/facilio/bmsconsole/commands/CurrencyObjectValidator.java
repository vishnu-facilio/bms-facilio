package com.facilio.bmsconsole.commands;

import org.apache.commons.collections4.CollectionUtils;
import com.facilio.bmsconsole.context.CurrencyContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.command.FacilioCommand;
import com.facilio.unitconversion.Metric;
import org.apache.commons.chain.Context;
import com.facilio.unitconversion.Unit;
import com.facilio.util.CurrencyUtil;


import java.util.Collection;
import java.util.List;

public class CurrencyObjectValidator extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        CurrencyContext currencyContext = (CurrencyContext) context.get(FacilioConstants.ContextNames.CURRENCY);
        CurrencyContext oldCurrencyContext = null;

        if (currencyContext != null) {
            String displaySymbol = null;
            int validDecimalPlaces = 10;
            long currencyId = currencyContext.getId();
            int decimalPlaces = currencyContext.getDecimalPlaces();
            double exchangeRate = currencyContext.getExchangeRate();
            String currencyCode = currencyContext.getCurrencyCode();

            if (currencyId == -1) {                                                         // Add Currency
                Collection<Unit> availableCurrencies = Unit.getUnitsForMetric(Metric.CURRENCY);
                for (Unit currencyUnit : availableCurrencies) {
                    if (currencyUnit.getDisplayName().equals(currencyCode)) {
                        displaySymbol = currencyUnit.getSymbol();
                        currencyContext.setDisplaySymbol(displaySymbol);
                        break;
                    }
                }
                if (displaySymbol == null) {
                    throw new IllegalArgumentException("Currency code does not exist");
                }

                oldCurrencyContext = CurrencyUtil.getCurrencyFromCode(currencyCode);
                if (oldCurrencyContext != null) {
                    throw new IllegalArgumentException("Currency already exists");
                }

                List<CurrencyContext> currencyList = CurrencyUtil.getCurrencyList(null, null);
                if (CollectionUtils.isEmpty(currencyList)) {                                // Add BaseCurrency
                    currencyContext.setBaseCurrency(true);
                    currencyContext.setExchangeRate(1);
                }
            } else {                                                                        // Update Currency
                oldCurrencyContext = CurrencyUtil.getCurrencyFromId(currencyId);
                CurrencyContext baseCurrency = CurrencyUtil.getBaseCurrency();
                if (!currencyCode.equals(oldCurrencyContext.getCurrencyCode())) {           // Cannot change currencyCode
                    throw new IllegalArgumentException("Currency Code cannot be changed");
                }
                if (baseCurrency != null && baseCurrency.getId() == currencyId) {           // Cant Edit BaseCurrency exchangeRate
                    currencyContext.setBaseCurrency(true);
                    currencyContext.setExchangeRate(1);
                } else {
                    currencyContext.setBaseCurrency(false);
                }
            }

            // Validate Decimal Places
            if (decimalPlaces == -1) {
                currencyContext.setDecimalPlaces(2);
            } else if (decimalPlaces > 10) {
                throw new IllegalArgumentException("Decimal Places is too high");
            }
            // Validate Exchange Rate
            if (exchangeRate <= 0.0) {
                throw new IllegalArgumentException("Exchange Rate should greater than 0");
            }
            exchangeRate = Math.round(exchangeRate * Math.pow(10, validDecimalPlaces)) / Math.pow(10, validDecimalPlaces);
            currencyContext.setExchangeRate(exchangeRate);
        }

        context.put(FacilioConstants.ContextNames.CURRENCY, currencyContext);
        context.put(FacilioConstants.ContextNames.OLD_CURRENCY, oldCurrencyContext);

        return false;
    }
}
