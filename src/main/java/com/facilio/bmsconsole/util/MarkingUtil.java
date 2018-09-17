package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.DeltaCalculationCommand;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.MarkedReadingContext;
import com.facilio.bmsconsole.context.MarkedReadingContext.MarkType;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;

public class MarkingUtil {
	
	private static final Logger LOGGER = LogManager.getLogger(MarkingUtil.class.getName());


private static FacilioModule getModule(String moduleName) throws Exception{
	ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
	return modBean.getModule(moduleName);
}





public static MarkedReadingContext getMarkedReading(ReadingContext reading,long fieldId,long moduleId, MarkType markType, Object currentReading, Object lastReading) {
	
	MarkedReadingContext mReading= new MarkedReadingContext();
	mReading.setReading(reading);
	mReading.setFieldId(fieldId);
	mReading.setModuleId(moduleId);
	mReading.setMarkType(markType);
	mReading.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
	mReading.setActualValue(String.valueOf(currentReading));
	mReading.setModifiedValue(String.valueOf(lastReading));
	return mReading;
	
}

public static void addMarkedreadings(List<MarkedReadingContext> markedList) throws Exception {
	GenericInsertRecordBuilder insertBuilder=getMarkedReadingBuilder();
	for (MarkedReadingContext markedReading: markedList) {

		ReadingContext reading=markedReading.getReading();
		if(reading!=null) {
			markedReading.setDataId(reading.getId());//doing this here as id will not be available for the new reading..
			markedReading.setReading(null);
		}
		Map <String,Object> record=  FieldUtil.getAsProperties(markedReading);
		insertBuilder.addRecord(record);
	}
	insertBuilder.save();

}

public static List<Double> getActualValues(long resourceId, long fieldId, long ttime, MarkType type)  {
	
	List<Double> actualValList = new ArrayList<>();
	FacilioModule module=ModuleFactory.getMarkedReadingModule();
	FacilioField actualValFld=FieldFactory.getField("actualValue", "ACTUAL_VALUE", module, FieldType.STRING);
	List<FacilioField> fields = new ArrayList<>();
	fields.add(actualValFld);
	
	GenericSelectRecordBuilder actualValueBuilder = new GenericSelectRecordBuilder()
			.select(fields)
			.table(module.getTableName())
			.andCustomWhere("ORGID = ?", AccountUtil.getCurrentOrg().getOrgId())
			.andCustomWhere("RESOURCE_ID = ?", resourceId)
			.andCustomWhere("FIELD_ID", fieldId)
			.andCustomWhere("TTIME >= ?", ttime)
			.andCustomWhere("MARK_TYPE = ?", type)
			.orderBy("TTIME");
	try {

		List<Map<String, Object>> actualValMap = actualValueBuilder.get();
		if(actualValMap == null || actualValMap.isEmpty()) {
			return actualValList;
		}
		for(Map<String, Object> actualVal : actualValMap) {
			String value=(String)actualVal.get("actualValue");
			try {
				actualValList.add(Double.parseDouble(value));
			}
			catch(NumberFormatException e) {}// intentionally leaving this exception..
		}
	}
	catch(Exception e) {
		LOGGER.error(e.getMessage(), e);
	}
	return actualValList;
}

private static GenericInsertRecordBuilder  getMarkedReadingBuilder()  throws Exception{
	
	GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
			.table(ModuleFactory.getMarkedReadingModule().getTableName())
			.fields(FieldFactory.getMarkedReadingFields());
	return insertBuilder;
	
}




}
