package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.tuple.Pair;

import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.unitconversion.Metric;
import com.facilio.unitconversion.Unit;
import com.facilio.unitconversion.UnitsUtil;

public class GetLatestReadingValuesCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<FacilioModule> modules = (List<FacilioModule>) context.get(FacilioConstants.ContextNames.MODULE_LIST);
		long parentId = (long) context.get(FacilioConstants.ContextNames.PARENT_ID);
		if(modules != null && !modules.isEmpty() && parentId != -1) {
			List<Pair<Long, FacilioField>> rdmPairs = new ArrayList<>();
			Map<String, List<ReadingContext>> readingData = new HashMap<>();
			for(FacilioModule module : modules) {
				for(FacilioField field : module.getFields()) {
					rdmPairs.add(Pair.of(parentId, field));
				}
			}
			
			List<ReadingDataMeta> rdms = ReadingsAPI.getReadingDataMetaList(rdmPairs);
			if (rdms != null && !rdms.isEmpty()) {
				for (ReadingDataMeta rdm : rdms) {
					if (rdm.getReadingDataId() != -1) {
						ReadingContext reading = new ReadingContext();
						reading.setParentId(rdm.getResourceId());
						reading.setTtime(rdm.getTtime());
						
						if (rdm.getField() instanceof NumberField) {
							Object value = rdm.getValue();
							
							NumberField numberField =  (NumberField)rdm.getField();
							if(numberField.getMetric() > 0) {
								
								if(numberField.getUnitId() > 0) {
									Unit siUnit = Unit.valueOf(Metric.valueOf(numberField.getMetric()).getSiUnitId());
									value = UnitsUtil.convert(rdm.getValue(), siUnit.getUnitId(), numberField.getUnitId());
								}
								else {
									value = UnitsUtil.convertToOrgDisplayUnitFromSi(rdm.getValue(), numberField.getMetric());
								}
							}
							rdm.setValue(value);
							reading.addReading(rdm.getField().getName(), value);
						}
						else {
							reading.addReading(rdm.getField().getName(), rdm.getValue());
						}
						
						reading.setId(rdm.getReadingDataId());
						
						readingData.put(rdm.getField().getName(), Collections.singletonList(reading));
					}
				}
			}
			
			context.put(FacilioConstants.ContextNames.READINGS, readingData);
		}
		return false;
	}

}
