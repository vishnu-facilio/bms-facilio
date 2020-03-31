package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.AssetDepreciationContext;
import com.facilio.bmsconsole.util.AssetDepreciationAPI;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.time.DateTimeUtil;
import org.apache.commons.chain.Context;

import java.util.*;

public class DepreciationChartCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long id = (Long) context.get(FacilioConstants.ContextNames.ID);
        if (id != null && id > 0) {
            AssetDepreciationContext assetDepreciation = AssetDepreciationAPI.getAssetDepreciation(id);

            long assetId = assetDepreciation.getAssetId();
            AssetContext assetContext = AssetsAPI.getAssetInfo(assetId);
            if (assetContext == null) {
                throw new IllegalArgumentException("Asset not found");
            }

            float unitPrice = assetContext.getUnitPrice();
            if (unitPrice == -1) {
                throw new IllegalArgumentException("Price cannot be empty");
            }

            List<Map<String, Object>> mapList = new ArrayList<>();
            long date = System.currentTimeMillis();
            Calendar instance = Calendar.getInstance();
            instance.setTimeInMillis(date);
            int dayOfMonth = instance.get(Calendar.DATE);

            float depreciationAmount = unitPrice / assetDepreciation.getFrequency();
            while (unitPrice >= 0) {
                Map<String, Object> map = new HashMap<>();

                instance.setTimeInMillis(date);
                instance.set(Calendar.DATE, dayOfMonth);
                date = instance.getTimeInMillis();

                date = assetDepreciation.nextDate(date);
                map.put("price", unitPrice);
                map.put("date", DateTimeUtil.getFormattedTime(date, "dd-MMM-yyyy"));
                unitPrice = Math.round(unitPrice - depreciationAmount);
                if (unitPrice > 0 && unitPrice < depreciationAmount) {
                    unitPrice = 0;
                }

                mapList.add(map);
            }

            context.put("depreciationList", mapList);
        }
        return false;
    }
}
