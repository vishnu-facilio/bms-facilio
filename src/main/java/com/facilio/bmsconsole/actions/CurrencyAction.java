package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.bmsconsole.context.CurrencyContext;
import org.json.simple.JSONObject;

public class CurrencyAction extends FacilioAction {
    private CurrencyContext currency;

    public CurrencyContext getCurrency() {
        return currency;
    }

    public void setCurrency(CurrencyContext currency) {
        this.currency = currency;
    }

    public String addOrUpdateCurrency() throws Exception {
        FacilioChain addOrUpdateCurrencyChain = TransactionChainFactory.getAddOrUpdateCurrencyChain();

        FacilioContext context = addOrUpdateCurrencyChain.getContext();
        context.put(FacilioConstants.ContextNames.CURRENCY, currency);
        addOrUpdateCurrencyChain.execute();

        setResult("currency", context.get(FacilioConstants.ContextNames.CURRENCY));
        return SUCCESS;
    }

    public String updateStatus() throws Exception {
        FacilioChain updateCurrencyChain = TransactionChainFactory.getUpdateCurrencyStatusChain();

        FacilioContext context = updateCurrencyChain.getContext();
        context.put(FacilioConstants.ContextNames.CURRENCY, currency);
        updateCurrencyChain.execute();

        setResult("result", "success");
        return SUCCESS;
    }

    public String getBaseCurrency() throws Exception {
        FacilioChain baseCurrencyChain = TransactionChainFactory.getBaseCurrencyChain();

        FacilioContext context = baseCurrencyChain.getContext();
        baseCurrencyChain.execute();

        setResult("currency", context.get(FacilioConstants.ContextNames.CURRENCY));
        return SUCCESS;
    }

    public String getAllCurrencies() throws Exception {
        FacilioChain listChain = TransactionChainFactory.getAllCurrenciesChain();

        FacilioContext context = listChain.getContext();
        JSONObject pagination = new JSONObject();
        pagination.put("page", getPage());
        pagination.put("perPage", getPerPage());
        if (getPerPage() < 0) {
            pagination.put("perPage", 5000);
        }
        context.put(FacilioConstants.ContextNames.PAGINATION, pagination);
        context.put(FacilioConstants.ContextNames.SEARCH, getSearch());
        listChain.execute();

        setResult(FacilioConstants.ContextNames.CURRENCIES_LIST, context.get(FacilioConstants.ContextNames.CURRENCIES_LIST));
        setResult(FacilioConstants.ContextNames.SUPPLEMENTS, context.get(FacilioConstants.ContextNames.SUPPLEMENTS));
        return SUCCESS;
    }

    public String getCurrenciesCount() throws Exception {
        FacilioChain listCountChain = TransactionChainFactory.getCurrenciesCountChain();

        FacilioContext context = listCountChain.getContext();
        context.put(FacilioConstants.ContextNames.SEARCH, getSearch());
        listCountChain.execute();

        setResult(FacilioConstants.ContextNames.COUNT, context.get(FacilioConstants.ContextNames.COUNT));
        return SUCCESS;
    }

    public String getFilteredCurrencies() throws Exception {
        FacilioChain filteredListChain = TransactionChainFactory.getUnconfiguredCurrenciesChain();

        FacilioContext context = filteredListChain.getContext();
        filteredListChain.execute();

        setResult(FacilioConstants.ContextNames.CURRENCIES_LIST, context.get(FacilioConstants.ContextNames.CURRENCIES_LIST));
        return SUCCESS;
    }
}
