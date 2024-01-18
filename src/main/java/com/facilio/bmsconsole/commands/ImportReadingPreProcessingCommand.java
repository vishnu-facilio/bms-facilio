package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.unitconversion.Unit;
import com.facilio.unitconversion.UnitsUtil;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class ImportReadingPreProcessingCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        convertSIUnits((FacilioContext) context);
        return false;
    }

    private void convertSIUnits(FacilioContext context) throws Exception {
        Map<String, List<ReadingContext>> readingMap = CommonCommandUtil.getReadingMap(context);
        ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        for (Map.Entry<String, List<ReadingContext>> entry : readingMap.entrySet()) {
            String moduleName = entry.getKey();
            List<ReadingContext> entryValue = entry.getValue();
            List<FacilioField> allFields= bean.getAllFields(moduleName);
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(allFields);
            for(ReadingContext readingContext : entryValue){
                Map<String, Integer> unitMap = readingContext.getUnits();
                if(!unitMap.isEmpty()) {
                    Map<String, Object> readings = readingContext.getReadings();
                    for (Map.Entry<String, Object> reading : readings.entrySet()) {

                        String fieldName = reading.getKey();
                        Integer fromUnit = unitMap.get(fieldName);
                        if (fromUnit != null) {
                            FacilioField field = fieldMap.get(fieldName);
                            Object convertedValue = UnitsUtil.convert(reading.getValue(), getUnitEnum(fromUnit), ((NumberField) field).getUnitEnum());
                            reading.setValue(convertedValue);
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
