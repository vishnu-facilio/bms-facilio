package com.facilio.bmsconsoleV3.actions;

import com.facilio.bmsconsole.context.PlannedMaintenance;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.inspection.InspectionTemplateContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.V3Action;

import java.util.List;

public class RotatingAssetAction extends V3Action {
    private static final long serialVersionUID = 1L;
    private Long assetId;

    public Long getAssetId() {
        return assetId;
    }

    public void setAssetId(Long assetId) {
        this.assetId = assetId;
    }

    public String rotatingAssetUsages() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.getRotatingAssetUsagesChainV3();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ASSET, assetId);
        chain.execute();
        setData(FacilioConstants.ContextNames.PLANNED_MAINTENANCE_LIST, FieldUtil.getAsJSONArray((List) context.get(FacilioConstants.ContextNames.PLANNED_MAINTENANCE_LIST), PlannedMaintenance.class));
        setData(FacilioConstants.ContextNames.INSPECTION_TEMPLATE_LIST, FieldUtil.getAsJSONArray((List) context.get(FacilioConstants.ContextNames.INSPECTION_TEMPLATE_LIST), InspectionTemplateContext.class));
        return V3Action.SUCCESS;
    }
}
