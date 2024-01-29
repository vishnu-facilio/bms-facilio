package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.unitconversion.Unit;
import com.facilio.unitconversion.UnitsUtil;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

public class ImportReadingPreProcessingCommand extends FacilioCommand {
    private static final Logger LOGGER = LogManager.getLogger(ImportReadingPreProcessingCommand.class.getName());
    @Override
    public boolean executeCommand(Context context) throws Exception {
        convertSIUnits((FacilioContext) context);
        return false;
    }

    private void convertSIUnits(FacilioContext context) {
        Map<String, List<ReadingContext>> readingMap = CommonCommandUtil.getReadingMap(context);
        for (Map.Entry<String, List<ReadingContext>> entry : readingMap.entrySet()) {
            List<ReadingContext> entryValue = entry.getValue();
            for(ReadingContext readingContext : entryValue){
                Map<String, Integer> unitMap = readingContext.getUnits();
                if(!unitMap.isEmpty()) {
                    Map<String, Object> readings = readingContext.getReadings();
                    for (Map.Entry<String, Object> reading : readings.entrySet()) {
                        try {
                            String fieldName = reading.getKey();
                            Integer fromUnit = unitMap.get(fieldName);
                            if (fromUnit != null) {
                                Object convertedValue = UnitsUtil.convertToSiUnit(reading.getValue(), getUnitEnum(fromUnit));
                                reading.setValue(convertedValue);
                            }
                        }catch (Exception e) {
                            LOGGER.error("Error occurred during readings import unit conversion" + e);
                            throw e;
                        }
                    }
                }
            }
        }
    }

    public Unit getUnitEnum(int unitId) {
        if(unitId != -1) {
            return Unit.valueOf(unitId);
        }
        return null;
    }

}
