package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

public class AssetDepreciationAction extends FacilioAction {

    private Long id;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getDepreciationChart() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getDepreciationChartChain();
        Context context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID, id);
        chain.execute();

        setResult("depreciationList", context.get("depreciationList"));

        return SUCCESS;
    }

    public String deactivate() throws Exception {
        activateDepreciation(false);

        return SUCCESS;
    }

    private void activateDepreciation(boolean activate) throws Exception {
        FacilioChain chain = TransactionChainFactory.getActivateAssetDepreciationChain();
        Context context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID, id);
        context.put(FacilioConstants.ContextNames.ACTIVATE, activate);
        chain.execute();
    }

    public String activate() throws Exception {
        activateDepreciation(true);

        return SUCCESS;
    }
}
