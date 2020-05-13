package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.RollUpField;
import com.facilio.bmsconsole.context.SensorEventContext;
import com.facilio.bmsconsole.context.SensorRuleContext;
import com.facilio.bmsconsole.instant.jobs.ParallelWorkflowRuleExecutionJob;
import com.facilio.bmsconsole.util.RollUpFieldUtil;
import com.facilio.bmsconsole.util.SensorRuleUtil;import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.events.constants.EventConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.LookupField;

public class ExecuteSensorRuleCommand extends FacilioCommand {

	private static final Logger LOGGER = Logger.getLogger(ExecuteSensorRuleCommand.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		try {
			Map<String, List<ReadingContext>> readingMap = CommonCommandUtil.getReadingMap((FacilioContext) context);
			if(readingMap != null && !readingMap.isEmpty()) 
			{		
				for(Map.Entry<String, List<ReadingContext>> entry : readingMap.entrySet()) 
				{				
					String moduleName = entry.getKey();
					List<ReadingContext> readings = new LinkedList<>(entry.getValue());
					if (moduleName == null || moduleName.isEmpty() || readings == null || readings.isEmpty()) {
						LOGGER.log(Level.WARNING, "Module Name / Records is null/ empty while executing readings in ExecuteSensorRuleCommand ==> "+moduleName+"==>"+entry.getValue());
						continue;
					}
					
					Boolean isHistorical = (Boolean) context.get(EventConstants.EventContextNames.IS_HISTORICAL_EVENT);
					if (isHistorical == null) {
						isHistorical = false;
					}
					
					ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					FacilioModule readingModule = modBean.getModule(moduleName);
					FacilioModule categoryModule = modBean.getParentModule(readingModule.getModuleId());
					List<SensorRuleContext> sensorRules = new ArrayList<SensorRuleContext>();
					sensorRules = SensorRuleUtil.getSensorRuleByModuleId(categoryModule,true);					
					
					if(sensorRules != null && !sensorRules.isEmpty()) 
					{		
						List<Long> sensorRuleIds = sensorRules.stream().map(sensorRule -> sensorRule.getId()).collect(Collectors.toList());
						HashMap<Long, JSONObject> sensorRuleValidatorPropsMap = SensorRuleUtil.getSensorRuleValidatorPropsByParentRuleIds(sensorRuleIds);
						for(SensorRuleContext sensorRule:sensorRules) 
						{
							List<SensorEventContext> sensorEvents= new ArrayList<SensorEventContext>();
							for(ReadingContext reading:readings) 
							{
								if(reading.getReading(sensorRule.getReadingField().getName()) != null) 
								{
									boolean result = sensorRule.getSensorRuleTypeEnum().getSensorRuleValidationType().evaluateSensorRule(sensorRule, reading.getReadings(), sensorRuleValidatorPropsMap.get(sensorRule.getId()));
									JSONObject defaultSeverityProps = sensorRule.getSensorRuleTypeEnum().getSensorRuleValidationType().addDefaultSeverityAndSubject();
									checkDefaultSeverityProps(defaultSeverityProps, sensorRuleValidatorPropsMap.get(sensorRule.getId()));
									
									if(result) {
										SensorEventContext sensorEvent = sensorRule.constructEvent(reading,sensorRuleValidatorPropsMap.get(sensorRule.getId()), defaultSeverityProps, isHistorical.booleanValue());
										sensorEvents.add(sensorEvent);
									}
									else {
										SensorEventContext sensorEvent = sensorRule.constructClearEvent(reading,sensorRuleValidatorPropsMap.get(sensorRule.getId()), defaultSeverityProps, isHistorical.booleanValue());
										if(sensorEvent != null) {
											sensorEvents.add(sensorEvent);
										}
									}
								}
							}
							
							FacilioChain addEventChain = TransactionChainFactory.getV2AddEventChain(true);
							addEventChain.getContext().put(EventConstants.EventContextNames.EVENT_LIST, sensorEvents);
							addEventChain.execute();
						}
					}			
				}	
			}			
		}
		catch(Exception e) {
			LOGGER.log(Level.SEVERE, "Error in ExecuteSensorRuleCommand -- "+
					" Exception: " + e.getMessage() , e);
		}
		return false;
	}
	
	private void checkDefaultSeverityProps(JSONObject defaultSeverityProps, JSONObject ruleProps) {
		if(ruleProps.get("severity") != null && ruleProps.get("subject") != null) {
			defaultSeverityProps.put("severity", ruleProps.get("severity"));
			defaultSeverityProps.put("subject", ruleProps.get("subject"));
		}
	}
}
