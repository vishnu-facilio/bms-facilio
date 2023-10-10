package com.facilio.remotemonitoring.action;

import com.facilio.bmsconsoleV3.commands.ReadOnlyChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.V3Action;
import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
public class AlarmFilterRuleAction extends V3Action {
    private Long assetCategoryId;
    public String getSelectedCategoryAssetRelationsships() throws Exception {
        FacilioChain chain = ReadOnlyChainFactoryV3.getAssetRelationChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ASSET_CATEGORY_ID, assetCategoryId);
        chain.execute();
        setData(FacilioConstants.ContextNames.RELATIONS_FOR_ASSET_CATEGORIES, context.get(FacilioConstants.ContextNames.RELATIONS_FOR_ASSET_CATEGORIES));
        return SUCCESS;
    }
}
