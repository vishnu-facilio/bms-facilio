package com.facilio.multiImport;

import com.facilio.bmsconsoleV3.commands.SetLocalIdCommandV3;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.commands.asset.RemoveAssetExtendedModulesFromRecordMap;
import com.facilio.bmsconsoleV3.commands.asset.multi_import.AssetCategoryAdditionInExtendModuleV3ImportCommand;
import com.facilio.bmsconsoleV3.commands.building.multi_import.CreateBuildingAfterSaveImport;
import com.facilio.bmsconsoleV3.commands.floor.multi_import.CreateFloorAfterSaveImport;
import com.facilio.bmsconsoleV3.commands.site.multi_import.CreateSiteAfterSaveImport;
import com.facilio.bmsconsoleV3.commands.space.multi_import.CreateSpaceAfterSaveImport;
import com.facilio.bmsconsoleV3.commands.tenant.multi_import.AddTenantUserImportCommand;
import com.facilio.bmsconsoleV3.commands.tenant.multi_import.BeforeTenantImportProcessCommand;
import com.facilio.bmsconsoleV3.commands.tenant.multi_import.UpdateTenantUserImportCommand;
import com.facilio.bmsconsoleV3.commands.tenant.multi_import.ValidatePeopleEmailBeforeAddOrUpdateImportCommand;
import com.facilio.bmsconsoleV3.context.*;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.multiImport.annotations.ImportModule;
import com.facilio.multiImport.config.ImportConfig;

import java.util.Arrays;
import java.util.function.Supplier;

public class MultiImportConfigurations {
    @ImportModule("workorder")
    public static Supplier<ImportConfig> getWorkOrderImportConfig() {
        return () -> new ImportConfig.ImportConfigBuilder(V3WorkOrderContext.class)
                .importHandler()
                .loadLookUpExtraSelectFields("workorder", Arrays.asList("status"))
                .done()
                .createHandler()
                .beforeSaveCommand(TransactionChainFactoryV3.getWorkOrderBeforeCreateImportChain())
                .done()
                .updateHandler()
                .beforeUpdateCommand()
                .done()
                .build();
    }
    @ImportModule("site")
    public static Supplier<ImportConfig> getSiteImportConfig(){
        return () -> new ImportConfig.ImportConfigBuilder(V3SiteContext.class)
                .importHandler()
                .afterProcessRowFunction((rowContext, rowValue, prop, context) -> {
                    prop.put("spaceType", V3BaseSpaceContext.SpaceType.SITE.getIntVal());
                    return null;
                })
                .done()
                .createHandler()
                .afterSaveCommand(new CreateSiteAfterSaveImport())
                .done()
                .build();
    }
    @ImportModule("building")
    public static Supplier<ImportConfig> getBuildingImportConfig(){
        return () -> new ImportConfig.ImportConfigBuilder(V3BuildingContext.class)
                .importHandler()
                .afterProcessRowFunction((rowContext, rowValue, prop, context) -> {
                    prop.put("spaceType", V3BaseSpaceContext.SpaceType.BUILDING.getIntVal());
                    return null;
                })
                .done()
                .createHandler()
                .afterSaveCommand(new CreateBuildingAfterSaveImport())
                .done()
                .build();
    }
    @ImportModule("floor")
    public static Supplier<ImportConfig> getFloorImportConfig(){
        return () -> new ImportConfig.ImportConfigBuilder(V3FloorContext.class)
                .importHandler()
                .afterProcessRowFunction((rowContext, rowValue, prop, context) -> {
                    prop.put("spaceType", V3BaseSpaceContext.SpaceType.FLOOR.getIntVal());
                    return null;
                })
                .done()
                .createHandler()
                .afterSaveCommand(new CreateFloorAfterSaveImport())
                .done()
                .build();
    }
    @ImportModule("space")
    public static Supplier<ImportConfig> getSpaceImportConfig(){
        return () -> new ImportConfig.ImportConfigBuilder(V3SpaceContext.class)
                .importHandler()
                .lookupUniqueFieldsMap("floor",Arrays.asList("name","building","site"))
                .lookupUniqueFieldsMap("building",Arrays.asList("name","site"))
                .lookupUniqueFieldsMap("space1",Arrays.asList("name","site","*building","*floor"))
                .lookupUniqueFieldsMap("space2",Arrays.asList("name","site","*building","*floor","space1"))
                .lookupUniqueFieldsMap("space3",Arrays.asList("name","site","*building","*floor","space1","space2"))
                .lookupUniqueFieldsMap("space4",Arrays.asList("name","site","*building","*floor","space1","space2","space3"))
                .lookupUniqueFieldsMap("space5",Arrays.asList("name","site","*building","*floor","space1","space2","space3","space4"))
                .afterProcessRowFunction((rowContext, rowValue, prop, context) -> {
                    prop.put("spaceType", V3BaseSpaceContext.SpaceType.SPACE.getIntVal());
                    return null;
                })
                .done()
                .createHandler()
                .afterSaveCommand(new CreateSpaceAfterSaveImport())
                .done()
                .build();
    }
    @ImportModule("asset")
    public static Supplier<ImportConfig> getAssetImportConfig(){
        return () -> new ImportConfig.ImportConfigBuilder(V3AssetContext.class)
                .importHandler()
                .lookupUniqueFieldsMap("space",Arrays.asList("name","site","*building","*floor","*space1","*space2","*space3","*space4","*space5"))
                .loadLookUpExtraSelectFields(FacilioConstants.ContextNames.ASSET_CATEGORY,Arrays.asList("assetModuleID"))
                .done()
                .createHandler()
                .beforeSaveCommand(new AssetCategoryAdditionInExtendModuleV3ImportCommand(),new SetLocalIdCommandV3())
                .afterSaveCommand(new RemoveAssetExtendedModulesFromRecordMap())
                .done()
                .build();
    }
    @ImportModule("tenantunit")
    public static Supplier<ImportConfig> getTenantSpaceImportConfig(){
        return () -> new ImportConfig.ImportConfigBuilder(V3TenantUnitSpaceContext.class)
                .importHandler()
                .lookupUniqueFieldsMap("floor",Arrays.asList("name","building","site"))
                .lookupUniqueFieldsMap("building",Arrays.asList("name","site"))
                .lookupUniqueFieldsMap("space",Arrays.asList("name","site","*building","*floor","*space1","*space2","*space3","*space4","*space5"))
                .afterProcessRowFunction((rowContext, rowValue, prop, context) -> {
                    prop.put("spaceType", V3BaseSpaceContext.SpaceType.SPACE.getIntVal());
                    return null;
                })
                .done()
                .createHandler()
                .afterSaveCommand(new CreateSpaceAfterSaveImport())
                .done()
                .build();
    }
    @ImportModule("tenant")
    public static Supplier<ImportConfig> getTenantImportConfig(){
        return () -> new ImportConfig.ImportConfigBuilder(V3TenantContext.class)
                .importHandler()
                .setBatchCollectFieldNames(Arrays.asList("primaryContactEmail"))
                .beforeImportCommand(new BeforeTenantImportProcessCommand())
                .done()
                .createHandler()
                .beforeSaveCommand(new SetLocalIdCommandV3(),new ValidatePeopleEmailBeforeAddOrUpdateImportCommand())
                .afterSaveCommand(new AddTenantUserImportCommand())
                .done()
                .updateHandler()
                .beforeUpdateCommand(new ValidatePeopleEmailBeforeAddOrUpdateImportCommand())
                .afterUpadteCommand(new UpdateTenantUserImportCommand())
                .done()
                .build();
    }
}
