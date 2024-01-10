package com.facilio.common.reading.add;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.util.FormulaFieldAPI;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import com.facilio.workflows.util.WorkflowUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@Log4j
public class CalculatePreFormulaCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long startTime = System.currentTimeMillis();
        Map<String, ReadingDataMeta> rdm = (Map<String, ReadingDataMeta>) context.get(FacilioConstants.ContextNames.PREVIOUS_READING_DATA_META);
        Map<String, List<ReadingContext>> readingMap = CommonCommandUtil.getReadingMap((FacilioContext) context);
        if (MapUtils.isNotEmpty(readingMap)) {
            long processStarttime = System.currentTimeMillis();
            Map<String, List<FormulaFieldContext>> formulaMap = FormulaFieldAPI.getActivePreFormulasOfModule(readingMap.keySet());
            LOGGER.debug("Pre Formulas of modules : " + readingMap.keySet() + ",  formula map : " + formulaMap);
            if (MapUtils.isNotEmpty(formulaMap)) {
                List<Pair<Long, FacilioField>> newRdmPairs = new ArrayList<>();
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                for (Entry<String, List<ReadingContext>> entry : readingMap.entrySet()) {
                    String moduleName = entry.getKey();
                    if (moduleName != null && !moduleName.isEmpty()) {
                        List<FormulaFieldContext> formulas = formulaMap.get(moduleName);
                        if (CollectionUtils.isNotEmpty(formulas)) {
                            List<ReadingContext> readings = entry.getValue();
                            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(moduleName));
                            if (CollectionUtils.isNotEmpty(readings)) {
                                for (int i = 0; i < readings.size(); i++) {
                                    ReadingContext reading = readings.get(i);
                                    ReadingContext transformedReading = calculateFormulas(reading, fieldMap, formulas, rdm, newRdmPairs);
                                    if (transformedReading != null) {
                                        readings.set(i, transformedReading);
                                    }
                                }
                            }
                        }
                    }
                }
                if (!newRdmPairs.isEmpty()) {
                    List<ReadingDataMeta> metaList = ReadingsAPI.getReadingDataMetaList(newRdmPairs);

                    if (metaList != null) { //To handle pre formula updation of system fields
                        for (ReadingDataMeta meta : metaList) {
                            rdm.put(ReadingsAPI.getRDMKey(meta.getResourceId(), meta.getField()), meta);
                        }
                    }
                }
            }
            LOGGER.debug("Time taken for pre formula calculation for modules : " + readingMap.keySet() + " is " + (System.currentTimeMillis() - processStarttime));
        }
        LOGGER.debug("CalculatePreFormulaCommand time taken " + (System.currentTimeMillis() - startTime));
        return false;
    }

    private static ReadingContext calculateFormulas(ReadingContext reading, Map<String, FacilioField> fieldMap, List<FormulaFieldContext> formulas, Map<String, ReadingDataMeta> rdmMap, List<Pair<Long, FacilioField>> newRdmPairs) throws Exception {
        Map<String, Object> data = reading.getReadings();
        if (formulas != null && !formulas.isEmpty() && data != null && !data.isEmpty()) {
            Map<String, Object> readingProps = FieldUtil.getAsProperties(reading);
            Map<String, Object> params = new HashMap<>(readingProps);
            params.put("resourceId", reading.getParentId());

            for (Map.Entry<String, Object> entry : data.entrySet()) {
                FacilioField field = fieldMap.get(entry.getKey());
                if (field != null) {
                    ReadingDataMeta rdm = rdmMap.get(ReadingsAPI.getRDMKey(reading.getParentId(), field));
                    if (rdm != null) {
                        params.put(entry.getKey()+".previousValue", rdm.getValue());
                    }
                }
            }

            boolean isChanged = false;
            for (FormulaFieldContext formula : formulas) {
                formula.getWorkflow().setIgnoreNullParams(true);
                if (formula.getMatchedResourcesIds().contains(reading.getParentId())) {
                    try {
                        Object resultVal = WorkflowUtil.getWorkflowExpressionResult(formula.getWorkflow(), params, null, false, false);
                        if (resultVal != null) {
                            isChanged = true;
                            resultVal = FacilioUtil.castOrParseValueAsPerType(formula.getReadingField(), resultVal);
                            readingProps.put(formula.getReadingField().getName(), resultVal);
                            params.put(formula.getReadingField().getName(), resultVal);

                            newRdmPairs.add(Pair.of(reading.getParentId(), formula.getReadingField()));
                        }
                    } catch (Exception e) {
                        LOGGER.error("Error occurred during pre formula calculation of "+formula.getName()+" for resource : "+reading.getParentId(), e);
                    }
                }
            }
            if (isChanged) {
                return FieldUtil.getAsBeanFromMap(readingProps, ReadingContext.class);
            }
        }
        return null;
    }

}
