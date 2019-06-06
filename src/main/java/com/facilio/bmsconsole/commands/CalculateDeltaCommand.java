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
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class CalculateDeltaCommand implements Command {

	private static final Logger LOGGER = LogManager.getLogger(CalculateDeltaCommand.class.getName());
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		Map<String, List<ReadingContext>> readingMap = CommonCommandUtil.getReadingMap((FacilioContext) context);
		if (readingMap != null && !readingMap.isEmpty()) {
			Map<String, ReadingDataMeta> rdmMap = (Map<String, ReadingDataMeta>)context.get(FacilioConstants.ContextNames.PREVIOUS_READING_DATA_META);
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			List<Pair<Long, FacilioField>> newRdmPairs = new ArrayList<>();
			for (Entry<String, List<ReadingContext>> entry : readingMap.entrySet()) {
				String moduleName = entry.getKey();
				if (moduleName != null && !moduleName.isEmpty()) {
					List<ReadingContext> readings = entry.getValue();
					if (readings != null && !readings.isEmpty()) {
						List<FacilioField> fields = modBean.getAllFields(moduleName);
						Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
						List<FacilioField> counterFields = fields.stream().filter(field -> (field.getDataTypeEnum() == FieldType.NUMBER || field.getDataTypeEnum() == FieldType.DECIMAL) && ((NumberField) field).isCounterField()).collect(Collectors.toList());
						if (counterFields != null && !counterFields.isEmpty()) {
							for (ReadingContext reading : readings) {
								for (FacilioField field : counterFields) {
									Object val = FieldUtil.castOrParseValueAsPerType(field, reading.getReading(field.getName()));
									if (AccountUtil.getCurrentOrg().getId() == 78) {
										LOGGER.info("Value for "+field.getName()+" is : "+val);
									}
									if (val != null) {
										ReadingDataMeta rdm = rdmMap.get(ReadingsAPI.getRDMKey(reading.getParentId(), field));
										Object deltaVal = null;
										if (field.getDataTypeEnum() == FieldType.DECIMAL) {
											Double prevVal = (Double) FieldUtil.castOrParseValueAsPerType(field, rdm.getValue());
											if (prevVal != -1) {
												if (AccountUtil.getCurrentOrg().getId() == 78) {
													LOGGER.info("Pre Value for "+field.getName()+" is : "+prevVal);
												}
												deltaVal = (Double) val - prevVal;
											}
										}
										else {
											Long prevVal = (Long) FieldUtil.castOrParseValueAsPerType(field, rdm.getValue());
											if (prevVal != -1) {
												if (AccountUtil.getCurrentOrg().getId() == 78) {
													LOGGER.info("Pre Value for "+field.getName()+" is : "+prevVal);
												}
												deltaVal = (Long) val - prevVal;
											}
										}
										
										if (deltaVal != null) {
											String fieldName = field.getName()+"Delta";
											reading.addReading(fieldName, deltaVal);
											newRdmPairs.add(Pair.of(reading.getParentId(), fieldMap.get(fieldName)));
											if (AccountUtil.getCurrentOrg().getId() == 78) {
												LOGGER.info("Delta Value for "+fieldName+" is : "+deltaVal);
											}
										}
									}
								}
							}
						}
					}
				}
			}
			
			if (!newRdmPairs.isEmpty()) {
				List<ReadingDataMeta> metaList = ReadingsAPI.getReadingDataMetaList(newRdmPairs) ;
				
				for(ReadingDataMeta meta : metaList) {
					rdmMap.put(ReadingsAPI.getRDMKey(meta.getResourceId(), meta.getField()), meta);
				}
			}
		}
		return false;
	}

}
