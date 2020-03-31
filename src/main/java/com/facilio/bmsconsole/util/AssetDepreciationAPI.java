package com.facilio.bmsconsole.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetDepreciationContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;

public class AssetDepreciationAPI {

    public static AssetDepreciationContext getAssetDepreciation(long id) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET_DEPRECIATION);
        SelectRecordsBuilder<AssetDepreciationContext> builder = new SelectRecordsBuilder<AssetDepreciationContext>()
                .module(module)
                .beanClass(AssetDepreciationContext.class)
                .select(modBean.getAllFields(FacilioConstants.ContextNames.ASSET_DEPRECIATION))
                .andCondition(CriteriaAPI.getIdCondition(id, module));
        return builder.fetchFirst();
    }
}
