package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.unitconversion.Metric;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddDefaultUnitsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {

		long orgId = (long) context.get("orgId");
		List<Map<String, Object>> props = new ArrayList<>();
		
		for(Metric metrics :Metric.getAllMetrics()) {
			
			Map<String, Object> prop = new HashMap<>();
			
			prop.put("orgId", orgId);
			prop.put("metric", metrics.getMetricId());
			prop.put("unit", metrics.getSiUnitId());
			
			props.add(prop);
			
		}
		GenericInsertRecordBuilder insert = new GenericInsertRecordBuilder();
		insert.table(ModuleFactory.getOrgUnitsModule().getTableName());
		insert.fields(FieldFactory.getOrgUnitsFields());
		insert.addRecords(props);

		insert.save(); 

		return false;
	}

}
