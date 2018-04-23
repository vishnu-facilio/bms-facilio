package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.MarkedReadingContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericInsertRecordBuilder;

public class AddMarkedReadingValuesCommand implements Command {

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		
		List<MarkedReadingContext> markedList= (List<MarkedReadingContext>)context.get(FacilioConstants.ContextNames.MARKED_READINGS);
		if(markedList==null || markedList.isEmpty()) {
			return false;
		}
		addMarkedreadings(markedList);
		return false;
	}
	
	private void addMarkedreadings(List<MarkedReadingContext> markedList) throws Exception {
		System.err.println( Thread.currentThread().getName()+"Inside addMarkedReadings in  MarkedCommand#######  "+markedList);
		GenericInsertRecordBuilder insertBuilder=getMarkedReadingBuilder();
		for (MarkedReadingContext markedReading: markedList) {

			ReadingContext reading=markedReading.getReading();
			if(reading!=null) {
				markedReading.setDataId(reading.getId());
				markedReading.setReading(null);
			}
			Map <String,Object> record=  FieldUtil.getAsProperties(markedReading);
			insertBuilder.addRecord(record);
		}
		insertBuilder.save();
		System.err.println( Thread.currentThread().getName()+"Exiting addMarkedReadings in  MarkedCommand#######  ");

	}

	private GenericInsertRecordBuilder  getMarkedReadingBuilder()  throws Exception{
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getMarkedReadingModule().getTableName())
				.fields(FieldFactory.getMarkedReadingFields());
		return insertBuilder;
		
	}

}
