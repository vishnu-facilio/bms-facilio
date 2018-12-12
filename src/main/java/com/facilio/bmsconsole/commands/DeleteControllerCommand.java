package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.tuple.Pair;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericDeleteRecordBuilder;
import com.facilio.timeseries.TimeSeriesAPI;

public class DeleteControllerCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		long id = (long) context.get(FacilioConstants.ContextNames.CONTROLLER_ID); 
		
		deleteReadings(id);
		
		FacilioModule module = ModuleFactory.getControllerModule();
		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getIdCondition(id, module));
		
		context.put(FacilioConstants.ContextNames.ROWS_UPDATED, builder.delete());
		
		return false;
	}
	
	private void deleteReadings(long id) throws Exception {
		List<Map<String, Object>> instances = TimeSeriesAPI.getMappedInstances(id);
		 if (instances != null && !instances.isEmpty()) {
			 ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			 Map<String, List<Pair<Long, FacilioField>>> moduleVsFields = new HashMap<>();
			 for(Map<String, Object> instance: instances) {
				 long assetId = (long) instance.get("assetId");
				 FacilioField field = bean.getField((long) instance.get("fieldId"));
				 String moduleName = field.getModule().getName();
				 
				 List<Pair<Long, FacilioField>> fields = moduleVsFields.get(moduleName);
				 if (fields == null) {
					 fields = new ArrayList<>();
					 moduleVsFields.put(moduleName, fields);
				 }
				 fields.add(Pair.of(assetId, field));
			 }
			 for(Map.Entry<String, List<Pair<Long, FacilioField>>> entry :moduleVsFields.entrySet()) {
				 String moduleName = entry.getKey();
				 List<Pair<Long, FacilioField>> pairs = entry.getValue();
				 
				 ReadingsAPI.deleteAssetReading(pairs, bean.getModule(moduleName), null, null);
			 }
		 }
	}

}
