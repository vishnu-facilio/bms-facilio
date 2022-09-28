package com.facilio.bmsconsoleV3.actions.formrelation;

import com.facilio.bmsconsoleV3.commands.ReadOnlyChainFactoryV3;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.spacebooking.V3SpaceBookingFormRelationContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.V3Action;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

@Log4j
@Setter
@Getter
public class FormRelationAction extends V3Action {

    private JSONObject data;

    V3SpaceBookingFormRelationContext formRelationData;


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

    public String getList() throws Exception {

        FacilioChain chain = ReadOnlyChainFactoryV3.getFormRelationList();
        FacilioContext context = chain.getContext();
        chain.execute();

        setData("getFormRelationData", context.get("FORM_RELATION_CONTEXT"));

        return V3Action.SUCCESS;
    }

    public String addformRelation() throws Exception {

        FacilioChain chain = TransactionChainFactoryV3.createFormRelationChain();
        FacilioContext context = chain.getContext();

        context.put("FORM_RELATION_CONTEXT", getFormRelationData());
        chain.execute();

        setData("getFormRelationData", context.get("FORM_RELATION_CONTEXT"));


        return V3Action.SUCCESS;
    }

    public String updateFormRelation() throws Exception {

        FacilioChain chain = TransactionChainFactoryV3.updateFormRelationChain();
        FacilioContext context = chain.getContext();

        context.put("FORM_RELATION_CONTEXT", getFormRelationData());
        chain.execute();

        setData("getFormRelationData", context.get("FORM_RELATION_CONTEXT"));


        return V3Action.SUCCESS;
    }


}
