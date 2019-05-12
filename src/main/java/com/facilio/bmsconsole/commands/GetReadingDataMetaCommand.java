package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.modules.FacilioField;
import com.facilio.modules.FieldFactory;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetReadingDataMetaCommand implements Command {
	private static final Logger LOGGER = LogManager.getLogger(GetReadingDataMetaCommand.class.getName());
	private static final int BATCH_SIZE = 100;
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		Map<String, List<ReadingContext>> readingMap = CommonCommandUtil.getReadingMap((FacilioContext) context);
		
		if (readingMap != null && !readingMap.isEmpty()) {
			Map<String, ReadingDataMeta> readingDataMeta = null;
			ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			List<Pair<Long, FacilioField>> rdmPairs = new ArrayList<>();
			for (Map.Entry<String, List<ReadingContext>> entry : readingMap.entrySet()) {
				String moduleName = entry.getKey();
				List<ReadingContext> readings = entry.getValue();
				List<FacilioField> allFields= bean.getAllFields(moduleName);
				Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(allFields);
				for(ReadingContext reading : readings) {
					Map<String, Object> readingData = reading.getReadings();
					if (readingData != null && !readingData.isEmpty()) {
						for (String fieldName : readingData.keySet()) {
							FacilioField field = fieldMap.get(fieldName);
							if (field != null) {
								Pair<Long, FacilioField> pair = Pair.of(reading.getParentId(), field);
								rdmPairs.add(pair);
								
								if (rdmPairs.size() == BATCH_SIZE) {
									readingDataMeta = fetchRDM(readingDataMeta, rdmPairs);
									rdmPairs.clear();
								}
							}
						}		
					}
				}
//				if (moduleName.equals(FacilioConstants.ContextNames.WEATHER_READING) || moduleName.equals(FacilioConstants.ContextNames.PSYCHROMETRIC_READING)) {
//					logger.log(Level.INFO, "Readings : "+readings);
//					logger.log(Level.INFO, "RDM Pairs : "+rdmPairs);
//				}
			}
			if (!rdmPairs.isEmpty()) {
				readingDataMeta = fetchRDM(readingDataMeta, rdmPairs);
			}
			context.put(FacilioConstants.ContextNames.PREVIOUS_READING_DATA_META, readingDataMeta);
		}
		return false;
	}
	
	
	private Map<String, ReadingDataMeta> fetchRDM(Map<String, ReadingDataMeta> readingDataMeta, List<Pair<Long, FacilioField>> rdmPairs) throws Exception {
		Map<String, ReadingDataMeta> rdm = ReadingsAPI.getReadingDataMetaMap(rdmPairs);
		if (readingDataMeta == null) {
			readingDataMeta = rdm;
		}
		else {
			readingDataMeta.putAll(rdm);
		}
		return readingDataMeta;
	}

}
