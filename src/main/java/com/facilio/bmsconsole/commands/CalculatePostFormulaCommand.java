package com.facilio.bmsconsole.commands;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.bmsconsole.context.FormulaFieldContext.TriggerType;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.FormulaFieldAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.tasker.FacilioTimer;
import com.facilio.time.SecondsChronoUnit;

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
			
			if (AccountUtil.getCurrentOrg().getId() == 88) {
				LOGGER.info("Post Formulas of modules : "+readingMap.keySet());
				LOGGER.info(formulaFields);
			}
			if (formulaFields != null && !formulaFields.isEmpty()) {
				Set<String> completedFormulas = new HashSet<>();
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				for (Entry<String, List<ReadingContext>> entry : readingMap.entrySet()) {
					String moduleName = entry.getKey();
					if (moduleName != null && !moduleName.isEmpty()) {
						List<ReadingContext> readings = entry.getValue();
						Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(entry.getKey()));
						if (readings != null && !readings.isEmpty()) {
							for (ReadingContext reading : readings) {
								calculateDependentFormulas(reading, completedFormulas, formulaFields, fieldMap);
							}
						}
					}
				}
			}
			LOGGER.info(AccountUtil.getCurrentOrg().getId()+"::Time taken for post formula calculation for modules : "+readingMap.keySet()+" is "+(System.currentTimeMillis() - processStarttime));
		}
		
		return false;
	}

	private void calculateDependentFormulas(ReadingContext reading, Set<String> completedFormulas, List<FormulaFieldContext> formulas, Map<String, FacilioField> fieldMap) throws Exception {
		// TODO Auto-generated method stub
		if (reading.getReadings() != null && !reading.getReadings().isEmpty()) {
			for (FormulaFieldContext formula : formulas) {
				if (formula.getMatchedResourcesIds().contains(reading.getParentId())) {
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
						
						FacilioTimer.scheduleInstantJob("PostFormulaCalculationJob", context);
						
						if (AccountUtil.getCurrentOrg().getId() == 88) {
							LOGGER.info("Adding instant job for Post formula calculation for  : "+completedKey);
						}
						
						completedFormulas.add(completedKey);
					}
				}
			}
		}
	}
}
