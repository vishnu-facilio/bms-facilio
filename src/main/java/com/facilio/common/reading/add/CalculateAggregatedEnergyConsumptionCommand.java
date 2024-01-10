package com.facilio.common.reading.add;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.ReadingDataMeta.ReadingInputType;
import com.facilio.bmsconsole.util.AggregatedEnergyConsumptionUtil;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.command.PostTransactionCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.transaction.NewTransactionService;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateRange;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Log4j
@Deprecated
public class CalculateAggregatedEnergyConsumptionCommand extends FacilioCommand implements PostTransactionCommand {
    private LinkedHashMap<Long, DateRange> meterIdVsMaxDateRange = new LinkedHashMap<Long, DateRange>();

    @Override
    public boolean executeCommand(Context context) throws Exception {
        long startTime = System.currentTimeMillis();
        try {
            Map<String, ReadingDataMeta> readingsMetaMap = (Map<String, ReadingDataMeta>) context.get(FacilioConstants.ContextNames.PREVIOUS_READING_DATA_META);
            if (MapUtils.isEmpty(readingsMetaMap)) {
                return false;
            }

            Map<String, List<ReadingContext>> readingMap = CommonCommandUtil.getReadingMap((FacilioContext) context);

            if (readingMap != null && !readingMap.isEmpty()) {
                for (Map.Entry<String, List<ReadingContext>> entry : readingMap.entrySet()) {
                    String moduleName = entry.getKey();
                    List<ReadingContext> readings = entry.getValue();
                    if (!FacilioConstants.ContextNames.ENERGY_DATA_READING.equals(moduleName)) {
                        continue;
                    }

                    List<Pair<Long, FacilioField>> aggregatedEnergyConsumptionRdmPairs = new ArrayList<>();
                    for (ReadingContext reading : readings) {
                        addAggregatedEnergyConsumptionModuleRDM(reading, readingsMetaMap, aggregatedEnergyConsumptionRdmPairs);
                    }

                    if (!aggregatedEnergyConsumptionRdmPairs.isEmpty()) {
                        LOGGER.info("Inside CalculateAggregatedEnergyConsumptionCommand lastReadingMap " + readingMap);
                        List<Long> filteredResourceIds = new ArrayList<Long>();
                        for (Pair<Long, FacilioField> aggregatedEnergyConsumptionRdmPair : aggregatedEnergyConsumptionRdmPairs) {
                            filteredResourceIds.add(aggregatedEnergyConsumptionRdmPair.getLeft());
                        }
                        meterIdVsMaxDateRange = AggregatedEnergyConsumptionUtil.getCompleteDateRangeFromReadings(readings, filteredResourceIds);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error in CalculateAggregatedEnergyConsumptionCommand -- meterIdVsMaxDateRange: " + meterIdVsMaxDateRange +
                    " Exception: " + e.getMessage(), e);
        }

        LOGGER.debug("CalculateAggregatedEnergyConsumption time taken " + (System.currentTimeMillis() - startTime));

        return false;
    }

    private void addAggregatedEnergyConsumptionModuleRDM(ReadingContext reading, Map<String, ReadingDataMeta> readingsMetaMap,
                                                         List<Pair<Long, FacilioField>> aggregatedEnergyConsumptionRdmPairs) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule energyDataModule = modBean.getModule(FacilioConstants.ContextNames.ENERGY_DATA_READING);
        Map<String, FacilioField> energyDataFieldMap = FieldFactory.getAsMap(modBean.getAllFields(energyDataModule.getName()));

        FacilioField readingDeltaField = energyDataFieldMap.get("totalEnergyConsumptionDelta");
        readingDeltaField.setModule(energyDataModule);
        FacilioField energyReadingField = energyDataFieldMap.get("totalEnergyConsumption");
        energyReadingField.setModule(energyDataModule);

        ReadingDataMeta deltaRdm = readingsMetaMap.get(ReadingsAPI.getRDMKey(reading.getParentId(), readingDeltaField));
        ReadingDataMeta energyReadingFieldRdm = readingsMetaMap.get(ReadingsAPI.getRDMKey(reading.getParentId(), energyReadingField));

        if (deltaRdm == null || energyReadingFieldRdm == null) {
            LOGGER.info("delta rdm or energyReadingFieldRdm is null");
            return;
        }

        Object readingDeltaVal = reading.getReading(readingDeltaField.getName());
        if (readingDeltaVal == null) {
            LOGGER.info("readingDeltaVal is null");
            return;
        }

        if (energyReadingFieldRdm.getInputType() != ReadingInputType.TASK.getValue() && energyReadingFieldRdm.getInputType() != ReadingInputType.WEB.getValue()) {
            return;
        }

        FacilioModule module = modBean.getModule(AggregatedEnergyConsumptionUtil.AGGREGATED_ENERGY_CONSUMPTION_MODULE_NAME);
        if (module == null) {
            return;
        }
        List<FacilioField> allFields = modBean.getAllFields(module.getName());
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(allFields);

        FacilioField aggregatedEnergyConsumptionField = fieldMap.get(AggregatedEnergyConsumptionUtil.AGGREGATED_ENERGY_CONSUMPTION_READING_NAME);
        aggregatedEnergyConsumptionField.setModule(module);
        aggregatedEnergyConsumptionRdmPairs.add(Pair.of(reading.getParentId(), aggregatedEnergyConsumptionField));
    }


    @Override
    public boolean postExecute() throws Exception {

        try {
            if (MapUtils.isNotEmpty(meterIdVsMaxDateRange)) {
                List<ReadingContext> finalDeltaAggregatedReadings = AggregatedEnergyConsumptionUtil.getFinalDeltaAggregatedReadings(meterIdVsMaxDateRange);
                LinkedHashMap<String, ReadingContext> alreadyPresentAggregatedReadingsMap = AggregatedEnergyConsumptionUtil.getAlreadyPresentAggregatedReadings(finalDeltaAggregatedReadings);

                if (CollectionUtils.isNotEmpty(finalDeltaAggregatedReadings)) {
                    LinkedHashMap<String, ReadingContext> finalDeltaReadingsMap = AggregatedEnergyConsumptionUtil.getReadingsMapWithParentTimeKey(finalDeltaAggregatedReadings);
                    NewTransactionService.newTransaction(() -> AggregatedEnergyConsumptionUtil.checkAddorUpdateOfAggregatedEnergyConsumptionData(finalDeltaReadingsMap, alreadyPresentAggregatedReadingsMap));
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error while updating CalculateAggregatedEnergyConsumptionCommand in Post Execute -- meterIdVsMaxDateRange: " + meterIdVsMaxDateRange + " Exception: " + e.getMessage(), e);
        }


        return false;
    }

}
