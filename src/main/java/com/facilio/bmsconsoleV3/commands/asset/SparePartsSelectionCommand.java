package com.facilio.bmsconsoleV3.commands.asset;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.asset.AssetSpareParts;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SupplementRecord;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class SparePartsSelectionCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> selectedIds = (List<Long>) context.get("selectedIds");
        Long assetId = (Long) context.get(FacilioConstants.ContextNames.ASSET);
        if(selectedIds != null && assetId != null) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            Collection<SupplementRecord> lookUpfields = new ArrayList<>();
            String moduleName = FacilioConstants.ContextNames.ASSET_SPARE_PARTS;
            List<FacilioField> fields = modBean.getAllFields(moduleName);
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
            lookUpfields.add((LookupField) fieldMap.get("asset"));
            lookUpfields.add((LookupField) fieldMap.get("itemType"));

            List<AssetSpareParts> existingRecords = V3RecordAPI.getRecordsListWithSupplements(moduleName,selectedIds,AssetSpareParts.class,lookUpfields);
            context.put(FacilioConstants.ContextNames.ASSET_SPARE_PARTS,associateExistingSpareParts(existingRecords,assetId));
        }

        return false;
    }
    public List<AssetSpareParts> associateExistingSpareParts(List<AssetSpareParts> existingRecords, Long assetId) {

        V3AssetContext currentAsset = new V3AssetContext();
        currentAsset.setId(assetId);

        List<AssetSpareParts> newSpareParts = new ArrayList<>();
        for (AssetSpareParts existingRecord: existingRecords) {
            AssetSpareParts newSparePart = new AssetSpareParts();
            newSparePart.setAsset(currentAsset);
            newSparePart.setItemType(existingRecord.getItemType());
            newSparePart.setRequiredCount(existingRecord.getRequiredCount());
            newSpareParts.add(newSparePart);
        }
        return newSpareParts;
    }
}


