package com.facilio.multiImport;

import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.commands.building.multi_import.CreateBuildingAfterSaveImport;
import com.facilio.bmsconsoleV3.commands.floor.multi_import.CreateFloorAfterSaveImport;
import com.facilio.bmsconsoleV3.commands.site.multi_import.CreateSiteAfterSaveImport;
import com.facilio.bmsconsoleV3.commands.space.multi_import.CreateSpaceAfterSaveImport;
import com.facilio.bmsconsoleV3.context.*;
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
                .afterProcessRowFunction((rowNumber, rowValue, prop, context) -> {
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
                .afterProcessRowFunction((rowNumber, rowValue, prop, context) -> {
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
                .afterProcessRowFunction((rowNumber, rowValue, prop, context) -> {
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
                .afterProcessRowFunction((rowNumber, rowValue, prop, context) -> {
                    prop.put("spaceType", V3BaseSpaceContext.SpaceType.SPACE.getIntVal());
                    return null;
                })
                .done()
                .createHandler()
                .afterSaveCommand(new CreateSpaceAfterSaveImport())
                .done()
                .build();
    }
}
