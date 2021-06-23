package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.FacilioFrequency;
import com.facilio.bmsconsole.util.FormulaFieldAPI;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;

public class SetCurrentValueForKPICommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		List<Map<String, Object>> kpis = (List<Map<String, Object>>) context.get(ContextNames.KPI_LIST);
		if (CollectionUtils.isEmpty(kpis)) {
			return false;
		}
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		Map<Long, FacilioField> fieldMap = new HashMap<>();
		
		FacilioFrequency frequency = (FacilioFrequency) context.get(ContextNames.FREQUENCY);
		long lastUpdatedTime = 0;
		
		for(Map<String, Object> kpi: kpis) {
			long fieldId = (long) kpi.get("readingFieldId");
			FacilioField field;
			if (fieldMap.containsKey(fieldId)) {
				field = fieldMap.get(fieldId);
			}
			else {
				field = modBean.getField(fieldId);
				fieldMap.put(fieldId, field);
			}
			long ttime = (long) kpi.get("ttime");
			if (ttime > lastUpdatedTime) {
				lastUpdatedTime = ttime;
			}
			boolean isLastest = FormulaFieldAPI.isLatestTimeBasedOnFrequency(frequency, ttime);
			if (field instanceof NumberField) {
				NumberField numberField =  (NumberField)field;
				kpi.put("unit", numberField.getUnit());
				if (!isLastest) {
					kpi.put("value", 0);
				}
			}
			else if (!isLastest) {
				kpi.put("value", null);
			}
		}
		
		context.put(ContextNames.MODIFIED_TIME, lastUpdatedTime);
		
		return false;
	}
	

}
