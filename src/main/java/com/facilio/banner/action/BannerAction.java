package com.facilio.banner.action;

import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class BannerAction extends FacilioAction {

    public String getBanners() throws Exception {
        FacilioChain bannerChain = ReadOnlyChainFactory.getBannerChain();
        bannerChain.execute();
        FacilioContext context = bannerChain.getContext();

        setResult(FacilioConstants.ContextNames.FACILIO_BANNERS, context.get(FacilioConstants.ContextNames.FACILIO_BANNERS));
        return SUCCESS;
    }

    private long id = -1;
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String closeBanner() throws Exception {
        FacilioChain chain = TransactionChainFactory.getCloseBannerChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID, id);
        chain.execute();

        return SUCCESS;
    }
}
