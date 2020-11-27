package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.sensor.SensorRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class UpdateSensorRulesCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<SensorRuleContext> sensorRules = (List<SensorRuleContext>) context.get(FacilioConstants.ContextNames.SENSOR_RULE_TYPES);
		long readingFieldId = (long) context.get(FacilioConstants.ContextNames.READING_FIELD_ID);
		long categoryId = (long) context.get(FacilioConstants.ContextNames.CATEGORY_ID);
		long moduleId = (long) context.get(FacilioConstants.ContextNames.MODULE_ID);
		if (readingFieldId > 0 && categoryId > 0) {
			FacilioModule module = ModuleFactory.getSensorRuleModule();
			List<Long> sensorRuleIds = sensorRules.stream().map(sensorRule -> sensorRule.getId()).collect(Collectors.toList());	
			GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
					.table(module.getTableName())
					.andCustomWhere("READING_FIELD_ID = ?", readingFieldId);			
			int count = builder.delete();			
			
			for (SensorRuleContext sensorRule : sensorRules) {
				GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
						.fields(FieldFactory.getSensorRuleFields())
						.table(module.getTableName()); 
				SensorRuleContext newContext = new SensorRuleContext();
				newContext.setReadingFieldId(readingFieldId);
				newContext.setAssetCategoryId(categoryId);
				newContext.setSensorRuleType(sensorRule.getSensorRuleType());
				newContext.setModuleId(moduleId);
				Map<String, Object> props = new HashMap<>();
				props = FieldUtil.getAsProperties(newContext);
				insertBuilder.addRecord(props);
				
				insertBuilder.save();
				
				if (sensorRule.getRulePropInfo() != null && !sensorRule.getRulePropInfo().isEmpty()) {
					GenericInsertRecordBuilder propsInsertBuilder = new GenericInsertRecordBuilder()
							.fields(FieldFactory.getSensorRulePropsFields())
							.table(ModuleFactory.getSensorRulePropsModule().getTableName());
					Map<String, Object> prop = new HashMap<>();
					prop.put("parentSensorRuleId", props.get("id"));
					prop.put("ruleValidatorProps", sensorRule.getRulePropInfo());
					propsInsertBuilder.addRecord(prop);
					propsInsertBuilder.save();
					
				}
			}			
			
		}
		
		
		return false;
	}

	

}
