package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.unitconversion.Metric;

public class AddDefaultUnitsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {

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
