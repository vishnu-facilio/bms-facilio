package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.util.FormulaFieldAPI;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.workflows.util.WorkflowUtil;

public class CalculatePreFormulaCommand implements Command {
	private static final Logger LOGGER = LogManager.getLogger(CalculatePreFormulaCommand.class.getName());
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		Map<String, ReadingDataMeta> rdm = (Map<String, ReadingDataMeta>) context.get(FacilioConstants.ContextNames.READING_DATA_META);
		Map<String, List<ReadingContext>> readingMap = CommonCommandUtil.getReadingMap((FacilioContext) context);
		if (readingMap != null && !readingMap.isEmpty()) {
			long processStarttime = System.currentTimeMillis();
			Map<String, List<FormulaFieldContext>> formulaMap = FormulaFieldAPI.getActivePreFormulasOfModule(readingMap.keySet());
			if (formulaMap != null && !formulaMap.isEmpty()) {
				List<Pair<Long, FacilioField>> newRdmPairs = new ArrayList<>();
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				for (Entry<String, List<ReadingContext>> entry : readingMap.entrySet()) {
					String moduleName = entry.getKey();
					if (moduleName != null && !moduleName.isEmpty()) {
						List<FormulaFieldContext> formulas = formulaMap.get(moduleName);
						if (formulas != null && !formulas.isEmpty()) {
							List<ReadingContext> readings = entry.getValue();
							Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(entry.getKey()));
							if (readings != null && !readings.isEmpty()) {
								for (ReadingContext reading : readings) {
									calculateFormulas(reading, fieldMap, formulas, rdm, newRdmPairs);
								}
							}
						}
					}
				}
				
				if (!newRdmPairs.isEmpty()) {
					List<ReadingDataMeta> metaList = ReadingsAPI.getReadingDataMetaList(newRdmPairs) ;
					
					for(ReadingDataMeta meta : metaList) {
						long resourceId = meta.getResourceId();
						long fieldId = meta.getField().getFieldId();
						rdm.put(resourceId+"_"+fieldId, meta);
					}
				}
			}
			LOGGER.info(AccountUtil.getCurrentOrg().getId()+"::Time taken for pre formula calculation for modules : "+readingMap.keySet()+" is "+(System.currentTimeMillis() - processStarttime));
		}
		return false;
	}
	
	private static void calculateFormulas (ReadingContext reading, Map<String, FacilioField> fieldMap, List<FormulaFieldContext> formulas, Map<String, ReadingDataMeta> rdmMap, List<Pair<Long, FacilioField>> newRdmPairs) throws Exception {
		Map<String, Object> data = reading.getReadings();
		if (formulas != null && !formulas.isEmpty() && data != null && !data.isEmpty()) {
			for (FormulaFieldContext formula : formulas) {
				if (formula.getMatchedResources().contains(reading.getParentId())) {
					formula.getWorkflow().setIgnoreNullParams(true);
					Map<String, Object> params = FieldUtil.getAsProperties(reading);
					params.put("resourceId", reading.getParentId());
					
					for (Map.Entry<String, Object> entry : data.entrySet()) {
						FacilioField field = fieldMap.get(entry.getKey());
						if (field != null) {
							ReadingDataMeta rdm = rdmMap.get(reading.getParentId()+"_"+field.getFieldId());
							params.put(entry.getKey()+".previousValue", rdm.getValue())  ;
						}
					}
					
					try {
						Double resultVal = (Double) WorkflowUtil.getWorkflowExpressionResult(formula.getWorkflow().getWorkflowString(), params, false);
						if (resultVal != null) {
							reading.addReading(formula.getReadingField().getName(), resultVal);
							newRdmPairs.add(Pair.of(reading.getParentId(), formula.getReadingField()));
						}
					}
					catch (Exception e) {
						LOGGER.error("Error occurred during pre formula calculation of "+formula.getName()+" for resource : "+reading.getParentId()+". Msg : "+e.getMessage());
					}
				}
			}
		}
	}

}
