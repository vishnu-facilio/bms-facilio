package com.facilio.bmsconsoleV3.commands.assetCategory;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.asset.V3AssetCategoryContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class UpdateCategoryAssetModuleIdCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        // Get the request payload data as Map
        Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);
        V3AssetCategoryContext assetCategory = (V3AssetCategoryContext) recordMap.get("assetcategory").get(0);

        // Get the Module object and set its ModuleId to AssetCategory
        FacilioModule module = ((List<FacilioModule>) context.get(FacilioConstants.ContextNames.MODULE_LIST)).get(0);
        assetCategory.setAssetModuleID(module.getModuleId());

        // Get the "assetcategory" Module
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule assetCategoryModule = modBean.getModule("assetcategory");

        // Convert the Fields' list as Map and create UpdateRecordBuilder to update
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(ModuleFactory.getAssetCategoryModule().getName()));
        UpdateRecordBuilder<V3AssetCategoryContext> updateBuilder = new UpdateRecordBuilder<V3AssetCategoryContext>()
                .module(assetCategoryModule)
                .fields(Collections.singletonList(fieldMap.get("assetModuleID")))
                .andCondition(CriteriaAPI.getIdCondition(assetCategory.getId(), assetCategoryModule));
        updateBuilder.update(assetCategory);

        return false;
    }
}
