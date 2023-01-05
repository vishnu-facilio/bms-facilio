package com.facilio.bmsconsoleV3.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.asset.AssetSpareParts;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemTypesContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.util.V3Util;
import org.apache.commons.collections4.MapUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class V3AssetAPI {
    public static void updateAsset(V3AssetContext asset, long assetId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule assetModule = modBean.getModule(FacilioConstants.ContextNames.ASSET);
        List<FacilioField> assetFields = modBean.getAllFields(FacilioConstants.ContextNames.ASSET);

        UpdateRecordBuilder<V3AssetContext> updateBuilder = new UpdateRecordBuilder<V3AssetContext>()
                .module(assetModule)
                .fields(assetFields)
                .andCondition(CriteriaAPI.getIdCondition(assetId, assetModule));

        updateBuilder.update(asset);

    }
    public static Boolean createSparePart (V3ItemTypesContext itemType, V3AssetContext asset) throws Exception {
        String ModName = FacilioConstants.ContextNames.ASSET_SPARE_PARTS;
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(ModName);
        List<FacilioField> fields = modBean.getAllFields(ModName);
        AssetSpareParts existingSparePart = getSparePart(itemType,asset);

        if (existingSparePart != null){
            return false; // returns if spare part already exists
        }
        AssetSpareParts newSparePart = new AssetSpareParts();

        newSparePart.setItemType(itemType);
        newSparePart.setAsset(asset);
        newSparePart.setRequiredCount(1);
        V3Util.createRecord(module, FieldUtil.getAsProperties(newSparePart));
        return true;
    }

    public static AssetSpareParts getSparePart(V3ItemTypesContext itemType, V3AssetContext asset) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String ModName = FacilioConstants.ContextNames.ASSET_SPARE_PARTS;
        FacilioModule module = modBean.getModule(ModName);
        List<FacilioField> fields = modBean.getAllFields(ModName);

        Long itemTypeId = itemType._getId();
        Long assetId = asset.getId();
        SelectRecordsBuilder<AssetSpareParts> selectRecordsBuilder = new SelectRecordsBuilder<AssetSpareParts>()
                .module(module)
                .beanClass(AssetSpareParts.class)
                .select(fields)
                .andCondition(CriteriaAPI.getCondition("ITEM_TYPE", "itemType", String.valueOf(itemTypeId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("ASSET_ID", "asset", String.valueOf(assetId), NumberOperators.EQUALS));
        List<AssetSpareParts> sparePartsList = selectRecordsBuilder.get();
        if(sparePartsList.isEmpty()){
            return null;
        } else {
            AssetSpareParts sparePart = sparePartsList.get(0);
            return sparePart;
        }

    }

    public static void updateSparePartsIssuedCount (V3ItemTypesContext itemType, V3AssetContext asset, Integer latestIssuedCount) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String ModName = FacilioConstants.ContextNames.ASSET_SPARE_PARTS;
        FacilioModule module = modBean.getModule(ModName);
        AssetSpareParts sparePartRecord = getSparePart(itemType,asset);
        Integer existingCount =   sparePartRecord.getIssuedCount();
        if(existingCount != null){
            sparePartRecord.setIssuedCount(existingCount + latestIssuedCount);
        }else {
            sparePartRecord.setIssuedCount(latestIssuedCount);
        }
        FacilioField issuedCountField = modBean.getField("issuedCount", module.getName());
        List<FacilioField> updatedfields = new ArrayList<FacilioField>();
        updatedfields.add(issuedCountField);
        V3RecordAPI.updateRecord(sparePartRecord,module,updatedfields);
    }
}
