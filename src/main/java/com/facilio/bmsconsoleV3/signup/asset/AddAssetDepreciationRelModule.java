package com.facilio.bmsconsoleV3.signup.asset;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.v3.context.Constants;

import java.util.ArrayList;
import java.util.List;

public class AddAssetDepreciationRelModule extends SignUpData {
    @Override
    public void addData() throws Exception {

        List<FacilioModule> modules = new ArrayList<>();

        FacilioModule assetDepreciationRelModule = constructAssetDepreciationRelModule();
        modules.add(assetDepreciationRelModule);

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain.execute();
    }

    private FacilioModule constructAssetDepreciationRelModule() throws Exception {

        FacilioModule module = new FacilioModule(
                FacilioConstants.ContextNames.ASSET_DEPRECIATION_REL,
                "Asset Depreciation Rel",
                "Asset_Depreciation_Rel",
                FacilioModule.ModuleType.BASE_ENTITY,
                true);

        List<FacilioField> fields = new ArrayList<>();

        LookupField asset = FieldFactory.getDefaultField("asset","Asset" ,"ASSET_ID", module, FieldType.LOOKUP,true);
        asset.setLookupModule(Constants.getModBean().getModule(FacilioConstants.ContextNames.ASSET));
        fields.add(asset);

        LookupField assetDepreciation = FieldFactory.getDefaultField("depreciation","Depreciation","DEPRECIATION_ID", module, FieldType.LOOKUP);
        assetDepreciation.setLookupModule(Constants.getModBean().getModule(FacilioConstants.ContextNames.ASSET_DEPRECIATION));
        fields.add(assetDepreciation);

        fields.add(FieldFactory.getDefaultField("depreciationAmount","Depreciation Amount", "DEPRECIATION_AMOUNT", module, FieldType.DECIMAL));
        fields.add(FieldFactory.getDefaultField("activated","Activated","ACTIVATED", module, FieldType.BOOLEAN));
        fields.add(FieldFactory.getDefaultField("lastCalculatedId","Last Calculated Id" ,"LAST_CALCULATED_ID", module, FieldType.NUMBER));

        module.setFields(fields);

        return module;
    }
}
