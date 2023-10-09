package com.facilio.multiImport.command;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.ReadingDataMetaAPI;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.bmsconsoleV3.context.V3ResourceContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.command.FacilioCommand;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.multiImport.util.MultiImportApi;
import com.facilio.v3.context.Constants;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j

public class InsertReadingDataMetaForMultiImportCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        LOGGER.info("InsertReadingDataMetaForMultiImportCommand start time:" + System.currentTimeMillis());
        long startTime = System.currentTimeMillis();
        Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);
        String moduleName = Constants.getModuleName(context);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);

        if (!MultiImportApi.isResourceExtendedModule(module)) {
            return false;
        }

        List<ModuleBaseWithCustomFields> records = recordMap.get(moduleName);

        if (CollectionUtils.isEmpty(records)) {
            LOGGER.info("No records found!!");
            return false;
        }

        V3ResourceContext firstRecord = (V3ResourceContext) records.get(0);
        LOGGER.info("Processing RDM updating. type: " + firstRecord.getResourceTypeEnum());

        //TODO:SPK - can delete this check, if all the stuffs moved with new one.
        boolean disableNewAssetRDMUpdate = Boolean.parseBoolean(CommonCommandUtil.getOrgInfo("disableNewAssetRDMUpdate", "false"));
        if (!disableNewAssetRDMUpdate && firstRecord.getResourceTypeEnum() == V3ResourceContext.ResourceType.ASSET) {
            List<V3AssetContext> assetRecords = records.stream().map(res -> (V3AssetContext) res).collect(Collectors.toList());
            ReadingDataMetaAPI.updateReadingDataMetaForAssets(assetRecords);
        } else {
            List<ResourceContext> resources = new ArrayList<>();
            for (ModuleBaseWithCustomFields record : records) {
                V3ResourceContext v3ResourceContext = (V3ResourceContext) record;
                ResourceContext resource = new ResourceContext();
                resource.setId(v3ResourceContext.getId());
                resource.setResourceType(v3ResourceContext.getResourceType());
                resources.add(resource);
            }
            ReadingsAPI.updateReadingDataMeta(resources);
        }

        LOGGER.info("Total time consumption for InsertReadingDataMetaForMultiImportCommand end time: " + (System.currentTimeMillis() - startTime) + "ms");
        return false;
    }
}
