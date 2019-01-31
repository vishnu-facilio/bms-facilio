package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.modules.NumberField;
import com.facilio.constants.FacilioConstants;
import com.facilio.unitconversion.Metric;
import com.facilio.unitconversion.Unit;
import com.facilio.unitconversion.UnitsUtil;

public class ConvertUnitForLatestReadingDataCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		if(context.get(FacilioConstants.ContextNames.IS_FETCH_RDM_FROM_UI) != null && (Boolean) context.get(FacilioConstants.ContextNames.IS_FETCH_RDM_FROM_UI)) {
			List<ReadingDataMeta> rdmList = (List<ReadingDataMeta>) context.get(FacilioConstants.ContextNames.READING_DATA_META_LIST);
			
			for(ReadingDataMeta meta : rdmList) {
				if (meta.getField() instanceof NumberField) {
					Object value = meta.getValue();
					
					NumberField numberField =  (NumberField)meta.getField();
					if(numberField.getMetric() > 0) {
						
						if(numberField.getUnitId() > 0) {
							Unit siUnit = Unit.valueOf(Metric.valueOf(numberField.getMetric()).getSiUnitId());
							value = UnitsUtil.convert(meta.getValue(), siUnit.getUnitId(), numberField.getUnitId());
						}
						else {
							value = UnitsUtil.convertToOrgDisplayUnitFromSi(meta.getValue(), numberField.getMetric());
						}
					}
					if(value != null) {
						meta.setValue(value);
					}
				}
			}
		}
		return false;
	}

}
