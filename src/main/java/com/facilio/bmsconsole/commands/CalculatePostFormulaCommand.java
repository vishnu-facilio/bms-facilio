package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ControllerContext;
import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.bmsconsole.context.FormulaFieldContext.TriggerType;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.modules.FacilioField;
import com.facilio.modules.FieldFactory;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.FormulaFieldAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.tasker.FacilioTimer;
import com.facilio.time.SecondsChronoUnit;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class CalculatePostFormulaCommand implements Command {
	private static final Logger LOGGER = LogManager.getLogger(CalculatePostFormulaCommand.class.getName());
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		Map<String, ReadingDataMeta> readingDataMeta = (Map<String, ReadingDataMeta>) context.get(FacilioConstants.ContextNames.PREVIOUS_READING_DATA_META);
		Map<String, List<ReadingContext>> readingMap = CommonCommandUtil.getReadingMap((FacilioContext) context);
		if (readingDataMeta != null && !readingDataMeta.isEmpty() && readingMap != null && !readingMap.isEmpty()) {
			long processStarttime = System.currentTimeMillis();
			Collection<ReadingDataMeta> metaList = readingDataMeta.values();
			Set<Long> fieldIds = metaList.stream().map(meta -> meta.getField().getId()).collect(Collectors.toSet());
			
			List<FormulaFieldContext> formulaFields = FormulaFieldAPI.getActiveFormulasDependingOnFields(TriggerType.POST_LIVE_READING, fieldIds);
			LOGGER.debug("Post Formulas of modules : "+readingMap.keySet());
			LOGGER.debug(formulaFields);
			if (formulaFields != null && !formulaFields.isEmpty()) {
				Set<String> completedFormulas = new HashSet<>();
				Long controllerTime = (Long) context.get(FacilioConstants.ContextNames.CONTROLLER_TIME);
				Integer controllerLevel = (Integer) context.get(FacilioConstants.ContextNames.CONTROLLER_LEVEL);
				List<ControllerContext> controllers = controllerTime == null ? null : new ArrayList<>();
				
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				for (Entry<String, List<ReadingContext>> entry : readingMap.entrySet()) {
					String moduleName = entry.getKey();
					if (moduleName != null && !moduleName.isEmpty()) {
						List<ReadingContext> readings = entry.getValue();
						Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(entry.getKey()));
						if (readings != null && !readings.isEmpty()) {
							for (ReadingContext reading : readings) {
								calculateDependentFormulas(reading, completedFormulas, formulaFields, fieldMap, controllers, controllerTime, controllerLevel);
							}
						}
					}
				}
				
				if (controllers != null) {
					context.put(FacilioConstants.ContextNames.CONTROLLER_LIST, controllers);
				}
			}
			LOGGER.info(AccountUtil.getCurrentOrg().getId()+"::Time taken for post formula calculation for modules : "+readingMap.keySet()+" is "+(System.currentTimeMillis() - processStarttime));
		}
		
		return false;
	}

	private void calculateDependentFormulas(ReadingContext reading, Set<String> completedFormulas, List<FormulaFieldContext> formulas, Map<String, FacilioField> fieldMap, List<ControllerContext> formulaControllers, Long controllerTime, Integer controllerLevel) throws Exception {
		// TODO Auto-generated method stub
		if (reading.getReadings() != null && !reading.getReadings().isEmpty()) {
			for (FormulaFieldContext formula : formulas) {
				if (formula.getMatchedResourcesIds().contains(reading.getParentId()) && containsDependentField(formula, reading, fieldMap)) {
					String completedKey = null;
					if (reading.isNewReading()) {
						completedKey = formula.getId()+"|"+reading.getParentId();
					}
					else {
						long ttime = reading.getTtime();
						ZonedDateTime zdt = DateTimeUtil.getDateTime(ttime);
						zdt = zdt.truncatedTo(new SecondsChronoUnit(formula.getInterval() * 60));
						long startTime = DateTimeUtil.getMillis(zdt, true);
						long endTime = (startTime + (formula.getInterval() * 60 * 1000)) - 1;
						completedKey = formula.getId()+"|"+reading.getParentId()+"|"+startTime+"|"+endTime;
					}
					
					if (!completedFormulas.contains(completedKey)) {
						FacilioContext context = new FacilioContext();
						context.put(FacilioConstants.ContextNames.READING, reading);
						context.put(FacilioConstants.ContextNames.FORMULA_FIELD, formula);
						context.put(FacilioConstants.ContextNames.MODULE_FIELD_MAP, fieldMap);
						
						if (controllerTime != null) {
							ControllerContext formulaController = getFormulaController(formula, completedKey);
							context.put(FacilioConstants.ContextNames.CONTROLLER, formulaController);
							context.put(FacilioConstants.ContextNames.CONTROLLER_TIME, controllerTime);
							context.put(FacilioConstants.ContextNames.CONTROLLER_LEVEL, controllerLevel);
							
							formulaControllers.add(formulaController);
						}
						
						FacilioTimer.scheduleInstantJob("PostFormulaCalculationJob", context);
						LOGGER.debug("Adding instant job for Post formula calculation for  : "+completedKey);
						
						completedFormulas.add(completedKey);
					}
				}
			}
		}
	}
	
	private ControllerContext getFormulaController (FormulaFieldContext formula, String name) {
		ControllerContext controller = new ControllerContext();
		controller.setMacAddr(name);
		controller.setDataInterval(formula.getInterval());
		controller.setName(name);
		controller.setActive(true);
		
		return controller;
	}
	
	private boolean containsDependentField(FormulaFieldContext formula, ReadingContext reading, Map<String, FacilioField> fieldMap) {
		Map<String, Object> readingData = reading.getData();
		if (readingData != null) {
			for (String fieldName : readingData.keySet()) {
				FacilioField field = fieldMap.get(fieldName);
				if (field != null && formula.getWorkflow().getDependentFieldIds().contains(field.getId())) {
					return true;
				}
			}
		}
		return false;
	}
}
