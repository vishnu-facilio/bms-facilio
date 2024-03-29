package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.tuple.Pair;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.timeseries.TimeSeriesAPI;

public class DeleteControllerCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		long id = (long) context.get(FacilioConstants.ContextNames.CONTROLLER_ID);
		boolean deleteReadings = (boolean) context.get(FacilioConstants.ContextNames.DEL_READING_RULE);
		
//		deleteReadings(id, deleteReadings);
		
		FacilioModule module = ModuleFactory.getControllerModule();
		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(module.getTableName())
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
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
//					 ReadingsAPI.deleteReadings(assetId, fields, null, deleteReadings);
				 }
			 }
		 }
	}

}
