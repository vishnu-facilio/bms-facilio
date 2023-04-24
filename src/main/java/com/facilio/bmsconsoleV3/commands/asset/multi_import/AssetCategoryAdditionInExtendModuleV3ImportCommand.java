package com.facilio.bmsconsoleV3.commands.asset.multi_import;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3BaseSpaceContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetCategoryContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.multiImport.constants.ImportConstants;
import com.facilio.multiImport.context.ImportRowContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class AssetCategoryAdditionInExtendModuleV3ImportCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List<Pair<Long,ModuleBaseWithCustomFields>>> insertRecordMap = ImportConstants.getInsertRecordMap(context);
        Map<Long, ImportRowContext> logIdVsImportRows = ImportConstants.getLogIdVsRowContextMap(context);

        List<Pair<Long,ModuleBaseWithCustomFields>> assetList = insertRecordMap.get(FacilioConstants.ContextNames.ASSET);

        if(CollectionUtils.isEmpty(assetList)){
            return false;
        }

        ModuleBean modBean = Constants.getModBean();

        for (Pair<Long,ModuleBaseWithCustomFields> logIdVsAssetPair : assetList) {
            Long logId = logIdVsAssetPair.getKey();
            try{
                V3AssetContext asset =(V3AssetContext)logIdVsAssetPair.getValue();
                V3AssetCategoryContext assetCategory = asset.getCategory();
                long assetModuleID = assetCategory.getAssetModuleID();
                FacilioModule module = modBean.getModule(assetModuleID);
                if(insertRecordMap.containsKey(module.getName())){
                    List<Pair<Long,ModuleBaseWithCustomFields>> list = insertRecordMap.get(module.getName());
                    list.add(logIdVsAssetPair);
                    insertRecordMap.put(module.getName(),list);
                }else{
                    insertRecordMap.put(module.getName(), new ArrayList<>(Arrays.asList(logIdVsAssetPair)));
                }
                if (asset.getSpace() == null || asset.getSpace().getId() < 0) {
                    V3BaseSpaceContext assetLocation = new V3BaseSpaceContext();
                    assetLocation.setId(asset.getSiteId());
                    asset.setSpace(assetLocation);
                }
            }catch (Exception e){
                ImportRowContext importRowContext = logIdVsImportRows.get(logId);
                importRowContext.setErrorOccurredRow(true);
                if(StringUtils.isNotEmpty(e.getMessage())) {
                    importRowContext.setErrorMessage(e.getMessage());
                }else {
                    importRowContext.setErrorMessage(e.toString());
                }
            }
        }
        Set<String> extendedModules = new HashSet<>(insertRecordMap.keySet());
        extendedModules.remove(FacilioConstants.ContextNames.ASSET);
        Constants.setExtendedModules(context, extendedModules);
        return false;
    }
}
