package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.dto.Group;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.sensor.SensorRuleContext;
import com.facilio.bmsconsole.context.sensor.SensorRuleType;
import com.facilio.bmsconsole.context.sensor.SensorRuleUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.controlaction.util.ControlActionUtil;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;

public class GenerateSensorRulesListCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		long readingFieldId = (long) context.get(FacilioConstants.ContextNames.READING_FIELD_ID);
		long categoryId = (long) context.get(FacilioConstants.ContextNames.CATEGORY_ID);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioField readingFieldObj = modBean.getField(readingFieldId);
		List<SensorRuleContext> sensorRules = new ArrayList<>();
		
		if (readingFieldId > 0 && categoryId > 0) {	
			
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getSensorRuleFields());	
			
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(FieldFactory.getSensorRuleFields())
					.table(ModuleFactory.getSensorRuleModule().getTableName())
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("readingFieldId"), "" +readingFieldId, NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("assetCategoryId"), "" +categoryId, NumberOperators.EQUALS));
			
			List<Map<String, Object>> props = selectBuilder.get();
		
			if (props != null && !props.isEmpty()) {
				
				sensorRules = FieldUtil.getAsBeanListFromMapList(props, SensorRuleContext.class);
				List<Long> sensorRuleIds = sensorRules.stream().map(sensorRule -> sensorRule.getId()).collect(Collectors.toList());	
				HashMap<Long, JSONObject> sensorRuleValidatorPropsMap = SensorRuleUtil.getSensorRuleValidatorPropsByParentRuleIds(sensorRuleIds);
				
				for(SensorRuleContext sensorRule : sensorRules) {
					if (sensorRuleValidatorPropsMap != null && sensorRuleValidatorPropsMap.get(sensorRule.getId()) != null) {
						sensorRule.setRulePropInfo(sensorRuleValidatorPropsMap.get(sensorRule.getId()));
					}	
				sensorRule.setSubject(SensorRuleType.valuOf(sensorRule.getSensorRuleType()).getValueString());
				}	
			}
			
			String[] specialFields = new String[] {"totalEnergyConsumption", "phaseEnergyR", "phaseEnergyY", "phaseEnergyB"};
			for (SensorRuleType type : SensorRuleType.values()) {	
				
			JSONObject rulePropInfo = new JSONObject();
				for (String prop : type.getSensorRuleValidationType().getSensorRuleProps()) {
					if (!prop.equals("subject") && !prop.equals("severity")) {
						rulePropInfo.put(prop, null);
					}
				
				}
				
			SensorRuleContext newContext = new SensorRuleContext();
			newContext.setSensorRuleType(type);
			newContext.setSubject(type.getValueString());
			newContext.setRulePropInfo(rulePropInfo);
			List<SensorRuleContext> found = sensorRules.stream().filter(i -> i.getSensorRuleType() == type.getIndex()).collect(Collectors.toList());
			
			
			if (found == null || found.isEmpty()) {
			if (readingFieldObj instanceof NumberField && !type.isCounterFieldType()) {	
				sensorRules.add(newContext);
			}
			NumberField numberField = (NumberField) readingFieldObj;
			if ((numberField.isCounterField() ||  Arrays.asList(specialFields).contains(readingFieldObj.getName())) && type.isCounterFieldType()) {
				sensorRules.add(newContext);
			}
			}
		}
		
	}
		context.put(FacilioConstants.ContextNames.SENSOR_RULE_TYPES, sensorRules);	
		
		return false;
	}

}
