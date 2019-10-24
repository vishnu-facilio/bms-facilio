package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.unitconversion.Metric;
import com.facilio.unitconversion.Unit;
import com.facilio.unitconversion.UnitsUtil;

public class SetCurrentValueForKPICommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		List<Map<String, Object>> kpis = (List<Map<String, Object>>) context.get(ContextNames.KPI_LIST);
		if (CollectionUtils.isEmpty(kpis)) {
			return false;
		}
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		Map<Long, FacilioField> fieldMap = new HashMap<>();
		
		// TODO mark the current value as null if empty
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
			if (field instanceof NumberField) {
				Object value = kpi.get("value");
				if (value != null && !value.equals("-1")) {
					NumberField numberField =  (NumberField)field;
					if(numberField.getMetric() > 0) {
						if(numberField.getUnitId() > 0) {
							Unit siUnit = Unit.valueOf(Metric.valueOf(numberField.getMetric()).getSiUnitId());
							value = UnitsUtil.convert(value, siUnit.getUnitId(), numberField.getUnitId());
							kpi.put("unit", numberField.getUnit());
						}
						else {
							Unit orgDisplayUnit = UnitsUtil.getOrgDisplayUnit(AccountUtil.getCurrentOrg().getOrgId(), numberField.getMetric());
							value = UnitsUtil.convertToOrgDisplayUnitFromSi(value, numberField.getMetric());
							kpi.put("unit", orgDisplayUnit.getSymbol());
						}
					}
					kpi.put("value", value);
				}
//				kpi.put("field", field);
			}
		}
		
		return false;
	}

}
