package com.facilio.bmsconsole.commands;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.commands.AgentV2Command;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.point.Point;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.timeseries.TimeSeriesAPI;
import com.facilio.util.FacilioUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;

import java.text.MessageFormat;
import java.util.*;

@Log4j
public class ModeledDataCommandV2 extends AgentV2Command {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long timeStamp = Long.parseLong(context.get(FacilioConstants.ContextNames.TIMESTAMP).toString());
        Map<String, Object> snapshot = (Map<String, Object>) context.get(FacilioConstants.ContextNames.DataProcessor.DATA_SNAPSHOT);
        Map<String, Point> pointRecords = (Map<String, Point>) context.get(FacilioConstants.ContextNames.DataProcessor.POINT_RECORDS);

        Map<String, ReadingContext> iModuleVsReading;
        if (pointRecords != null) {
            iModuleVsReading = getModuleVsReading(context, pointRecords, timeStamp, snapshot);
            addToContext(context, iModuleVsReading);
        }
        return false;
    }

    private Map<String, ReadingContext> getModuleVsReading(Context context, Map<String, Point> dataPointsRecords, Long timeStamp, Map<String, Object> data) throws Exception {
    	FacilioAgent agent = (FacilioAgent) context.get(AgentConstants.AGENT);
    	int agentInterval = (int)agent.getInterval(); 
    	
        Map<String, Object> unmodelled = new HashMap<>();
        Map<String, ReadingContext> iModuleVsReading = new HashMap<>();
        for (String pointName : data.keySet()) {
            Object pointValue = data.get(pointName);
            if (dataPointsRecords.containsKey(pointName)) {
                Point p = dataPointsRecords.get(pointName);
                Long resourceId = p.getResourceId();
                Long fieldId = p.getFieldId();
                if (fieldId != null && resourceId != null) {
                    ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                    FacilioField field = bean.getField(fieldId);
                    FieldType type = field.getDataTypeEnum();
                    String moduleName = field.getModule().getName();
                    
                    try {
                        Object value = pointValue;
                        if (type == FieldType.DECIMAL || type == FieldType.NUMBER) {
                            value = FacilioUtil.castOrParseValueAsPerType(field, pointValue);
                        }
                        
                        int interval = p.getInterval() > 0 ? p.getInterval() : agentInterval;
                        String readingKey = moduleName + "|" + resourceId + "|" + interval;
                        ReadingContext reading = iModuleVsReading.get(readingKey);
                        if (reading == null) {
                            reading = new ReadingContext();
                            reading.setParentId(resourceId);
                            reading.setTtime(timeStamp);
                            reading.setDataInterval(interval);
                            iModuleVsReading.put(readingKey, reading);
                        }
                        
                        reading.addReading(field.getName(), value);
                        
                    } catch (NumberFormatException ex) {
                        LOGGER.info(MessageFormat.format("Error while converting to reading. Field: {0}, Parent: {1}, Value: {2}", field, resourceId, pointValue), ex);
                    }

                } else {
                    unmodelled.put(pointName, pointValue);
                }
            }
        }
        context.put(FacilioConstants.ContextNames.DataProcessor.UNMODELED, unmodelled);
        return iModuleVsReading;
    }


    private void addToContext(Context context, Map<String, ReadingContext> iModuleVsReading) {
        Map<String, List<ReadingContext>> moduleVsReading = new HashMap<>();
        for (Map.Entry<String, ReadingContext> iMap : iModuleVsReading.entrySet()) { //send the data to their's module eg.Energy_Meter...
            String key = iMap.getKey();
            String moduleName = key.substring(0, key.indexOf("|"));
            ReadingContext reading = iMap.getValue();
            List<ReadingContext> readings = moduleVsReading.get(moduleName);
            if (readings == null) {
                readings = new ArrayList<ReadingContext>();
                moduleVsReading.put(moduleName, readings);
            }
            readings.add(reading);
        }

        context.put(FacilioConstants.ContextNames.READINGS_MAP, moduleVsReading);
        context.put(FacilioConstants.ContextNames.HISTORY_READINGS, false);
    }
}
