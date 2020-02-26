package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.RatingContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class RatingAction extends FacilioAction {

    private RatingContext rating;
    public RatingContext getRating() {
        return rating;
    }
    public void setRating(RatingContext rating) {
        this.rating = rating;
    }

    private String parentModuleName;
    public String getParentModuleName() {
        return parentModuleName;
    }
    public void setParentModuleName(String parentModuleName) {
        this.parentModuleName = parentModuleName;
    }

    public String addOrUpdateRating() throws Exception {
        FacilioChain chain = TransactionChainFactory.getAddOrUpdateRatingChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.RECORD, rating);
        context.put(FacilioConstants.ContextNames.PARENT_MODULE_NAME, parentModuleName);
        chain.execute();

        return SUCCESS;
    }
}
