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

    private Long assetId;
    public Long getAssetId() {
        return assetId;
    }
    public void setAssetId(Long assetId) {
        this.assetId = assetId;
    }

    public String getDepreciationChart() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getDepreciationChartChain();
        Context context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID, id);
        context.put(FacilioConstants.ContextNames.ASSET_ID, assetId);
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

    public String addAssetInDepreciation() throws Exception {
        FacilioChain chain = TransactionChainFactory.getAddAssetToDepreciationChain();
        Context context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID, id);
        context.put(FacilioConstants.ContextNames.ASSET_ID, assetId);
        chain.execute();

        return SUCCESS;
    }

    public String removeAssetInDepreciation() throws Exception {
        FacilioChain chain = TransactionChainFactory.getRemoveAssetToDepreciationChain();
        Context context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID, id);
        context.put(FacilioConstants.ContextNames.ASSET_ID, assetId);
        chain.execute();

        return SUCCESS;
    }

    public String getForAsset() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getDepreciationForAsset();
        Context context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ASSET_ID, assetId);
        chain.execute();

        setResult(FacilioConstants.ContextNames.ASSET_DEPRECIATION, context.get(FacilioConstants.ContextNames.ASSET_DEPRECIATION));
        return SUCCESS;
    }
}
