package com.facilio.bmsconsoleV3.actions;

import com.facilio.bmsconsoleV3.commands.ReadOnlyChainFactoryV3;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.quotation.QuotationSettingContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.V3Action;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.json.simple.JSONObject;

@Log4j
@Setter
@Getter
public class QuotationSettingAction extends V3Action {


    public QuotationSettingContext getQuotationsetting() {
        return quotationsetting;
    }

    public void setQuotationsetting(QuotationSettingContext quotationsetting) {
        this.quotationsetting = quotationsetting;
    }

    QuotationSettingContext quotationsetting;

    @Setter @Getter
    long id;

    private JSONObject data;


    public JSONObject getData() {
        return this.data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }

    public void setData(String key, Object result) {
        if (this.data == null) {
            this.data = new JSONObject();
        }
        this.data.put(key, result);
    }
    public String addQuotationSetting() throws Exception {

        FacilioChain chain = TransactionChainFactoryV3.addQuotationSetting();
        FacilioContext context = chain.getContext();

        context.put(FacilioConstants.ContextNames.QUOTATIONSETTING, getQuotationsetting());
        chain.execute();

        setData(FacilioConstants.ContextNames.QUOTATIONSETTING, context.get(FacilioConstants.ContextNames.QUOTATIONSETTING));

        return V3Action.SUCCESS;
    }

    public String updateQuotationSetting() throws Exception {

        FacilioChain chain = TransactionChainFactoryV3.updateQuotationSetting();
        FacilioContext context = chain.getContext();

        context.put(FacilioConstants.ContextNames.QUOTATIONSETTING, getQuotationsetting());
        chain.execute();

        setData(FacilioConstants.ContextNames.QUOTATIONSETTING, context.get(FacilioConstants.ContextNames.QUOTATIONSETTING));

        return V3Action.SUCCESS;
    }

    public String fetchQuotationSetting() throws Exception {

        FacilioChain chain = ReadOnlyChainFactoryV3.getQuotationSettingData();
        FacilioContext context = chain.getContext();

        context.put(FacilioConstants.ContextNames.QUOTATIONSETTING, getQuotationsetting());
        chain.execute();

        setData(FacilioConstants.ContextNames.QUOTATIONSETTING, context.get(FacilioConstants.ContextNames.QUOTATIONSETTING));

        return V3Action.SUCCESS;
    }

    public String deleteQuoteSetting() throws Exception {

        FacilioChain chain = TransactionChainFactoryV3.getDeleteQuoteSettingChain();

        FacilioContext context = chain.getContext();

        chain.execute();
        setData(FacilioConstants.ContextNames.QUOTATIONSETTING, context.get(FacilioConstants.ContextNames.QUOTATIONSETTING));


        return V3Action.SUCCESS;
    }
}
