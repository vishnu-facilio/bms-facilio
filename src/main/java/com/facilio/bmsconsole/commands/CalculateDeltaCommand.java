package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.util.FacilioUtil;

public class CalculateDeltaCommand extends FacilioCommand {

	private static final Logger LOGGER = LogManager.getLogger(CalculateDeltaCommand.class.getName());
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		Map<String, List<ReadingContext>> readingMap = CommonCommandUtil.getReadingMap((FacilioContext) context);
		if (readingMap != null && !readingMap.isEmpty()) {
			Map<String, ReadingDataMeta> rdmMap = (Map<String, ReadingDataMeta>)context.get(FacilioConstants.ContextNames.PREVIOUS_READING_DATA_META);
			Boolean ignoreSplNullHandling = (Boolean) context.get(FacilioConstants.ContextNames.IGNORE_SPL_NULL_HANDLING);
			ignoreSplNullHandling = ignoreSplNullHandling == null ? Boolean.FALSE : ignoreSplNullHandling;
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			List<Pair<Long, FacilioField>> newRdmPairs = new ArrayList<>();
			for (Entry<String, List<ReadingContext>> entry : readingMap.entrySet()) {
				String moduleName = entry.getKey();
				if (moduleName != null && !moduleName.isEmpty()) {
					List<ReadingContext> readings = entry.getValue();
					if (readings != null && !readings.isEmpty()) {
						FacilioModule module = modBean.getModule(moduleName);
						List<FacilioField> fields = modBean.getAllFields(moduleName);
						Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
						List<FacilioField> counterFields = fields.stream().filter(field -> (field.getDataTypeEnum() == FieldType.NUMBER || field.getDataTypeEnum() == FieldType.DECIMAL) && ((NumberField) field).isCounterField()).collect(Collectors.toList());
						if (counterFields != null && !counterFields.isEmpty()) {
							for (ReadingContext reading : readings) {
								for (FacilioField field : counterFields) {
									Object val = FacilioUtil.castOrParseValueAsPerType(field, reading.getReading(field.getName()));
									if (AccountUtil.getCurrentOrg().getId() == 78) {
										LOGGER.info("Value for "+field.getName()+" is : "+val);
									}
									if (val != null) {
										ReadingDataMeta rdm = rdmMap.get(ReadingsAPI.getRDMKey(reading.getParentId(), field));

										if (reading.getId()!=-1 || (reading.getTtime() != -1 && reading.getTtime() < rdm.getTtime())) {
											newRdmPairs.add(Pair.of(reading.getParentId(), fieldMap.get(field.getName()+"Delta")));
											continue; //Not calculating delta for older values
										}

										Object deltaVal = null;
										if (field.getDataTypeEnum() == FieldType.DECIMAL) {
											Double prevVal = (Double) FacilioUtil.castOrParseValueAsPerType(field, rdm.getValue());
											if (prevVal != -1) {
												if (AccountUtil.getCurrentOrg().getId() == 78) {
													LOGGER.info("Pre Value for "+field.getName()+" is : "+prevVal);
												}
												deltaVal = (Double) val - prevVal;
											}
										}
										else {
											Long prevVal = (Long) FacilioUtil.castOrParseValueAsPerType(field, rdm.getValue());
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
											FacilioField deltaField = fieldMap.get(fieldName);
											if (deltaField == null) {
												throw new IllegalArgumentException("Delta field is not found for counter field : "+fieldName+". This is not supposed to happen");
											}
											newRdmPairs.add(Pair.of(reading.getParentId(), deltaField));
											if (AccountUtil.getCurrentOrg().getId() == 78) {
												LOGGER.info("Delta Value for "+fieldName+" is : "+deltaVal);
											}
										}
									}
								}
								if (reading.getId() != -1) {
									ReadingContext readingBeforeUpdate= ReadingsAPI.getReading(module, fields, reading.getId());
									if(readingBeforeUpdate.getTtime()!=reading.getTtime()){
										ReadingsAPI.updateDeltaForCurrentAndNextRecords(module,fields,readingBeforeUpdate,false,reading.getTtime(),false,rdmMap,ignoreSplNullHandling);
									}
									ReadingsAPI.updateDeltaForCurrentAndNextRecords(module, fields, reading, true,reading.getTtime(),false,rdmMap,ignoreSplNullHandling);
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
