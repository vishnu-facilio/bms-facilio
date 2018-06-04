package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class GetReadingDataMetaCommand implements Command {
	private static final Logger logger = LogManager.getLogger(GetReadingDataMetaCommand.class.getName());
	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		
		
		Boolean historyReading = (Boolean) context.get(FacilioConstants.ContextNames.HISTORY_READINGS);
		if (historyReading != null && historyReading==true) {
			return false;
		}
		Map<String, List<ReadingContext>> readingMap = CommonCommandUtil.getReadingMap((FacilioContext) context);
		
		if (readingMap != null && !readingMap.isEmpty()) {
			ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			Map<String, ReadingDataMeta> readingDataMeta = new HashMap<> ();
			
			for (Map.Entry<String, List<ReadingContext>> entry : readingMap.entrySet()) {
				String moduleName = entry.getKey();
				List<ReadingContext> readings = entry.getValue();
				List<FacilioField> allFields= bean.getAllFields(moduleName);
				Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(allFields);
				List<Pair<Long, FacilioField>> rdmPairs = new ArrayList<>();
				for(ReadingContext reading : readings) {
					Map<String, Object> readingData = reading.getData();
					if (readingData != null && !readingData.isEmpty()) {
						for (String fieldName : readingData.keySet()) {
							Pair<Long, FacilioField> pair = Pair.of(reading.getParentId(), fieldMap.get(fieldName));
							rdmPairs.add(pair);
						}		
					}
				}
//				if (moduleName.equals(FacilioConstants.ContextNames.WEATHER_READING) || moduleName.equals(FacilioConstants.ContextNames.PSYCHROMETRIC_READING)) {
					logger.log(Level.INFO, "Readings : "+readings);
					logger.log(Level.INFO, "RDM Pairs : "+rdmPairs);
//				}
				List<ReadingDataMeta> metaList = ReadingsAPI.getReadingDataMetaList(rdmPairs) ;
				for(ReadingDataMeta meta : metaList) {
					long resourceId = meta.getResourceId();
					long fieldId = meta.getField().getFieldId();
					readingDataMeta.put(resourceId+"_"+fieldId, meta);
				}
			}
			context.put(FacilioConstants.ContextNames.READING_DATA_META, readingDataMeta);
		}
		return false;
	}
	

}
