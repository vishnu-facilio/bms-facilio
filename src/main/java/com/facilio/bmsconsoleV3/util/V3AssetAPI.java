package com.facilio.bmsconsoleV3.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;

import java.util.List;

public class V3AssetAPI {
    public static void updateAsset(V3AssetContext asset, long assetId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule assetModule = modBean.getModule(FacilioConstants.ContextNames.ASSET);
        List<FacilioField> assetFields = modBean.getAllFields(FacilioConstants.ContextNames.ASSET);

        UpdateRecordBuilder<V3AssetContext> updateBuilder = new UpdateRecordBuilder<V3AssetContext>()
                .module(assetModule)
                .fields(assetFields)
                .andCondition(CriteriaAPI.getIdCondition(assetId, assetModule));

        updateBuilder.update(asset);

    }
}
