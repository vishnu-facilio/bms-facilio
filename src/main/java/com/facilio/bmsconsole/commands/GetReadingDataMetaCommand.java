package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetReadingDataMetaCommand extends FacilioCommand {
	private static final Logger LOGGER = LogManager.getLogger(GetReadingDataMetaCommand.class.getName());
	private static final int BATCH_SIZE = 100;
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean executeCommand(Context context) throws Exception {
		Map<String, List<ReadingContext>> readingMap = CommonCommandUtil.getReadingMap((FacilioContext) context);
		
		if (readingMap != null && !readingMap.isEmpty()) {
			Map<String, ReadingDataMeta> readingDataMeta = null;
			ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			List<Pair<Long, FacilioField>> rdmPairs = new ArrayList<>();
			for (Map.Entry<String, List<ReadingContext>> entry : readingMap.entrySet()) {
				String moduleName = entry.getKey();
				if(AccountUtil.getCurrentOrg().getId() == 78l){
					LOGGER.info("multivariate check moduleName->" + moduleName);
				}
				List<ReadingContext> readings = entry.getValue();
				List<FacilioField> allFields= bean.getAllFields(moduleName);
				Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(allFields);
				if(AccountUtil.getCurrentOrg().getId() == 78l){
					LOGGER.info("multivariate check readings->" + readings);
				}
				for(ReadingContext reading : readings) {
					Map<String, Object> readingData = reading.getReadings();
					if(AccountUtil.getCurrentOrg().getId() == 78l){
						LOGGER.info("multivariate check readingData->" + readingData);
					}
					if (readingData != null && !readingData.isEmpty()) {
						for (String fieldName : readingData.keySet()) {
							FacilioField field = fieldMap.get(fieldName);
							if(AccountUtil.getCurrentOrg().getId() == 78l){
								LOGGER.info("multivariate check fieldName->" + fieldName);
							}
							if(AccountUtil.getCurrentOrg().getId() == 78l){
								LOGGER.info("multivariate check field->" + field);
							}
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
			if(AccountUtil.getCurrentOrg().getId() == 78l){
				LOGGER.info("multivariate check rdmPairs->" + rdmPairs);
			}
			
			if (!rdmPairs.isEmpty()) {
				readingDataMeta = fetchRDM(readingDataMeta, rdmPairs);
				if(AccountUtil.getCurrentOrg().getId() == 78l){
					LOGGER.info("multivariate check readingDataMeta->" + readingDataMeta);
				}
			}
			if ((readingDataMeta == null) || (readingDataMeta.isEmpty())) {
				LOGGER.info("multivariate Org id ->" + AccountUtil.getCurrentOrg().getId());
				LOGGER.info("multivariate Org id -> ->" + (AccountUtil.getCurrentOrg().getId() == 78l));
				LOGGER.info("multivariate Org id -> ->" + (AccountUtil.getCurrentOrg().getId() == 78));

				LOGGER.info(" reading data meta empty->" + readingDataMeta);
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
