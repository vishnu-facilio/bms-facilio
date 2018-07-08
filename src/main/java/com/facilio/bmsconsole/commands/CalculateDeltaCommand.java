package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.tuple.Pair;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.NumberField;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class CalculateDeltaCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		Map<String, List<ReadingContext>> readingMap = CommonCommandUtil.getReadingMap((FacilioContext) context);
		if (readingMap != null && !readingMap.isEmpty()) {
			Map<String, ReadingDataMeta> rdmMap = (Map<String, ReadingDataMeta>)context.get(FacilioConstants.ContextNames.READING_DATA_META);
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			List<Pair<Long, FacilioField>> newRdmPairs = new ArrayList<>();
			for (Entry<String, List<ReadingContext>> entry : readingMap.entrySet()) {
				String moduleName = entry.getKey();
				if (moduleName != null && !moduleName.isEmpty()) {
					List<ReadingContext> readings = entry.getValue();
					if (readings != null && !readings.isEmpty()) {
						List<FacilioField> fields = modBean.getAllFields(moduleName);
						Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
						List<FacilioField> counterFields = fields.stream().filter(field -> (field.getDataTypeEnum() == FieldType.NUMBER) && ((NumberField) field).isCounterField()).collect(Collectors.toList());
						if (counterFields != null && !counterFields.isEmpty()) {
							for (ReadingContext reading : readings) {
								for (FacilioField field : counterFields) {
									Object val = FieldUtil.castOrParseValueAsPerType(field, reading.getReading(field.getName()));
									if (val != null) {
										ReadingDataMeta rdm = rdmMap.get(reading.getParentId()+"_"+field.getFieldId());
										Long prevVal = (Long) FieldUtil.castOrParseValueAsPerType(field, rdm.getValue());
										if (prevVal != -1) {
											String fieldName = field.getName()+"Delta";
											Long deltaVal = ((Long) val - prevVal);
											
											reading.addReading(fieldName, deltaVal);
											newRdmPairs.add(Pair.of(reading.getParentId(), fieldMap.get(fieldName)));
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
					long resourceId = meta.getResourceId();
					long fieldId = meta.getField().getFieldId();
					rdmMap.put(resourceId+"_"+fieldId, meta);
				}
			}
		}
		return false;
	}

}
