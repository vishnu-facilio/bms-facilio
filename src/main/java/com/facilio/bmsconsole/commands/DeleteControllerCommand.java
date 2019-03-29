package com.facilio.bmsconsole.commands;

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
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeleteControllerCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		long id = (long) context.get(FacilioConstants.ContextNames.CONTROLLER_ID);
		boolean deleteReadings = (boolean) context.get(FacilioConstants.ContextNames.DEL_READING_RULE);
		
		deleteReadings(id, deleteReadings);
		
		FacilioModule module = ModuleFactory.getControllerModule();
		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getIdCondition(id, module));
		
		context.put(FacilioConstants.ContextNames.ROWS_UPDATED, builder.delete());
		
		return false;
	}
	
	private void deleteReadings(long id, boolean deleteReadings) throws Exception {
		List<Map<String, Object>> instances = TimeSeriesAPI.getMappedInstances(id);
		 if (instances != null && !instances.isEmpty()) {
			 ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			 Map<String, List<Pair<Long, FacilioField>>> moduleVsFields = new HashMap<>();
			 Map<String, Map<Long, List<FacilioField>>> moduleVsAssetVsFields = new HashMap<>();
			 for(Map<String, Object> instance: instances) {
				 long assetId = (long) instance.get("assetId");
				 FacilioField field = bean.getField((long) instance.get("fieldId"));
				 String moduleName = field.getModule().getName();
				 
				 /*List<Pair<Long, FacilioField>> fields = moduleVsFields.get(moduleName);
				 if (fields == null) {
					 fields = new ArrayList<>();
					 moduleVsFields.put(moduleName, fields);
				 }
				 fields.add(Pair.of(assetId, field));*/
				 
				 Map<Long, List<FacilioField>> assetVsFields = moduleVsAssetVsFields.get(moduleName);
				 if (assetVsFields == null) {
					 assetVsFields = new HashMap<>();
					 moduleVsAssetVsFields.put(moduleName, assetVsFields);
				 }
				 
				 List<FacilioField> fields = assetVsFields.get(assetId);
				 if (fields == null) {
					 fields = new ArrayList<>();
					 assetVsFields.put(assetId, fields);
				 }
				 
				 fields.add(field);
				 
			 }
			 /*for(Map.Entry<String, List<Pair<Long, FacilioField>>> entry :moduleVsFields.entrySet()) {
				 String moduleName = entry.getKey();
				 List<Pair<Long, FacilioField>> pairs = entry.getValue();
				 
				 ReadingsAPI.deleteAssetReading(pairs, bean.getModule(moduleName), null, null);
			 }*/
			 for(Map.Entry<String, Map<Long, List<FacilioField>>> entry :moduleVsAssetVsFields.entrySet()) {
				 String moduleName = entry.getKey();
				 Map<Long, List<FacilioField>> assetVsFields = entry.getValue();
				 for(Map.Entry<Long, List<FacilioField>> assetEntry :assetVsFields.entrySet()) {
					 long assetId = assetEntry.getKey();
					 List<FacilioField> fields = assetEntry.getValue();
					 ReadingsAPI.deleteReadings(assetId, fields, bean.getModule(moduleName), null, null, deleteReadings);
				 }
			 }
		 }
	}

}
