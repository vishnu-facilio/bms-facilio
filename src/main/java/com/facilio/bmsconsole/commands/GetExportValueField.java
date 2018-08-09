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
		Map<String,Object> table = exportFormatObject(fieldMap, events, readings);
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
	
	private Map<String,Object> exportFormatObject (Map<FacilioModule, List<FacilioField>> fieldMap,List<EventContext> events, List<ReadingContext> readings) throws Exception {
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
		for (Map.Entry<FacilioModule, List<FacilioField>> entry : fieldMap.entrySet()) {
		for(FacilioField fiel: entry.getValue()) {
			if (fiel.getDisplayName().equals("Actual Timestamp")) {
				
			} else {
				header.add(fiel.getDisplayName());
				headerFields.put(fiel.getDisplayName(), fiel);
			}
		}
		for (EventContext event : events) {
			for(ReadingContext reading : readings) {
				
				if(reading.getActualTtime() == event.getCreatedTime())
				{
					HashMap<String, Object> hmap = new HashMap<String, Object>();
				      hmap.put("Event Message", event.getEventMessage());
				      hmap.put("Event Id", String.valueOf(event.getId()));
				      hmap.put("Created Time", DateTimeUtil.getFormattedTime(event.getCreatedTime()));
				      hmap.put("Severity", event.getSeverity());
				      records.add(hmap);
				      for(FacilioField fiel: entry.getValue()) {
				    	  Object val = "";
				    	  if (reading.getDatum(fiel.getName()) != null)
				    	  {
				    		  if (fiel.getDisplayName().equals("Actual Timestamp")) {
				    			  val = reading.getActualTtime();
				    		  }
				    		  val = reading.getDatum(fiel.getName());
				    	  }
				    	  if (fiel.getDisplayName().equals("Actual Timestamp")) {
				    		  
				    	  } else {
				    		  hmap.put(fiel.getDisplayName(), val);
				    	  }
						}
					readingList.put(event.getId(), reading);
				}
			}
		}
		}
		Map<String,Object> table = new HashMap<String, Object>();
		table.put("headers", header);
		table.put("records", records);
		return table;
	}
}
