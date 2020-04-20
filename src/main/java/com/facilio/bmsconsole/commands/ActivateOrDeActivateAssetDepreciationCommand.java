package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetDepreciationContext;
import com.facilio.bmsconsole.util.AssetDepreciationAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import org.apache.commons.chain.Context;

import java.util.Collections;

public class ActivateOrDeActivateAssetDepreciationCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long id = (Long) context.get(FacilioConstants.ContextNames.ID);
        Boolean activate = (Boolean) context.get(FacilioConstants.ContextNames.ACTIVATE);
        if (id != null && id > 0) {
            if (activate == null) {
                activate = false;
            }

            AssetDepreciationContext assetDepreciation = AssetDepreciationAPI.getAssetDepreciation(id);
            assetDepreciation.setActive(activate);

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET_DEPRECIATION);
            UpdateRecordBuilder<AssetDepreciationContext> builder = new UpdateRecordBuilder<AssetDepreciationContext>()
                    .module(module)
                    .fields(Collections.singletonList(modBean.getField("active", FacilioConstants.ContextNames.ASSET_DEPRECIATION)))
                    .andCondition(CriteriaAPI.getIdCondition(id, module));
            builder.update(assetDepreciation);
        }
        return false;
    }
}
