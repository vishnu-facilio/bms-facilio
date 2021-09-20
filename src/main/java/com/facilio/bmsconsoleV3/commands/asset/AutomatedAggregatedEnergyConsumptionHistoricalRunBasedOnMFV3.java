package com.facilio.bmsconsoleV3.commands.asset;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.bmsconsoleV3.context.asset.V3EnergyMeterContext;
import com.facilio.command.FacilioCommand;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.MapUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.util.AggregatedEnergyConsumptionUtil;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.DeviceAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;

public class AutomatedAggregatedEnergyConsumptionHistoricalRunBasedOnMFV3 extends FacilioCommand {

    private static final Logger LOGGER = LogManager.getLogger(AutomatedAggregatedEnergyConsumptionHistoricalRunBasedOnMFV3.class.getName());
    @Override
    public boolean executeCommand(Context context) throws Exception {

        try {
            Set<String> extendedModules = Constants.getExtendedModules(context);

            if(!extendedModules.contains(FacilioConstants.ContextNames.ENERGY_METER)){
                return false;
            }


            List<V3AssetContext> assetList = (List<V3AssetContext>) (((Map<String,Object>)context.get(FacilioConstants.ContextNames.RECORD_MAP)).get(FacilioConstants.ContextNames.ENERGY_METER));

            if(assetList != null && !assetList.isEmpty())
            {
                for(V3AssetContext asset:assetList)
                {
                    EnergyMeterContext energyMeterContext = DeviceAPI.getEnergyMeter(asset.getId());

                    if(asset != null && (asset.getConnected() == null || asset.getConnected().equals(Boolean.FALSE)))
                    {
                        Map<String,Object> datamap = asset.getData();
                        if((!datamap.containsKey("multiplicationFactor")) ||( (Long)datamap.get("multiplicationFactor") == -99l || (Long)datamap.get("multiplicationFactor") == -1l)){

                            datamap.put("multiplicationFactor", 1);
                            asset.setData(datamap);
                        }else{
                            datamap.put("multiplicationFactor", (Long)datamap.get("multiplicationFactor"));
                        }
                        if(Double.valueOf(datamap.get("multiplicationFactor").toString()) != -1 && energyMeterContext != null && energyMeterContext.getMultiplicationFactor() == Double.valueOf(datamap.get("multiplicationFactor").toString())) {
                            continue;
                        }
                        if(energyMeterContext == null){
                            energyMeterContext = FieldUtil.getAsBeanFromMap(FieldUtil.getAsProperties(asset), EnergyMeterContext.class);
                        }
                        energyMeterContext.setMultiplicationFactor(Double.valueOf(datamap.get("multiplicationFactor").toString()));
                        AggregatedEnergyConsumptionUtil.calculateHistoryForAggregatedEnergyConsumption(-1l, -1l, Collections.singletonList(asset.getId()), Collections.singletonList(energyMeterContext));
                    }

                }
            }

        }
        catch(Exception e) {
            LOGGER.log(Level.ERROR,"AutomatedAggregatedEnergyConsumptionHistoricalRunBasedOnMFV3 Error -- "+e, e);
        }
        return false;
    }



}
