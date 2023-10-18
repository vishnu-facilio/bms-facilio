package com.facilio.bmsconsoleV3.actions;

import com.facilio.bmsconsoleV3.commands.ReadOnlyChainFactoryV3;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.quotation.NumberFormatContext;
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
public class NumberFormatAction extends V3Action {


    public NumberFormatContext getNumberformat() {
        return numberformat;
    }

    public void setNumberformat(NumberFormatContext numberformat) {
        this.numberformat = numberformat;
    }

    NumberFormatContext numberformat;

    @Setter
    @Getter
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
    public String addNumberFormat() throws Exception {

        FacilioChain chain = TransactionChainFactoryV3.addNumberformat();
        FacilioContext context = chain.getContext();

        context.put(FacilioConstants.ContextNames.NUMBER_FORMAT, getNumberformat());
        chain.execute();

        setData(FacilioConstants.ContextNames.NUMBER_FORMAT, context.get(FacilioConstants.ContextNames.NUMBER_FORMAT));

        return V3Action.SUCCESS;
    }

    public String updateNumberFormat() throws Exception {

        FacilioChain chain = TransactionChainFactoryV3.updateNumberFormat();
        FacilioContext context = chain.getContext();

        context.put(FacilioConstants.ContextNames.NUMBER_FORMAT, getNumberformat());
        chain.execute();

        setData(FacilioConstants.ContextNames.NUMBER_FORMAT, context.get(FacilioConstants.ContextNames.NUMBER_FORMAT));

        return V3Action.SUCCESS;
    }

    public String fetchNumberFormat() throws Exception {

        FacilioChain chain = ReadOnlyChainFactoryV3.getNumberFormatData();
        FacilioContext context = chain.getContext();

        context.put(FacilioConstants.ContextNames.NUMBER_FORMAT, getNumberformat());
        chain.execute();

        setData(FacilioConstants.ContextNames.NUMBER_FORMAT, context.get(FacilioConstants.ContextNames.NUMBER_FORMAT));

        return V3Action.SUCCESS;
    }

    public String deleteNumberFromat() throws Exception {

        FacilioChain chain = TransactionChainFactoryV3.deleteNumberFormat();

        FacilioContext context = chain.getContext();

        chain.execute();
        setData(FacilioConstants.ContextNames.NUMBER_FORMAT, context.get(FacilioConstants.ContextNames.NUMBER_FORMAT));


        return V3Action.SUCCESS;
    }
}
