package com.facilio.bmsconsoleV3.commands;

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

import java.util.*;

public class ExistingSparePartSelectionCommandV3 extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List> recordMap = (Map<String, List>) context.get("recordMap");
        List<AssetSpareParts> existingSpareParts = recordMap.get("assetSpareParts");
        Map<String, Object> paramMap = (Map<String, Object>) context.get("bodyParams");
        if(paramMap == null){
            return false;
        }
        if(!paramMap.containsKey("isSelectSparePart")){
            return false;
        }
        Boolean isSelectSparePart = (Boolean) paramMap.get("isSelectSparePart");
        if(!isSelectSparePart){
            return false;
        }
        Long assetId = (Long) paramMap.get("assetId");
        if( assetId == null){
            return false;
        }
        if( existingSpareParts.isEmpty()) {
            return false;
        }
        ArrayList<Long> sparePartIds = new ArrayList<>();
        for (AssetSpareParts record: existingSpareParts) {
            sparePartIds.add( record.getId());
        }

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Collection<SupplementRecord> lookUpfields = new ArrayList<>();
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        List<FacilioField> fields = modBean.getAllFields(moduleName);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        lookUpfields.add((LookupField) fieldMap.get("asset"));
        lookUpfields.add((LookupField) fieldMap.get("itemType"));

        List<AssetSpareParts> existingRecords =V3RecordAPI.getRecordsListWithSupplements(moduleName,sparePartIds,AssetSpareParts.class,lookUpfields);
        recordMap.replace("assetSpareParts", associateExistingSpareParts(existingRecords,assetId));
        System.out.println(recordMap);


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
            newSparePart.setRequiredCount(1);
            newSpareParts.add(newSparePart);
        }
        return newSpareParts;
    }
}
