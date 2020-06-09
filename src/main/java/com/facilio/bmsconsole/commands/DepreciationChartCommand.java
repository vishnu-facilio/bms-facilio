package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.AssetDepreciationContext;
import com.facilio.bmsconsole.context.AssetDepreciationRelContext;
import com.facilio.bmsconsole.util.AssetDepreciationAPI;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class DepreciationChartCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long id = (Long) context.get(FacilioConstants.ContextNames.ID);
        Long assetId = (Long) context.get(FacilioConstants.ContextNames.ASSET_ID);
        if (id != null && id > 0 && assetId != null && assetId > 0) {
            AssetDepreciationContext assetDepreciation = AssetDepreciationAPI.getAssetDepreciation(id);
            if (assetDepreciation == null) {
                throw new IllegalArgumentException("Invalid asset depreciation");
            }

            List<AssetDepreciationRelContext> assetDepreciationRelList = assetDepreciation.getAssetDepreciationRelList();
            AssetContext assetContext = null;
            if (CollectionUtils.isNotEmpty(assetDepreciationRelList)) {
                for (AssetDepreciationRelContext relContext : assetDepreciationRelList) {
                    if (relContext.getAssetId() == assetId) {
                        assetContext = AssetsAPI.getAssetInfo(assetId);
                        break;
                    }
                }
            }
            if (assetContext == null) {
                throw new IllegalArgumentException("Asset not found");
            }

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

            FacilioModule assetModule = modBean.getModule(FacilioConstants.ContextNames.ASSET);
            float totalPrice = (float) FieldUtil.getValue(assetContext, assetDepreciation.getTotalPriceFieldId(), assetModule);
            if (totalPrice == -1) {
                throw new IllegalArgumentException("Price cannot be empty");
            }
            float salvageAmount = (float) FieldUtil.getValue(assetContext, assetDepreciation.getSalvagePriceFieldId(), assetModule);
            long date = (long) FieldUtil.getValue(assetContext, assetDepreciation.getStartDateFieldId(), assetModule);
            if (date == -1) {
                throw new IllegalArgumentException("Start date cannot be empty");
            }

            AssetDepreciationContext.DepreciationType depreciationType = assetDepreciation.getDepreciationTypeEnum();

            List<Map<String, Object>> mapList = new ArrayList<>();
            Calendar instance = Calendar.getInstance();
            instance.setTimeInMillis(date);
            int dayOfMonth = instance.get(Calendar.DATE);

            float unitPrice = totalPrice;

            // remove the salvage amount from total depreciate amount
            if (salvageAmount > 0) {
                totalPrice -= salvageAmount;
            }

            while (unitPrice >= 0) {

                Map<String, Object> map = new HashMap<>();

                instance.setTimeInMillis(date);
                instance.set(Calendar.DATE, dayOfMonth);
                date = instance.getTimeInMillis();

                map.put("price", unitPrice);
                map.put("date", DateTimeUtil.getFormattedTime(date, "dd-MMM-yyyy"));

                if (unitPrice <= 0) {
                    break;
                }

                date = assetDepreciation.nextDate(date);
                unitPrice = depreciationType.nextDepreciatedUnitPrice(totalPrice, assetDepreciation.getFrequency(), unitPrice);
                unitPrice = Math.round(unitPrice);
                mapList.add(map);
            }

            context.put("depreciationList", mapList);
        }
        return false;
    }
}
