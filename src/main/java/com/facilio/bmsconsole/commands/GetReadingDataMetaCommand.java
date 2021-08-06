package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.accounts.util.AccountUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;

public class GetReadingDataMetaCommand extends FacilioCommand {
	private static final Logger LOGGER = LogManager.getLogger(GetReadingDataMetaCommand.class.getName());
	private static final int BATCH_SIZE = 100;
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean executeCommand(Context context) throws Exception {
		Map<String, List<ReadingContext>> readingMap = CommonCommandUtil.getReadingMap((FacilioContext) context);
		if(AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getId() == 445L && readingMap == null){
			LOGGER.info("GetReadingDataMetaCommand - readingMap is null");
		}
		if (readingMap != null && !readingMap.isEmpty()) {
			Map<String, ReadingDataMeta> readingDataMeta = null;
			ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			List<Pair<Long, FacilioField>> rdmPairs = new ArrayList<>();
			long parentId = -1;
			long readingFieldId = -1;
			for (Map.Entry<String, List<ReadingContext>> entry : readingMap.entrySet()) {
				String moduleName = entry.getKey();
				List<ReadingContext> readings = entry.getValue();
				List<FacilioField> allFields= bean.getAllFields(moduleName);
				Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(allFields);
				if(AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getId() == 445L){
					LOGGER.info("moduleName : " + moduleName + " readings : " + readings + " , fieldMap : " + fieldMap);
				}
				for(ReadingContext reading : readings) {
					Map<String, Object> readingData = reading.getReadings();
					if (readingData != null && !readingData.isEmpty()) {
						parentId = reading.getParentId();
						for (String fieldName : readingData.keySet()) {
							FacilioField field = fieldMap.get(fieldName);
							if (field != null) {
								Pair<Long, FacilioField> pair = Pair.of(reading.getParentId(), field);
								rdmPairs.add(pair);
								if (rdmPairs.size() == BATCH_SIZE) {
									readingDataMeta = fetchRDM(readingDataMeta, rdmPairs);
									rdmPairs.clear();
								}
								readingFieldId = field.getFieldId();
							}else if(field == null && AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getId() == 445L){
								LOGGER.info("Field map is null : field name : " + fieldName + " reading data : " + readingData);
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
			}else{
				if(AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getId() == 445L){
					LOGGER.info("rdmPairs is empty");
				}
			}
			if ((readingDataMeta == null) || (readingDataMeta.isEmpty())) {
				LOGGER.info(" reading data meta empty-> "  + ", parentId - " + parentId + ", readingFieldId - " + readingFieldId);
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
