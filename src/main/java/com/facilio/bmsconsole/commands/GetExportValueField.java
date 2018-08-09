package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.StringOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.ExportUtil;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventContext;
import com.facilio.fs.FileInfo.FileFormat;
import com.facilio.fw.BeanFactory;

public class GetExportValueField implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		int type= (int) context.get(EventConstants.EventContextNames.TYPE);
		List<EventContext> events = (List<EventContext>) context.get(EventConstants.EventContextNames.EVENT_LIST);
		List<Long> fieldIds = (List<Long>) context.get(EventConstants.EventContextNames.FIELD_ID);
		long parentId = (long) context.get(EventConstants.EventContextNames.PARENT_ID);
		ModuleBean  modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		Map<FacilioModule, List<FacilioField>> fieldMap = mapModuleFields(modBean, fieldIds);
		
		List<Long> times = events.stream().map(event -> event.getCreatedTime()).collect(Collectors.toList());
		List<ReadingContext> readings = new ArrayList<ReadingContext>();
		for (Map.Entry<FacilioModule, List<FacilioField>> entry : fieldMap.entrySet()) {
			Map<String, FacilioField> currentFieldMap = FieldFactory.getAsMap(modBean.getAllFields(entry.getKey().getName()));
			entry.getValue().add(currentFieldMap.get("actualTtime"));
			SelectRecordsBuilder<ReadingContext> builder = new SelectRecordsBuilder<ReadingContext>()
					.module(entry.getKey())
					.beanClass(ReadingContext.class)
					.select(entry.getValue())
					.andCondition(CriteriaAPI.getCondition(currentFieldMap.get("actualTtime"), times, StringOperators.IS))
					.andCondition(CriteriaAPI.getCondition(currentFieldMap.get("parentId"), Collections.singletonList(parentId), NumberOperators.EQUALS));
			readings.addAll(builder.get());
		}
		Map<Long, Map<String, Object>> valueMap = new HashMap<>();
		for(ReadingContext reading: readings) {
			Map<String, Object> data;
			if (!valueMap.containsKey(reading.getActualTtime())) {
				data = new HashMap<>();
				valueMap.put(reading.getActualTtime(), data);
			}
			else {
				data = valueMap.get(reading.getActualTtime());
			}
			data.putAll(reading.getData());
		}
		List<FacilioField> fieldsList = new ArrayList<FacilioField>();
		fieldMap.values().forEach(fieldsList::addAll);
		Map<String,Object> table = exportFormatObject(fieldsList, events, valueMap);
		String fileUrl = ExportUtil.exportData(FileFormat.getFileFormat(type), "Alarm Summary", table);
		context.put(EventConstants.EventContextNames.FILEURL, fileUrl);
		return false;
	}
	
	private Map<FacilioModule, List<FacilioField>> mapModuleFields(ModuleBean modBean, List<Long> fieldIds) throws Exception {
		Map<FacilioModule, List<FacilioField>> fieldMap = new HashMap<>();
		for (Long fieldId : fieldIds) {
			FacilioField field = modBean.getField(fieldId);
			List<FacilioField> fields = fieldMap.get(field.getModule());
			if (fields == null) {
				fields = new ArrayList<>();
				fieldMap.put(field.getModule(), fields);
			}
			fields.add(field);
		}
		return fieldMap;
	}
	
	private Map<String,Object> exportFormatObject (List<FacilioField> fieldList,List<EventContext> events, Map<Long, Map<String, Object>> readings) throws Exception {
		Map<Long , ReadingContext> readingList = new HashMap<Long, ReadingContext>();
		List<Object> records = new ArrayList<Object>();
		List<String> header = new ArrayList<String>();
		Map<String, FacilioField> headerFields = new HashMap<String, FacilioField>();
		Map<String, FacilioField> eventField = FieldFactory.getAsMap(EventConstants.EventFieldFactory.getEventFields());
		header.add("Event Id");
		header.add("Event Message");
		header.add("Created Time");
		header.add("Severity");
		headerFields.put("Event Id", eventField.get("id"));
		headerFields.put("Event Message", eventField.get("eventMessage"));
		headerFields.put("Created Time", eventField.get("createdTime"));
		headerFields.put("Severity", eventField.get("severity"));
		for (FacilioField fields: fieldList) {
			if (!fields.getName().equals("actualTtime")) {
  				header.add(fields.getDisplayName());
  				headerFields.put(fields.getDisplayName(), fields);
  			}
		}
		for (EventContext event : events) {
			HashMap<String, Object> hmap = new HashMap<String, Object>();
		      hmap.put("Event Message", event.getEventMessage());
		      hmap.put("Event Id", String.valueOf(event.getId()));
		      hmap.put("Created Time", DateTimeUtil.getFormattedTime(event.getCreatedTime()));
		      hmap.put("Severity", event.getSeverity());
		      records.add(hmap);
		      Map<String, Object> maps = readings.get(event.getCreatedTime());
		    
		      
//			for(ReadingContext reading : readings) {
				if(maps != null)
				{
					for (FacilioField field: fieldList) {
							if (!field.getName().equals("actualTtime") && maps.containsKey(field.getName())) {
					    		  hmap.put(field.getDisplayName(), maps.get(field.getName()));
					    	  }
					}
				}
//			}
		}
		Map<String,Object> table = new HashMap<String, Object>();
		table.put("headers", header);
		table.put("records", records);
		return table;
	}
}
