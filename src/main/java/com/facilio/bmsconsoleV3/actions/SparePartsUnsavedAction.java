package com.facilio.bmsconsoleV3.actions;

import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.asset.AssetSpareParts;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.V3Action;

import java.util.List;

public class SparePartsUnsavedAction extends V3Action {
    private static final long serialVersionUID = 1L;
    private Long assetId;
    List<Long> selectedIds;

    public List<Long> getSelectedIds() {
        return selectedIds;
    }

    public void setSelectedIds(List<Long> selectedIds) {
        this.selectedIds = selectedIds;
    }

    public Long getAssetId() {
        return assetId;
    }

    public void setAssetId(Long assetId) {
        this.assetId = assetId;
    }

    public String select() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.getUnsavedSparePartsSelectionChainV3();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ASSET, assetId);
        context.put("selectedIds", selectedIds);
        chain.execute();
        setData(FacilioConstants.ContextNames.ASSET_SPARE_PARTS, FieldUtil.getAsJSONArray((List)context.get(FacilioConstants.ContextNames.ASSET_SPARE_PARTS), AssetSpareParts.class) );
        return V3Action.SUCCESS;
    }
}
